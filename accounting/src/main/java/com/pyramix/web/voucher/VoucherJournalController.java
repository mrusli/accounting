package com.pyramix.web.voucher;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.domain.user.User;
import com.pyramix.domain.voucher.VoucherJournal;
import com.pyramix.domain.voucher.VoucherJournalDebitCredit;
import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherStatus;
import com.pyramix.domain.voucher.VoucherType;
import com.pyramix.persistence.user.dao.UserDao;
import com.pyramix.persistence.voucher.dao.VoucherJournalDao;
import com.pyramix.web.common.GFCBaseController;
import com.pyramix.web.serial.SerialNumberGenerator;

public class VoucherJournalController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2137211043971278337L;

	private SerialNumberGenerator serialNumberGenerator;
	private VoucherJournalDao voucherJournalDao;
	private UserDao userDao;
	
	private Listbox voucherJournalListbox;
	
	private List<VoucherJournal> voucherJournalList;
	private ListModelList<VoucherJournal> voucherJournalListModelList;
	private List<VoucherJournalDebitCredit> voucherJournalDebitCreditList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	private User userCreate;
	
	private String[] dataStateDef = {"View", "Edit"};
	private String dataState;
	
	private static final Logger log = Logger.getLogger(VoucherJournalController.class);
	
	public void onCreate$voucherJournalPanel(Event event) throws Exception {
		log.info("voucherJournalPanel created.");
		
		userCreate = getUserDao().findUserByUsername(getLoginUsername());
		dataState = dataStateDef[0]; // set to "View"
		
		listVoucherJournal();
	}
	
	private void listVoucherJournal() throws Exception {
		voucherJournalList = 
				getVoucherJournalDao().findAllVoucherJournal();
		// comparator
		Comparator<VoucherJournal> compareAllColumns =
				Comparator.comparing(VoucherJournal::getTransactionDate);
		// sort - reversed the transactionDate (earliest first)
		voucherJournalList.sort(compareAllColumns.reversed());
		
		voucherJournalListModelList =
				new ListModelList<VoucherJournal>(voucherJournalList);
		
		voucherJournalListbox.setModel(voucherJournalListModelList);
		voucherJournalListbox.setItemRenderer(getVoucherJournalListItemRenderer());
	}

	private ListitemRenderer<VoucherJournal> getVoucherJournalListItemRenderer() {
		
		return new ListitemRenderer<VoucherJournal>() {
			
			@Override
			public void render(Listitem item, VoucherJournal voucherJournal, int index) throws Exception {
				Listcell lc;
				
				// Tgl.Journal
				lc = initTransactionDate(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// No.Voucher
				lc = initVoucherNumber(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Nominal(Rp.)
				lc = initTheSumbOf(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Debit
				lc = initDebit(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Tipe Journal
				lc = initVoucherType(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Status
				lc = initVoucherStatus(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// posting
				lc = initPosting(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Transaksi Info
				lc = initTransactionDescription(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// Referensi
				lc = initDocumentRef(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// User
				lc = initUserCreate(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// DbCr
				lc = initDbCr(new Listcell(), voucherJournal);
				lc.setParent(item);
				
				// edit
				lc = initSaveOrEdit(new Listcell(), voucherJournal);
				lc.setParent(item);
			}

			private Listcell initTransactionDate(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupJournalDatebox(listcell, todayDate);					
				} else {
					listcell.setLabel(dateToStringDisplay(asLocalDate(voucherJournal.getTransactionDate()),
							"dd-MM-yyyy", getLocale()));
				}
				return listcell;
			}

			private Listcell initVoucherNumber(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// created later just before saving
				} else {
					listcell.setLabel(voucherJournal.getVoucherNumber().getSerialComp());
				}
				
				return listcell;
			}

			private Listcell initTheSumbOf(Listcell listcell, VoucherJournal voucherJournal) throws Exception {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupSumOfDecimalbox(listcell, BigDecimal.ZERO);					
				} else {
					listcell.setLabel(toDecimalFormat(voucherJournal.getTheSumOf(), getLocale(), "###.###.###,-"));
				}
				
				return listcell;
			}

			private Listcell initDebit(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// new
					setupDebitOrCreditCheckbox(listcell, true);
				} else {
					// do nothing
				}
				
				return listcell;
			}
			
			private Listcell initVoucherType(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					setupVoucherTypeCombobox(listcell, VoucherType.GENERAL);
				} else {
					listcell.setLabel(voucherJournal.getVoucherType().toString());
				}
				
				return listcell;
			}

			private Listcell initVoucherStatus(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// default to 'submitted'
					listcell.setLabel(VoucherStatus.Submitted.toString());
				} else {
					// determine action taken by the user to change the status to 'POST'
					listcell.setLabel(voucherJournal.getVoucherStatus().toString());
				}
				
				return listcell;
			}

			private Listcell initPosting(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// do nothing
				} else {
					
					if (voucherJournal.getVoucherStatus().equals(VoucherStatus.Submitted)) {
						// requires user to post
						Button button = new Button();
						button.setWidth("60px");
						button.setLabel("Post");
						button.setParent(listcell);
						button.addEventListener(Events.ON_CLICK, onPostButtonClick(listcell, voucherJournal));
					} else {
						// already posted or cancel
						listcell.setLabel(null);
					}
					
				}
				
				return listcell;
			}

			private EventListener<Event> onPostButtonClick(Listcell listcell, VoucherJournal voucherJournal) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						List<GeneralLedger> glList = new ArrayList<GeneralLedger>();
						
						// create posting voucher serialnumber
						VoucherSerialNumber postingVoucherNumber =
								getVoucherSerialNumber(VoucherType.POSTING_GENERAL, asDate(todayDate, zoneId));
						
						// create a new GL object and populate the object using data from voucherJournal and dbcr
						for (VoucherJournalDebitCredit dbcr : voucherJournal.getVoucherJournalDebitCredits()) {
							GeneralLedger gl = new GeneralLedger();
							
							gl.setMasterCoa(dbcr.getMasterCoa());
							gl.setPostingDate(asDate(todayDate, zoneId));
							gl.setPostingVoucherNumber(postingVoucherNumber);
							gl.setCreditAmount(dbcr.getCreditAmount());
							gl.setDebitAmount(dbcr.getDebitAmount());
							gl.setDbcrDescription(dbcr.getDbcrDescription());
							gl.setTransactionDescription(voucherJournal.getTransactionDescription());
							gl.setDocumentRef(voucherJournal.getDocumentRef());
							gl.setTransactionDate(voucherJournal.getTransactionDate());
							gl.setVoucherType(voucherJournal.getVoucherType());
							gl.setVoucherNumber(voucherJournal.getVoucherNumber());
							gl.setCreateDate(asDate(todayDate, zoneId));
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							gl.setLastModified(asDateTime(currentDateTime, zoneId));
							
							glList.add(gl);
						}
						
						// update voucherJournal voucherStatus
						voucherJournal.setVoucherStatus(VoucherStatus.Posted);
						voucherJournal.setCheckDate(asDate(todayDate, zoneId));
						voucherJournal.setPostingVoucherSerialNumber(postingVoucherNumber);
						voucherJournal.setPostingDate(asDate(todayDate, zoneId));
						// set
						voucherJournal.setGeneralLedgers(glList);
						
						// update
						getVoucherJournalDao().update(voucherJournal);
						
						// re-list to hide the modify button
						listVoucherJournal();
					}
				};
			}

			private Listcell initTransactionDescription(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(voucherJournal.getTransactionDescription());
				}
				
				return listcell;
			}

			private Listcell initDocumentRef(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					setupTextbox(listcell, "");
				} else {
					listcell.setLabel(voucherJournal.getDocumentRef());
				}
				
				return listcell;
			}

			private Listcell initUserCreate(Listcell listcell, VoucherJournal voucherJournal) throws Exception {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					// do nothing
				} else {
					VoucherJournal voucherJournalUserCreateByProxy = 
							getVoucherJournalDao().findVoucherJournalUserCreateByProxy(voucherJournal.getId());
					listcell.setLabel(
							voucherJournalUserCreateByProxy.getUserCreate().getReal_name());
					listcell.setValue(voucherJournalUserCreateByProxy.getUserCreate());
				}
				
				return listcell;
			}

			private Listcell initDbCr(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					log.info("new object...");
					listcell.setLabel("Db/Cr");
					listcell.setStyle("text-decoration: underline;");
					listcell.addEventListener(Events.ON_CLICK, onDbCrCreateButtonClicked(listcell, voucherJournal)); 
				} else {
					log.info("existing object...");
					listcell.setLabel("Db/Cr");
					listcell.setStyle("text-decoration: underline;");
					listcell.addEventListener(Events.ON_CLICK, onDbCrButtonClicked(listcell, voucherJournal));
				}				
				
				return listcell;
			}

			private EventListener<Event> onDbCrCreateButtonClicked(Listcell listcell,
					VoucherJournal voucherJournal) {
				
				return new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Create DbCr");
						
						Decimalbox decimalbox = (Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
						if (decimalbox.getValue().compareTo(BigDecimal.ZERO)==0) {
							throw new Exception("Invalid Amount.");
						}
						Textbox textbox = (Textbox) listcell.getParent().getChildren().get(7).getFirstChild();
						if (textbox.getValue().isBlank()) {
							throw new Exception("Blank Transaction Info.");
						}
						textbox = (Textbox) listcell.getParent().getChildren().get(8).getFirstChild();
						if (textbox.getValue().isBlank()) {
							throw new Exception("Blank Journal Reference.");
						}				
						
						setupVoucherJournalDialog(listcell, voucherJournal);
					}
				};
			}

			private EventListener<Event> onDbCrButtonClicked(Listcell listcell,
					VoucherJournal voucherJournal) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {							
						if (dataState.equals(dataStateDef[0])) {
							log.info("View dbcr");
							
							setupViewVoucherJournalDialog(listcell, voucherJournal);
						} else {
							log.info("Edit dbcr");

							setupVoucherJournalDialog(listcell, voucherJournal);
						}						
					}
				};
			}			
			
			private Listcell initSaveOrEdit(Listcell listcell, VoucherJournal voucherJournal) {
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClicked(listcell, voucherJournal));
				} else if (voucherJournal.getVoucherStatus().equals(VoucherStatus.Submitted)) {
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClicked(listcell, voucherJournal));
				}
				
				return listcell;
			}

			private EventListener<Event> onSaveButtonClicked(Listcell listcell,
					VoucherJournal voucherJournal) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						VoucherJournal voucherJournalData =
								getVoucherJournalData(listcell, voucherJournal);
						// 
						
						// check dbcr
						if (voucherJournalData.getVoucherJournalDebitCredits()==null) {
							throw new Exception("Debit Credit Not Completed.");
						}
						log.info(voucherJournalData.toString());
						// save
						getVoucherJournalDao().save(voucherJournalData);
						// re-list
						listVoucherJournal();
						// reset to view
						dataState = dataStateDef[0];
					}
				};
			}
			
			private VoucherJournal getVoucherJournalData(Listcell listcell, VoucherJournal voucherJournal) throws Exception {
				Datebox datebox =
						(Datebox) listcell.getParent().getChildren().get(0).getFirstChild();
				Decimalbox decimalbox =
						(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
				Combobox combobox =
						(Combobox) listcell.getParent().getChildren().get(4).getFirstChild();
				Textbox textboxTransInfo =
						(Textbox) listcell.getParent().getChildren().get(7).getFirstChild();
				Textbox textboxRef =
						(Textbox) listcell.getParent().getChildren().get(8).getFirstChild();
				
				VoucherType voucherType = combobox.getSelectedItem().getValue();
				
				// set
				voucherJournal.setTransactionDate(datebox.getValue());
				voucherJournal.setTheSumOf(decimalbox.getValue());
				voucherJournal.setVoucherType(voucherType);
				voucherJournal.setVoucherStatus(VoucherStatus.Submitted);
				voucherJournal.setTransactionDescription(textboxTransInfo.getValue());
				voucherJournal.setDocumentRef(textboxRef.getValue());
				if (voucherJournal.getId().compareTo(Long.MIN_VALUE)==0) {
					voucherJournal.setVoucherNumber(getVoucherSerialNumber(voucherType, asDate(todayDate, zoneId)));
					voucherJournal.setCreateDate(asDate(todayDate, zoneId));					
				} else {
					// voucherNumber and createDate NOT changed
				}
				voucherJournal.setUserCreate(userCreate);
				LocalDateTime currentDateTime = getLocalDateTime(zoneId);
				voucherJournal.setLastModified(asDateTime(currentDateTime, zoneId));
				// voucherJournal.setVoucherJournalDebitCredits(voucherJournal.getVoucherJournalDebitCredits());
				
				return voucherJournal;
			}			
			
			private EventListener<Event> onModifyButtonClicked(Listcell listcell,
					VoucherJournal voucherJournal) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Button button = (Button) event.getTarget();
												
						if (button.getLabel().compareTo("Modify")==0) {
							log.info("Modify button clicked...");
							dataState = dataStateDef[1]; // set to "Edit"
							// make the row data editable
							log.info("allow user to edit");
							modifyVoucherJournalData(voucherJournal);
							// change button to Save
							button.setLabel("Save");
						} else {
							log.info("Save button clicked...");
							// grab all the modified data
							VoucherJournal voucherJournalData =
									getVoucherJournalData(listcell, voucherJournal);
							log.info("Update : "+voucherJournalData.toString());
							// update
							getVoucherJournalDao().update(voucherJournalData);
							// re-list
							listVoucherJournal();
							// change to "View"
							dataState = dataStateDef[0];
							// change button back to modify
							button.setLabel("Modify");
						}
					}

					private void modifyVoucherJournalData(VoucherJournal voucherJournal) {
						// datebox
						Listcell lc0 = (Listcell) listcell.getParent().getChildren().get(0);
						lc0.setLabel(null);
						setupJournalDatebox(lc0, asLocalDate(voucherJournal.getTransactionDate()));
						
						// nominal
						Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
						lc2.setLabel(null);
						setupSumOfDecimalbox(lc2, voucherJournal.getTheSumOf());
						
						// Db/Cr
						Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
						lc3.setLabel(null);
						setupDebitOrCreditCheckbox(lc3, true);
						
						// Tipe Journal
						Listcell lc4 = (Listcell) listcell.getParent().getChildren().get(4);
						lc4.setLabel(null);
						setupVoucherTypeCombobox(lc4, voucherJournal.getVoucherType());
						
						// Transaksi Info
						Listcell lc7 = (Listcell) listcell.getParent().getChildren().get(7);
						lc7.setLabel(null);
						setupTextbox(lc7, voucherJournal.getTransactionDescription());
						
						// Referensi
						Listcell lc8 = (Listcell) listcell.getParent().getChildren().get(8);
						lc8.setLabel(null);
						setupTextbox(lc8, voucherJournal.getDocumentRef());					
					}
				};
			}

			private void setupVoucherJournalDialog(Listcell listcell, VoucherJournal voucherJournal) {
				Map<String, VoucherJournalDialogData> arg = 
						Collections.singletonMap("voucherJournalDialogData", getVoucherJournalDialogData(listcell, voucherJournal));
				
				Window window = 
						(Window) Executions.createComponents("~./secure/voucher/VoucherJournalDialog.zul", null, arg);
				window.doModal();
				window.addEventListener("onDbCrCompleted", new EventListener<Event>() {

					@SuppressWarnings("unchecked")
					@Override
					public void onEvent(Event event) throws Exception {
						log.info("onDbCrCompleted....");
						voucherJournalDebitCreditList =
								(List<VoucherJournalDebitCredit>) event.getData();
						// check voucher amount and dbcr total
						Decimalbox decimalbox =
								(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
						BigDecimal totalbigDecimal = BigDecimal.ZERO;
						for (VoucherJournalDebitCredit dbcr : voucherJournalDebitCreditList) {
							log.info(dbcr.toString());
							totalbigDecimal = totalbigDecimal.add(dbcr.getCreditAmount());
						}
						if (decimalbox.getValue().compareTo(totalbigDecimal)!=0) {
							Messagebox.show("Voucher amount is NOT the same as the total Debit/Credit.  "
									+ "Do you want to update voucher Amount?",
								    "Confirmation", 
								    Messagebox.YES | Messagebox.NO,  
								    Messagebox.QUESTION, onConfirmationClick(totalbigDecimal));							
						}
						
						// must switch from 'View' to 'Edit'
						// log.info(dataState);
						dataState = dataStateDef[1];
						// assign dbcr list objects from the dbcr dialog to voucherJournal
						voucherJournal.setVoucherJournalDebitCredits(voucherJournalDebitCreditList);
					}

					private EventListener<Event> onConfirmationClick(BigDecimal totalbigDecimal) {
						
						return new EventListener<Event>() {
							
							@Override
							public void onEvent(Event event) throws Exception {
								Decimalbox decimalbox =
										(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
								decimalbox.setValue(totalbigDecimal);
							}
						};
					}
				});
				
			}
			
			private VoucherJournalDialogData getVoucherJournalDialogData(Listcell listcell, VoucherJournal voucherJournal) {
				// Nominal
				Decimalbox nominalDecimalbox =
						(Decimalbox) listcell.getParent().getChildren().get(2).getFirstChild();
				// Debit or Credit amount? if checked then it's to put the nominal into Credit column
				//   else into debit column
				Checkbox dbcrCheckbox =
						(Checkbox) listcell.getParent().getChildren().get(3).getFirstChild();
				// Tipe Journal
				Combobox voucherTypeCombobox =
						(Combobox) listcell.getParent().getChildren().get(4).getFirstChild();
				// Transaction Info
				Textbox transactionInfoTextbox =
				 		(Textbox) listcell.getParent().getChildren().get(7).getFirstChild();

				VoucherJournalDialogData voucherJournalDialogData =
				 		new VoucherJournalDialogData();
				
				voucherJournalDialogData.setAmount(nominalDecimalbox.getValue());
				voucherJournalDialogData.setCredit(dbcrCheckbox.isChecked());
				voucherJournalDialogData.setVoucherType(
				 		voucherTypeCombobox.getSelectedItem().getValue());
				voucherJournalDialogData.setTransactionDescription(
						transactionInfoTextbox.getValue());
				voucherJournalDialogData.setDataState(dataState);
				
				log.info("State: "+dataState);
				
				if (dataState.equals(dataStateDef[1])) {
					// edit
					voucherJournalDialogData.setVoucherJournalDebitCredit(
							voucherJournal.getVoucherJournalDebitCredits());
				} else {
					// new
					if (voucherJournalDebitCreditList!=null) {
						voucherJournalDebitCreditList.forEach((VoucherJournalDebitCredit dbcr)->log.info(dbcr.toString()));
					} else {
						log.info("voucherJournalDebitCreditList is null");
					}
					voucherJournalDialogData.setVoucherJournalDebitCredit(
							voucherJournalDebitCreditList);
				}
				
				return voucherJournalDialogData;
			}
			
			private void setupViewVoucherJournalDialog(Listcell listcell, VoucherJournal voucherJournal) {
				Map<String, VoucherJournalDialogData> arg = 
						Collections.singletonMap("voucherJournalDialogData", getViewVoucherJournalDialogData(listcell, voucherJournal));
				Window window = 
						(Window) Executions.createComponents("~./secure/voucher/VoucherJournalDialog.zul", null, arg);
				window.doModal();				
			}
			
			private VoucherJournalDialogData getViewVoucherJournalDialogData(Listcell listcell, VoucherJournal voucherJournal) {
				VoucherJournalDialogData voucherJournalDialogData =
				 		new VoucherJournalDialogData();
				
				voucherJournalDialogData.setAmount(BigDecimal.ZERO);
				voucherJournalDialogData.setCredit(false);
				voucherJournalDialogData.setVoucherType(null);
				voucherJournalDialogData.setTransactionDescription(null);
				voucherJournalDialogData.setDataState(dataState);
				voucherJournalDialogData.setVoucherJournalDebitCredit(
							voucherJournal.getVoucherJournalDebitCredits());
				
				return voucherJournalDialogData;
			}			
		};
	}	

	protected void setupJournalDatebox(Listcell listcell, LocalDate localDate) {
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
	
	protected void setupDebitOrCreditCheckbox(Listcell listcell, boolean check) {
		Checkbox checkbox = new Checkbox();
		checkbox.setChecked(check);
		checkbox.setLabel(check ? "Db" : "Cr");
		checkbox.setParent(listcell);
		checkbox.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				checkbox.setLabel(checkbox.isChecked() ? "Db" : "Cr");
			}
		});
	}
	
	protected void setupVoucherTypeCombobox(Listcell listcell, VoucherType voucherType) {
		Combobox combobox = new Combobox();
		combobox.setWidth("100px");
		combobox.setParent(listcell);
		// set voucherType comboitems
		setVoucherTypeComboitems(combobox);
		// select
		selectVoucherTypeCombobox(combobox, voucherType);
	}

	private void setVoucherTypeComboitems(Combobox combobox) {
		Comboitem comboitem;
		for (VoucherType voucherType : VoucherType.values()) {
			if (voucherType.getValue()<=6) {
				comboitem = new Comboitem();
				comboitem.setLabel(voucherType.toString());
				comboitem.setValue(voucherType);
				comboitem.setParent(combobox);
			}
		}
	}	

	private void selectVoucherTypeCombobox(Combobox combobox, VoucherType voucherType) {
		for (Comboitem comboitem : combobox.getItems()) {
			if (comboitem.getValue().equals(voucherType)) {
				combobox.setSelectedItem(comboitem);
			}
		}
	}	
	
	protected void setupTextbox(Listcell listcell, String description) {
		Textbox textbox = new Textbox();
		textbox.setWidth("180px");
		textbox.setValue(description);
		textbox.setParent(listcell);
	}	
	
	public void onClick$newVoucherJournalButton(Event event) throws Exception {
		log.info("newVoucherJournalButton clicked.");
		
		voucherJournalDebitCreditList = null;
		
		voucherJournalListModelList.add(0, new VoucherJournal());
	}
	
	public void onClick$cancelVoucherJournalButton(Event event) throws Exception {
		log.info("cancelVoucherJournalButton clicked.");
		
		// reset state to 'View'
		dataState = dataStateDef[0];
		
		voucherJournalDebitCreditList = null;
		
		// re-list
		listVoucherJournal();
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
	
	public SerialNumberGenerator getSerialNumberGenerator() {
		return serialNumberGenerator;
	}

	public void setSerialNumberGenerator(SerialNumberGenerator serialNumberGenerator) {
		this.serialNumberGenerator = serialNumberGenerator;
	}

	public VoucherJournalDao getVoucherJournalDao() {
		return voucherJournalDao;
	}

	public void setVoucherJournalDao(VoucherJournalDao voucherJournalDao) {
		this.voucherJournalDao = voucherJournalDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
}