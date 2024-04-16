package com.pyramix.web.gl;

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
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.gl.dao.BalanceDao;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.web.common.GFCBaseController;

import jakarta.persistence.NoResultException;

public class BalanceController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6201847421068284577L;

	private GeneralLedgerDao generalLedgerDao;
	private BalanceDao balanceDao;
	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	
	private Combobox periodCombobox;
	private Datebox startDatebox, endDatebox;
	private Listbox accountBalanceListbox;
	
	private List<Coa_05_Master> coaMasterList;
	private List<Balance> balanceList;
	private BigDecimal ledgerCreditTotal = BigDecimal.ZERO;
	private BigDecimal ledgerDebitTotal = BigDecimal.ZERO;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private static final int START_YEAR = 2024;
	// temporary ONLY -- MUST default to 30 days ago
	// private static final int START_DAY = 90;
	
	final private static String PROPERTIES_FILE_PATH="/pyramix/config.properties";
	
	private static final Logger log = Logger.getLogger(BalanceController.class);
	
	public void onCreate$balancePanel(Event event) throws Exception {
		log.info("accountBalancePanel created.");
		
		// read config
		int selIndex = getConfigSelectedIndex();
		log.info("selectedIndex : "+selIndex);
		// periods
		listYearMonthPeriods();
		periodCombobox.setSelectedIndex(selIndex);
		
		// set start and end date -- default to previous month start and end date 
		setClosingStartAndEndDate();
		
		// get all the assets, liabilities, and capital accounts from CoaMaster
		coaMasterList = getCoa_05_AccountMasterDao().findAllCoa_05_Master();
		
		// get all account balances and create new ones if not existed
		processAccountBalance();
				
		// list Account Balance
		listAccountBalance();
	}
	
	private int getConfigSelectedIndex() {
		String idxStr = "0";
		
		try (InputStream input = new FileInputStream(PROPERTIES_FILE_PATH)) {

            Properties prop = new Properties();

            // load the properties file
            prop.load(input);

            // get the property value and print it out
            idxStr = prop.getProperty("balance_period_index");
            log.info("balance_period_index = "+idxStr);

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
			prop.setProperty("balance_period_index", String.valueOf(selIndex));
			// save properties to project root folder
			prop.store(new FileOutputStream(PROPERTIES_FILE_PATH), null);
            
		} catch (IOException io) {
			io.printStackTrace();
		}		
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
	
	private void setClosingStartAndEndDate() {
		// MUST default to 30 days ago -- since we're starting, we default to Jan 2024,
		// which is 90 days
		// LocalDate lastMonthDate = todayDate.minusDays(START_DAY); 

		int selPeriod = periodCombobox.getSelectedIndex();
		
		// selPeriod to month
		Month month = Month.of(selPeriod+1);		
		
		LocalDate startDate = LocalDate.of(START_YEAR, month, 1);
		// log.info("Start Date: "+startDate.toString());
		startDatebox.setLocale(getLocale());
		startDatebox.setValue(asDate(startDate, zoneId));
		
		LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
		// log.info("End Date: "+endDate.toString());
		endDatebox.setLocale(getLocale());
		endDatebox.setValue(asDate(endDate, zoneId));
	}	
	
	private void processAccountBalance() throws Exception {
		log.info("processAccountBalance to produce balanceList");
		
		// get existing account balances
		balanceList = getBalanceDao().findAccountBalanceBy_ClosingDate(endDatebox.getValue());
		// check whether all of the accounts already in the balance table
		// --for each of the accounts not in the balance table, create a balance row
		Balance balance=null;
		for (Coa_05_Master coaMaster : coaMasterList) {
			try {
				balance = getBalanceDao()
						.findAccountBalanceByCoa_ClosingDate(coaMaster, endDatebox.getValue());					
			} catch (NoResultException e) {
				// create a new accountbalance and add to list of account balance
				balance = createAccountBalance(coaMaster);
				balanceList.add(balance);
			}
		}
		
		// balanceList.forEach(c -> log.info("balance: "+c.toString()));
	}


	private Balance createAccountBalance(Coa_05_Master coaMaster) throws Exception {
		Balance balance = new Balance();
		balance.setBalanceAmount(getBalanceAmountFromLedger(coaMaster));
		balance.setClosingDate(asDate(todayDate, zoneId));
		balance.setCoa_05_Master(coaMaster);
		balance.setCreditTotal(ledgerCreditTotal);
		balance.setDebitTotal(ledgerDebitTotal);
		balance.setResetBalance(false);
		balance.setCreateDate(asDate(todayDate, zoneId));
		LocalDateTime currentDateTime = getLocalDateTime(zoneId);
		balance.setLastModified(asDateTime(currentDateTime, zoneId));
		
		return balance;
	}
	
	private BigDecimal getBalanceAmountFromLedger(Coa_05_Master coaMaster) throws Exception {
		BigDecimal balanceAmount = BigDecimal.ZERO;
		BigDecimal creditTotal = BigDecimal.ZERO;
		BigDecimal debitTotal = BigDecimal.ZERO;
		
		List<GeneralLedger> glList = 
				getGeneralLedgerDao().findAllGeneralLedgerByCoaMaster(
						coaMaster, startDatebox.getValue(), endDatebox.getValue());
		
		// find balance amount from previous month
		LocalDate prevEndDate = endDatebox.getValueInLocalDate().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth());
		// log.info("End Date: "+endDatebox.getValue());
		// log.info("PreviousMonth: "+prevEndDate);
		// getting the previous month balance amount
		BigDecimal prevBalanceAmount = BigDecimal.ZERO; 
		try {
			Balance balanceAccount = getBalanceDao()
					.findAccountBalanceByCoa_ClosingDate(coaMaster, asDate(prevEndDate, zoneId));
			// log.info("balanceAcct: "+balanceAccount.toString());
			prevBalanceAmount = balanceAccount.getBalanceAmount();
			// log.info("prevBalance: "+prevBalanceAmount);
		} catch (NoResultException e) {
			log.info("No previous month closing balance.  Previous month closing balance set 0 (zero).");
		}
		
		for (GeneralLedger gl : glList) {
			creditTotal = creditTotal.add(gl.getCreditAmount());
			debitTotal = debitTotal.add(gl.getDebitAmount());
		}

		if (coaMaster.isCreditAccount()) {
			// all credit minus debit
			balanceAmount = creditTotal.add(prevBalanceAmount).subtract(debitTotal);
		} else {
			// all debit minus all credit
			balanceAmount = debitTotal.add(prevBalanceAmount).subtract(creditTotal);
		}			
		
		// set ledger db/cr total
		ledgerDebitTotal = debitTotal;
		ledgerCreditTotal = creditTotal;
		
		return balanceAmount;
	}

	private void listAccountBalance() {
		balanceList.sort((c1,c2) -> {
			return c1.getCoa_05_Master().getMasterCoaComp().compareTo(
					c2.getCoa_05_Master().getMasterCoaComp());
		});
		
		ListModelList<Balance> accountBalanceListModelList =
				new ListModelList<Balance>(balanceList);
		
		accountBalanceListbox.setModel(accountBalanceListModelList);
		accountBalanceListbox.setItemRenderer(getAccountBalanceListitemRenderer());		
	}	
	

	
	private ListitemRenderer<Balance> getAccountBalanceListitemRenderer() {
		
		return new ListitemRenderer<Balance>() {
			
			@Override
			public void render(Listitem item, Balance accountBalance, int index) throws Exception {
				Listcell lc;
				
				// Date
				lc = initClosingDate(new Listcell(), accountBalance);
				lc.setParent(item);
				
				// COA
				lc = new Listcell(accountBalance.getCoa_05_Master().getMasterCoaComp()
						+ "-" + accountBalance.getCoa_05_Master().getMasterCoaName());
				lc.setParent(item);
				
				// Total Debit(Rp.)
				lc = new Listcell(toDecimalFormat(accountBalance.getDebitTotal(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
				
				// Total Credit(Rp.)
				lc = new Listcell(toDecimalFormat(accountBalance.getCreditTotal(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
				
				// Close Amount(Rp.)
				lc = new Listcell(toDecimalFormat(accountBalance.getBalanceAmount(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
				
				// Reset Balance
				lc = initResetBalance(new Listcell(), accountBalance);
				lc.setParent(item);
				
				lc = initSave(new Listcell(), accountBalance);
				lc.setParent(item);
			}

			private Listcell initClosingDate(Listcell listcell, Balance acctBalance) {
				if (acctBalance.getId().compareTo(Long.MIN_VALUE)==0) {
					Datebox datebox = new Datebox();
					datebox.setWidth("130px");
					datebox.setLocale(getLocale());
					datebox.setValue(endDatebox.getValue());
					datebox.setParent(listcell);
				} else {
					listcell.setLabel(dateToStringDisplay(asLocalDate(acctBalance.getClosingDate()),
							"yyyy-MM-dd", getLocale()));
				}
				
				return listcell;
			}

			private Listcell initResetBalance(Listcell listcell, Balance acctBalance) {
				Checkbox checkbox = new Checkbox();
				checkbox.setChecked(acctBalance.isResetBalance());
				checkbox.setDisabled(true);
				checkbox.setParent(listcell);
				
				return listcell;
			}

			private Listcell initSave(Listcell listcell, Balance acctBalance) {
				Button button = new Button();
				button.setParent(listcell);
				if (acctBalance.getId().compareTo(Long.MIN_VALUE)==0) {
					button.setLabel("Save");
					button.addEventListener(Events.ON_CLICK, onSaveButtonClick(listcell, acctBalance));					
				} else {
					// no need to save anymore, user just need to refresh
					button.setLabel("Refresh");
					button.addEventListener(Events.ON_CLICK, onRefreshButtonClick(listcell, acctBalance));
				}
				
				return listcell;
			}

			private EventListener<Event> onSaveButtonClick(Listcell listcell, Balance acctBalance) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Save button click...");
						// get the closing date from user's date selection
						Datebox datebox =
								(Datebox) listcell.getParent().getChildren().get(0).getFirstChild();
						acctBalance.setClosingDate(datebox.getValue());
						log.info(acctBalance.toString());
						// save
						getBalanceDao().save(acctBalance);
						// re-list
						listAccountBalance();
					}
				};
			}
			
			private EventListener<Event> onRefreshButtonClick(Listcell listcell, Balance acctBalance) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Refresh button click...");
						// find the balance account by id
						Balance acctBalanceToUpdate =
								getBalanceDao().findAccountBalanceById(acctBalance.getId());
						// re-calculate db/cr and balance amount
						acctBalanceToUpdate.setBalanceAmount(
								getBalanceAmountFromLedger(acctBalance.getCoa_05_Master()));
						acctBalanceToUpdate.setDebitTotal(ledgerDebitTotal);
						acctBalanceToUpdate.setCreditTotal(ledgerCreditTotal);
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						acctBalanceToUpdate.setLastModified(asDateTime(currentDateTime, zoneId));
						// update balance account
						getBalanceDao().update(acctBalanceToUpdate);
						// get all account balances and create new ones if not existed
						processAccountBalance();
						// re-list
						listAccountBalance();
					}
				};
			}			
		};
	}
	
	public void onSelect$periodCombobox(Event event) throws Exception {
		int selPeriod = periodCombobox.getSelectedIndex();

		// selPeriod to month
		Month month = Month.of(selPeriod+1);
		
		LocalDate startDate = LocalDate.of(START_YEAR, month, 1);		
		LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
		
		startDatebox.setValue(asDate(startDate, zoneId));
		endDatebox.setValue(asDate(endDate, zoneId));
	}
	
	public void onClick$findAccountBalanceButton(Event event) throws Exception {
		// get all account balances and create new ones if not existed
		processAccountBalance();
				
		// list Account Balance
		listAccountBalance();		
	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}

	public BalanceDao getBalanceDao() {
		return balanceDao;
	}

	public void setBalanceDao(BalanceDao balanceDao) {
		this.balanceDao = balanceDao;
	}

	public Coa_05_AccountMasterDao getCoa_05_AccountMasterDao() {
		return coa_05_AccountMasterDao;
	}

	public void setCoa_05_AccountMasterDao(Coa_05_AccountMasterDao coa_05_AccountMasterDao) {
		this.coa_05_AccountMasterDao = coa_05_AccountMasterDao;
	}	
}
