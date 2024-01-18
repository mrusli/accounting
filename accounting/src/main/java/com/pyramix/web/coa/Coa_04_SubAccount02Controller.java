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

public class Coa_04_SubAccount02Controller extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5198527336046004378L;

	private Coa_01_AccountTypeDao coa_01_AccountTypeDao;
	private Coa_02_AccountGroupDao coa_02_AccountGroupDao; 
	private Coa_03_SubAccount01Dao coa_03_SubAccount01Dao;
	private Coa_04_SubAccount02Dao coa_04_SubAccount02Dao;
	
	private Listbox coaSubAccount02Listbox;
	
	private List<Coa_04_SubAccount02> coa_04_SubAccount02List;
	private ListModelList<Coa_04_SubAccount02> coa_04_SubAccount02ListModelList;
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private static final Logger log = Logger.getLogger(Coa_04_SubAccount02Controller.class);
	
	public void onCreate$coaSubAccount02Panel(Event event) throws Exception {
		log.info("coaSubAccount02Panel created");
		
		listCoaSubAccount02();
	}
	
	private void listCoaSubAccount02() throws Exception {
		coa_04_SubAccount02List = getCoa_04_SubAccount02Dao().findAllCoa_04_SubAccount02();
		Comparator<Coa_04_SubAccount02> compareAllAccounts =
				Comparator.comparing(Coa_04_SubAccount02::getTypeCoaNumber)
					.thenComparing(Coa_04_SubAccount02::getGroupCoaNumber)
					.thenComparing(Coa_04_SubAccount02::getSubAccount01CoaNumber)
					.thenComparing(Coa_04_SubAccount02::getSubAccount02Number);
		coa_04_SubAccount02List.sort(compareAllAccounts);
		
		coa_04_SubAccount02ListModelList = 
				new ListModelList<Coa_04_SubAccount02>(coa_04_SubAccount02List);
		
		coaSubAccount02Listbox.setModel(coa_04_SubAccount02ListModelList);
		coaSubAccount02Listbox.setItemRenderer(getCoa_04_SubAccount02ListItemRenderer());
	}

	private ListitemRenderer<Coa_04_SubAccount02> getCoa_04_SubAccount02ListItemRenderer() {
		
		return new ListitemRenderer<Coa_04_SubAccount02>() {
			
			Coa_03_SubAccount01 coa_03_SubAccount01ByProxy;
			Coa_02_AccountGroup coa_02_AccountGroupByProxy;
			Coa_01_AccountType coa_01_AccountTypeByProxy;
			
			@Override
			public void render(Listitem item, Coa_04_SubAccount02 subAccount02, int index) throws Exception {
				Listcell lc;
				
				//	AccountTypeName
				lc = initAccountType(new Listcell(), subAccount02);
				lc.setParent(item);
				
				//	AccountGroupName
				lc = initAccountGroup(new Listcell(), subAccount02);
				lc.setParent(item);
				
				//	SubAccount01Name
				lc = initSubAccount01(new Listcell(), subAccount02);
				lc.setParent(item);
				
				//	SubAccount_02 Number
				lc = initSubAccount_02_Number(new Listcell(), subAccount02);
				lc.setParent(item);
				
				//	SubAccount_02 Name
				lc = initSubAccount_02_Name(new Listcell(), subAccount02);
				lc.setParent(item);
				
				//	COA
				lc = initCOA(new Listcell(), subAccount02);
				lc.setParent(item);
				
				//	Mod
				lc = initSaveOrEdit(new Listcell(), subAccount02);
				lc.setParent(item);
				
				item.setValue(subAccount02);
				
			}

			private Listcell initAccountType(Listcell listcell, Coa_04_SubAccount02 subAccount02) throws Exception {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY -- no modify
					// -- displayed only upon selection of subAccount01
					// -- user NOT ALLOWED to change accountType					
				} else {
					coa_03_SubAccount01ByProxy =
							getCoa_03_SubAccount01ByProxy(subAccount02);
					coa_02_AccountGroupByProxy =
							getCoa_02_AccountGroupByProxy(coa_03_SubAccount01ByProxy);
					coa_01_AccountTypeByProxy =
							getCoa_01_AccountTypeByProxy(coa_02_AccountGroupByProxy);

					listcell.setLabel(coa_01_AccountTypeByProxy.getAccountTypeNumber()
							+"-"+coa_01_AccountTypeByProxy.getAccountTypeName());
				}
				
				return listcell;
			}

			private Listcell initAccountGroup(Listcell listcell, Coa_04_SubAccount02 subAccount02) throws Exception {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY -- no modify
					// -- displayed only upon selection of subAccount01
					// -- user NOT ALLOWED to change accountType										
				} else {
					coa_03_SubAccount01ByProxy =
							getCoa_03_SubAccount01ByProxy(subAccount02);
					coa_02_AccountGroupByProxy =
							getCoa_02_AccountGroupByProxy(coa_03_SubAccount01ByProxy);

					listcell.setStyle("white-space:nowrap;");
					listcell.setLabel(coa_02_AccountGroupByProxy.getAccountGroupNumber()
							+"-"+coa_02_AccountGroupByProxy.getAccountGroupName());
				}
				
				return listcell;
			}

			private Listcell initSubAccount01(Listcell listcell, Coa_04_SubAccount02 subAccount02) throws Exception {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// user selects existing subAccount01
					Combobox combobox = new Combobox();
					combobox.setWidth("140px");
					combobox.setPlaceholder("Select...");
					combobox.setParent(listcell);
					// setup comboitems
					setupCombobitemOfSubAccount01(combobox);
					// event
					combobox.addEventListener(Events.ON_SELECT, onSubAccount01ComboboxSelect(listcell, subAccount02));
				} else {
					coa_03_SubAccount01ByProxy =
							getCoa_03_SubAccount01ByProxy(subAccount02);

					listcell.setStyle("white-space:nowrap;");
					listcell.setLabel(coa_03_SubAccount01ByProxy.getSubAccount01Number()
							+"-"+coa_03_SubAccount01ByProxy.getSubAccount01Name());
				}
				
				return listcell;
			}

			private void setupCombobitemOfSubAccount01(Combobox combobox) throws Exception {
				// get the list of subAccount01
				List<Coa_03_SubAccount01> subAccount01List =
						getCoa_03_SubAccount01Dao().findAllCoa_03SubAccount01();
				// sort
				Comparator<Coa_03_SubAccount01> compareAllAccounts =
						Comparator.comparing(Coa_03_SubAccount01::getTypeCoaNumber)
							.thenComparing(Coa_03_SubAccount01::getGroupCoaNumber)
							.thenComparing(Coa_03_SubAccount01::getSubAccount01Number);
				subAccount01List.sort(compareAllAccounts);
				// setup comboitems
				Comboitem comboitem;
				for (int i = 0; i < subAccount01List.size(); i++) {
					Coa_03_SubAccount01 subAccount01 =
							subAccount01List.get(i);
					comboitem = new Comboitem();
					comboitem.setLabel(subAccount01.getSubAccount01Number()
							+"-"+subAccount01.getSubAccount01Name());
					comboitem.setValue(subAccount01);
					comboitem.setParent(combobox);
				}
			}

			private EventListener<Event> onSubAccount01ComboboxSelect(Listcell listcell,
					Coa_04_SubAccount02 subAccount02) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Combobox combobox = (Combobox) event.getTarget();
						
						Coa_03_SubAccount01 selSubAccount01 = combobox.getSelectedItem().getValue();
						Coa_03_SubAccount01 selSubAccount01AccountGroupByProxy =
								getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(selSubAccount01.getId());
						
						Coa_02_AccountGroup selAccountGroup = selSubAccount01AccountGroupByProxy.getAccountGroup();
						// accountGroup listcell
						Listcell accountGroupListcell =
								(Listcell) listcell.getParent().getChildren().get(1);
						accountGroupListcell.setLabel(selAccountGroup.getAccountGroupNumber()
								+"-"+selAccountGroup.getAccountGroupName());
						
						Coa_02_AccountGroup selAccountGroupAccountTypeByProxy =
								getCoa_02_AccountGroupDao().findCoa_01_AccountType_ByProxy(selAccountGroup.getId());
						
						Coa_01_AccountType selAccountType = selAccountGroupAccountTypeByProxy.getAccountType();
						// accountType listcell
						Listcell accountTypeListcell =
								(Listcell) listcell.getParent().getChildren().get(0);
						accountTypeListcell.setLabel(selAccountType.getAccountTypeNumber()
								+"-"+selAccountType.getAccountTypeName());
						
					}
				};
			}			
			
			private Listcell initSubAccount_02_Number(Listcell listcell, Coa_04_SubAccount02 subAccount02) {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// new subAccount02
					Intbox intbox = new Intbox();
					intbox.setWidth("60px");
					intbox.setValue(subAccount02.getSubAccount02Number());
					intbox.setParent(listcell);
				} else {
					listcell.setLabel(String.valueOf(subAccount02.getSubAccount02Number()));
				}
				
				return listcell;
			}

			private Listcell initSubAccount_02_Name(Listcell listcell, Coa_04_SubAccount02 subAccount02) {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// new subAccount02
					Textbox textbox = new Textbox();
					textbox.setWidth("140px");
					textbox.setValue("");
					textbox.setPlaceholder("New SubAccount02 Name...");
					textbox.setParent(listcell);					
				} else {
					listcell.setStyle("white-space:nowrap;");
					listcell.setLabel(subAccount02.getSubAccount02Name());
				}
				
				return listcell;
			}

			private Listcell initCOA(Listcell listcell, Coa_04_SubAccount02 subAccount02) {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// READ ONLY - no modif
					
				} else {
					listcell.setLabel(coa_01_AccountTypeByProxy.getAccountTypeNumber()
							+"."
							+coa_02_AccountGroupByProxy.getAccountGroupNumber()
							+coa_03_SubAccount01ByProxy.getSubAccount01Number()
							+subAccount02.getSubAccount02Number());
				}
				
				return listcell;
			}

			private Listcell initSaveOrEdit(Listcell listcell, Coa_04_SubAccount02 subAccount02) {
				if (subAccount02.getId().compareTo(Long.MIN_VALUE)==0) {
					// new subAccount01 - SAVE
					Button button = new Button();
					button.setLabel("Save");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onSaveButtonClick(listcell, subAccount02));					
				} else {
					Button button = new Button();
					button.setLabel("Modify");
					button.setParent(listcell);
					button.addEventListener(Events.ON_CLICK, onModifyButtonClick(listcell, subAccount02));
					
				}

				return listcell;
			}

			private EventListener<Event> onSaveButtonClick(Listcell listcell,
					Coa_04_SubAccount02 subAccount02) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						Coa_04_SubAccount02 coa_04_SubAccount02 =
								getCoa_04_SubAccountData(subAccount02);
						// save
						getCoa_04_SubAccount02Dao().save(coa_04_SubAccount02);
						// re-list
						listCoaSubAccount02();
					}

					private Coa_04_SubAccount02 getCoa_04_SubAccountData(Coa_04_SubAccount02 subAccount02) throws Exception {
						// combobox
						Combobox subAccount01Combobox =
								(Combobox) listcell.getParent().getChildren().get(2).getFirstChild();
						log.info("SubAccount01 combobox : "+subAccount01Combobox.getSelectedItem().getValue());
						// intbox
						Intbox subAccount02Intbox =
								(Intbox) listcell.getParent().getChildren().get(3).getFirstChild();
						log.info("SubAccount02 intbox : "+subAccount02Intbox.getValue());
						// textbox
						Textbox subAccount02Textbox =
								(Textbox) listcell.getParent().getChildren().get(4).getFirstChild();
						log.info("SubAccount02 textbox : "+subAccount02Textbox.getValue());
						
						// set
						Coa_03_SubAccount01 sel_coa_03_SubAccount01 =
								subAccount01Combobox.getSelectedItem().getValue();
						Coa_02_AccountGroup sel_coa_02_AccountGrup =
								getCoa_02_AccountGroupByProxy(sel_coa_03_SubAccount01);
						Coa_01_AccountType sel_coa_01_AccountType =
								getCoa_01_AccountTypeByProxy(sel_coa_02_AccountGrup);
						subAccount02.setSubAccount01(sel_coa_03_SubAccount01);
						subAccount02.setSubAccount01CoaNumber(sel_coa_03_SubAccount01.getSubAccount01Number());
						subAccount02.setGroupCoaNumber(sel_coa_02_AccountGrup.getAccountGroupNumber());
						subAccount02.setTypeCoaNumber(sel_coa_01_AccountType.getAccountTypeNumber());
						subAccount02.setSubAccount02Number(subAccount02Intbox.getValue());
						subAccount02.setSubAccount02Name(subAccount02Textbox.getValue());
						subAccount02.setCreateDate(asDate(todayDate, zoneId));
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						subAccount02.setLastModified(asDateTime(currentDateTime, zoneId));
						
						return subAccount02;
					}
				};
			}
			
			private EventListener<Event> onModifyButtonClick(Listcell listcell,	Coa_04_SubAccount02 subAccount02) {
				
				return new EventListener<Event>() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						// access the listitem and get the existing subAccount02 object
						// Listitem listitem = (Listitem) listcell.getParent();
						// Coa_04_SubAccount02 subAccount02 = listitem.getValue();
						log.info("SubAccount01 : "+subAccount02.toString());
						
						Button button = (Button) event.getTarget();
						if (button.getLabel().compareTo("Modify")==0) {
							modify_Coa_04_SubAccount02Data(subAccount02);
							button.setLabel("Update");
						} else {
							//
							Coa_04_SubAccount02 modSubAccount02 =
									getCoa_04_SubAccount02ModifiedData(subAccount02);
							log.info("Update : "+modSubAccount02.toString());
							// update
							getCoa_04_SubAccount02Dao().update(modSubAccount02);
							// re-list
							listCoaSubAccount02();
						}
					}

					private void modify_Coa_04_SubAccount02Data(Coa_04_SubAccount02 subAccount02) throws Exception {
						// combobox - list of subAccount01
						Combobox combobox = new Combobox();
						combobox.setWidth("140px");
						// setup comboitems of subAccount01
						setupComboitemOfSubAccount01(subAccount02, combobox);
						// select the comboitem
						selectComboitemOfSubAccount01(subAccount02, combobox);
						Listcell lc2 = (Listcell) listcell.getParent().getChildren().get(2);
						lc2.setLabel(null);
						combobox.setParent(lc2);
						combobox.addEventListener(Events.ON_SELECT, onSubAccount01ComboboxSelect(lc2, subAccount02));
						
						// intbox
						Intbox intbox = new Intbox();
						intbox.setWidth("60px");
						intbox.setValue(subAccount02.getSubAccount02Number());
						// set intbox to lc3
						Listcell lc3 = (Listcell) listcell.getParent().getChildren().get(3);
						lc3.setLabel(null);
						intbox.setParent(lc3);
						
						// textbox
						Textbox textbox = new Textbox();
						textbox.setWidth("140px");
						textbox.setValue(subAccount02.getSubAccount02Name());
						// set textbox to lc4
						Listcell lc4 = (Listcell) listcell.getParent().getChildren().get(4);
						lc4.setLabel(null);
						textbox.setParent(lc4);
						
						// COA
						Listcell lc5 = (Listcell) listcell.getParent().getChildren().get(5);
						lc5.setLabel(null);
					}

					private void setupComboitemOfSubAccount01(Coa_04_SubAccount02 subAccount02, Combobox combobox) throws Exception {
						// get the list of subAccount01
						List<Coa_03_SubAccount01> subAccount01List =
								getCoa_03_SubAccount01Dao().findAllCoa_03SubAccount01();
						// sort
						Comparator<Coa_03_SubAccount01> compareAllAccounts =
								Comparator.comparing(Coa_03_SubAccount01::getTypeCoaNumber)
									.thenComparing(Coa_03_SubAccount01::getGroupCoaNumber)
									.thenComparing(Coa_03_SubAccount01::getSubAccount01Number);
						subAccount01List.sort(compareAllAccounts);
						// setup comboitems
						Comboitem comboitem;
						for (int i = 0; i < subAccount01List.size(); i++) {
							Coa_03_SubAccount01 coa_03_SubAccount01 =
									subAccount01List.get(i);
							comboitem = new Comboitem();
							comboitem.setLabel(coa_03_SubAccount01.getSubAccount01Number()
									+"-"+coa_03_SubAccount01.getSubAccount01Name());
							comboitem.setValue(coa_03_SubAccount01);
							comboitem.setParent(combobox);
						}
					}
					
					private void selectComboitemOfSubAccount01(Coa_04_SubAccount02 subAccount02, Combobox combobox) throws Exception {
						// obtain the subAccount01 thru proxy
						Coa_04_SubAccount02 subAccount02Proxy =
								getCoa_04_SubAccount02Dao().findCoa_03_SubAccount01_ByProxy(subAccount02.getId());
						for (Comboitem comboitem : combobox.getItems()) {
							if (comboitem.getLabel().compareTo(subAccount02Proxy.getSubAccount01().getSubAccount01Number()
									+"-"+subAccount02Proxy.getSubAccount01().getSubAccount01Name())==0) {
								combobox.setSelectedItem(comboitem);
								break;
							}
						}
					}
					
					private Coa_04_SubAccount02 getCoa_04_SubAccount02ModifiedData(Coa_04_SubAccount02 subAccount02) throws Exception {
						// combobox
						Combobox subAccount02Combobox =
								(Combobox) listcell.getParent().getChildren().get(2).getFirstChild();
						log.info(subAccount02Combobox);
						// intbox
						Intbox subAccount02Intbox =
								(Intbox) listcell.getParent().getChildren().get(3).getFirstChild();
						log.info(subAccount02Intbox);
						// textbox
						Textbox subAccount02Textbox =
								(Textbox) listcell.getParent().getChildren().get(4).getFirstChild();
						log.info(subAccount02Textbox);
						//set
						Coa_03_SubAccount01 coa_03_SubAccount01 =
								subAccount02Combobox.getSelectedItem().getValue();
						Coa_02_AccountGroup coa_02_AccountGroup =
								getCoa_02_AccountGroupByProxy(coa_03_SubAccount01);
						Coa_01_AccountType coa_01_AccountType =
								getCoa_01_AccountTypeByProxy(coa_02_AccountGroup);
						subAccount02.setSubAccount01(coa_03_SubAccount01);
						subAccount02.setSubAccount01CoaNumber(coa_03_SubAccount01.getSubAccount01Number());
						subAccount02.setGroupCoaNumber(coa_02_AccountGroup.getAccountGroupNumber());
						subAccount02.setTypeCoaNumber(coa_01_AccountType.getAccountTypeNumber());
						subAccount02.setSubAccount02Number(subAccount02Intbox.getValue());
						subAccount02.setSubAccount02Name(subAccount02Textbox.getValue());
						LocalDateTime currentDateTime = getLocalDateTime(zoneId);
						subAccount02.setLastModified(asDateTime(currentDateTime, zoneId));
						
						// check whether this account is used by accountMaster
						Coa_04_SubAccount02 coa_04_SubAccount02ByProxy =
								getCoa_04_SubAccount02Dao().findAccountMastersByProxy(subAccount02.getId());
						List<Coa_05_Master> masterList =
								coa_04_SubAccount02ByProxy.getMasters();
						masterList.forEach((Coa_05_Master m) -> log.info(m.toString()));
						
						if (!masterList.isEmpty()) {
							for (Coa_05_Master coa_05_Master : masterList) {
								coa_05_Master.setSubAccount02(subAccount02);
								coa_05_Master.setSubaccount02CoaNumber(subAccount02Intbox.getValue());
								log.info("set change to accountMaster : "+coa_05_Master.toString());
							}
							// re-assign
							subAccount02.setMasters(masterList);							
						}
						
						return subAccount02;
					}
				};
			}
		};
	}

	public void onClick$newSubAccount02Button(Event event) throws Exception {
		Coa_04_SubAccount02 coa_04_SubAccount02 = new Coa_04_SubAccount02();
		coa_04_SubAccount02.setSubAccount02Number(getNextSubAccount02Number());
		
		coa_04_SubAccount02ListModelList.add(coa_04_SubAccount02);
	}
	
	private int getNextSubAccount02Number() {
		if (coa_04_SubAccount02List.size()==0) {
			return 1;
		} else {
			// get the last subAccount02
			Coa_04_SubAccount02 coa_04_SubAccount02 =
					coa_04_SubAccount02List.get(coa_04_SubAccount02List.size()-1);
			
			return coa_04_SubAccount02.getSubAccount02Number()+1;			
		}
	}

	private Coa_03_SubAccount01 getCoa_03_SubAccount01ByProxy(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception {
		Coa_04_SubAccount02 coa_04_SubAccount02SubAccount01ByProxy =
				getCoa_04_SubAccount02Dao().findCoa_03_SubAccount01_ByProxy(coa_04_SubAccount02.getId());
		Coa_03_SubAccount01 coa_03_SubAccount01 =
				coa_04_SubAccount02SubAccount01ByProxy.getSubAccount01();
		
		return coa_03_SubAccount01;
	}
	
	private Coa_02_AccountGroup getCoa_02_AccountGroupByProxy(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception {
		Coa_03_SubAccount01 coa_03_SubAccount01AccountGroupByProxy =
				getCoa_03_SubAccount01Dao().findCoa_02_AccountGroup_ByProxy(coa_03_SubAccount01.getId());
		Coa_02_AccountGroup coa_02_accountGroup =
				coa_03_SubAccount01AccountGroupByProxy.getAccountGroup();
		log.info(coa_02_accountGroup.toString());
		
		return coa_02_accountGroup;
	}
	
	private Coa_01_AccountType getCoa_01_AccountTypeByProxy(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception {
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
