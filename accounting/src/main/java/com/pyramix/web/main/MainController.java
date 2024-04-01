package com.pyramix.web.main;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Include;

import com.pyramix.web.common.GFCBaseController;

public class MainController extends GFCBaseController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4499698721908927968L;
	
	private Include mainInclude;
//	private String[] dataStateDef = {"View", "Edit"};
	
	private static final Logger log = Logger.getLogger(MainController.class);
	
	public MainController() {
		super();
		
		log.info("MainController created...");
	}

	public void onCreate$mainWin(Event event) throws Exception {
		log.info("Main Windows created...");
		
		mainInclude.setSrc("~./secure/coa/Coa_04_SubAccount02.zul");

//		Map<String, VoucherJournalDialogData> arg = 
//				Collections.singletonMap("voucherJournalDialogData", getVoucherJournalDialogData());
//		
//		Window window = 
//				(Window) Executions.createComponents("~./secure/voucher/VoucherJournalDialog.zul", null, arg);
//		window.doModal();	
	}
	
//	private VoucherJournalDialogData getVoucherJournalDialogData() {
//		
//		VoucherJournalDialogData voucherJournalDialogData =
//		 		new VoucherJournalDialogData();
//		
//		voucherJournalDialogData.setAmount(new BigDecimal(150000));
//		voucherJournalDialogData.setCredit(true);
//		voucherJournalDialogData.setVoucherType(VoucherType.GENERAL);
//		voucherJournalDialogData.setTransactionDescription("Beli ATK");
//		voucherJournalDialogData.setDataState(dataStateDef[0]);
//		
//		return voucherJournalDialogData;
//	}
	
	public void onClickDashboardMenu(Event event) {
		log.info("onClickDashboardMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/dashboard/Dashboard.zul");		
	}
	
	public void onClickCoaTypeMenu(Event event) {
		log.info("onClickCoaTypeMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/coa/Coa_01_AccountType.zul");				
	}

	public void onClickCoaGroupMenu(Event event) {
		log.info("onClickCoaGroupMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/coa/Coa_02_AccountGroup.zul");		
	}
	
	public void onClickCoaSubAccount01Menu(Event event) {
		log.info("onClickCoaSubAccount01Menu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/coa/Coa_03_SubAccount01.zul");				
	}
	
	public void onClickCoaSubAccount02Menu(Event event) {
		log.info("onClickCoaSubAccount02Menu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/coa/Coa_04_SubAccount02.zul");						
	}
	
	public void onClickCoaMasterMenu(Event event) {
		log.info("onClickCoaMasterMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/coa/Coa_05_Master.zul");		
	}
	
	public void onClickGeneralLedgerMenu(Event event) {
		log.info("onClickGeneralLedgerMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/gl/GeneralLedger.zul");						
	}
	
	public void onClickActivityJournalMenu(Event event) {
		log.info("onClickActivityJournalMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/gl/JournalActivity.zul");				
	}
	
	public void onClickAccountBalanceMenu(Event event) {
		log.info("onClickAccountBalanceMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/gl/Balance.zul");				
	}
	
	public void onClickVoucherGeneralMenu(Event event) {
		log.info("onClickVoucherGeneralMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/voucher/VoucherJournal.zul");		
	}
	
	public void onClickReportTrialBalanceMenu(Event event) {
		log.info("onClickReportTrialBalanceMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/report/TrialBalanceReport.zul");		
	}
	
	public void onClickReportJournalMenu(Event event) {
		log.info("onClickReportJournalMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/report/JournalReport.zul");		
	}
	
	public void onClickProfileMenu(Event event) {
		log.info("onClickProfileMenu..."+event.getData().toString());
		
		mainInclude.setSrc("~./secure/profile/Profile.zul");		
	}
	
	public void onClickLogout(Event event) {
		log.info("onClickLogout..."+event.getData().toString());
		
		Executions.sendRedirect("/logout");
	}	
}
