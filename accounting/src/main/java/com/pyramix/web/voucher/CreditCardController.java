package com.pyramix.web.voucher;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.coa.Coa_04_SubAccount02;
import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.creditcard.CreditCard;
import com.pyramix.domain.creditcard.CreditCardDebitCredit;
import com.pyramix.domain.gl.Balance;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.domain.user.User;
import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherStatus;
import com.pyramix.domain.voucher.VoucherType;
import com.pyramix.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.creditcard.dao.CreditCardDao;
import com.pyramix.persistence.gl.dao.BalanceDao;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.persistence.user.dao.UserDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.serial.SerialNumberGenerator;

public class CreditCardController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1566223262801482958L;

	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	private BalanceDao balanceDao;
	private CreditCardDao creditCardDao;
	private UserDao userDao;
	private SerialNumberGenerator serialNumberGenerator;
	private GeneralLedgerDao generalLedgerDao;
	
	private Combobox periodCombobox, ccAcctCombobox;
	private Listbox creditCardListbox;
	private Label begBalanceLabel, endBalanceLabel;
	
	private Balance begBalance;
	private BigDecimal paymentAmount;
	private String begBalanceString, endBalanceString;
	private List<CreditCard> creditCardList;
	private ListModelList<CreditCard> creditCardListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private LocalDate periodStartDate, periodEndDate;	
	private User userCreate;
	private Coa_05_Master masterCoaToCredit;
	
	private static final Logger log = Logger.getLogger(CreditCardController.class);
	
	private static final Long KARTU_KREDIT_SUB_ACCOUNT = 43L; // Kartu Kredit account in SubAccount02
	
	/*
	 * ASSUMING User will be able to define starting year - in settings, perhaps
	 * But, to start with, we assume starting year is 2024
	 */
	private static final int START_YEAR = 2024;	
	
	final private static String PROPERTIES_FILE_PATH="/pyramix/config.properties";
	
	public void onCreate$creditCardPanel(Event event) throws Exception {
		log.info("creditCardPanel created.");
		
		// login user
		userCreate = getUserDao().findUserByUsername(getLoginUsername());
		
		// read config
		int selIndex = getConfigSelectedIndex();
		
		// list periods
		listYearMonthPeriods();
		periodCombobox.setSelectedIndex(selIndex);
		setPeriodStartAndEndDate();
		
		// list accounts -- to Credit (CR)
		listCreditCardAccounts();
		ccAcctCombobox.setSelectedIndex(0);
		masterCoaToCredit = ccAcctCombobox.getSelectedItem().getValue();
		
		// balance / closing
		findBeginingBalance();
		begBalanceLabel.setValue(begBalanceString);
		
		// list creditcard
		listCreditCardTransactions();

		// calc ending balance
		calculateEndingBalance();
		endBalanceLabel.setValue(endBalanceString);
	}	

	public void onCheck$defaultCheckbox(Event event) throws Exception {
		int selIndex =	periodCombobox.getSelectedIndex();

		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            Properties prop = new Properties();

            // load the properties file
            prop.load(input);
			// set the properties value
			prop.setProperty("creditcard_period_index", String.valueOf(selIndex));
			// save properties to project root folder
			prop.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            
			log.info(prop);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	private int getConfigSelectedIndex() {
		String idxStr = "0";
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {

            Properties prop = new Properties();

            // load the properties file
            prop.load(input);

            // get the property value and print it out
            idxStr = prop.getProperty("creditcard_period_index");

		} catch (IOException ex) {
            ex.printStackTrace();
        }

		return Integer.valueOf(idxStr);
	}

	private void listYearMonthPeriods() {
		Comboitem comboitem;
		for (int i=1; i<13; i++) {
			comboitem = new Comboitem();
			comboitem.setLabel(START_YEAR
					+"-"+i);
			comboitem.setParent(periodCombobox);
		}		
	}	
	
	private void setPeriodStartAndEndDate() {
		int selPeriod = periodCombobox.getSelectedIndex();

		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		
		periodStartDate = LocalDate.of(START_YEAR, month, 1);		
		periodEndDate = periodStartDate.with(TemporalAdjusters.lastDayOfMonth());
		
		// log.info("periodStartDate: "+periodStartDate);
		// log.info("periodEndDate: "+periodEndDate);		
	}	
	
	private void listCreditCardAccounts() throws Exception {
		Coa_04_SubAccount02 creditCardSubAccount02 =
				getCoa_04_SubAccount02Dao().findCoa_04_SubAccount02ById(KARTU_KREDIT_SUB_ACCOUNT);
		// proxy
		Coa_04_SubAccount02 coaMasterByProxy =
				getCoa_04_SubAccount02Dao().findCoa_04_AccountMastersByProxy(creditCardSubAccount02.getId());
		// all creditcard accounts
		List<Coa_05_Master> creditCardMasterCoaList = coaMasterByProxy.getMasters();
		
		Comboitem comboitem;
		for(Coa_05_Master creditCardAcct : creditCardMasterCoaList) {
			comboitem = new Comboitem();
			comboitem.setLabel(creditCardAcct.getMasterCoaComp()
					+"-"+creditCardAcct.getMasterCoaName());
			comboitem.setValue(creditCardAcct);
			comboitem.setParent(ccAcctCombobox);
		}
	}
	
	private void findBeginingBalance() throws Exception {
		// coa
		Coa_05_Master coaMaster = ccAcctCombobox.getSelectedItem().getValue();
		
		LocalDate closingLocalDate = findClosingDateForThisPeriod();
		Date closingDate = asDate(closingLocalDate, zoneId);
		try {
			// find the balance
			begBalance =
					getBalanceDao().findAccountBalanceByCoa_ClosingDate(coaMaster, closingDate);
			// find the payment for this period
			paymentAmount = 
					getPaymentAmountForThisPeriod(coaMaster);
			begBalanceString = "Balance as of "
					+dateToStringDisplay(closingLocalDate, getEmphYearMonthShort(), getLocale())
					+" : "
					+toDecimalFormat(begBalance.getBalanceAmount(), getLocale(), getDecimalFormatLocal())
					+" Payment: "
					+toDecimalFormat(paymentAmount, getLocale(), getDecimalFormatLocal());
		} catch (Exception e) {
			begBalanceString = "Balance as of "
					+dateToStringDisplay(closingLocalDate, getEmphYearMonthShort(), getLocale())
					+" : "
					+toDecimalFormat(BigDecimal.ZERO, getLocale(), getDecimalFormatLocal())
					+" Payment: "
					+toDecimalFormat(BigDecimal.ZERO, getLocale(), getDecimalFormatLocal());
		}
		
	}
	
	private BigDecimal getPaymentAmountForThisPeriod(Coa_05_Master coaMaster) throws Exception {
		// INCLUDE Payment for this period -- look for it in the GL using coaMaster
		List<GeneralLedger> glList =
				getGeneralLedgerDao().findAllGeneralLedgerByCoaMaster(coaMaster, asDate(periodStartDate, zoneId), asDate(periodEndDate, zoneId));
		// get all the debitAmount
		BigDecimal payment = BigDecimal.ZERO;
		for(GeneralLedger gl : glList) {
			if (gl.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
				continue;
			}
			// accumulate all the debitAmount
			payment = payment.add(gl.getDebitAmount());
		}
		
		return payment;
	}

	private LocalDate findClosingDateForThisPeriod() {
		// closing date based on period
		int selPeriod = periodCombobox.getSelectedIndex();
		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		// month to localdate
		LocalDate periodDate = LocalDate.of(START_YEAR, month, 1);
		// periodDate minus 30 days
		periodDate = periodDate.minusMonths(1);
		
		// return closingDate
		return periodDate.with(TemporalAdjusters.lastDayOfMonth());
	}

	private void listCreditCardTransactions() throws Exception {
		creditCardList =
				getCreditCardDao().findAllCreditCardByDate(masterCoaToCredit,
						asDate(periodStartDate, zoneId), asDate(periodEndDate, zoneId));
		
		creditCardListModelList =
				new ListModelList<CreditCard>(creditCardList);
		
		creditCardListbox.setModel(creditCardListModelList);
		creditCardListbox.setItemRenderer(getCreditCardListItemRenderer());
	}	
	
	private ListitemRenderer<CreditCard> getCreditCardListItemRenderer() {
		
		return new ListitemRenderer<CreditCard>() {
			
			@Override
			public void render(Listitem item, CreditCard creditCard, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Jurnal
				lc = initTransactionDate(new Listcell(), creditCard);
				lc.setParent(item);
				
				// No.Voucher
				lc = initVoucherNumber(new Listcell(), creditCard);
				lc.setParent(item);
				
				// Nominal(Rp.)
				lc = initTheSumOf(new Listcell(), creditCard);
				lc.setParent(item);
				
				// COA
				lc = initCOA(new Listcell(), creditCard);
				lc.setParent(item);
				
				// Transaksi Info
				lc = initTransactionDescription(new Listcell(), creditCard);
				lc.setParent(item);
				
				// Referensi
				lc = initDocumentRef(new Listcell(), creditCard);
				lc.setParent(item);
				
				// User Create
				lc = initUserCreate(new Listcell(), creditCard);
				lc.setParent(item);
				
				// edit
				lc = initSaveOrEdit(new Listcell(), creditCard);
				lc.setParent(item);
				
				// Status
				lc = initStatus(new Listcell(), creditCard);
				lc.setParent(item);
				
				// posting
				lc = initPosting(new Listcell(), creditCard);
				lc.setParent(item);
				
			}

			private Listcell initTransactionDate(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// set todayDate according to the 'Period'
					int selPeriod = periodCombobox.getSelectedIndex();
					// selPeriod to month
					Month month = Month.of(selPeriod+1);
					// new
					setupJournalDatebox(listcell, LocalDate.of(START_YEAR, month, 1));					
				} else {
					listcell.setLabel(dateToStringDisplay(asLocalDate(creditCard.getTransactionDate()),
							getEmphYearMonthShort(), getLocale()));					
				}
				
				return listcell;
			}

			private Listcell initVoucherNumber(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// created later just before saving
				} else {
					listcell.setLabel(creditCard.getVoucherNumber().getSerialComp());
				}
				
				return listcell;
			}

			private Listcell initTheSumOf(Listcell listcell, CreditCard creditCard) throws Exception {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupSumOfDecimalbox(listcell, BigDecimal.ZERO);					
				} else {
					listcell.setLabel(toDecimalFormat(creditCard.getTheSumOf(), getLocale(), "###.###.###,-"));
				}
				
				return listcell;
			}

			private Listcell initCOA(Listcell listcell, CreditCard creditCard) throws Exception {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupCoaSelection(listcell, creditCard);
				} else {
					listcell.setLabel(creditCard.getMasterCoa().getMasterCoaComp()
							+"-"+creditCard.getMasterCoa().getMasterCoaName());
				}
				
				return listcell;
			}

			private Listcell initTransactionDescription(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(creditCard.getTransactionDescription());
				}

				return listcell;
			}

			private Listcell initDocumentRef(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(creditCard.getDocumentRef());
				}

				return listcell;
			}

			private Listcell initUserCreate(Listcell listcell, CreditCard creditCard) throws Exception {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// do nothing
				} else {
					CreditCard creditCardUserCreateByProxy =
							getCreditCardDao().findCreditCardUserCreateByProxy(creditCard.getId());
					listcell.setLabel(
							creditCardUserCreateByProxy.getUserCreate().getReal_name());
					listcell.setValue(creditCardUserCreateByProxy.getUserCreate());
				}

				return listcell;
			}

			private Listcell initSaveOrEdit(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClicked(listcell, creditCard));
				} else if(creditCard.getVoucherStatus().compareTo(VoucherStatus.Posted)==0) {
					// do nothing
				} else {
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClicked(listcell, creditCard));
				}
				return listcell;
			}

			// ************************** NEW ****************************

			private EventListener<Event> onSaveButtonClicked(Listcell listcell, CreditCard creditCard) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						CreditCard creditCardData =
								getCreditCardData(listcell, creditCard);
						// save
						getCreditCardDao().save(creditCardData);
						// re-list
						listCreditCardTransactions();
					}
				};
			}

			private CreditCard getCreditCardData(Listcell listcell, CreditCard creditCard) throws Exception {
				Datebox datebox =
						(Datebox) listcell.getParent().getChildren().get(0).getFirstChild();
				Decimalbox decimalbox =
						(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
				Combobox combobox =
						(Combobox) listcell.getParent().getChildren().get(3).getFirstChild();
				Coa_05_Master masterCoaToDebit = combobox.getSelectedItem().getValue();
				Textbox textboxTransInfo =
						(Textbox) listcell.getParent().getChildren().get(4).getFirstChild();
				Textbox textboxRef =
						(Textbox) listcell.getParent().getChildren().get(5).getFirstChild();				
				// voucher type is
				VoucherType voucherType = VoucherType.CREDITCARD;
				//	set
				creditCard.setTransactionDate(datebox.getValue());
				creditCard.setTheSumOf(decimalbox.getValue());
				creditCard.setMasterCoa(masterCoaToDebit);
				creditCard.setCreditCardMasterCoa(masterCoaToCredit);
				creditCard.setTransactionDescription(textboxTransInfo.getValue());
				creditCard.setDocumentRef(textboxRef.getValue());
				
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					creditCard.setVoucherNumber(getVoucherSerialNumber(voucherType, datebox.getValue()));
					creditCard.setCreateDate(asDate(todayDate, zoneId));
					// create creditCardDebitCredit list
					creditCard.setCreditCardDebitCredits(getCreditCardDebitCredits(creditCard, masterCoaToDebit));
				} else {
					log.info("update creditCardDebitCreditList");
				}
				creditCard.setVoucherStatus(VoucherStatus.Submitted);
				creditCard.setUserCreate(userCreate);
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				creditCard.setLastModified(asDateTime(currentDateTime, zoneId));
				
				return creditCard;
			}			

			private List<CreditCardDebitCredit> getCreditCardDebitCredits(CreditCard creditCard, Coa_05_Master masterCoaToDebit) {
				List<CreditCardDebitCredit> creditCardDebitCredits = new ArrayList<CreditCardDebitCredit>();
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				
				CreditCardDebitCredit dbcr;
				
				// DB account
				dbcr = new CreditCardDebitCredit();
				dbcr.setCreateDate(asDate(todayDate, zoneId));
				dbcr.setCreditAmount(BigDecimal.ZERO);
				dbcr.setDebitAmount(creditCard.getTheSumOf());
				dbcr.setDbcrDescription(creditCard.getTransactionDescription());
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(masterCoaToDebit);
				
				// add to list
				creditCardDebitCredits.add(dbcr);
				
				// CR account
				dbcr = new CreditCardDebitCredit();
				dbcr.setCreateDate(asDate(todayDate, zoneId));				
				dbcr.setCreditAmount(creditCard.getTheSumOf());
				dbcr.setDebitAmount(BigDecimal.ZERO);
				dbcr.setDbcrDescription(creditCard.getTransactionDescription());
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(masterCoaToCredit);

				// add to list
				creditCardDebitCredits.add(dbcr);
				
				return creditCardDebitCredits;
			}

			// ************************** EXISTING ****************************
			

			private EventListener<Event> onModifyButtonClicked(Listcell listcell, CreditCard creditCard) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("modifyButton Clicked.");						
					}
				};
			}			
			
			//***************************** Posting ***********************************
			
			private Listcell initStatus(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					// default to 'submitted'
					listcell.setLabel(VoucherStatus.Submitted.toString());
				} else {
					// determine action taken by the user to change the status to 'POST'
					listcell.setLabel(creditCard.getVoucherStatus().toString());
				}
				
				return listcell;
			}

			private Listcell initPosting(Listcell listcell, CreditCard creditCard) {
				if (creditCard.getId().compareTo(Long.MIN_VALUE)==0) {
					
				} else if (creditCard.getVoucherStatus().compareTo(VoucherStatus.Submitted)==0) {
					Button button = new Button();
					button.setLabel("Post");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onPostButtonClicked(listcell, creditCard));
				}
				return listcell;
			}

			private EventListener<Event> onPostButtonClicked(Listcell listcell, CreditCard creditCard) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						List<GeneralLedger> glList = new ArrayList<GeneralLedger>();
						
						// create posting voucher serialnumber
						VoucherSerialNumber postingVoucherNumber =
								getVoucherSerialNumber(VoucherType.POSTING_CREDITCARD, creditCard.getTransactionDate());
						// create new GL objects
						for(CreditCardDebitCredit dbcr : creditCard.getCreditCardDebitCredits()) {
							GeneralLedger gl = new GeneralLedger();
							
							gl.setMasterCoa(dbcr.getMasterCoa());
							gl.setPostingDate(creditCard.getTransactionDate());
							gl.setPostingVoucherNumber(postingVoucherNumber);
							gl.setCreditAmount(dbcr.getCreditAmount());
							gl.setDebitAmount(dbcr.getDebitAmount());
							gl.setDbcrDescription(dbcr.getDbcrDescription());
							gl.setTransactionDescription(creditCard.getTransactionDescription());
							gl.setDocumentRef(creditCard.getDocumentRef());
							gl.setTransactionDate(creditCard.getTransactionDate());
							gl.setVoucherType(VoucherType.CREDITCARD);
							gl.setVoucherNumber(creditCard.getVoucherNumber());
							gl.setCreateDate(asDate(todayDate, zoneId));
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							gl.setLastModified(asDateTime(currentDateTime, zoneId));
							
							glList.add(gl);
						}
						// set
						creditCard.setGeneralLedgers(glList);
						
						// update creditCard status
						creditCard.setVoucherStatus(VoucherStatus.Posted);
						creditCard.setPostingVoucherNumber(postingVoucherNumber);
						creditCard.setPostingDate(asDate(todayDate, zoneId));
						
						// update
						getCreditCardDao().update(creditCard);
						
						// re-list
						listCreditCardTransactions();
						
						// re-calc ending balance
						calculateEndingBalance();
						endBalanceLabel.setValue(endBalanceString);						
					}
				};
			}
		};
	}

	private void setupJournalDatebox(Listcell listcell, LocalDate localDate) {
		Datebox datebox = new Datebox();
		datebox.setWidth("130px");
		datebox.setLocale(getLocale());
		datebox.setValue(asDate(localDate, zoneId));
		datebox.setParent(listcell);		
	}	
	
	protected void setupSumOfDecimalbox(Listcell listcell, BigDecimal amount) {
		Decimalbox decimalbox = new Decimalbox();
		decimalbox.setWidth("110px");
		decimalbox.setLocale(getLocale());
		decimalbox.setValue(amount);
		decimalbox.setParent(listcell);
	}	
	
	
	/**
	 * Expense Account - to Debit (DB)
	 * 
	 * This requires ANOTHER MODULE to manage COA that will be displayed
	 * in the selection.  The module requires user to select the COA and
	 * add into the list.
	 * 
	 * This function picks up the list from that MODULE.
	 * 
	 * @param listcell
	 * @param creditCard
	 * @throws Exception 
	 */
	protected void setupCoaSelection(Listcell listcell, CreditCard creditCard) throws Exception {
		Combobox combobox = new Combobox();
		combobox.setWidth("230px");
		combobox.setParent(listcell);
		
		Comboitem comboitem;
		Coa_05_Master coaMaster;
		
		Long[] expenseAcctIds = { 75L, 76L, 43L, 68L, 72L, 74L, 85L, 62L, 86L, 87L, 88L, 89L, 90L, 91L, 92L, 93L, 23L, 94L };
		
		for(Long id : expenseAcctIds) {
			coaMaster = getCoa_05_AccountMasterDao().findCoa_05_MasterById(id);
			comboitem = new Comboitem();
			comboitem.setLabel(coaMaster.getMasterCoaComp()
					+"-"+coaMaster.getMasterCoaName());
			comboitem.setValue(coaMaster);
			comboitem.setParent(combobox);			
		}
	}
	
	protected void setupTextbox(Listcell listcell, String description) {
		Textbox textbox = new Textbox();
		textbox.setWidth("180px");
		textbox.setValue(description);
		textbox.setParent(listcell);
	}		
	
	private VoucherSerialNumber getVoucherSerialNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = getSerialNumberGenerator().getSerialNumber(voucherType, currentDate);
		
		VoucherSerialNumber voucherSerNum = new VoucherSerialNumber();
		voucherSerNum.setVoucherType(voucherType);
		voucherSerNum.setSerialDate(currentDate);
		voucherSerNum.setSerialNo(serialNum);
		voucherSerNum.setSerialComp(formatSerialComp(voucherType.toCode(voucherType.getValue()), currentDate, serialNum));
		voucherSerNum.setCreateDate(asDate(todayDate, zoneId));
		LocalDateTime localDateTime = getLocalDateTime(zoneId);
		voucherSerNum.setLastModified(asDateTime(localDateTime, zoneId));
		
		return voucherSerNum;
	}		
	
	private void calculateEndingBalance() throws Exception {
		BigDecimal nominal = BigDecimal.ZERO;
		for(CreditCard creditCard : creditCardList) {
			nominal = nominal.add(creditCard.getTheSumOf());
		}
		if (begBalance != null) {
			BigDecimal endBalance =
					begBalance.getBalanceAmount().subtract(paymentAmount).add(nominal);
			endBalanceString = "Total Usage:" 
					+ toDecimalFormat(nominal, getLocale(), getDecimalFormatLocal())
					+ " Current Balance: "
					+ toDecimalFormat(endBalance, getLocale(), getDecimalFormatLocal());				
		} else {
			endBalanceString = "Total Usage:"
					+ toDecimalFormat(BigDecimal.ZERO, getLocale(), getDecimalFormatLocal())
					+ " Current Balance: "
					+ toDecimalFormat(BigDecimal.ZERO, getLocale(), getDecimalFormatLocal());
		}
	}
	
	public void onSelect$ccAcctCombobox(Event event) throws Exception {
		masterCoaToCredit = ccAcctCombobox.getSelectedItem().getValue();
	}
	
	public void onClick$findButton(Event event) throws Exception {
		// init
		begBalance = null;
		paymentAmount = BigDecimal.ZERO;
		
		// set start and end date
		setPeriodStartAndEndDate();
		
		// coa to credit
		masterCoaToCredit = ccAcctCombobox.getSelectedItem().getValue();
		
		// begining balance
		findBeginingBalance();
		begBalanceLabel.setValue(begBalanceString);
		
		// re-list
		listCreditCardTransactions();
		
		// calc ending balance
		calculateEndingBalance();
		endBalanceLabel.setValue(endBalanceString);
	}
	
	public void onClick$newCreditCardButton(Event event) throws Exception {
		creditCardListModelList.add(0, new CreditCard());
	}
	
	public void onClick$cancelCreditCardButton(Event event) throws Exception {
		// balance / closing
		findBeginingBalance();
		begBalanceLabel.setValue(begBalanceString);
		
		// list creditcard
		listCreditCardTransactions();

		// calc ending balance
		calculateEndingBalance();
		endBalanceLabel.setValue(endBalanceString);		
	}
	
	public Coa_05_AccountMasterDao getCoa_05_AccountMasterDao() {
		return coa_05_AccountMasterDao;
	}

	public void setCoa_05_AccountMasterDao(Coa_05_AccountMasterDao coa_05_AccountMasterDao) {
		this.coa_05_AccountMasterDao = coa_05_AccountMasterDao;
	}

	public BalanceDao getBalanceDao() {
		return balanceDao;
	}

	public void setBalanceDao(BalanceDao balanceDao) {
		this.balanceDao = balanceDao;
	}

	public CreditCardDao getCreditCardDao() {
		return creditCardDao;
	}

	public void setCreditCardDao(CreditCardDao creditCardDao) {
		this.creditCardDao = creditCardDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}

	public Coa_04_SubAccount02Dao getCoa_04_SubAccount02Dao() {
		return coa_04_SubAccount02Dao;
	}

	public void setCoa_04_SubAccount02Dao(Coa_04_SubAccount02Dao coa_04_SubAccount02Dao) {
		this.coa_04_SubAccount02Dao = coa_04_SubAccount02Dao;
	}
}
