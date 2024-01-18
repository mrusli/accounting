package com.pyramix.web.coa;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_02_AccountGroup;
import com.pyramix.domain.coa.Coa_03_SubAccount01;
import com.pyramix.domain.coa.Coa_04_SubAccount02;
import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.web.common.GFCBaseController;

public class Coa_05_MasterController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3071123018345032873L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	
	private Listbox coaMasterListbox;
	
	private List<Coa_05_Master> coa_05_MasterList;
	private ListModelList<Coa_05_Master> coa_05_MasterListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private final int ZERO_PADDED_DIGIT = 3;
	
	private static final Logger log = Logger.getLogger(Coa_05_MasterController.class);
	
	public void onCreate$coaMasterPanel(Event event) throws Exception {
		log.info("coaMasterPanel created");
		
		listCoaMaster();
	}
	
	private void listCoaMaster() throws Exception {
		coa_05_MasterList = getCoa_05_AccountMasterDao().findAllCoa_05_Master();
		Comparator<Coa_05_Master> compareAllAccounts =
			Comparator.comparing(Coa_05_Master::getTypeCoaNumber)
				.thenComparingInt(Coa_05_Master::getGroupCoaNumber)
				.thenComparingInt(Coa_05_Master::getSubaccount01CoaNumber)
				.thenComparingInt(Coa_05_Master::getSubaccount02CoaNumber)
				.thenComparingInt(Coa_05_Master::getMasterCoaNumber);
		coa_05_MasterList.sort(compareAllAccounts);
		
		coa_05_MasterListModelList =
				new ListModelList<Coa_05_Master>(coa_05_MasterList);
		
		coaMasterListbox.setModel(coa_05_MasterListModelList);
		coaMasterListbox.setItemRenderer(getCoa_05_MasterListItemRenderer());
	}

	private ListitemRenderer<Coa_05_Master> getCoa_05_MasterListItemRenderer() {
		
		return new ListitemRenderer<Coa_05_Master>() {
			
			@Override
			public void render(Listitem item, Coa_05_Master accountMaster, int index) throws Exception {
				Listcell lc;
				
				//	Account Type
				lc = initAccountType(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	Account Group
				lc = initAccountGroup(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	SubAccount-01
				lc = initSubAccount_01(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	SubAccount-02
				lc = initSubAccount_02(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	Acc Master
				lc = initAccountMasterNumber(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	Acc Master Name
				lc = initAccountMasterName(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	COA
				lc = initCOA(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	Credit Account
				lc = initCrDBAccount(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	Active
				lc = initActive(new Listcell(), accountMaster);
				lc.setParent(item);
				
				//	Mod
				lc = initSaveOrEdit(new Listcell(), accountMaster);
				lc.setParent(item);
				
				item.setValue(accountMaster);
			}

			private Listcell initAccountType(Listcell listcell, Coa_05_Master accountMaster) throws Exception {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountMaster
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setPlaceholder("Select...");
					combobox.setParent(listcell);
					// setup comboitems
					setupComboitemOfAccountTypes(combobox);
					// event
					combobox.addEventListener(Events.ON_SELECT, onAccountTypeComboboxSelect(listcell));
				} else {
					// existing accountMaster
					Coa_05_Master proxyCoa_05_Master =
							getCoa_05_AccountMasterDao().findCoa_01_AccountType_ByProxy(accountMaster.getId());
					listcell.setLabel(proxyCoa_05_Master.getAccountType().getAccountTypeNumber()
							+"-"+proxyCoa_05_Master.getAccountType().getAccountTypeName());					
				}
				
				return listcell;
			}

			private EventListener<Event> onAccountTypeComboboxSelect(Listcell listcell) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Combobox combobox = (Combobox) event.getTarget();
						Coa_01_AccountType selAccountType = combobox.getSelectedItem().getValue();
						Coa_01_AccountType selAccountTypeProxy = getCoa_01_AccountTypeDao()
								.findCoa_01_AccountTypeAccountGroupsByProxy(selAccountType.getId());
						List<Coa_02_AccountGroup> accountGroupList =
								selAccountTypeProxy.getAccountGroups();
						accountGroupList.forEach(Coa_02_AccountGroup -> log.info(Coa_02_AccountGroup.toString()));
						// accountGroup combobox
						Combobox accountGroupCombobox = 
								(Combobox) listcell.getParent().getChildren().get(1).getFirstChild();
						// clear comboitems
						accountGroupCombobox.getItems().clear();
						// enable
						accountGroupCombobox.setDisabled(false);
						accountGroupCombobox.setPlaceholder("Select...");
						log.info("combobox is clear : "+combobox.getItems());
						// setup comboitems
						setupComboitemOfAccountGroups(accountGroupCombobox, accountGroupList);
					}
				};
			}

			
			private Listcell initAccountGroup(Listcell listcell, Coa_05_Master accountMaster) throws Exception {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountMaster
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setDisabled(true);
					combobox.setParent(listcell);
					combobox.addEventListener(Events.ON_SELECT, onAccountGroupComboboxSelect(listcell));
				} else {
					Coa_05_Master proxyCoa_05_Master =
							getCoa_05_AccountMasterDao().findCoa_02_AccountGroup_ByProxy(accountMaster.getId());
					
					listcell.setStyle("white-space: nowrap;");
					listcell.setLabel(proxyCoa_05_Master.getAccountGroup().getAccountGroupNumber()
							+"-"+proxyCoa_05_Master.getAccountGroup().getAccountGroupName());
				}
				
				return listcell;
			}

			private EventListener<Event> onAccountGroupComboboxSelect(Listcell listcell) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Combobox combobox = (Combobox) event.getTarget();
						Coa_02_AccountGroup selAccountGroup =	
								combobox.getSelectedItem().getValue();
						log.info(selAccountGroup.toString());
						Coa_02_AccountGroup accountGroupByProxy =
								getCoa_02_AccountGroupDao().findCoa_03_SubAccount01s_ByProxy(selAccountGroup.getId());
						List<Coa_03_SubAccount01> coa_03_SubAccount01List =
								accountGroupByProxy.getSubAccount01s();
						coa_03_SubAccount01List.forEach((Coa_03_SubAccount01 c) -> log.info(c.toString()));
						Combobox subAccount01Combobox =
								(Combobox) listcell.getParent().getChildren().get(2).getFirstChild();
						// enable
						subAccount01Combobox.setDisabled(false);
						subAccount01Combobox.setPlaceholder("Select...");
						// clear
						subAccount01Combobox.getItems().clear();
						// set comboitems
						setupComboitemsOfSubAccount01(subAccount01Combobox, coa_03_SubAccount01List);
					}
				};
			}

			private Listcell initSubAccount_01(Listcell listcell, Coa_05_Master accountMaster) throws Exception {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setDisabled(true);
					combobox.setParent(listcell);
					combobox.addEventListener(Events.ON_SELECT, onSubAccount01ComboboxSelect(listcell));
				} else {
					Coa_05_Master proxyCoa_05_Master =
							getCoa_05_AccountMasterDao().findCoa_03_SubAccount01_ByProxy(accountMaster.getId());
					
					listcell.setStyle("white-space: nowrap;");
					listcell.setLabel(proxyCoa_05_Master.getSubAccount01().getSubAccount01Number()
							+"-"+proxyCoa_05_Master.getSubAccount01().getSubAccount01Name());
				}
				
				return listcell;
			}

			private EventListener<Event> onSubAccount01ComboboxSelect(Listcell listcell) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Combobox combobox = (Combobox) event.getTarget();
						Coa_03_SubAccount01 selSubAccount01 =
								combobox.getSelectedItem().getValue();
						log.info(selSubAccount01.toString());
						Coa_03_SubAccount01 subAccount01Proxy =
								getCoa_03_SubAccount01Dao().findCoa_04_SubAccount02s_ByProxy(selSubAccount01.getId());
						List<Coa_04_SubAccount02> coa_04_SubAccount02List =
								subAccount01Proxy.getSubAccount02s();
						coa_04_SubAccount02List.forEach((Coa_04_SubAccount02 s) -> log.info(s.toString()));
						Combobox subAccount02Combobox =
								(Combobox) listcell.getParent().getChildren().get(3).getFirstChild();
						// enable
						subAccount02Combobox.setDisabled(false);
						subAccount02Combobox.setPlaceholder("Select...");
						// clear
						subAccount02Combobox.getItems().clear();
						// set comboitems
						setupComboitemsOfSubAccount02(subAccount02Combobox, coa_04_SubAccount02List);						
					}
				};
			}

			private Listcell initSubAccount_02(Listcell listcell, Coa_05_Master accountMaster) throws Exception {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setDisabled(true);
					combobox.setParent(listcell);					
				} else {
					Coa_05_Master proxyCoa_05_Master =
							getCoa_05_AccountMasterDao().findCoa_04_SubAccount02_ByProxy(accountMaster.getId());
					
					listcell.setStyle("white-space: nowrap;");
					listcell.setLabel(proxyCoa_05_Master.getSubAccount02().getSubAccount02Number()
							+"-"+proxyCoa_05_Master.getSubAccount02().getSubAccount02Name());
				}
				return listcell;
			}

			private Listcell initAccountMasterNumber(Listcell listcell, Coa_05_Master accountMaster) {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					Intbox intbox = new Intbox();
					intbox.setWidth("60px");
					intbox.setValue(accountMaster.getMasterCoaNumber());
					intbox.setParent(listcell);
				} else {
					listcell.setStyle("white-space: nowrap;");
					listcell.setLabel(String.valueOf(accountMaster.getMasterCoaNumber()));
				}
				
				return listcell;
			}

			private Listcell initAccountMasterName(Listcell listcell, Coa_05_Master accountMaster) {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					Textbox textbox = new Textbox();
					textbox.setWidth("140px");
					textbox.setValue("");
					textbox.setPlaceholder("New Account Master Name...");
					textbox.setParent(listcell);
				} else {
					listcell.setLabel(accountMaster.getMasterCoaName());					
				}
				
				return listcell;
			}

			private Listcell initCOA(Listcell listcell, Coa_05_Master accountMaster) {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY - no modif
					
				} else {
					listcell.setLabel(accountMaster.getMasterCoaComp());
				}
				
				return listcell;
			}

			private Listcell initCrDBAccount(Listcell listcell, Coa_05_Master accountMaster) {
				Checkbox checkbox = new Checkbox();
				checkbox.setParent(listcell);
				
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountMaster - allows user to set
					checkbox.setDisabled(false);
					// default to 'Credit' account
					checkbox.setChecked(true);
				} else {
					// existing accountMaster - view only
					checkbox.setDisabled(true);
					// set according to db
					checkbox.setChecked(accountMaster.isCreditAccount());
				}
				
				return listcell;
			}

			private Listcell initActive(Listcell listcell, Coa_05_Master accountMaster) {
				Checkbox checkbox = new Checkbox();
				checkbox.setParent(listcell);
				
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountMaster - allows user to set
					checkbox.setDisabled(false);
					// default to 'Active' account
					checkbox.setChecked(true);
				} else {
					// existing accountMaster - view only
					checkbox.setDisabled(accountMaster.isActive());
					// set according to db
					checkbox.setChecked(accountMaster.isActive());
				}
				
				return listcell;
			}			
			
			private Listcell initSaveOrEdit(Listcell listcell, Coa_05_Master accountMaster) {
				if (accountMaster.getId().compareTo(Long.MIN_VALUE)==0) {
					// new master account - SAVE
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClick(listcell, accountMaster));
				} else {
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClick(listcell, accountMaster));
					
				}
				
				return listcell;
			}

			private EventListener<Event> onSaveButtonClick(Listcell listcell, Coa_05_Master accountMaster) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Coa_05_Master coa_05_Master =
								getCoa_05_MasterData(accountMaster);
						log.info(coa_05_Master.toString());
						// save
						getCoa_05_AccountMasterDao().save(coa_05_Master);
						// re-list
						listCoaMaster();
					}

					private Coa_05_Master getCoa_05_MasterData(Coa_05_Master accountMaster) throws Exception {
						// combobox
						Combobox subAccount02Combobox =
								(Combobox) listcell.getParent().getChildren().get(3).getFirstChild();
						log.info("SubAccount02 combobox : "+subAccount02Combobox.getSelectedItem().getValue());
						// intbox
						Intbox accountMasterIntbox =
								(Intbox) listcell.getParent().getChildren().get(4).getFirstChild();
						log.info("MasterAccount intbox : "+accountMasterIntbox.getValue());
						// textbox
						Textbox accountMasterTextbox =
								(Textbox) listcell.getParent().getChildren().get(5).getFirstChild();
						log.info("MasterAccount textbox : "+accountMasterTextbox.getValue());
						// credit-debit account
						Checkbox creditDebitAccountCheckbox =
								(Checkbox) listcell.getParent().getChildren().get(7).getFirstChild();
						log.info("MasterAccount Credit? : "+creditDebitAccountCheckbox.isChecked());
						// account active
						Checkbox activeAccountCheckbox =
								(Checkbox) listcell.getParent().getChildren().get(8).getFirstChild();
						log.info("MasterAccount Active? : "+activeAccountCheckbox.isChecked());
						
						// set
						Coa_04_SubAccount02 sel_coa_04_SubAccount02 =
								subAccount02Combobox.getSelectedItem().getValue();
						Coa_03_SubAccount01 sel_coa_03_SubAccount01 =
								getCoa_03_SubAccount01ByProxy(sel_coa_04_SubAccount02);
						Coa_02_AccountGroup sel_coa_02_AccountGroup =
								getCoa_02_AccountGroupByProxy(sel_coa_03_SubAccount01);
						Coa_01_AccountType sel_coa_01_AccountType =
								getCoa_01_AccountTypeByProxy(sel_coa_02_AccountGroup);
						int coa_05_MasterNumber = accountMasterIntbox.getValue();
						accountMaster.setSubAccount02(sel_coa_04_SubAccount02);
						accountMaster.setSubaccount02CoaNumber(
								sel_coa_04_SubAccount02.getSubAccount02Number());
						accountMaster.setSubAccount01(sel_coa_03_SubAccount01);
						accountMaster.setSubaccount01CoaNumber(
								sel_coa_03_SubAccount01.getSubAccount01Number());
						accountMaster.setAccountGroup(sel_coa_02_AccountGroup);
						accountMaster.setGroupCoaNumber(
								sel_coa_02_AccountGroup.getAccountGroupNumber());
						accountMaster.setAccountType(sel_coa_01_AccountType);
						accountMaster.setTypeCoaNumber(
								sel_coa_01_AccountType.getAccountTypeNumber());
						accountMaster.setMasterCoaNumber(coa_05_MasterNumber);
						accountMaster.setMasterCoaName(accountMasterTextbox.getValue());
						accountMaster.setCreditAccount(creditDebitAccountCheckbox.isChecked());
						accountMaster.setActive(activeAccountCheckbox.isChecked());
						accountMaster.setMasterCoaComp(createCoaComp(sel_coa_01_AccountType, sel_coa_02_AccountGroup,
								sel_coa_03_SubAccount01, sel_coa_04_SubAccount02, coa_05_MasterNumber));
						accountMaster.setCreateDate(asDate(todayDate, zoneId));
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						accountMaster.setLastModified(asDateTime(currentDateTime, zoneId));
						
						return accountMaster;
					}

				};
			}
			
			private void setupComboitemOfAccountTypes(Combobox combobox) throws Exception {
				// get the list of account types
				List<Coa_01_AccountType> accountTypeList = 
						getCoa_01_AccountTypeDao().findAllCoa_01_AccountType();
				// sort
				accountTypeList.sort((a1, a2) -> {
					return ((Integer)a1.getAccountTypeNumber()).compareTo(
							a2.getAccountTypeNumber());
				});
				// new items
				Comboitem comboitem;
				for (int i = 0; i < accountTypeList.size(); i++) {
					Coa_01_AccountType accountType =
							accountTypeList.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(accountType.getAccountTypeNumber()
							+"-"+accountType.getAccountTypeName());
					comboitem.setValue(accountType);
					comboitem.setParent(combobox);
				}				
			}			
			
			private void setupComboitemOfAccountGroups(Combobox combobox, 
					List<Coa_02_AccountGroup> accountGroupList) throws Exception {
				// comparator
				Comparator<Coa_02_AccountGroup> compareAllAccounts =
						Comparator.comparing(Coa_02_AccountGroup::getTypeCoaNumber)
							.thenComparing(Coa_02_AccountGroup::getAccountGroupNumber);
				// sort
				accountGroupList.sort(compareAllAccounts);
				// items
				Comboitem comboitem;
				for (int i = 0; i < accountGroupList.size(); i++) {
					Coa_02_AccountGroup accountGroup =
							accountGroupList.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(accountGroup.getAccountGroupNumber()
							+"-"+accountGroup.getAccountGroupName());
					comboitem.setValue(accountGroup);
					comboitem.setParent(combobox);
				}					
				
			}

			private void setupComboitemsOfSubAccount01(Combobox combobox,
					List<Coa_03_SubAccount01> coa_03_SubAccount01List) {
				// comparator
				Comparator<Coa_03_SubAccount01> compareAllAccounts =
						Comparator.comparing(Coa_03_SubAccount01::getTypeCoaNumber)
							.thenComparing(Coa_03_SubAccount01::getGroupCoaNumber)
							.thenComparing(Coa_03_SubAccount01::getSubAccount01Number);
				// sort
				coa_03_SubAccount01List.sort(compareAllAccounts);						
				// comboitems
				Comboitem comboitem;
				for (int i = 0; i < coa_03_SubAccount01List.size(); i++) {
					Coa_03_SubAccount01 subAccount01 =
							coa_03_SubAccount01List.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(subAccount01.getSubAccount01Number()
							+"-"+subAccount01.getSubAccount01Name());
					comboitem.setValue(subAccount01);
					comboitem.setParent(combobox);
				}
			}
			
			private void setupComboitemsOfSubAccount02(Combobox combobox,
					List<Coa_04_SubAccount02> coa_04_SubAccount02List) {
				// comparator
				Comparator<Coa_04_SubAccount02> compareAllAccounts =
						Comparator.comparing(Coa_04_SubAccount02::getTypeCoaNumber)
							.thenComparing(Coa_04_SubAccount02::getGroupCoaNumber)
							.thenComparing(Coa_04_SubAccount02::getSubAccount01CoaNumber)
							.thenComparing(Coa_04_SubAccount02::getSubAccount02Number);
				// sort
				coa_04_SubAccount02List.sort(compareAllAccounts);
				// comboitems
				Comboitem comboitem;
				for (Coa_04_SubAccount02 subAccount02 : coa_04_SubAccount02List) {
					comboitem = new Comboitem();
					comboitem.setLabel(subAccount02.getSubAccount02Number()
							+"-"+subAccount02.getSubAccount02Name());
					comboitem.setValue(subAccount02);
					comboitem.setParent(combobox);
				}
				
			}
			
			private EventListener<Event> onModifyButtonClick(Listcell listcell, Coa_05_Master accountMaster) {
			
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						// existing accountMaster object
						// log.info("existing account master : "+accountMaster.toString());
						Button button = (Button) event.getTarget();
						if (button.getLabel().compareTo("Modify")==0) {
							modify_Coa_05_AccountMasterData(accountMaster);
							button.setLabel("Update");
						} else {
							// to Update the modified accountMaster
							Coa_05_Master modAccountMaster =
									getCoa_05_AccountMasterModifiedData(accountMaster);
							log.info("Update : "+modAccountMaster.toString());
							// update
							getCoa_05_AccountMasterDao().update(modAccountMaster);
							// re-list
							listCoaMaster();
						}
					}

					/*
					 * to Modify accountMaster
					 */

					private void modify_Coa_05_AccountMasterData(Coa_05_Master accountMaster) throws Exception {
						// accountType combobox - list of accountTypes
						Combobox combobox = new Combobox();
						combobox.setWidth("140px");
						combobox.setPlaceholder("Select...");
						Listcell lc0 = (Listcell) listcell.getParent().getChildren().get(0);
						lc0.setLabel(null);
						combobox.setParent(lc0);
						// setup comboitems of accountType
						setupComboitemOfAccountTypes(combobox);
						// select the comboitem
						// selectComboitemOfAccountType(combobox, accountMaster);
						// on select accountType combobox
						combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								Combobox combobox = (Combobox) event.getTarget();
								Coa_01_AccountType selAccountType = 
										combobox.getSelectedItem().getValue();
								Coa_01_AccountType selAccountTypeProxy = getCoa_01_AccountTypeDao()
										.findCoa_01_AccountTypeAccountGroupsByProxy(selAccountType.getId());
								List<Coa_02_AccountGroup> accountGroupList =
										selAccountTypeProxy.getAccountGroups();
								accountGroupList.forEach((Coa_02_AccountGroup g) -> log.info(g.toString()));
								// accountGroup combobox
								Combobox accountGroupCombobox = 
										(Combobox) listcell.getParent().getChildren().get(1).getFirstChild();
								// clear comboitems
								accountGroupCombobox.getItems().clear(); 
								// setup comboitems of accountGroup
								setupComboitemOfAccountGroups(accountGroupCombobox, accountGroupList);
								// enabled
								accountGroupCombobox.setDisabled(false);
								accountGroupCombobox.setValue("");
								accountGroupCombobox.setPlaceholder("Select...");
							}
						});
						
						// lc1 - accountGroup
						Listcell lc1 = (Listcell) listcell.getParent().getChildren().get(1);
						lc1.setLabel(null);
						// accountGroup combobox - list of accountGroups
						combobox = new Combobox();
						combobox.setWidth("140px");
						combobox.setValue(lc1.getLabel());
						combobox.setDisabled(true);
						combobox.setParent(lc1);
						combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								log.info("onSelect accountGroup...");
								Combobox combobox = (Combobox) event.getTarget();
								Coa_02_AccountGroup selAccountGroup = 
										combobox.getSelectedItem().getValue();
								Coa_02_AccountGroup selAccountGroupProxy = getCoa_02_AccountGroupDao()
										.findCoa_03_SubAccount01s_ByProxy(selAccountGroup.getId());
								List<Coa_03_SubAccount01> subAccount01List =
										selAccountGroupProxy.getSubAccount01s();
								subAccount01List.forEach((Coa_03_SubAccount01 s1) -> log.info(s1.toString()));
								// subAccount01 combobox
								Combobox subAccount01Combobox = 
										(Combobox) listcell.getParent().getChildren().get(2).getFirstChild();
								// clear comboitems
								subAccount01Combobox.getItems().clear(); 
								// setup comboitems of accountGroup
								setupComboitemsOfSubAccount01(subAccount01Combobox, subAccount01List);
								// enabled
								subAccount01Combobox.setDisabled(false);
								subAccount01Combobox.setValue("");
								subAccount01Combobox.setPlaceholder("Select...");
							}
						});

						// lc2 - subAccount-01
						Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
						lc2.setLabel(null);
						// subAccount01 combobox - list of accountGroups
						combobox = new Combobox();
						combobox.setWidth("140px");
						combobox.setValue(lc1.getLabel());
						combobox.setDisabled(true);
						combobox.setParent(lc2);
						combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

							@Override
							public void onEvent(Event event) throws Exception {
								log.info("onSelect subAccount01...");
								Combobox combobox = (Combobox) event.getTarget();
								Coa_03_SubAccount01 selSubAccount01 = 
										combobox.getSelectedItem().getValue();
								Coa_03_SubAccount01 selSubAccount01Proxy = getCoa_03_SubAccount01Dao()
										.findCoa_04_SubAccount02s_ByProxy(selSubAccount01.getId());
								List<Coa_04_SubAccount02> subAccount02List =
										selSubAccount01Proxy.getSubAccount02s();
								subAccount02List.forEach((Coa_04_SubAccount02 s2) -> log.info(s2.toString()));
								// subAccount02 combobox
								Combobox subAccount02Combobox = 
										(Combobox) listcell.getParent().getChildren().get(3).getFirstChild();
								// clear comboitems
								subAccount02Combobox.getItems().clear(); 
								// setup comboitems of accountGroup
								setupComboitemsOfSubAccount02(subAccount02Combobox, subAccount02List);
								// enabled
								subAccount02Combobox.setDisabled(false);
								subAccount02Combobox.setValue("");
								subAccount02Combobox.setPlaceholder("Select...");								
							}
						});

						// lc3 - subAccount-02
						Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
						lc3.setLabel(null);
						// subAccount02 combobox - list of accountGroups
						combobox = new Combobox();
						combobox.setWidth("140px");
						combobox.setValue(lc1.getLabel());
						combobox.setDisabled(true);
						combobox.setParent(lc3);
						
						// lc4 - accoutMaster Number
						Listcell lc4 = (Listcell) listcell.getParent().getChildren().get(4);
						lc4.setLabel(null);
						// accountMaster intbox
						Intbox intbox = new Intbox();
						intbox.setWidth("60px");
						intbox.setValue(accountMaster.getMasterCoaNumber());
						intbox.setParent(lc4);
						
						// lc5 - accountMaster Name
						Listcell lc5 = (Listcell) listcell.getParent().getChildren().get(5);
						lc5.setLabel(null);
						// accountMaster textbox
						Textbox textbox = new Textbox();
						textbox.setWidth("140px");
						textbox.setValue(accountMaster.getMasterCoaName());
						textbox.setParent(lc5);
						
						// lc6 - accountMaster coaComp
						Listcell lc6 = (Listcell) listcell.getParent().getChildren().get(6);
						lc6.setLabel(null);
						// accountMaster label
						lc6.setLabel(null);
						
						// lc7 - accountMaster isCreditAccount
						Listcell lc7 = (Listcell) listcell.getParent().getChildren().get(7);
						Checkbox checkbox = (Checkbox) lc7.getFirstChild();
						checkbox.setChecked(false);
						checkbox.setDisabled(false); // -- allow user to make changes. Previously set to disable (VIEW).
						
						// lc8 - accountMaster isActive
						Listcell lc8 = (Listcell) listcell.getParent().getChildren().get(8);
						checkbox = (Checkbox) lc8.getFirstChild();
						checkbox.setChecked(false);
						checkbox.setDisabled(false); // -- allow user to make changes. Previously set to disable (VIEW).
					}

					/*
					 * to get the modified AccountMaster (after user make changes)
					 */
					private Coa_05_Master getCoa_05_AccountMasterModifiedData(Coa_05_Master accountMaster) throws Exception {
						// accountType combobox
						Combobox combobox = 
								(Combobox) listcell.getParent().getChildren().get(0).getFirstChild();
						log.info(combobox.getValue());
						// accountGroup combobox
						combobox =
								(Combobox) listcell.getParent().getChildren().get(1).getFirstChild();
						log.info(combobox.getValue());
						// subAccount01 combobox
						combobox = 
								(Combobox) listcell.getParent().getChildren().get(2).getFirstChild();
						log.info(combobox.getValue());				
						// subAccount02 combobox
						Combobox subAccount02Combobox = 
								(Combobox) listcell.getParent().getChildren().get(3).getFirstChild();
						log.info(combobox.getValue());
						// accountMaster Number intbox
						Intbox accountMasterIntbox =
								(Intbox) listcell.getParent().getChildren().get(4).getFirstChild();
						log.info(accountMasterIntbox.getValue());
						// accountMaster Name textbox
						Textbox accountMasterTextbox =
								(Textbox) listcell.getParent().getChildren().get(5).getFirstChild();
						log.info(accountMasterTextbox.getValue());
						// coa label - READ ONLY - no user modif
						// Listcell lc =
						//		(Listcell) listcell.getParent().getChildren().get(6).getFirstChild();
						
						// creditAccount checkbox
						Checkbox creditDebitAccountCheckbox =
								(Checkbox) listcell.getParent().getChildren().get(7).getFirstChild();
						log.info("creditAccount : "+creditDebitAccountCheckbox.isChecked());
						// active checkbox
						Checkbox activeAccountCheckbox =
								(Checkbox) listcell.getParent().getChildren().get(8).getFirstChild();
						log.info("active: "+activeAccountCheckbox.isChecked());
						// set
						Coa_04_SubAccount02 sel_coa_04_SubAccount02 =
								subAccount02Combobox.getSelectedItem().getValue();
						Coa_03_SubAccount01 sel_coa_03_SubAccount01 =
								getCoa_03_SubAccount01ByProxy(sel_coa_04_SubAccount02);
						Coa_02_AccountGroup sel_coa_02_AccountGroup =
								getCoa_02_AccountGroupByProxy(sel_coa_03_SubAccount01);
						Coa_01_AccountType sel_coa_01_AccountType =
								getCoa_01_AccountTypeByProxy(sel_coa_02_AccountGroup);
						int coa_05_MasterNumber = accountMasterIntbox.getValue();
						// accountMaster.setSubAccount02(sel_coa_04_SubAccount02);
						accountMaster.setSubaccount02CoaNumber(
								sel_coa_04_SubAccount02.getSubAccount02Number());
						// accountMaster.setSubAccount01(sel_coa_03_SubAccount01);
						accountMaster.setSubaccount01CoaNumber(
								sel_coa_03_SubAccount01.getSubAccount01Number());
						// accountMaster.setAccountGroup(sel_coa_02_AccountGroup);
						accountMaster.setGroupCoaNumber(
								sel_coa_02_AccountGroup.getAccountGroupNumber());
						// accountMaster.setAccountType(sel_coa_01_AccountType);
						accountMaster.setTypeCoaNumber(
								sel_coa_01_AccountType.getAccountTypeNumber());
						accountMaster.setMasterCoaNumber(coa_05_MasterNumber);
						accountMaster.setMasterCoaName(accountMasterTextbox.getValue());
						accountMaster.setCreditAccount(creditDebitAccountCheckbox.isChecked());
						accountMaster.setActive(activeAccountCheckbox.isChecked());
						accountMaster.setMasterCoaComp(createCoaComp(sel_coa_01_AccountType, sel_coa_02_AccountGroup,
								sel_coa_03_SubAccount01, sel_coa_04_SubAccount02, coa_05_MasterNumber));
						accountMaster.setCreateDate(asDate(todayDate, zoneId));
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						accountMaster.setLastModified(asDateTime(currentDateTime, zoneId));
						
						return accountMaster;
					}
					
					@SuppressWarnings("unused")
					private void selectComboitemOfAccountType(Combobox combobox, Coa_05_Master accountMaster) throws Exception {
						// obtain the accountType thru proxy
						Coa_05_Master accountMasterProxy =
								getCoa_05_AccountMasterDao().findCoa_01_AccountType_ByProxy(accountMaster.getId());
						for (Comboitem comboitem : combobox.getItems()) {
							if (comboitem.getLabel().compareTo(accountMasterProxy.getAccountType().getAccountTypeNumber()
									+"-"+accountMasterProxy.getAccountType().getAccountTypeName())==0) {
								combobox.setSelectedItem(comboitem);
								break;
							}
						}
					}

				};
			}
		};
	}

	private String createCoaComp(Coa_01_AccountType coa_01_AccountType,
			Coa_02_AccountGroup coa_02_AccountGroup, Coa_03_SubAccount01 coa_03_SubAccount01,
			Coa_04_SubAccount02 coa_04_SubAccount02, int coa_05_MasterNumber) {
	
		String coaComp = "";
		
		coaComp = coa_01_AccountType.getAccountTypeNumber()+"."
				+ coa_02_AccountGroup.getAccountGroupNumber()
				+ coa_03_SubAccount01.getSubAccount01Number()
				+ coa_04_SubAccount02.getSubAccount02Number()+"."
				+ tripleDigitZeroPaddedNumberString(coa_05_MasterNumber);
		
		return coaComp;
	}	
	
	private String tripleDigitZeroPaddedNumberString(int coa_05_MasterNumber) {
		String numString = String.valueOf(coa_05_MasterNumber);
		
		for (int i = 0; i <= ZERO_PADDED_DIGIT-numString.length(); i++) {			
			numString = "0"+numString;
		}
		
		return numString;
	}

	public void onClick$newAccountMasterButton(Event event) throws Exception {
		Coa_05_Master coa_05_Master = new Coa_05_Master();
		coa_05_Master.setMasterCoaNumber(getNextAccountMasterNumber());
		
		coa_05_MasterListModelList.add(coa_05_Master);
	}
	
	private int getNextAccountMasterNumber() {
		if (coa_05_MasterList.size()==0) {
			return 1;
		} else {
			// get the last AccountMaster
			Coa_05_Master coa_05_Master =
					coa_05_MasterList.get(coa_05_MasterList.size()-1);
			
			return coa_05_Master.getMasterCoaNumber()+1;
		}
	}

	private Coa_03_SubAccount01 getCoa_03_SubAccount01ByProxy(
			Coa_04_SubAccount02 sel_coa_04_SubAccount02) throws Exception {
		
		Coa_04_SubAccount02 coa_04_SubAccount02SubAccount01ByProxy =
				getCoa_04_SubAccount02Dao().findCoa_03_SubAccount01_ByProxy(sel_coa_04_SubAccount02.getId());
		Coa_03_SubAccount01 coa_03_SubAccount01 =
				coa_04_SubAccount02SubAccount01ByProxy.getSubAccount01();
		log.info(coa_03_SubAccount01.toString());
		
		return coa_03_SubAccount01;
	}	
	
	private Coa_02_AccountGroup getCoa_02_AccountGroupByProxy(
			Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception {
		
		Coa_03_SubAccount01 coa_03_SubAccount01AccountGroupByProxy =
				getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(coa_03_SubAccount01.getId());
		Coa_02_AccountGroup coa_02_AccountGroup =
				coa_03_SubAccount01AccountGroupByProxy.getAccountGroup();
		
		return coa_02_AccountGroup;
	}
	
	private Coa_01_AccountType getCoa_01_AccountTypeByProxy(
			Coa_02_AccountGroup coa_02_AccountGroup) throws Exception {
		
		Coa_02_AccountGroup coa_02_AccountGroupAccoutTypeByProxy =
				getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(coa_02_AccountGroup.getId());
		Coa_01_AccountType coa_01_accountType =
				coa_02_AccountGroupAccoutTypeByProxy.getAccountType();
		log.info(coa_01_accountType.toString());

		return coa_01_accountType;
	}
	
	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}
	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}
	public Coa_02_AccountGroupDao getCoa_02_AccountGroupDao() {
		return coa_02_AccountGroupDao;
	}
	public void setCoa_02_AccountGroupDao(Coa_02_AccountGroupDao coa_02_AccountGroupDao) {
		this.coa_02_AccountGroupDao = coa_02_AccountGroupDao;
	}
	public Coa_04_SubAccount02Dao getCoa_04_SubAccount02Dao() {
		return coa_04_SubAccount02Dao;
	}
	public void setCoa_04_SubAccount02Dao(Coa_04_SubAccount02Dao coa_04_SubAccount02Dao) {
		this.coa_04_SubAccount02Dao = coa_04_SubAccount02Dao;
	}

	public Coa_03_SubAccount01Dao getCoa_03_SubAccount01Dao() {
		return coa_03_SubAccount01Dao;
	}

	public void setCoa_03_SubAccount01Dao(Coa_03_SubAccount01Dao coa_03_SubAccount01Dao) {
		this.coa_03_SubAccount01Dao = coa_03_SubAccount01Dao;
	}

	public Coa_05_AccountMasterDao getCoa_05_AccountMasterDao() {
		return coa_05_AccountMasterDao;
	}

	public void setCoa_05_AccountMasterDao(Coa_05_AccountMasterDao coa_05_AccountMasterDao) {
		this.coa_05_AccountMasterDao = coa_05_AccountMasterDao;
	} 
}
