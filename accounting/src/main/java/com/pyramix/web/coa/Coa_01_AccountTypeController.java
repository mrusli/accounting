package com.pyramix.web.coa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.web.common.GFCBaseController;

public class Coa_01_AccountTypeController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7403187763205054344L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	
	private Listbox coaTypeListbox;
	
	private List<Coa_01_AccountType> coa_01_AccountTypeList;
	private ListModelList<Coa_01_AccountType> coa_01_AccountTypeListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private static final Logger log = Logger.getLogger(Coa_01_AccountTypeController.class);
	
	public void onCreate$coaTypePanel(Event event) throws Exception {
		log.info("coaTypePanel created");
		
		listCoaType();
	}

	private void listCoaType() throws Exception {
		coa_01_AccountTypeList = getCoa_01_AccountTypeDao().findAllCoa_01_AccountType(); 
		coa_01_AccountTypeList.sort((a1, a2) -> {
			return ((Integer)a1.getAccountTypeNumber()).compareTo(
					a2.getAccountTypeNumber());
		});
		coa_01_AccountTypeListModelList = 
				new ListModelList<Coa_01_AccountType>(coa_01_AccountTypeList);
				
		coaTypeListbox.setModel(coa_01_AccountTypeListModelList);
		coaTypeListbox.setItemRenderer(getCoa_01_AccountTypeListItemRenderer());
	}

	private ListitemRenderer<Coa_01_AccountType> getCoa_01_AccountTypeListItemRenderer() {
		
		return new ListitemRenderer<Coa_01_AccountType>() {
			
			@Override
			public void render(Listitem item, Coa_01_AccountType accountType, int index) throws Exception {
				Listcell lc;
				
				// No
				lc = initAccountTypeNumber(new Listcell(), accountType);
				lc.setParent(item);
				
				// Account Type Name
				lc = initAccountTypeName(new Listcell(), accountType);
				lc.setParent(item);
				
				// save or edit
				lc = initSaveOrEdit(new Listcell(), accountType);
				lc.setParent(item);
				
				// cancel
				lc = initCancel(new Listcell(), accountType);
				lc.setParent(item);
				
				item.setValue(accountType);
			}

			private Listcell initAccountTypeNumber(Listcell listcell, Coa_01_AccountType accountType) {
				if (accountType.getId().compareTo(Long.MIN_VALUE)==0) {
					// new account type
					Intbox intbox = new Intbox();
					intbox.setWidth("60px");
					intbox.setValue(getNextAccountTypeNumber());
					intbox.setParent(listcell);
				} else {
					// existing account type - display only
					listcell.setLabel(String.valueOf(accountType.getAccountTypeNumber()));
				}
				
				return listcell;
			}
			
			private Listcell initAccountTypeName(Listcell listcell, Coa_01_AccountType accountType) {
				if (accountType.getId().compareTo(Long.MIN_VALUE)==0) {
					// new account type
					Textbox textbox = new Textbox();
					textbox.setWidth("180px");
					textbox.setValue("");
					textbox.setPlaceholder("New AccountType Name...");
					textbox.setParent(listcell);
				} else {
					// existing account type - display only
					listcell.setLabel(accountType.getAccountTypeName());
				}
				
				return listcell;
			}

			private Listcell initSaveOrEdit(Listcell listcell, Coa_01_AccountType accountType) throws Exception {
				if (accountType.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accounttype - SAVE
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Coa_01_AccountType coa_01_AccountType = 
									getCoa_01_AccountTypeData(new Coa_01_AccountType());
							// save
							getCoa_01_AccountTypeDao().save(coa_01_AccountType);
							// re-list
							listCoaType();
						}

						private Coa_01_AccountType getCoa_01_AccountTypeData(Coa_01_AccountType coa_01_AccountType) {
							// intbox
							Intbox accountTypeNumberIntbox = 
									(Intbox) listcell.getParent().getChildren().get(0).getFirstChild();
							log.info("AccountTypeNumber : "+accountTypeNumberIntbox);
							// textbox
							Textbox accountTypeNameTextbox =
									(Textbox) listcell.getParent().getChildren().get(1).getFirstChild();
							log.info("AccountTypeName : "+accountTypeNameTextbox);
							
							coa_01_AccountType.setAccountTypeNumber(accountTypeNumberIntbox.getValue());
							coa_01_AccountType.setAccountTypeName(accountTypeNameTextbox.getValue());
							coa_01_AccountType.setCreateDate(asDate(todayDate, zoneId));
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							coa_01_AccountType.setLastModified(asDateTime(currentDateTime, zoneId));
							
							return coa_01_AccountType;
						}
					});
				} else {
					// existing accounttype - allow EDIT
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							// check whether allow to EDIT
							Coa_01_AccountType coa_01_AccountTypeProxy =
									getCoa_01_AccountTypeDao().findCoa_01_AccountTypeAccountGroupsByProxy(accountType.getId());
							if (!coa_01_AccountTypeProxy.getAccountGroups().isEmpty()) {
								// re-list
								listCoaType();
								// raise exception
								throw new Exception("Cannot Modify. This account type is currently used by AccountGroup.");
							}
							// 
							Button button = (Button) event.getTarget();
							// access the listitem
							// Listitem item = (Listitem) listcell.getParent();
							// get the value from the listitem
							// Coa_01_AccountType accountType = item.getValue();
							log.info("Existing accountType : "+accountType.toString());
							
							if (button.getLabel().compareTo("Modify")==0) {								
								modify_Coa_01_AccountTypeData(accountType);
								button.setLabel("Update");
								// enable cancel
								Button cancelButton = 
										(Button) listcell.getParent().getChildren().get(3).getFirstChild();
								cancelButton.setVisible(true);
							} else {
								//
								Coa_01_AccountType modAccountType = 
										getCoa_01_AccountTypeModifiedData(accountType);
								log.info("Update : " + modAccountType.toString());
								// update
								getCoa_01_AccountTypeDao().update(modAccountType);
								// re-list
								listCoaType();
							}
						}

						private void modify_Coa_01_AccountTypeData(Coa_01_AccountType accountType) {
							// intbox
							Intbox intbox = new Intbox();
							intbox.setWidth("60px");
							intbox.setValue(accountType.getAccountTypeNumber());
							// set intbox to lc0
							Listcell lc0 = (Listcell) listcell.getParent().getChildren().get(0);
							lc0.setLabel(null);
							intbox.setParent(lc0);
							// textbox
							Textbox textbox = new Textbox();
							textbox.setWidth("180px");
							textbox.setValue(accountType.getAccountTypeName());
							// set textbox to lc1
							Listcell lc1 = (Listcell) listcell.getParent().getChildren().get(1);
							lc1.setLabel(null);
							textbox.setParent(lc1);							
						}
						
						private Coa_01_AccountType getCoa_01_AccountTypeModifiedData(Coa_01_AccountType accountType) {
							// intbox
							Intbox accountTypeNumberIntbox = 
									(Intbox) listcell.getParent().getChildren().get(0).getFirstChild();
							log.info("AccountTypeNumber : "+accountTypeNumberIntbox.getValue());
							// textbox
							Textbox accountTypeNameTextbox =
									(Textbox) listcell.getParent().getChildren().get(1).getFirstChild();
							log.info("AccountTypeName : "+accountTypeNameTextbox.getValue());
							// set
							accountType.setAccountTypeNumber(accountTypeNumberIntbox.getValue());
							accountType.setAccountTypeName(accountTypeNameTextbox.getValue());
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							accountType.setLastModified(asDateTime(currentDateTime, zoneId));
							
							return accountType;
						}
					});
				}
				
				return listcell;
			}
			
			private Listcell initCancel(Listcell listcell, Coa_01_AccountType accountType) {
				Button button = new Button();
				button.setLabel("Cancel");
				button.setVisible(accountType.getId().compareTo(Long.MIN_VALUE)==0);
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// re-list
						listCoaType();
					}
				});
				
				return listcell;
			}

		};
	}

	public void onClick$newAccounTypeButton(Event event) throws Exception {
		Coa_01_AccountType coa_01_AccountType = new Coa_01_AccountType();
		coa_01_AccountType.setAccountTypeNumber(getNextAccountTypeNumber());
		
		coa_01_AccountTypeListModelList.add(coa_01_AccountType);
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		log.info("Cancel button clicked.");
		
		listCoaType();
	}
	
	private int getNextAccountTypeNumber() {
		if (coa_01_AccountTypeList.size()==0) {
			return 1;
		} else {
			// get the last accountType
			Coa_01_AccountType coa_01_AccountType = 
					coa_01_AccountTypeList.get(coa_01_AccountTypeList.size()-1);
			
			return coa_01_AccountType.getAccountTypeNumber()+1;
		}
	}

	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}

	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}
}
