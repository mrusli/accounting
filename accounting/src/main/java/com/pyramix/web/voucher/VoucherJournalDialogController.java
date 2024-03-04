package com.pyramix.web.voucher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.web.common.GFCBaseController;

public class VoucherJournalDialogController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3577476996364742245L;

	private Window voucherJournalDialogWin;
	private Listbox dbcrListbox;
	private Button checkAndSaveButton, addButton, cancelButton;
	private Label statusLabel;
	
	private VoucherJournalDialogData dialogData;
	private ListModelList<VoucherJournalDebitCredit> listModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private BigDecimal totalDebit = BigDecimal.ZERO;
	private BigDecimal totalCredit = BigDecimal.ZERO;
	
	private String[] coaSelectRef = {"enable", "disable"};
	private String coaSelect;
	
	private static final Logger log = Logger.getLogger(VoucherJournalDialogController.class);
	private static final int MAX_ROW = 4;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		// grab the data from the parent caller
		dialogData = (VoucherJournalDialogData) arg.get("voucherJournalDialogData");
	}

	public void onCreate$voucherJournalDialogWin(Event event) throws Exception {
		log.info("voucherJournalDialogWin created.");
		
		// enable coa select by default
		coaSelect = coaSelectRef[0];
		
		listVoucherJournalDebitCredits();
	}
	
	private void resetStatusLabel() {
		statusLabel.setValue("[Data-Entry] Enter the required data for this transaction. You can enter up to 4 rows of debit-credit.");		
		statusLabel.setStyle("color:var(--color-info-dark);");
	}
	
	private void listVoucherJournalDebitCredits() {
		listModelList = new ListModelList<VoucherJournalDebitCredit>();
		if (dialogData.getVoucherJournalDebitCredit()==null) {
			log.info("empty data...");
			listModelList.add(new VoucherJournalDebitCredit());
			listModelList.add(new VoucherJournalDebitCredit());			

		} else {
			log.info("populate the listbox...");
			List<VoucherJournalDebitCredit> voucherJournalDebitCreditList =
					dialogData.getVoucherJournalDebitCredit();
			if (dialogData.getDataState().equals(dialogData.getDataStateDef()[0])) {
				// view
				listModelList.addAll(voucherJournalDebitCreditList);
				// set to 'Close' ONLY - no checking
				checkAndSaveButton.setLabel("Close");
				// hide the cancel button
				cancelButton.setVisible(false);
				// cannot Add
				addButton.setVisible(false);
			} else {
				// others
				log.info("others...");

				listModelList.addAll(voucherJournalDebitCreditList);

				// statusLabel.setValue("[Data-Entry] Enter the required data for this transaction. You can enter up to 4 rows of debit-credit.");
				resetStatusLabel();
			}
		}
		
		dbcrListbox.setModel(listModelList);
		dbcrListbox.setItemRenderer(getDbCrListItemrenderer());
	}

	private ListitemRenderer<VoucherJournalDebitCredit> getDbCrListItemrenderer() {

		return new ListitemRenderer<VoucherJournalDebitCredit>() {

			@Override
			public void render(Listitem item, VoucherJournalDebitCredit dbcr, int index) throws Exception {
				Listcell lc;

				// COA
				lc = initCOA(new Listcell(), dbcr);
				lc.setParent(item);
				
				// Description
				lc = initDescription(new Listcell(), dbcr);
				lc.setParent(item);
				
				// Debit(Rp.)
				lc = initDebit(new Listcell(), dbcr, index);
				lc.setParent(item);
				
				// Credit(Rp.)
				lc = initCredit(new Listcell(), dbcr, index);
				lc.setParent(item);
				
				// mod
				lc = initModify(new Listcell(), dbcr);
				lc.setParent(item);
				
				item.setValue(dbcr);
			}

			private Listcell initCOA(Listcell listcell, VoucherJournalDebitCredit dbcr) {				
				if (dbcr.getId().compareTo(Long.MIN_VALUE)==0) {
					listcell.setLabel(dbcr.getMasterCoa()==null ? "Select..." : 
						dbcr.getMasterCoa().getMasterCoaComp()
						+"-"+dbcr.getMasterCoa().getMasterCoaName());
					listcell.setValue(dbcr.getMasterCoa());
					listcell.addEventListener(Events.ON_CLICK, onClickSelectCoa(listcell));					
				} else if (dialogData.getDataState().equals(dialogData.getDataStateDef()[1])) {
					listcell.setLabel(dbcr.getMasterCoa().getMasterCoaComp()
							+"-"+dbcr.getMasterCoa().getMasterCoaName());
					listcell.setValue(dbcr.getMasterCoa());
					listcell.addEventListener(Events.ON_CLICK, onClickSelectCoa(listcell));					
				} else {
					listcell.setLabel(dbcr.getMasterCoa().getMasterCoaComp()
							+"-"+dbcr.getMasterCoa().getMasterCoaName());										
				}
				
				return listcell;
			}

			private EventListener<Event> onClickSelectCoa(Listcell listcell) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						if (coaSelect.equals(coaSelectRef[0])) {
							log.info("allow to select coa...");
							// reset status
							resetStatusLabel();
							// get the credit(Rp.) amount
							Decimalbox creditDecimalbox = 
									(Decimalbox) listcell.getParent().getChildren().get(3).getFirstChild();
							// if it's zero amount, it means list only Debit Accounts
							boolean creditAccount = creditDecimalbox.getValue().compareTo(BigDecimal.ZERO)==0;
							Map<String, Boolean> arg = Collections.singletonMap("creditAccount", !creditAccount);
							Window window = 
									(Window) Executions.createComponents("~./secure/coa/Coa_05_MasterDialog.zul", 
											null, arg);
							window.doModal();
							window.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
								
								@Override
								public void onEvent(Event event) throws Exception {
									Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
									log.info("Received Master COA: "+masterCoa.toString());
									
									listcell.setStyle("white-space:nowrap;");
									listcell.setLabel(masterCoa.getMasterCoaComp()
											+"-"+masterCoa.getMasterCoaName());
									listcell.setValue(masterCoa);
									
								}
							});							
						} else {
							log.info("disable coa selection...");
						}
					}
				};
			}
			
			protected Listcell initDescription(Listcell listcell, VoucherJournalDebitCredit dbcr) {
				if ((dbcr.getId().compareTo(Long.MIN_VALUE)==0) || 
						(dialogData.getDataState().equals(dialogData.getDataStateDef()[1]))) {
					Textbox textbox = new Textbox();
					textbox.setValue(dialogData.getTransactionDescription());
					textbox.setWidth("280px");
					textbox.setConstraint("no empty: Please provide the description");
					textbox.setParent(listcell);
				// } else if (dialogData.getDataState().equals(dialogData.getDataStateDef()[1])) {
				//	Textbox textbox = new Textbox();
				//	textbox.setValue(dbcr.getDbcrDescription());
				//	textbox.setWidth("280px");
				//	textbox.setParent(listcell);				
				} else {
					listcell.setLabel(dbcr.getDbcrDescription());
				}
				
				return listcell;
			}
			
			private Listcell initDebit(Listcell listcell, VoucherJournalDebitCredit dbcr, int index) throws Exception {
				if ((dbcr.getId().compareTo(Long.MIN_VALUE)==0) ||
						(dialogData.getDataState().equals(dialogData.getDataStateDef()[1]))) {
					Decimalbox decimalbox = new Decimalbox();
					decimalbox.setWidth("110px");
					decimalbox.setStyle("text-align: right;");
					decimalbox.setConstraint("no empty: Please provide debit amount. Minimal 0 (zero) amount.");
					decimalbox.setLocale(getLocale());
					// Debit or Credit amount? if checked then it's to put the nominal into Credit column
					//   else into debit column
					if (index==0) {
						decimalbox.setValue(dialogData.isCredit() ? dialogData.getAmount() : BigDecimal.ZERO);						
					} else {
						decimalbox.setValue(dialogData.isCredit() ? BigDecimal.ZERO : dialogData.getAmount()); 
					}
					decimalbox.setParent(listcell);	
			
				} else {
					listcell.setLabel(toDecimalFormat(dbcr.getDebitAmount(), getLocale(), "###.###.###,-"));					
				}				
				
				return listcell;
			}
			

			private Listcell initCredit(Listcell listcell, VoucherJournalDebitCredit dbcr, int index) throws Exception {
				if ((dbcr.getId().compareTo(Long.MIN_VALUE)==0) ||
						(dialogData.getDataState().equals(dialogData.getDataStateDef()[1]))) {
					Decimalbox decimalbox = new Decimalbox();
					decimalbox.setWidth("110px");
					decimalbox.setStyle("text-align: right;");
					decimalbox.setConstraint("no empty: Please provide credit amount. Minimal 0 (zero) amount.");
					decimalbox.setLocale(getLocale());
					// Debit or Credit amount? if checked then it's to put the nominal into Credit column
					//   else into debit column
					if (index==0) {
						decimalbox.setValue(dialogData.isCredit() ? BigDecimal.ZERO : dialogData.getAmount());						
					} else {
						decimalbox.setValue(dialogData.isCredit() ? dialogData.getAmount() : BigDecimal.ZERO);
					}
					decimalbox.setParent(listcell);						
				
				} else {
					listcell.setLabel(toDecimalFormat(dbcr.getCreditAmount(), getLocale(), "###.###.###,-"));					
				}

				return listcell;
			}
			
			private Listcell initModify(Listcell listcell, VoucherJournalDebitCredit dbcr) {
				Button button = new Button();
				button.setLabel("Modify");
				button.setVisible(false);
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// make this row data editable
						
						// enable coa selection
						coaSelect = coaSelectRef[0];
						
						Textbox descriptionTextbox = (Textbox) listcell.getParent().getChildren().get(1).getFirstChild();
						descriptionTextbox.setDisabled(false);
						
						Decimalbox debitDecimalbox = (Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
						debitDecimalbox.setDisabled(false);
						
						Decimalbox creditDecimalbox = (Decimalbox) listcell.getParent().getChildren().get(3).getFirstChild();
						creditDecimalbox.setDisabled(false);
						
						// change the status label
						// statusLabel.setValue("[Data-Entry] Enter the required data for this transaction. You can enter up to 4 rows of debit-credit.");
						resetStatusLabel();
						
						// change the button label
						checkAndSaveButton.setLabel("Check"); 
					}
				});
				
				return listcell;
			}
		};
	}

	public void onAfterRender$dbcrListbox(Event event) throws Exception {
		if (dialogData.getVoucherJournalDebitCredit()==null) {
			displayTotalDbCr(BigDecimal.ZERO, BigDecimal.ZERO);
		} else {
			dialogData.getVoucherJournalDebitCredit().forEach(
					dbcr -> log.info(dbcr.toString()));
			calcTotalDbCr(
					dialogData.getVoucherJournalDebitCredit());
			displayTotalDbCr(totalDebit, totalCredit);
		}
	}
	
	public void calcTotalDbCr(List<VoucherJournalDebitCredit> dbcrList) {
		totalDebit = BigDecimal.ZERO;
		totalCredit = BigDecimal.ZERO;
		
		for (VoucherJournalDebitCredit dbcr : dbcrList) {
			totalDebit = totalDebit.add(dbcr.getDebitAmount());
			totalCredit = totalCredit.add(dbcr.getCreditAmount());
		}
		log.info("Debit Amount: "+totalDebit);
		log.info("Credit Amount: "+totalCredit);
	}
	
	public void displayTotalDbCr(BigDecimal totalDebit, BigDecimal totalCredit) throws Exception {
		Listfooter listfooterDb, listfooterCr;
		listfooterDb = (Listfooter) dbcrListbox.getListfoot().getChildren().get(2);
		listfooterCr = (Listfooter) dbcrListbox.getListfoot().getChildren().get(3);

		listfooterDb.setLabel(toDecimalFormat(totalDebit, getLocale(), "###.###.###,-"));
		listfooterCr.setLabel(toDecimalFormat(totalCredit, getLocale(), "###.###.###,-"));
	}
	
	public void onClick$addButton(Event event) throws Exception {
		if (listModelList.size()<MAX_ROW) {
			log.info("add new debit-credit");
			listModelList.add(new VoucherJournalDebitCredit());			
		}
	}
	
	public void onClick$checkAndSaveButton(Event event) throws Exception {
		if (checkAndSaveButton.getLabel().compareTo("Close")==0) {
			voucherJournalDialogWin.detach();
		} else if (checkAndSaveButton.getLabel().compareTo("Check")==0) {
			// reset status
			resetStatusLabel();
			// check all data have been filled-up
			checkNewVoucherJournalDebitCredits();				
			// total up the debit and credit columns
			calcTotalDbCr(getNewVoucherJournalDebitCredits());
			// display total dbcr
			displayTotalDbCr(totalDebit, totalCredit);	
			// check whether debit == credit?
			checkDebitCreditEqualAmount();
			// check whether debit or credit amount equals to transaction amount
			checkDebitCreditEqualTransactionAmount();
			
			// make all data READ-ONLY and enable the Modify button
			switchToReadOnly();
			
			// change the status to 'Checked'
			statusLabel.setValue("[Checked] Checked completed.  You can modify if not correctly entered.");
			
			// change the button label to 'Save'
			checkAndSaveButton.setLabel("Save");
			
			// disable coa selection
			coaSelect = coaSelectRef[1];
		} else {
			List<VoucherJournalDebitCredit> dbcrList;
			
			if (dialogData.getDataState().equals(dialogData.getDataStateDef()[1])) {
				// edit 
				dbcrList = getEditedVoucherJournalDebitCredits();
			} else {
				// new
				dbcrList =	getNewVoucherJournalDebitCredits();
			}
			
			// send the list to the sender
			Events.sendEvent(new Event("onDbCrCompleted", voucherJournalDialogWin, dbcrList));
			
			voucherJournalDialogWin.detach();
		}
	}

	private void checkNewVoucherJournalDebitCredits() throws Exception {
		for (Listitem item : dbcrListbox.getItems()) {
			// COA
			Listcell listcellCoa = (Listcell) item.getChildren().get(0);
			if (listcellCoa.getValue()==null) {
				statusLabel.setValue("[ERROR] COA not completed. Select COA and check again.");
				statusLabel.setStyle("color: red;");
				throw new Exception("COA Not Completed");
			}			
		}
	}

	private void checkDebitCreditEqualAmount() throws Exception {
		if (totalDebit.compareTo(totalCredit)!=0) {
			statusLabel.setValue("[ERROR] Debit-Credit not balanced. Re-enter the debit/credit amount and check again.");
			statusLabel.setStyle("color: red;");			
			throw new Exception("Debit-Credit not balanced");
		}
	}	
	
	private void checkDebitCreditEqualTransactionAmount() throws Exception {
		if ((totalDebit.compareTo(dialogData.getAmount())!=0) || 
				(totalCredit.compareTo(dialogData.getAmount())!=0)) {
			statusLabel.setValue("[ERROR] Debit or Credit Amount not the same as the Transaction amount.");
			statusLabel.setStyle("color: red;");						
			throw new Exception("Debit or Credit Amount not the same as the Transaction amount");
		}
	}

	private void switchToReadOnly() {
		for (Listitem item : dbcrListbox.getItems()) {
			// Listcell listcellCoa = (Listcell) item.getChildren().get(0);
			// listcellCoa.removeEventListener(Events.ON_CLICK, coaClickListnener);
			
			Textbox textboxDescription = (Textbox) item.getChildren().get(1).getFirstChild();
			// log.info(textboxDescription);
			textboxDescription.setDisabled(true);
			
			Decimalbox decimalboxDebit = (Decimalbox) item.getChildren().get(2).getFirstChild();
			// log.info(decimalboxDebit);
			decimalboxDebit.setDisabled(true);
			
			Decimalbox decimalboxCredit = (Decimalbox) item.getChildren().get(3).getFirstChild();
			// log.info(decimalboxCredit);
			decimalboxCredit.setDisabled(true);
			
			Button buttonModify = (Button) item.getChildren().get(4).getFirstChild();
			buttonModify.setVisible(true);
			
			log.info(buttonModify);
		}
		
	}
	
	private List<VoucherJournalDebitCredit> getNewVoucherJournalDebitCredits() {
		List<VoucherJournalDebitCredit> dbcrList = new ArrayList<VoucherJournalDebitCredit>();
		
		for (Listitem item : dbcrListbox.getItems()) {
			VoucherJournalDebitCredit dbcr = new VoucherJournalDebitCredit();
			
			Listcell listcellCoa = (Listcell) item.getChildren().get(0);
			dbcr.setMasterCoa(listcellCoa.getValue());
			
			Textbox textboxDescription = (Textbox) item.getChildren().get(1).getFirstChild();
			dbcr.setDbcrDescription(textboxDescription.getValue());
			
			Decimalbox decimalboxDebit = (Decimalbox) item.getChildren().get(2).getFirstChild();
			dbcr.setDebitAmount(decimalboxDebit.getValue());
			
			Decimalbox decimalboxCredit = (Decimalbox) item.getChildren().get(3).getFirstChild();
			dbcr.setCreditAmount(decimalboxCredit.getValue());
			
			dbcr.setCreateDate(asDate(todayDate, zoneId));
			LocalDateTime currentDateTime = getLocalDateTime(zoneId);
			dbcr.setLastModified(asDateTime(currentDateTime, zoneId));
			
			dbcrList.add(dbcr);
		}
		
		return dbcrList;
	}
	
	private List<VoucherJournalDebitCredit> getEditedVoucherJournalDebitCredits() {
		List<VoucherJournalDebitCredit> dbcrList = dialogData.getVoucherJournalDebitCredit();

		// listbox index
		int index = 0;
		
		for (VoucherJournalDebitCredit dbcr : dbcrList) {
			Listitem item = dbcrListbox.getItemAtIndex(index);
			
			// COA
			Listcell coaListcell = (Listcell) item.getChildren().get(0);
			dbcr.setMasterCoa(coaListcell.getValue());
			
			// Description
			Textbox descriptionTextbox = (Textbox) item.getChildren().get(1).getFirstChild();
			dbcr.setDbcrDescription(descriptionTextbox.getValue());
			
			// Debit(Rp.)
			Decimalbox debitDecimalbox = (Decimalbox) item.getChildren().get(2).getFirstChild();
			dbcr.setDebitAmount(debitDecimalbox.getValue());
			
			// Credit(Rp.)
			Decimalbox creditDecimalbox = (Decimalbox) item.getChildren().get(3).getFirstChild();
			dbcr.setCreditAmount(creditDecimalbox.getValue());
			
			LocalDateTime currentDateTime = getLocalDateTime(zoneId);
			dbcr.setLastModified(asDateTime(currentDateTime, zoneId));			
			
			index++;
		}
		
		return dbcrList;
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		voucherJournalDialogWin.detach();
	}
}
