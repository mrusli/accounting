/**
 * 
 */
let arrow = document.querySelectorAll(".arrow");
for (var i = 0; i < arrow.length; i++) {
	arrow[i].addEventListener("click", (e)=>{
    	let arrowParent = e.target.parentElement.parentElement;
    	arrowParent.classList.toggle("showMenu");
    });       
}
const body = document.querySelector("body"),
	sidebar = body.querySelector(".sidebar"),
	toggle = body.querySelector(".toggle"),
	panel = body.querySelector(".panel-section");
toggle.addEventListener("click", () => {
	sidebar.classList.toggle("close");
});
function logoutClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickLogout', 'Logout...'));
}
function dashboardMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickDashboardMenu', 'Dashboard...'));
}
/************************COA************************/
function coaTypeMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickCoaTypeMenu', 'COA-Type...'));
}
function coaGroupMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickCoaGroupMenu', 'COA-Group...'));
}
function coaSubAccount01Click() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickCoaSubAccount01Menu', 'COA-Group...'));
}
function coaSubAccount02Click() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickCoaSubAccount02Menu', 'COA-Group...'));
}
function coaMasterMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickCoaMasterMenu', 'COA-Master...'));
}
/************************VOUCHER************************/
function voucherGeneralMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickVoucherGeneralMenu', 'VoucherGeneral...'));		
}
function pettyCashMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickPettyCashMenu', 'PettyCash...'));
}
function creditCardMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickCreditCardMenu', 'CreditCard...'));
}
function projectJournalMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickProjectJournalMenu', 'Project...'));
}
/************************GL************************/
function generalLedgerMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickGeneralLedgerMenu', 'General Ledger...'));		
}
function activityJournalMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickActivityJournalMenu', 'ActivityJournal...'));	
}
function accountBalanceMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickAccountBalanceMenu', 'AccountBalance...'));
}
/************************REPORT************************/
function trialBalanceMenuClick() {
	// alert("trialBalanceMenuClick...");	
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickReportTrialBalanceMenu', 'ReportTrialBalance...'));
}
function reportJournalMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickReportJournalMenu', 'ReportJournal...'));
}
/***********************ADMIN**************************/
function adminMenuClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickAdminMenu', 'Admin...'));
}
/***********************PROFILE************************/
function profileClick() {
	zAu.send(new zk.Event(zk.Widget.$(this), 'onClickProfileMenu', 'Profile...'));
}
