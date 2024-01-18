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
import com.pyramix.web.common.GFCBaseController;

public class Coa_02_AccountGroupController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6306044130996806583L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	
	private Listbox coaGroupListbox;
	
	private List<Coa_02_AccountGroup> coa_02_AccountGroupList;
	private ListModelList<Coa_02_AccountGroup> coa_02_AccountGroupListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private final int ZERO_PADDED_DIGIT = 3;
	
	private static final Logger log = Logger.getLogger(Coa_02_AccountGroupController.class);
	
	public void onCreate$coaGroupPanel(Event event) throws Exception {
		log.info("coaGroupPanel created");
		
		listCoaGroup();
	}
	
	private void listCoaGroup() throws Exception {
		coa_02_AccountGroupList = getCoa_02_AccountGroupDao().findAllCoa_02_AccountGroup();
		coa_02_AccountGroupList.sort((a1,a2) -> {
			return ((Integer)a1.getTypeCoaNumber()).compareTo(
					a2.getTypeCoaNumber());
		});
		
		coa_02_AccountGroupListModelList = 
				new ListModelList<Coa_02_AccountGroup>(coa_02_AccountGroupList);
		
		coaGroupListbox.setModel(coa_02_AccountGroupListModelList);
		coaGroupListbox.setItemRenderer(getCoa_02_AccountGroupListItemRenderer());
	}

	private ListitemRenderer<Coa_02_AccountGroup> getCoa_02_AccountGroupListItemRenderer() {
		
		return new ListitemRenderer<Coa_02_AccountGroup>() {
			
			@Override
			public void render(Listitem item, Coa_02_AccountGroup accountGroup, int index) throws Exception {
				Listcell lc;
				
				// Account Type Name
				lc = initAccountTypeName(new Listcell(), accountGroup);
				lc.setParent(item);

				// Account Group No
				lc = initAccountGroupNumber(new Listcell(), accountGroup);
				lc.setParent(item);
				
				// Account Group Name
				lc = intAccountGroupName(new Listcell(), accountGroup);
				lc.setParent(item);
				
				// COA
				lc = initCOA(new Listcell(), accountGroup);
				lc.setParent(item);
				
				// save or edit
				lc = initSaveOrEdit(new Listcell(), accountGroup);
				lc.setParent(item);
				
				item.setValue(accountGroup);
			}

			private Listcell initAccountGroupNumber(Listcell listcell, Coa_02_AccountGroup accountGroup) {
				if (accountGroup.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountGroup
					Intbox intbox = new Intbox();
					intbox.setWidth("60px");
					intbox.setValue(accountGroup.getAccountGroupNumber());
					intbox.setParent(listcell);					
				} else {
					// existing accountGroup
					listcell.setLabel(String.valueOf(accountGroup.getAccountGroupNumber()));
				}
				
				return listcell;
			}

			private Listcell initAccountTypeName(Listcell listcell, Coa_02_AccountGroup accountGroup) throws Exception {
				if (accountGroup.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountGroup -- allows user to select AccountType
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setPlaceholder("Select...");
					combobox.setParent(listcell);
					// setup comboitems
					setupComboitemOfAccountTypes(combobox);
		
				} else {
					// existing accountGroup
					Coa_02_AccountGroup proxyAccountGroup = 
							getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(accountGroup.getId());
					listcell.setLabel(proxyAccountGroup.getAccountType().getAccountTypeNumber()
							+"-"+proxyAccountGroup.getAccountType().getAccountTypeName());
				}
				
				
				return listcell;
			}
			
			private Listcell intAccountGroupName(Listcell listcell, Coa_02_AccountGroup accountGroup) {
				if (accountGroup.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountGroup
					Textbox textbox = new Textbox();
					textbox.setWidth("140px");
					textbox.setValue("");
					textbox.setPlaceholder("New AccountGroup Name...");
					textbox.setParent(listcell);
				} else {
					// existing accountGroup
					listcell.setStyle("white-space:nowrap;");
					listcell.setLabel(accountGroup.getAccountGroupName());
				}
				
				return listcell;
			}
			
			private Listcell initCOA(Listcell listcell, Coa_02_AccountGroup accountGroup) throws Exception {
				if (accountGroup.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY - no modif
					
				} else {
					// existing accountGroup
					Coa_02_AccountGroup proxyAccountGroup = 
							getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(accountGroup.getId());

					listcell.setLabel(proxyAccountGroup.getAccountType().getAccountTypeNumber()
							+"."+accountGroup.getAccountGroupNumber());
				}
				return listcell;
			}
			
			private Listcell initSaveOrEdit(Listcell listcell, Coa_02_AccountGroup accountGroup) {
				if (accountGroup.getId().compareTo(Long.MIN_VALUE)==0) {
					// new accountGroup - SAVE
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							Coa_02_AccountGroup coa_02_AccountGroup =
									getCoa_02_AccountGroupData(new Coa_02_AccountGroup());
							// save
							getCoa_02_AccountGroupDao().save(coa_02_AccountGroup);
							// re-list
							listCoaGroup();
						}

						private Coa_02_AccountGroup getCoa_02_AccountGroupData(
								Coa_02_AccountGroup coa_02_AccountGroup) {
							// combobox
							Combobox accountTypeCombobox =
									(Combobox) listcell.getParent().getChildren().get(0).getFirstChild();
							log.info("AccountType Combobox : "+accountTypeCombobox.getValue());
							// intbox
							Intbox accountGroupIntbox =
									(Intbox) listcell.getParent().getChildren().get(1).getFirstChild();
							log.info("AccountGroup Intbox : "+accountGroupIntbox.getValue());
							// textbox
							Textbox accountGroupTextbox =
									(Textbox) listcell.getParent().getChildren().get(2).getFirstChild();
							log.info("AccountGroup Textbox : "+accountGroupTextbox.getValue());
							
							// set
							Coa_01_AccountType sel_coa_01_AccountType =
									accountTypeCombobox.getSelectedItem().getValue();
							coa_02_AccountGroup.setAccountGroupNumber(accountGroupIntbox.getValue());
							coa_02_AccountGroup.setAccountType(sel_coa_01_AccountType);
							coa_02_AccountGroup.setAccountGroupName(accountGroupTextbox.getValue());
							coa_02_AccountGroup.setTypeCoaNumber(sel_coa_01_AccountType.getAccountTypeNumber());
							coa_02_AccountGroup.setCreateDate(asDate(todayDate, zoneId));
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							coa_02_AccountGroup.setLastModified(asDateTime(currentDateTime, zoneId));
							
							return coa_02_AccountGroup;
						}
					});
				} else {
					// existing accountGroup - allow to edit
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

						@Override
						public void onEvent(Event event) throws Exception {
							// access the listitem and get the existing accountGroup object
							// Listitem listitem = (Listitem) listcell.getParent();
							// Coa_02_AccountGroup acctGroup = listitem.getValue();
							log.info("Existing accountGroup : "+accountGroup.toString());
							
							if (button.getLabel().compareTo("Modify")==0) {
								modify_Coa_02_AccountGroupData(accountGroup);
								button.setLabel("Update");
							} else {
								//
								Coa_02_AccountGroup modAccountGroup =
										getCoa_02_AccountGroupModifiedData(accountGroup);
								log.info("Update : "+modAccountGroup.toString());
								// update
								getCoa_02_AccountGroupDao().update(modAccountGroup);
								// re-list
								listCoaGroup();
							}
						}

						private void modify_Coa_02_AccountGroupData(Coa_02_AccountGroup accountGroup) throws Exception {
							// combobox - list of accountType
							Combobox combobox = new Combobox();
							combobox.setWidth("140px");
							// setup comboitems of accountTypes
							setupComboitemOfAccountTypes(combobox);
							// select the comboitem
							selectComboitemOfAccountType(accountGroup, combobox);
							// set combobox to lc0
							Listcell lc0 = (Listcell) listcell.getParent().getChildren().get(0);
							lc0.setLabel(null);
							combobox.setParent(lc0);
							combobox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

								@Override
								public void onEvent(Event event) throws Exception {
									// check is this acctGroup currently used in SubAccount01
									Coa_02_AccountGroup coa_02_AccountGroupProxy =
											getCoa_02_AccountGroupDao().findCoa_03_SubAccount01s_ByProxy(accountGroup.getId());
									if (!coa_02_AccountGroupProxy.getSubAccount01s().isEmpty()) {
										// re-select the comboitem to its previous selection
										selectComboitemOfAccountType(accountGroup, combobox);
										// re-list
										listCoaGroup();
										// raise exception
										throw new Exception("Cannot Modify. This account group is currently used by SubAccount01.");
									}									
								}
							});
							
							// intbox
							Intbox intbox = new Intbox();
							intbox.setWidth("60px");
							intbox.setValue(accountGroup.getAccountGroupNumber());
							// set intbox to lc1
							Listcell lc1 = (Listcell) listcell.getParent().getChildren().get(1);
							lc1.setLabel(null);
							intbox.setParent(lc1);
							
							// textbox
							Textbox textbox = new Textbox();
							textbox.setWidth("140px");
							textbox.setValue(accountGroup.getAccountGroupName());
							// set textbox to lc2
							Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
							lc2.setLabel(null);
							textbox.setParent(lc2);
							
							// COA
							Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
							lc3.setLabel(null);
						}

						private void selectComboitemOfAccountType(Coa_02_AccountGroup accountGroup, Combobox combobox) throws Exception {
							// obtain the accountType thru proxy
							Coa_02_AccountGroup acctGroupProxy =
									getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(accountGroup.getId());
							for (Comboitem comboitem : combobox.getItems()) {
								if (comboitem.getLabel().compareTo(acctGroupProxy.getAccountType().getAccountTypeNumber()
										+"-"+acctGroupProxy.getAccountType().getAccountTypeName())==0) {
									combobox.setSelectedItem(comboitem);
									break;
								}
							}
							
						}

						private Coa_02_AccountGroup getCoa_02_AccountGroupModifiedData(
								Coa_02_AccountGroup accountGroup) throws Exception {
							// combobox
							Combobox accountTypeCombobox =
									(Combobox) listcell.getParent().getChildren().get(0).getFirstChild();
							log.info(accountTypeCombobox);
							// intbox
							Intbox accountGroupNumberIntbox =
									(Intbox) listcell.getParent().getChildren().get(1).getFirstChild();
							log.info(accountGroupNumberIntbox);
							// textbox
							Textbox accountGroupNameTextbox =
									(Textbox) listcell.getParent().getChildren().get(2).getFirstChild();
							log.info(accountGroupNameTextbox);
							// set
							Coa_01_AccountType coa_01_AccountType =
									accountTypeCombobox.getSelectedItem().getValue();
							accountGroup.setAccountType(coa_01_AccountType);
							accountGroup.setTypeCoaNumber(coa_01_AccountType.getAccountTypeNumber());
							accountGroup.setAccountGroupNumber(accountGroupNumberIntbox.getValue());
							accountGroup.setAccountGroupName(accountGroupNameTextbox.getValue());
							LocalDateTime currentDateTime = getLocalDateTime(zoneId);
							accountGroup.setLastModified(asDateTime(currentDateTime, zoneId));
							
							Coa_02_AccountGroup accountGroupByProxy =
									getCoa_02_AccountGroupDao().findCoa_03_SubAccount01s_ByProxy(accountGroup.getId());
							List<Coa_03_SubAccount01> subAccount01List =
									accountGroupByProxy.getSubAccount01s();
							subAccount01List.forEach((Coa_03_SubAccount01 s) -> log.info(s.toString()));
							
							if (!subAccount01List.isEmpty()) {
								for (Coa_03_SubAccount01 coa_03_SubAccount01 : subAccount01List) {
									coa_03_SubAccount01.setAccountGroup(accountGroup);
									coa_03_SubAccount01.setGroupCoaNumber(accountGroupNumberIntbox.getValue());
									log.info("set changes to subAccount01 : "+coa_03_SubAccount01);
									
									Coa_03_SubAccount01 coa_03_SubAccount01Proxy =
											getCoa_03_SubAccount01Dao().findCoa_04_SubAccount02s_ByProxy(coa_03_SubAccount01.getId());
									List<Coa_04_SubAccount02> subAccount02List =
											coa_03_SubAccount01Proxy.getSubAccount02s();
									subAccount02List.forEach((Coa_04_SubAccount02 s) -> log.info(s.toString()));
									
									if (!subAccount02List.isEmpty()) {
										for (Coa_04_SubAccount02 coa_04_SubAccount02 : subAccount02List) {
											coa_04_SubAccount02.setGroupCoaNumber(accountGroupNumberIntbox.getValue());
											log.info("set changes to subAccount02 : "+coa_04_SubAccount02.toString());

											Coa_04_SubAccount02 coa_04_SubAccount02ByProxy =
													getCoa_04_SubAccount02Dao().findAccountMastersByProxy(coa_04_SubAccount02.getId());
											List<Coa_05_Master> masterList =
													coa_04_SubAccount02ByProxy.getMasters();
											masterList.forEach((Coa_05_Master m) -> log.info(m.toString()));
											
											if (!masterList.isEmpty()) {
												for (Coa_05_Master coa_05_master : masterList) {
													coa_05_master.setAccountGroup(accountGroup);
													coa_05_master.setGroupCoaNumber(accountGroupNumberIntbox.getValue());
													coa_05_master.setMasterCoaComp(createCoaComp(
															accountGroup.getTypeCoaNumber(),
															accountGroupNumberIntbox.getValue(),
															coa_03_SubAccount01.getSubAccount01Number(),
															coa_04_SubAccount02.getSubAccount02Number(),
															coa_05_master.getMasterCoaNumber()));
													log.info("set change to accountMaster : "+coa_05_master.toString());
												}
												// re-assign
												coa_04_SubAccount02.setMasters(masterList);
											}
										}
										// re-assign
										coa_03_SubAccount01.setSubAccount02s(subAccount02List);
									}
								}
								// re-assign
								accountGroup.setSubAccount01s(subAccount01List);
							}
							
							return accountGroup;
						}
					});
				}
				return listcell;
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
	
	public void onClick$newAccounGroupButton(Event event) throws Exception {
		Coa_02_AccountGroup coa_02_AccountGroup = new Coa_02_AccountGroup();
		coa_02_AccountGroup.setAccountGroupNumber(getNextAccountGroupNumber());
		
		coa_02_AccountGroupListModelList.add(coa_02_AccountGroup);
	}

	private int getNextAccountGroupNumber() {
		if (coa_02_AccountGroupList.size()==0) {
			return 1;
		} else {
			// get the last AccountGroup
			Coa_02_AccountGroup coa_02_AccountGroup =
					coa_02_AccountGroupList.get(coa_02_AccountGroupList.size()-1);
			
			return coa_02_AccountGroup.getAccountGroupNumber() + 1;
		}
	}
	
	private String createCoaComp(int coa_01_AccountType,
			int coa_02_AccountGroup, int coa_03_SubAccount01,
			int coa_04_SubAccount02, int coa_05_MasterNumber) {
	
		String coaComp = "";
		
		coaComp = coa_01_AccountType+"."
				+ coa_02_AccountGroup
				+ coa_03_SubAccount01
				+ coa_04_SubAccount02+"."
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

	public Coa_02_AccountGroupDao getCoa_02_AccountGroupDao() {
		return coa_02_AccountGroupDao;
	}

	public void setCoa_02_AccountGroupDao(Coa_02_AccountGroupDao coa_02_AccountGroupDao) {
		this.coa_02_AccountGroupDao = coa_02_AccountGroupDao;
	}

	public Coa_01_AccountTypeDao getCoa_01_AccountTypeDao() {
		return coa_01_AccountTypeDao;
	}

	public void setCoa_01_AccountTypeDao(Coa_01_AccountTypeDao coa_01_AccountTypeDao) {
		this.coa_01_AccountTypeDao = coa_01_AccountTypeDao;
	}

	public Coa_03_SubAccount01Dao getCoa_03_SubAccount01Dao() {
		return coa_03_SubAccount01Dao;
	}

	public void setCoa_03_SubAccount01Dao(Coa_03_SubAccount01Dao coa_03_SubAccount01Dao) {
		this.coa_03_SubAccount01Dao = coa_03_SubAccount01Dao;
	}

	public Coa_04_SubAccount02Dao getCoa_04_SubAccount02Dao() {
		return coa_04_SubAccount02Dao;
	}

	public void setCoa_04_SubAccount02Dao(Coa_04_SubAccount02Dao coa_04_SubAccount02Dao) {
		this.coa_04_SubAccount02Dao = coa_04_SubAccount02Dao;
	}
	
	
}
