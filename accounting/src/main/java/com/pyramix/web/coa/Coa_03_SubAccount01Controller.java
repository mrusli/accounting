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

public class Coa_03_SubAccount01Controller extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6830899745356072145L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao coa_02_AccountGroupDao;
	private Coa_03_SubAccount01Dao coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	
	private Listbox coaSubAccount01Listbox;
	
	private List<Coa_03_SubAccount01> coa_03_SubAccount01List;
	private ListModelList<Coa_03_SubAccount01> coa_03_SubAccount01ListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private final int ZERO_PADDED_DIGIT = 3;
	
	private static final Logger log = Logger.getLogger(Coa_03_SubAccount01Controller.class);
	
	public void onCreate$coaSubAccount01Panel(Event event) throws Exception {
		log.info("coaSubAccount01Panel created");
		
		listCoaSubAccount01();
	}
	
	private void listCoaSubAccount01() throws Exception {
		coa_03_SubAccount01List = getCoa_03_SubAccount01Dao().findAllCoa_03SubAccount01();
		Comparator<Coa_03_SubAccount01> compareAllAccounts =
			Comparator.comparing(Coa_03_SubAccount01::getTypeCoaNumber)
				.thenComparing(Coa_03_SubAccount01::getGroupCoaNumber)
				.thenComparing(Coa_03_SubAccount01::getSubAccount01Number);
		coa_03_SubAccount01List.sort(compareAllAccounts);
		
		coa_03_SubAccount01ListModelList = 
				new ListModelList<Coa_03_SubAccount01>(coa_03_SubAccount01List);
		
		coaSubAccount01Listbox.setModel(coa_03_SubAccount01ListModelList);
		coaSubAccount01Listbox.setItemRenderer(getCoa_03_SubAccount01ListItemRenderer());
	}

	private ListitemRenderer<Coa_03_SubAccount01> getCoa_03_SubAccount01ListItemRenderer() {
		
		return new ListitemRenderer<Coa_03_SubAccount01>() {
			
			Coa_02_AccountGroup coa_02_AccountGroupByProxy;
			Coa_01_AccountType coa_01_AccountTypeByProxy;
			
			@Override
			public void render(Listitem item, Coa_03_SubAccount01 subAccount01, int index) throws Exception {
				Listcell lc;
				
				//	Account Type
				lc = initAccountType(new Listcell(), subAccount01);
				lc.setParent(item);
				
				//	Account Group
				lc = initAccountGroup(new Listcell(), subAccount01);
				lc.setParent(item);

				//	SubAccount01
				lc = initSubAccount_01_Number(new Listcell(), subAccount01);
				lc.setParent(item);

				//	SubAccount01 Name
				lc = initSubAccount_01_Name(new Listcell(), subAccount01);
				lc.setParent(item);
				
				//	COA
				lc = initCOA(new Listcell(), subAccount01);
				lc.setParent(item);
				
				//	Mod
				lc = initSaveOrEdit(new Listcell(), subAccount01);
				lc.setParent(item);

				item.setValue(subAccount01);
			}

			private Listcell initAccountType(Listcell listcell, Coa_03_SubAccount01 subAccount01) throws Exception {
				if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY -- no modify
					// -- displayed only upon selection of accountGroup
					// -- user NOT ALLOWED to change accountType
				} else {
					coa_02_AccountGroupByProxy =
							getCoa_02_AccountGroupByProxy(subAccount01);
					coa_01_AccountTypeByProxy =
							getCoa_01_AccountTypeByProxy(coa_02_AccountGroupByProxy);
										
					listcell.setLabel(coa_01_AccountTypeByProxy.getAccountTypeNumber()
							+"-"+coa_01_AccountTypeByProxy.getAccountTypeName());
				}
				
				return listcell;
			}

			private Listcell initAccountGroup(Listcell listcell, Coa_03_SubAccount01 subAccount01) throws Exception {
				if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
					// user selects existing accountGroup
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setPlaceholder("Select...");
					combobox.setParent(listcell);
					// setup comboitems
					setupComboitemOfAccountGroups(combobox);
					// event
					combobox.addEventListener(Events.ON_SELECT, onAccountGroupComboboxSelect(listcell, subAccount01));
				} else {
					coa_02_AccountGroupByProxy =
							getCoa_02_AccountGroupByProxy(subAccount01);
					
					listcell.setStyle("white-space:nowrap;");
					listcell.setLabel(coa_02_AccountGroupByProxy.getAccountGroupNumber()
							+"-"+coa_02_AccountGroupByProxy.getAccountGroupName());
				}

				return listcell;
			}

			private void setupComboitemOfAccountGroups(Combobox combobox) throws Exception {
				// get the list of account groups
				List<Coa_02_AccountGroup> accountGroupList =
						getCoa_02_AccountGroupDao().findAllCoa_02_AccountGroup();
				// sort
				Comparator<Coa_02_AccountGroup> compareAllAccounts =
						Comparator.comparing(Coa_02_AccountGroup::getTypeCoaNumber)
							.thenComparing(Coa_02_AccountGroup::getAccountGroupNumber);
				accountGroupList.sort(compareAllAccounts);
				// setup comboitems
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

			private EventListener<Event> onAccountGroupComboboxSelect(Listcell listcell, Coa_03_SubAccount01 subAccount01) {

				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Combobox combobox = (Combobox) event.getTarget();
						
						if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
							// do nothing
						} else {
							// check is this subAaccount01 currently used in SubAccount02
							Coa_03_SubAccount01 coa_03_SubAccount01Proxy =
									getCoa_03_SubAccount01Dao().findCoa_04_SubAccount02s_ByProxy(subAccount01.getId());
							if (!coa_03_SubAccount01Proxy.getSubAccount02s().isEmpty()) {
								// re-select
								Coa_03_SubAccount01 subAccount01Proxy =
										getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(subAccount01.getId());
								for (Comboitem comboitem : combobox.getItems()) {
									if (comboitem.getLabel().compareTo(subAccount01Proxy.getAccountGroup().getAccountGroupNumber()
											+"-"+subAccount01Proxy.getAccountGroup().getAccountGroupName())==0) {
										combobox.setSelectedItem(comboitem);
										break;
									}
								}
								// re-list
								listCoaSubAccount01();
								// raise exception
								throw new Exception("Cannot Modify. This SubAccount01 is currently used by SubAccount02");
							}
						}
						
						Coa_02_AccountGroup selAccountGroup = combobox.getSelectedItem().getValue();
						Coa_02_AccountGroup selAccountGroupAccountTypeByProxy =
								getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(selAccountGroup.getId());
						Coa_01_AccountType coa_01_AccountType =
								selAccountGroupAccountTypeByProxy.getAccountType();
						// accountType listcell
						Listcell accountTypeListcell =
								(Listcell) listcell.getParent().getChildren().get(0);
						accountTypeListcell.setLabel(coa_01_AccountType.getAccountTypeNumber()
								+"-"+coa_01_AccountType.getAccountTypeName());
					}
				};
			}

			private Listcell initSubAccount_01_Number(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
					// new subAccount01
					Intbox intbox = new Intbox();
					intbox.setWidth("60px");
					intbox.setValue(subAccount01.getSubAccount01Number());
					intbox.setParent(listcell);
				} else {
					listcell.setLabel(String.valueOf(subAccount01.getSubAccount01Number()));
				}

				return listcell;
			}

			private Listcell initSubAccount_01_Name(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
					// new subAccount01
					Textbox textbox = new Textbox();
					textbox.setWidth("140px");
					textbox.setValue("");
					textbox.setPlaceholder("New SubAccount01 Name...");
					textbox.setParent(listcell);					
				} else {
					listcell.setStyle("white-space:nowrap;");
					listcell.setLabel(subAccount01.getSubAccount01Name());
				}

				return listcell;
			}

			private Listcell initCOA(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY - no modif
					
				} else {
					listcell.setLabel(coa_01_AccountTypeByProxy.getAccountTypeNumber()
							+"."
							+coa_02_AccountGroupByProxy.getAccountGroupNumber()
							+subAccount01.getSubAccount01Number());
				}

				return listcell;
			}

			private Listcell initSaveOrEdit(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				if (subAccount01.getId().compareTo(Long.MIN_VALUE)==0) {
					// new subAccount01 - SAVE
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClick(listcell, subAccount01));
				} else {
					// existing subAccount01 - allow to edit
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClick(listcell, subAccount01));
				}
				
				return listcell;
			}

			private EventListener<Event> onSaveButtonClick(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Coa_03_SubAccount01 coa_03_SubAccount01 =
								getCoa_03_SubAccount01Data(subAccount01);
						// save
						getCoa_03_SubAccount01Dao().save(coa_03_SubAccount01);
						// re-list
						listCoaSubAccount01();
					}

					private Coa_03_SubAccount01 getCoa_03_SubAccount01Data(Coa_03_SubAccount01 subAccount01) throws Exception {
						// combobox
						Combobox accountGroupCombobox =
								(Combobox) listcell.getParent().getChildren().get(1).getFirstChild();
						log.info("AccountGroup combobox : "+accountGroupCombobox.getSelectedItem().getValue());
						// intbox
						Intbox subAccount01Intbox =
								(Intbox) listcell.getParent().getChildren().get(2).getFirstChild();
						log.info("SubAccount01 intbox : "+subAccount01Intbox.getValue());
						// textbox
						Textbox subAccount01Textbox =
								(Textbox) listcell.getParent().getChildren().get(3).getFirstChild();
						log.info("SubAccount01 textbox : "+subAccount01Textbox.getValue());
						
						// set
						Coa_02_AccountGroup sel_coa_02_AccountGroup =
								accountGroupCombobox.getSelectedItem().getValue();
						Coa_01_AccountType sel_coa_01_AccountType =
								getCoa_01_AccountTypeByProxy(sel_coa_02_AccountGroup);
						subAccount01.setAccountGroup(sel_coa_02_AccountGroup);
						subAccount01.setSubAccount01Number(subAccount01Intbox.getValue());
						subAccount01.setSubAccount01Name(subAccount01Textbox.getValue());
						subAccount01.setTypeCoaNumber(sel_coa_01_AccountType.getAccountTypeNumber());
						subAccount01.setGroupCoaNumber(sel_coa_02_AccountGroup.getAccountGroupNumber());
						subAccount01.setCreateDate(asDate(todayDate, zoneId));
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						subAccount01.setLastModified(asDateTime(currentDateTime, zoneId));
						
						return subAccount01;
					}
				};
			}
			
			private EventListener<Event> onModifyButtonClick(Listcell listcell, Coa_03_SubAccount01 subAccount01) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						// access the listitem and get the existing subAccount01 object
						// Listitem listitem = (Listitem) listcell.getParent();
						// Coa_03_SubAccount01 subAccount01 = listitem.getValue();
						log.info("SubAccount01 : "+subAccount01.toString());
						
						Button button = (Button) event.getTarget();
						if (button.getLabel().compareTo("Modify")==0) {
							modify_Coa_03_SubAccount01Data(subAccount01);
							button.setLabel("Update");
						} else {
							//
							Coa_03_SubAccount01 modSubAccount01 =
									getCoa_03_SubAccount01ModifiedData(subAccount01);
							log.info("Update : "+modSubAccount01.toString());
							// update
							getCoa_03_SubAccount01Dao().update(modSubAccount01);
							// re-list
							listCoaSubAccount01();
						}
						
					}

					private void modify_Coa_03_SubAccount01Data(Coa_03_SubAccount01 subAccount01) throws Exception {
						// combobox - list of accountGroup
						Combobox combobox = new Combobox();
						combobox.setWidth("140px");
						// setup comboitems of accountGroup
						setupComboitemOfAccountGroups(combobox);
						// selete the comboitem
						selectComboitemOfAccountGroup(subAccount01, combobox);
						// set combobox to lc1
						Listcell lc1 = (Listcell) listcell.getParent().getChildren().get(1);
						lc1.setLabel(null);
						combobox.setParent(lc1);
						combobox.addEventListener(Events.ON_SELECT, onAccountGroupComboboxSelect(lc1, subAccount01));
						
						// intbox
						Intbox intbox = new Intbox();
						intbox.setWidth("60px");
						intbox.setValue(subAccount01.getSubAccount01Number());
						// set intbox to lc2
						Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
						lc2.setLabel(null);
						intbox.setParent(lc2);
						
						// textbox
						Textbox textbox = new Textbox();
						textbox.setWidth("140px");
						textbox.setValue(subAccount01.getSubAccount01Name());
						// set textbox to lc3
						Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
						lc3.setLabel(null);
						textbox.setParent(lc3);
						
						// COA
						Listcell lc4 = (Listcell) listcell.getParent().getChildren().get(4);
						lc4.setLabel(null);
						
					}

					private void selectComboitemOfAccountGroup(Coa_03_SubAccount01 subAccount01, Combobox combobox) throws Exception {
						// obtain the accountGroup thru proxy
						Coa_03_SubAccount01 subAccount01Proxy =
								getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(subAccount01.getId());
						for (Comboitem comboitem : combobox.getItems()) {
							if (comboitem.getLabel().compareTo(subAccount01Proxy.getAccountGroup().getAccountGroupNumber()
									+"-"+subAccount01Proxy.getAccountGroup().getAccountGroupName())==0) {
								combobox.setSelectedItem(comboitem);
								break;
							}
						}						
					}
					
					private Coa_03_SubAccount01 getCoa_03_SubAccount01ModifiedData(Coa_03_SubAccount01 subAccount01) throws Exception {
						// combobox
						Combobox accountGroupCombobox =
								(Combobox) listcell.getParent().getChildren().get(1).getFirstChild();
						log.info(accountGroupCombobox);
						// intbox
						Intbox subAccount01NumberIntbox =
								(Intbox) listcell.getParent().getChildren().get(2).getFirstChild();
						log.info(subAccount01NumberIntbox);
						// textbox
						Textbox subAccount01NameTextbox =
								(Textbox) listcell.getParent().getChildren().get(3).getFirstChild();
						log.info(subAccount01NameTextbox);
						// set
						Coa_02_AccountGroup coa_02_AccountGroup =
								accountGroupCombobox.getSelectedItem().getValue();
						Coa_01_AccountType coa_01_AccountType =
								getCoa_01_AccountTypeByProxy(coa_02_AccountGroup);
						subAccount01.setAccountGroup(coa_02_AccountGroup);
						subAccount01.setGroupCoaNumber(coa_02_AccountGroup.getAccountGroupNumber());
						subAccount01.setTypeCoaNumber(coa_01_AccountType.getAccountTypeNumber());
						subAccount01.setSubAccount01Number(subAccount01NumberIntbox.getValue());
						subAccount01.setSubAccount01Name(subAccount01NameTextbox.getValue());
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						subAccount01.setLastModified(asDateTime(currentDateTime, zoneId));
						
						Coa_03_SubAccount01 coa_03_SubAccount01Proxy =
								getCoa_03_SubAccount01Dao().findCoa_04_SubAccount02s_ByProxy(subAccount01.getId());
						List<Coa_04_SubAccount02> subAccount02List =
								coa_03_SubAccount01Proxy.getSubAccount02s();
						subAccount02List.forEach((Coa_04_SubAccount02 s) -> log.info(s.toString()));
						
						if (!subAccount02List.isEmpty()) {
							for (Coa_04_SubAccount02 coa_04_SubAccount02 : subAccount02List) {
								coa_04_SubAccount02.setSubAccount01(subAccount01);
								coa_04_SubAccount02.setSubAccount01CoaNumber(subAccount01NumberIntbox.getValue());								
								log.info("set changes to subAccount02 : "+coa_04_SubAccount02.toString());
																
								Coa_04_SubAccount02 coa_04_SubAccount02ByProxy =
										getCoa_04_SubAccount02Dao().findAccountMastersByProxy(coa_04_SubAccount02.getId());
								List<Coa_05_Master> masterList =
										coa_04_SubAccount02ByProxy.getMasters();
								masterList.forEach((Coa_05_Master m) -> log.info(m.toString()));
								
								if (!masterList.isEmpty()) {
									for (Coa_05_Master coa_05_Master : masterList) {
										coa_05_Master.setSubAccount01(subAccount01);
										coa_05_Master.setSubaccount01CoaNumber(subAccount01NumberIntbox.getValue());
										// coa_05_Master.setSubAccount02(coa_04_SubAccount02);
										// coa_05_Master.setSubaccount02CoaNumber(coa_04_SubAccount02.getSubAccount02Number());
										coa_05_Master.setMasterCoaComp(createCoaComp(
												subAccount01.getTypeCoaNumber(),
												subAccount01.getGroupCoaNumber(),
												subAccount01.getSubAccount01Number(),
												coa_04_SubAccount02.getSubAccount02Number(),
												coa_05_Master.getMasterCoaNumber()));
										log.info("set change to accountMaster : "+coa_05_Master.toString());
									}
									// re-assign
									coa_04_SubAccount02.setMasters(masterList);							
								}								
							}
							// re-assign
							subAccount01.setSubAccount02s(subAccount02List);
						}
						
						return subAccount01;
					}
				};
			}			
		};
	}	
	
	public void onClick$newSubAccount01Button(Event event) throws Exception {
		Coa_03_SubAccount01 coa_03_SubAccount01 = new Coa_03_SubAccount01();
		coa_03_SubAccount01.setSubAccount01Number(getNextSubAccount01Number());
		
		coa_03_SubAccount01ListModelList.add(coa_03_SubAccount01);
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		log.info("Cancel button clicked.");
		
		listCoaSubAccount01();
	}
	
	private int getNextSubAccount01Number() {
		if (coa_03_SubAccount01List.size()==0) {
			return 1;
		} else {
			// get the last subAccount01
			Coa_03_SubAccount01 coa_03_SubAccount01 =
					coa_03_SubAccount01List.get(coa_03_SubAccount01List.size()-1);
			
			return coa_03_SubAccount01.getSubAccount01Number()+1;
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
	
	private Coa_02_AccountGroup getCoa_02_AccountGroupByProxy(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception {
		Coa_03_SubAccount01 coa_03_SubAccount01AccountGroupByProxy =
				getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(coa_03_SubAccount01.getId());
		Coa_02_AccountGroup coa_02_accountGroup =
				coa_03_SubAccount01AccountGroupByProxy.getAccountGroup();
		log.info(coa_02_accountGroup.toString());
		
		return coa_02_accountGroup;
	}
	
	private Coa_01_AccountType getCoa_01_AccountTypeByProxy(Coa_02_AccountGroup coa_02_accountGroup) throws Exception {
		Coa_02_AccountGroup coa_02_AccountGroupAccoutTypeByProxy =
				getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(coa_02_accountGroup.getId());
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
