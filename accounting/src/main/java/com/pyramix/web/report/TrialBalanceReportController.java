package com.pyramix.web.report;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;
import com.pyramix.domain.report.TrialBalance;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.gl.dao.BalanceDao;
import com.pyramix.web.common.GFCBaseController;

public class TrialBalanceReportController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6797038172292689250L;

	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	private BalanceDao balanceDao;
	
	private Datebox startDatebox, endDatebox;
	private Listbox trialBalanceListbox;
	
	private List<TrialBalance> trialBalanceList = new ArrayList<TrialBalance>();
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private static final int COA_01_ASSETS = 1;
	private static final int COA_02_LIABILITIES = 2;
	private static final int COA_03_EQUITY = 3;
	
	private static final Logger log = Logger.getLogger(TrialBalanceReportController.class);
	
	public void onCreate$trialBalanceReportPanel(Event event) throws Throwable {
		log.info("trialBalanceReportPanel created.");
		
		// set start and end date -- default to previous month start and end date 
		setClosingStartAndEndDate();		
		// create trialBalance list from accountBalance
		createTrialBalanceList();
		
		listTrialBalance();
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

	private void createTrialBalanceList() throws Throwable {
		List<Coa_05_Master> coaAssetsList = getCoa_05_AccountMasterDao()
				.find_ActiveOnly_Coa_05_Master_by_AccountType(COA_01_ASSETS);
		coaAssetsList.forEach((Coa_05_Master c) -> log.info(c.toString()));
		List<TrialBalance> trialBalanceAssetList = createTrialBalanceAssetList(coaAssetsList);
		trialBalanceList.addAll(trialBalanceAssetList);
		
		List<Coa_05_Master> coaLiabilitiesList = getCoa_05_AccountMasterDao()
				.find_ActiveOnly_Coa_05_Master_by_AccountType(COA_02_LIABILITIES);
		coaLiabilitiesList.forEach((Coa_05_Master c) -> log.info(c.toString()));
		
		List<Coa_05_Master> coaEquityList = getCoa_05_AccountMasterDao()
				.find_ActiveOnly_Coa_05_Master_by_AccountType(COA_03_EQUITY);
		coaEquityList.forEach((Coa_05_Master c) -> log.info(c.toString()));
		
	}

	private List<TrialBalance> createTrialBalanceAssetList(List<Coa_05_Master> coaAssetsList) throws Exception {
		List<TrialBalance> trialBalanceList = new ArrayList<TrialBalance>();
		
		// title row
		TrialBalance trialBalanceTitle = new TrialBalance();
		trialBalanceTitle.setCoaTypeName("ASSETS");
		trialBalanceTitle.setCoa_05_Master(null);
		trialBalanceList.add(trialBalanceTitle);
		
		for (Coa_05_Master coa : coaAssetsList) {
			TrialBalance trialBalance = new TrialBalance();

			Balance accountBalance = getBalanceDao().findAccountBalanceByCoa(coa);
			
			trialBalance.setCoa_05_Master(coa);
			trialBalance.setTotalDebit(accountBalance.getDebitTotal());
			trialBalance.setTotalCredit(accountBalance.getCreditTotal());
			
			trialBalanceList.add(trialBalance);
		}
		
		return trialBalanceList;
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
					// Chart of Account title
					lc = new Listcell(trialBalance.getCoaTypeName());
					lc.setParent(item);
				} else {
					// Chart of Account
					lc = new Listcell(trialBalance.getCoa_05_Master().getMasterCoaName());
					lc.setParent(item);
					
					// Account No
					lc = new Listcell(trialBalance.getCoa_05_Master().getMasterCoaComp());
					lc.setParent(item);
					
					// Total Debit
					lc = new Listcell(toDecimalFormat(trialBalance.getTotalDebit(), getLocale(), "###.###.###,-"));
					lc.setParent(item);
					
					// Total Credit
					lc = new Listcell(toDecimalFormat(trialBalance.getTotalCredit(), getLocale(), "###.###.###,-"));
					lc.setParent(item);
				}
 			}
		};
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
