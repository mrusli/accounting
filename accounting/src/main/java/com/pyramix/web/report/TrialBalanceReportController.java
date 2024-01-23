package com.pyramix.web.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabs;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;
import com.pyramix.domain.report.TrialBalance;
import com.pyramix.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.gl.dao.BalanceDao;
import com.pyramix.persistence.report.TrialBalanceDao;
import com.pyramix.web.common.GFCBaseController;

import jakarta.persistence.NoResultException;

public class TrialBalanceReportController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6797038172292689250L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	private BalanceDao balanceDao;
	private TrialBalanceDao trialBalanceDao;
	
	private Datebox startDatebox, endDatebox;
	private Listbox trialBalanceListbox;
	private Tabbox coaTabbox;
	
	private List<TrialBalance> trialBalanceList = new ArrayList<TrialBalance>();
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private BigDecimal totalDebit = BigDecimal.ZERO;
	private BigDecimal totalCredit = BigDecimal.ZERO;
		
	private static final Logger log = Logger.getLogger(TrialBalanceReportController.class);
	
	public void onCreate$trialBalanceReportPanel(Event event) throws Throwable {
		log.info("trialBalanceReportPanel created.");
		
		// set coa type for the tabbox
		setupCoaTypeTabbox();

		// set start and end date -- default to previous month start and end date 
		setClosingStartAndEndDate();		
		// find all trialBalance list from accountBalance, create new ones if not in the db yet
		findTrialBalanceList(coaTabbox.getSelectedTab().getValue());
		
		listTrialBalance();
	}

	private void setupCoaTypeTabbox() throws Exception {
		Tabs tabs = new Tabs();
		tabs.setParent(coaTabbox);
		
		List<Coa_01_AccountType> accountTypeList =
				getCoa_01_AccountTypeDao().findAllCoa_01_AccountType();
		Tab tab;
		for (Coa_01_AccountType accountType : accountTypeList) {
			tab = new Tab();		
			tab.setLabel(accountType.getAccountTypeName());
			tab.setValue(accountType);
			tab.setParent(tabs);			
		}
		// add 'Summary' tab
		tab = new Tab();
		tab.setLabel("Summary");
		tab.setValue(null);
		tab.setParent(tabs);
	}

	private void setClosingStartAndEndDate() {
		LocalDate lastMonthDate = todayDate.minusDays(30); 
		log.info("30 days ago: "+lastMonthDate.toString());
		
		LocalDate startDate = lastMonthDate.with(TemporalAdjusters.firstDayOfMonth());
		log.info("Start Date: "+startDate.toString());
		startDatebox.setLocale(getLocale());
		startDatebox.setValue(asDate(startDate, zoneId));
		
		LocalDate endDate = lastMonthDate.with(TemporalAdjusters.lastDayOfMonth());
		log.info("End Date: "+endDate.toString());
		endDatebox.setLocale(getLocale());
		endDatebox.setValue(asDate(endDate, zoneId));
	}

	private void findTrialBalanceList(Coa_01_AccountType accountType) throws Exception {
		// clear the list
		trialBalanceList.clear();

		// title row
		TrialBalance trialBalanceTitle = createTitleRow(accountType);
		trialBalanceList.add(trialBalanceTitle);
		
		List<Coa_05_Master> coaList = getCoa_05_AccountMasterDao()
				.find_ActiveOnly_Coa_05_Master_by_AccountType(accountType.getAccountTypeNumber());
		coaList.forEach((Coa_05_Master c) -> log.info(c.toString()));
		coaList.sort((c1,c2) -> {
			return c1.getMasterCoaComp().compareTo(
					c2.getMasterCoaComp());
		});
		
		// reset total
		totalDebit = BigDecimal.ZERO;
		totalCredit = BigDecimal.ZERO;
		
		for (Coa_05_Master coaMaster : coaList) {
			try {
				TrialBalance existingTrialBalance = 
						getTrialBalanceDao().findTrialBalanceByCoa_ReportEndDate(coaMaster, endDatebox.getValue());
				trialBalanceList.add(existingTrialBalance);
				
				totalDebit = totalDebit.add(existingTrialBalance.getDebit());
				totalCredit = totalCredit.add(existingTrialBalance.getCredit());				
				
			} catch (NoResultException e) {
				TrialBalance newTrialBalance= createTrialBalance(coaMaster);
				if (newTrialBalance!=null) {
					trialBalanceList.add(newTrialBalance);
					
					totalDebit = totalDebit.add(newTrialBalance.getDebit());
					totalCredit = totalCredit.add(newTrialBalance.getCredit());	
				}	
			}
		}
	}

	private TrialBalance createTrialBalance(Coa_05_Master coaMaster) throws Exception {		
		
		TrialBalance trialBalance = new TrialBalance();
		// set report end date
		trialBalance.setReportEndDate(endDatebox.getValue());
		// set coa accountType title to null - indicator to save to db
		trialBalance.setCoaTypeName(null);
		// create date
		trialBalance.setCreateDate(asDate(todayDate, zoneId));
		LocalDateTime currentDateTime = getLocalDateTime(zoneId);
		trialBalance.setLastModified(asDateTime(currentDateTime, zoneId));
			
		try {				
			Balance accountBalance = 
					getBalanceDao().findAccountBalanceByCoa_ClosingDate(coaMaster, endDatebox.getValue());

			trialBalance.setCoa_05_Master(coaMaster);
			// trialBalance.setTotalDebit(accountBalance.getDebitTotal());
			// trialBalance.setTotalCredit(accountBalance.getCreditTotal());
			if (coaMaster.isCreditAccount()) {
				trialBalance.setDebit(BigDecimal.ZERO);
				trialBalance.setCredit(accountBalance.getBalanceAmount());
			} else {
				trialBalance.setDebit(accountBalance.getBalanceAmount());
				trialBalance.setCredit(BigDecimal.ZERO);
			}
			// trialBalance.setClosingAmount(accountBalance.getBalanceAmount());
			

		} catch (NoResultException e) {
			log.info("No account balance for this coa: "
				+coaMaster.getMasterCoaComp()+"-"+coaMaster.getMasterCoaName());
			// no trialBalance - signal to ignore this trialBalance
			trialBalance = null;
		}
		
		return trialBalance;
	}

	private TrialBalance createTitleRow(Coa_01_AccountType accountType) {
		TrialBalance trialBalanceTitle = new TrialBalance();
		trialBalanceTitle.setCoaTypeName(accountType.getAccountTypeName());
		trialBalanceTitle.setCoa_05_Master(null);
		
		return trialBalanceTitle;
	}

	private void listTrialBalance() {
		ListModelList<TrialBalance> trialBalanceListModelList = 
				new ListModelList<TrialBalance>(trialBalanceList);
		
		trialBalanceListbox.setModel(trialBalanceListModelList);
		trialBalanceListbox.setItemRenderer(getTrialBalanceListitemRenderer());
	}	
	
	private ListitemRenderer<TrialBalance> getTrialBalanceListitemRenderer() {
		
		return new ListitemRenderer<TrialBalance>() {
			
			@Override
			public void render(Listitem item, TrialBalance trialBalance, int index) throws Exception {
				Listcell lc;
				
				
				if (trialBalance.getCoa_05_Master()==null) {
					// Chart of Account - AccountType title
					lc = new Listcell(trialBalance.getCoaTypeName());
					lc.setStyle("font-weight: 700; text-decoration: underline;");
					lc.setParent(item);
				} else {
					// Chart of Account
					lc = new Listcell(trialBalance.getCoa_05_Master().getMasterCoaName());
					lc.setParent(item);
					
					// Account No
					lc = new Listcell(trialBalance.getCoa_05_Master().getMasterCoaComp());
					lc.setParent(item);
					
					// Total Debit
					lc = new Listcell(toDecimalFormat(trialBalance.getDebit(), getLocale(), "###.###.###,-"));
					lc.setParent(item);
					
					// Total Credit
					lc = new Listcell(toDecimalFormat(trialBalance.getCredit(), getLocale(), "###.###.###,-"));
					lc.setParent(item);
					
					// save or refresh
					lc = initSaveOrRefresh(new Listcell(), trialBalance);
					lc.setParent(item);
				}
 			}

			private Listcell initSaveOrRefresh(Listcell listcell, TrialBalance trialBalance) {
				if (coaTabbox.getSelectedIndex()==5) {
					// user select 'Summary' - no need to provide refresh
					return listcell;
				}
				Button button = new Button();
				button.setParent(listcell);				
				if (trialBalance.getId().compareTo(Long.MIN_VALUE)==0) {
					// not saved yet
					button.setLabel("Save");
					button.addEventListener(Events.ON_CLICK, onSaveButtonClick(listcell, trialBalance));										
				} else {
					// already in database table
					button.setLabel("Refresh");
					button.addEventListener(Events.ON_CLICK, onRefreshButtonClick(listcell, trialBalance));					
				}
				
				return listcell;
			}

			private EventListener<Event> onSaveButtonClick(Listcell listcell, TrialBalance trialBalance) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Save button click...");
						log.info("Saving : "+trialBalance.toString());
						// save
						getTrialBalanceDao().save(trialBalance);
						// find all trialBalance list from accountBalance, create new ones if not in the db yet
						findTrialBalanceList(coaTabbox.getSelectedTab().getValue());
						// re-list
						listTrialBalance();						
					}
				};
			}

			private EventListener<Event> onRefreshButtonClick(Listcell listcell, TrialBalance trialBalance) {
				
				return new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Refresh button click...");
					}
				};
			}
		};
	}
	
	public void onAfterRender$trialBalanceListbox(Event event) throws Exception {
		Listfooter lf2 =
				(Listfooter) trialBalanceListbox.getListfoot().getChildren().get(2);
		lf2.setLabel(toDecimalFormat(totalDebit, getLocale(), "###.###.###,-"));
		Listfooter lf3 =
				(Listfooter) trialBalanceListbox.getListfoot().getChildren().get(3);
		lf3.setLabel(toDecimalFormat(totalCredit, getLocale(), "###.###.###,-"));		
	}

	public void onSelect$coaTabbox(Event event) throws Exception {
		int tabIndex = coaTabbox.getSelectedIndex();
		
		if (tabIndex==5) {
			log.info("Summary Tab selected...");
			listTrialBalanceSummary();
		} else {
			Tab selTab = coaTabbox.getSelectedTab();
		
			// value of the selected tab
			Coa_01_AccountType accountType = selTab.getValue();
			log.info(accountType.toString());
			
			// find all trialBalance list from accountBalance, create new ones if not in the db yet
			findTrialBalanceList(accountType);
			
			listTrialBalance();
		}
	}

	public void onClick$findTrialBalanceButton(Event event) throws Exception {
		// reset tab to index 0
		coaTabbox.setSelectedIndex(0);
		
		// value of the selected tab
		Coa_01_AccountType accountType = coaTabbox.getSelectedTab().getValue();
		log.info(accountType.toString());

		// create trialBalance list from accountBalance
		findTrialBalanceList(accountType);
		
		listTrialBalance();		
	}
	
	private void listTrialBalanceSummary() throws Exception {
		totalDebit = BigDecimal.ZERO;
		totalCredit = BigDecimal.ZERO;

		List<TrialBalance> trialBalanceList =
				getTrialBalanceDao().findTrialBalanceBy_ReportEndDate(endDatebox.getValue());
		trialBalanceList.sort((t1, t2) -> t1.getCoa_05_Master().getMasterCoaComp().compareTo(
				t2.getCoa_05_Master().getMasterCoaComp()));
		for (TrialBalance trialBalance : trialBalanceList) {
			totalDebit = totalDebit.add(trialBalance.getDebit());
			totalCredit = totalCredit.add(trialBalance.getCredit());
		}
		
		trialBalanceListbox.setModel(new ListModelList<TrialBalance>(trialBalanceList));
		trialBalanceListbox.setItemRenderer(getTrialBalanceListitemRenderer());		
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

	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}

	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}

	public TrialBalanceDao getTrialBalanceDao() {
		return trialBalanceDao;
	}

	public void setTrialBalanceDao(TrialBalanceDao trialBalanceDao) {
		this.trialBalanceDao = trialBalanceDao;
	}
	
}
