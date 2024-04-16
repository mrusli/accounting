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

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.domain.pettycash.PettyCash;
import com.pyramix.domain.pettycash.PettyCashDebitCredit;
import com.pyramix.domain.user.User;
import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherStatus;
import com.pyramix.domain.voucher.VoucherType;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.gl.dao.BalanceDao;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.persistence.pettycash.dao.PettyCashDao;
import com.pyramix.persistence.user.dao.UserDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.serial.SerialNumberGenerator;

public class PettyCashController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6126380610562961547L;

	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	private PettyCashDao pettyCashDao;
	private BalanceDao balanceDao;
	private UserDao userDao;
	private SerialNumberGenerator serialNumberGenerator;
	private GeneralLedgerDao generalLedgerDao;
	
	private Combobox periodCombobox, pcAcctCombobox;
	private Listbox pettyCashListbox;
	private Label begBalanceLabel, endBalanceLabel;
	
	private Balance begBalance;
	private BigDecimal topUpAmount;
	private String begBalanceString, endBalanceString;
	private List<PettyCash> pettyCashList;
	private ListModelList<PettyCash> pettyCashListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private LocalDate periodStartDate, periodEndDate;
	private User userCreate;
	private Coa_05_Master masterCoaToCredit, masterCoaToDebit;
	
	private static final Logger log = Logger.getLogger(PettyCashController.class);
	
	/*
	 * ASSUMING User will be able to choose petty cash accounts eventually.
	 * But, to start with, we assume there're 2 accounts assigned as petty cash
	 * accounts: GoPay Account and Cadangan Tunai
	 * 
	 * CURRENTLY DEFAULTED TO GoPay Account (1.122.002) ONLY
	 * 
	 * ASSUMING User will be able to choose expenses accounts (Makan / JamuTamu)
	 * eventually.  But, to start with, we use only Konsumsi Operasional (5.111.006)
	 * 
	 * ASSUMING User will be able to select year and month eventually.
	 * But, to start with, we assume starting in January 2024, which requires
	 * balance / closing in December 2023.
	 */
	
	private static final Long PETTYCASH_01 = 35L; // GoPay Account
	private static final Long PETTYCASH_02 = 69L; // Cadangan Tunai
	private static final Long EXPENSE_01 = 75L;	// Konsumsi Operational
	
	/*
	 * ASSUMING User will be able to define starting year - in settings, perhaps
	 * But, to start with, we assume starting year is 2024
	 */
	private static final int START_YEAR = 2024;
	
	final private static String PROPERTIES_FILE_PATH="/pyramix/config.properties";	
	
	public void onCreate$pettyCashPanel(Event event) throws Exception {
		log.info("PettyCashPanel created");
		
		userCreate = getUserDao().findUserByUsername(getLoginUsername());
		
		// read config
		int selIndex = getConfigSelectedIndex();
		
		// list periods
		listYearMonthPeriods();
		periodCombobox.setSelectedIndex(selIndex);
		setPeriodStartAndEndDate();
		
		// list accounts -- to Credit (CR)
		listPettyCashAccounts();
		pcAcctCombobox.setSelectedIndex(0);
		masterCoaToCredit = pcAcctCombobox.getSelectedItem().getValue();
		
		// find expense account -- to Debit (DB)
		findExpenseAccount();
		
		// balance / closing
		findBeginingBalance();
		begBalanceLabel.setValue(begBalanceString);
		
		// list pettycash
		listPettyCashTransactions();
		
		// calc ending balance
		calculateEndingBalance();
		endBalanceLabel.setValue(endBalanceString);
	}

	private int getConfigSelectedIndex() {
		String idxStr = "0";
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {

            Properties prop = new Properties();

            // load the properties file
            prop.load(input);

            // get the property value and print it out
            idxStr = prop.getProperty("pettycash_period_index");
            log.info(idxStr);

		} catch (IOException ex) {
            ex.printStackTrace();
        }

		return Integer.valueOf(idxStr);
		
	}

	public void onCheck$defaultCheckbox(Event event) throws Exception {
		int selIndex =	periodCombobox.getSelectedIndex();
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {
            Properties prop = new Properties();

            // load the properties file
            prop.load(input);
			// set the properties value
			prop.setProperty("pettycash_period_index", String.valueOf(selIndex));
			// save properties to project root folder
			prop.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            
		} catch (IOException io) {
			io.printStackTrace();
		}		
		
//		try (OutputStream output = new FileOutputStream(PROPERTIES_FILE_PATH)) {
//
//			Properties prop = new Properties();
//
//			// set the properties value
//			prop.setProperty("pettycash_period_index", String.valueOf(selIndex));
//
//			// save properties to project root folder
//			prop.store(output, null);
//
//			log.info(prop);
//
//		} catch (IOException io) {
//			io.printStackTrace();
//		}			
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

	private void findBeginingBalance() throws Exception {
		// coa
		Coa_05_Master coaMaster = pcAcctCombobox.getSelectedItem().getValue();
		
		LocalDate closingLocalDate = findClosingDateForThisPeriod(); 
		Date closingDate = asDate(closingLocalDate, zoneId);
		try {
			// find the balance
			begBalance =
					getBalanceDao().findAccountBalanceByCoa_ClosingDate(coaMaster, closingDate);
			// find the topUpAmount for this period
			topUpAmount = 
					getDebitAmountForThisPeriod(coaMaster);
			begBalanceString = "Balance as of "
					+dateToStringDisplay(closingLocalDate, getEmphYearMonthShort(), getLocale())
					+" : "
					+toDecimalFormat(begBalance.getBalanceAmount(), getLocale(), getDecimalFormatLocal())
					+" Top Up: "
					+toDecimalFormat(topUpAmount, getLocale(), getDecimalFormatLocal());
		} catch (Exception e) {
			begBalanceString = "Balance as of "
					+dateToStringDisplay(closingLocalDate, getEmphYearMonthShort(), getLocale())
					+" : "
					+toDecimalFormat(BigDecimal.ZERO, getLocale(), getDecimalFormatLocal())
					+" Top Up: "
					+toDecimalFormat(BigDecimal.ZERO, getLocale(), getDecimalFormatLocal());			
		}
	}

	private BigDecimal getDebitAmountForThisPeriod(Coa_05_Master coaMaster) throws Exception {
		// MUST ALSO INCLUDE topup amount -- look for it in the GL using coa 1.122.002 (id=35) for the current period
		List<GeneralLedger> glList =
			getGeneralLedgerDao().findAllGeneralLedgerByCoaMaster(coaMaster, asDate(periodStartDate, zoneId), asDate(periodEndDate, zoneId));
		// get all the debitAmount
		BigDecimal topUpAmount = BigDecimal.ZERO;
		for(GeneralLedger gl : glList) {
			if (gl.getDebitAmount().compareTo(BigDecimal.ZERO)==0) {
				continue;
			}
			// accumulate all the debitAmount
			topUpAmount = topUpAmount.add(gl.getDebitAmount());
		}
		log.info("topUpAmount: "+topUpAmount);
		
		return topUpAmount;
	}

	private LocalDate findClosingDateForThisPeriod() {
		// closing date based on period
		int selPeriod = periodCombobox.getSelectedIndex();
		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		// month to localdate
		LocalDate periodDate = LocalDate.of(START_YEAR, month, 1);
		// periodDate minus 30 days
		periodDate = periodDate.minusDays(30);
		
		// return closingDate
		return periodDate.with(TemporalAdjusters.lastDayOfMonth());
	}

	private void listPettyCashAccounts() throws Exception {
		Coa_05_Master coaMaster;
		Comboitem comboitem;
		// GoPay Account
		coaMaster =
				getCoa_05_AccountMasterDao().findCoa_05_MasterById(PETTYCASH_01);
		comboitem = new Comboitem();
		comboitem.setLabel(coaMaster.getMasterCoaComp()
				+"-"+coaMaster.getMasterCoaName());
		comboitem.setValue(coaMaster);
		comboitem.setParent(pcAcctCombobox);
		// Cadangan Tunai
		coaMaster =
				getCoa_05_AccountMasterDao().findCoa_05_MasterById(PETTYCASH_02);
		comboitem = new Comboitem();
		comboitem.setLabel(coaMaster.getMasterCoaComp()
				+"-"+coaMaster.getMasterCoaName());
		comboitem.setValue(coaMaster);
		comboitem.setParent(pcAcctCombobox);
	}


	private void findExpenseAccount() throws Exception {
		masterCoaToDebit =
				getCoa_05_AccountMasterDao().findCoa_05_MasterById(EXPENSE_01);
	}
	
	private void listPettyCashTransactions() throws Exception {
		pettyCashList =
				getPettyCashDao().findAllPettyCashByDate(
						asDate(periodStartDate, zoneId), asDate(periodEndDate, zoneId));
		
		// NOTE: no need to do sort, pettyCashList already ordered by desc transactionDate
		// 
		// comparator
		// Comparator<PettyCash> compareAllColumns =
		//		Comparator.comparing(PettyCash::getTransactionDate);
		// sort - reversed the transactionDate (earliest first)
		// pettyCashList.sort(compareAllColumns.reversed());
		
		pettyCashListModelList =
				new ListModelList<PettyCash>(pettyCashList);
		
		pettyCashListbox.setModel(pettyCashListModelList);
		pettyCashListbox.setItemRenderer(getPettyCashListItemRenderer());
	}
	
	private ListitemRenderer<PettyCash> getPettyCashListItemRenderer() {

		return new ListitemRenderer<PettyCash>() {
			
			@Override
			public void render(Listitem item, PettyCash pettyCash, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Journal
				lc = initTransactionDate(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// No.Voucher
				lc = initVoucherNumber(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// Nominal(Rp.)
				lc = initTheSumOf(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// Transaksi Info
				lc = initTransactionDescription(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// Referensi
				lc = initDocumentRef(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// User
				lc = initUserCreate(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// edit
				lc = initSaveOrEdit(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// status
				lc = initStatus(new Listcell(), pettyCash);
				lc.setParent(item);
				
				// posting
				lc = initPosting(new Listcell(), pettyCash);
				lc.setParent(item);
			}

			private Listcell initTransactionDate(Listcell listcell, PettyCash pettyCash) {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// set todayDate according to the 'Period'
					int selPeriod = periodCombobox.getSelectedIndex();

					// selPeriod to month
					Month month = Month.of(selPeriod+1);
					
					todayDate = LocalDate.of(START_YEAR, month, 1);
					// new
					setupJournalDatebox(listcell, todayDate);
				} else {
					listcell.setLabel(dateToStringDisplay(asLocalDate(pettyCash.getTransactionDate()),
							getEmphYearMonthShort(), getLocale()));
				}
				return listcell;
			}

			private Listcell initVoucherNumber(Listcell listcell, PettyCash pettyCash) {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// created later just before saving
				} else {
					listcell.setLabel(pettyCash.getVoucherNumber().getSerialComp());
				}
				return listcell;
			}

			private Listcell initTheSumOf(Listcell listcell, PettyCash pettyCash) throws Exception {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupSumOfDecimalbox(listcell, BigDecimal.ZERO);					
				} else {
					listcell.setLabel(toDecimalFormat(pettyCash.getTheSumOf(), getLocale(), "###.###.###,-"));
				}

				return listcell;
			}

			private Listcell initTransactionDescription(Listcell listcell, PettyCash pettyCash) {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(pettyCash.getTransactionDescription());
				}
				
				return listcell;
			}

			private Listcell initDocumentRef(Listcell listcell, PettyCash pettyCash) {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(pettyCash.getDocumentRef());
				}
				
				return listcell;
			}

			private Listcell initUserCreate(Listcell listcell, PettyCash pettyCash) throws Exception {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// do nothing
				} else {
					PettyCash pettyCashUserCreateByProxy =
							getPettyCashDao().findPettyCashUserCreateByProxy(pettyCash.getId());
					listcell.setLabel(
							pettyCashUserCreateByProxy.getUserCreate().getReal_name());
					listcell.setValue(pettyCashUserCreateByProxy.getUserCreate());
				}
				
				return listcell;
			}

			private Listcell initSaveOrEdit(Listcell listcell, PettyCash pettyCash) {				
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClicked(listcell, pettyCash));
				} else {
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClicked(listcell, pettyCash));
				}

				return listcell;
			}

			// ************************** NEW ****************************

			private EventListener<Event> onSaveButtonClicked(Listcell listcell, PettyCash pettyCash) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						PettyCash pettyCashData =
								getPettyCashData(listcell, pettyCash);
						// save
						getPettyCashDao().save(pettyCashData);
						// re-list
						listPettyCashTransactions();
						// calc
						calculateEndingBalance();
						endBalanceLabel.setValue(endBalanceString);
					}
				};				
			}
			
			private PettyCash getPettyCashData(Listcell listcell, PettyCash pettyCash) throws Exception {
				Datebox datebox =
						(Datebox) listcell.getParent().getChildren().get(0).getFirstChild();
				Decimalbox decimalbox =
						(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
				Textbox textboxTransInfo =
						(Textbox) listcell.getParent().getChildren().get(3).getFirstChild();
				Textbox textboxRef =
						(Textbox) listcell.getParent().getChildren().get(4).getFirstChild();
				// voucher type is
				VoucherType voucherType = VoucherType.PETTYCASH;
				// set
				pettyCash.setTransactionDate(datebox.getValue());
				pettyCash.setTheSumOf(decimalbox.getValue());
				pettyCash.setTransactionDescription(textboxTransInfo.getValue());
				pettyCash.setDocumentRef(textboxRef.getValue());
				
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					pettyCash.setVoucherNumber(getVoucherSerialNumber(voucherType, datebox.getValue()));
							// asDate(todayDate, zoneId)));
					pettyCash.setCreateDate(asDate(todayDate, zoneId));

					// create pettycashDebitCredit list
					pettyCash.setPettyCashDebitCredits(getPettyCashDebitCredits(pettyCash));
				} else {
					// voucherNumber and createDate NOT changed
					
					// update pettycashDebitCredit list
					pettyCash.setPettyCashDebitCredits(updatePettyCashDebitCredits(pettyCash));
				}
				pettyCash.setVoucherStatus(VoucherStatus.Submitted);
				pettyCash.setUserCreate(userCreate);
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				pettyCash.setLastModified(asDateTime(currentDateTime, zoneId));
				
				
				return pettyCash;
			}

			private List<PettyCashDebitCredit> getPettyCashDebitCredits(PettyCash pettyCash) {
				List<PettyCashDebitCredit> pettyCashDebitCredits = new ArrayList<PettyCashDebitCredit>();
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				
				PettyCashDebitCredit dbcr;
				
				// create debitcredit (DB) based on pettyCash
				// DB account : Konsumsi Operasional
				dbcr = new PettyCashDebitCredit();
				dbcr.setCreateDate(asDate(todayDate, zoneId));
				dbcr.setCreditAmount(BigDecimal.ZERO);
				dbcr.setDebitAmount(pettyCash.getTheSumOf());
				dbcr.setDbcrDescription(pettyCash.getTransactionDescription());
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(masterCoaToDebit);
				
				// add to list
				pettyCashDebitCredits.add(dbcr);
				
				// create debitcredit (CR) based on pettyCash
				// CR account : GoPay
				dbcr = new PettyCashDebitCredit();
				dbcr.setCreateDate(asDate(todayDate, zoneId));
				dbcr.setCreditAmount(pettyCash.getTheSumOf());
				dbcr.setDebitAmount(BigDecimal.ZERO);
				dbcr.setDbcrDescription(pettyCash.getTransactionDescription());
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
				dbcr.setMasterCoa(masterCoaToCredit);
				
				// add to list
				pettyCashDebitCredits.add(dbcr);
				
				return pettyCashDebitCredits;
			}
			
			private List<PettyCashDebitCredit> updatePettyCashDebitCredits(PettyCash pettyCash) {
				List<PettyCashDebitCredit> pettyCashDebitCredits = pettyCash.getPettyCashDebitCredits();
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				PettyCashDebitCredit dbcr;
				// ASSUME always 2 accounts for DB and CR - with 2 Rows
				
				// 1st Row is DB account
				dbcr = pettyCashDebitCredits.get(0);
				dbcr.setDbcrDescription(pettyCash.getTransactionDescription());
				dbcr.setCreditAmount(BigDecimal.ZERO);
				dbcr.setDebitAmount(pettyCash.getTheSumOf()); // <-- update DB
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));				
				// TODO set dbAccount pettyCashDebitCredits
				
				// 2nd Row is CR account
				dbcr = pettyCashDebitCredits.get(1);
				dbcr.setDbcrDescription(pettyCash.getTransactionDescription());
				dbcr.setCreditAmount(pettyCash.getTheSumOf()); // <-- update CR
				dbcr.setDebitAmount(BigDecimal.ZERO);
				dbcr.setLastModified(asDateTime(currentDateTime, zoneId));								
				// TODO set crAccount pettyCashDebitCredits
				
				return pettyCashDebitCredits;
			}			

			// ************************** EXISTING ****************************
			
			private EventListener<Event> onModifyButtonClicked(Listcell listcell, PettyCash pettyCash) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Button button = (Button) event.getTarget();
						
						if (button.getLabel().compareTo("Modify")==0) {
							// re-create components for data entry - allow user to edit
							modifyPettyCashData(listcell, pettyCash);
							// change button to 'save'
							button.setLabel("Save");
						} else {
							PettyCash pettyCashModData = 
									getPettyCashData(listcell, pettyCash);
							// update
							getPettyCashDao().update(pettyCashModData);
							// re-list
							listPettyCashTransactions();
							// change button to 'modify'
							button.setLabel("Modify");
						}
					}
				};
			}			
			
			private void modifyPettyCashData(Listcell listcell, PettyCash pettyCash) {
				// re-create datebox for data entry
				Listcell lc0 = (Listcell) listcell.getParent().getChildren().get(0);
				lc0.setLabel(null);
				setupJournalDatebox(lc0, asLocalDate(pettyCash.getTransactionDate()));

				// re-create nominal decimalbox for data entry
				Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
				lc2.setLabel(null);
				setupSumOfDecimalbox(lc2, pettyCash.getTheSumOf());
				
				// re-create textbox transaksi info for data entry
				Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
				lc3.setLabel(null);
				setupTextbox(lc3, pettyCash.getTransactionDescription());
				
				// re-create textbox referensi for data entry
				Listcell lc4 = (Listcell) listcell.getParent().getChildren().get(4);
				lc4.setLabel(null);
				setupTextbox(lc4, pettyCash.getDocumentRef());				
			}
			
			//***************************** Posting ***********************************
			

			private Listcell initStatus(Listcell listcell, PettyCash pettyCash) {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					// default to 'submitted'
					listcell.setLabel(VoucherStatus.Submitted.toString());
				} else {
					// determine action taken by the user to change the status to 'POST'
					listcell.setLabel(pettyCash.getVoucherStatus().toString());
				}				
				return listcell;
			}

			private Listcell initPosting(Listcell listcell, PettyCash pettyCash) {
				if (pettyCash.getId().compareTo(Long.MIN_VALUE)==0) {
					
				} else if (pettyCash.getVoucherStatus().compareTo(VoucherStatus.Submitted)==0) {
					Button button = new Button();
					button.setLabel("Post");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onPostButtonClicked(listcell, pettyCash));
				}

				return listcell;
			}

			private EventListener<Event> onPostButtonClicked(Listcell listcell, PettyCash pettyCash) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						List<GeneralLedger> glList = new ArrayList<GeneralLedger>();
						
						// create posting voucher serialnumber
						VoucherSerialNumber postingVoucherNumber =
								getVoucherSerialNumber(VoucherType.POSTING_GENERAL, asDate(todayDate, zoneId));
						// create new GL objects
						for(PettyCashDebitCredit dbcr : pettyCash.getPettyCashDebitCredits()) {
							GeneralLedger gl = new GeneralLedger();
							
							gl.setMasterCoa(dbcr.getMasterCoa());
							gl.setPostingDate(asDate(todayDate, zoneId));
							gl.setPostingVoucherNumber(postingVoucherNumber);
							gl.setCreditAmount(dbcr.getCreditAmount());
							gl.setDebitAmount(dbcr.getDebitAmount());
							gl.setDbcrDescription(dbcr.getDbcrDescription());
							gl.setTransactionDescription(pettyCash.getTransactionDescription());
							gl.setDocumentRef(pettyCash.getDocumentRef());
							gl.setTransactionDate(pettyCash.getTransactionDate());
							gl.setVoucherType(VoucherType.PETTYCASH);
							gl.setVoucherNumber(pettyCash.getVoucherNumber());
							gl.setCreateDate(asDate(todayDate, zoneId));
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							gl.setLastModified(asDateTime(currentDateTime, zoneId));
							
							glList.add(gl);
						}
						// set
						pettyCash.setGeneralLedgers(glList);
						
						// update pettyCash status
						pettyCash.setVoucherStatus(VoucherStatus.Posted);
						pettyCash.setPostingVoucherNumber(postingVoucherNumber);
						pettyCash.setPostingDate(asDate(todayDate, zoneId));
						
						// update
						getPettyCashDao().update(pettyCash);
						
						// re-list
						listPettyCashTransactions();					
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
	
	protected void setupTextbox(Listcell listcell, String description) {
		Textbox textbox = new Textbox();
		textbox.setWidth("180px");
		textbox.setValue(description);
		textbox.setParent(listcell);
	}	
	
	private void calculateEndingBalance() throws Exception {
		BigDecimal nominal = BigDecimal.ZERO;
		for (PettyCash pettyCash : pettyCashList) {
			nominal = nominal.add(pettyCash.getTheSumOf());
		}
		// log.info(begBalance);
		if (begBalance != null) {
			BigDecimal endBalance = begBalance.getBalanceAmount().add(topUpAmount).subtract(nominal);
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
	
	public void onClick$newPettyCashButton(Event event) throws Exception {
		pettyCashListModelList.add(0, new PettyCash());
	}
	
	public void onClick$findButton(Event event) throws Exception {
		// init
		begBalance = null;
		topUpAmount = BigDecimal.ZERO;
		
		// set start and end date
		setPeriodStartAndEndDate();

		// coa to credit
		masterCoaToCredit = pcAcctCombobox.getSelectedItem().getValue();
		
		// begining balance
		findBeginingBalance();
		begBalanceLabel.setValue(begBalanceString);
		
		// re-list
		listPettyCashTransactions();
		
		// calc ending balance
		calculateEndingBalance();
		endBalanceLabel.setValue(endBalanceString);
	}
	

	private void setPeriodStartAndEndDate() {
		int selPeriod = periodCombobox.getSelectedIndex();

		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		
		periodStartDate = LocalDate.of(START_YEAR, month, 1);		
		periodEndDate = periodStartDate.with(TemporalAdjusters.lastDayOfMonth());
		
		// log.info("periodStartDate: "+periodStartDate);
		// log.info("periodEndDate : "+periodEndDate);
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
	
	public Coa_05_AccountMasterDao getCoa_05_AccountMasterDao() {
		return coa_05_AccountMasterDao;
	}

	public void setCoa_05_AccountMasterDao(Coa_05_AccountMasterDao coa_05_AccountMasterDao) {
		this.coa_05_AccountMasterDao = coa_05_AccountMasterDao;
	}

	public PettyCashDao getPettyCashDao() {
		return pettyCashDao;
	}

	public void setPettyCashDao(PettyCashDao pettyCashDao) {
		this.pettyCashDao = pettyCashDao;
	}

	public BalanceDao getBalanceDao() {
		return balanceDao;
	}

	public void setBalanceDao(BalanceDao balanceDao) {
		this.balanceDao = balanceDao;
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
}
