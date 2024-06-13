package com.pyramix.web.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.creditcard.CreditCardCoa;
import com.pyramix.persistence.creditcardcoa.dao.CreditCardCoaDao;
import com.pyramix.web.common.GFCBaseController;

public class AdminController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4986270943283931280L;

	private CreditCardCoaDao creditCardCoaDao;
	
	private Listbox creditCardCoaListbox;
	
	private ZoneId zoneId = getZoneId();
	private LocalDate todayDate = getLocalDate(zoneId);
	
	private static final Logger log = Logger.getLogger(AdminController.class);
	
	public void onCreate$adminPanel(Event event) throws Exception {
		log.info("adminPanel Created");
		
		listCreditCardCoa();
	}

	private void listCreditCardCoa() throws Exception {
		List<CreditCardCoa> creditCardCoaList =
				getCreditCardCoaDao().findAllCreditCardCoa();
		
		creditCardCoaList.sort((c1, c2) -> {
			return c1.getMasterCoaComp().compareTo(c2.getMasterCoaComp());
		});
		
		ListModelList<CreditCardCoa> creditCardCoaListModelList =
				new ListModelList<CreditCardCoa>(creditCardCoaList);
		
		creditCardCoaListbox.setModel(creditCardCoaListModelList);
		creditCardCoaListbox.setItemRenderer(getCreditCardCoaListitemRenderer());
	}

	private ListitemRenderer<CreditCardCoa> getCreditCardCoaListitemRenderer() {
		
		return new ListitemRenderer<CreditCardCoa>() {
			
			@Override
			public void render(Listitem item, CreditCardCoa creditCardCoa, int index) throws Exception {
				Listcell lc;
				
				// Acc Master
				lc = new Listcell(creditCardCoa.getMasterCoaComp());
				lc.setParent(item);
				
				// Acc Master Name
				lc = new Listcell(creditCardCoa.getMasterCoaName());
				lc.setParent(item);
				
				// Active
				lc = new Listcell(creditCardCoa.isActive()? "Active" : "Non-Active");
				lc.setParent(item);
				
				// Remove
				lc = initRemove(new Listcell(), creditCardCoa);
				lc.setParent(item);
				
			}

			private Listcell initRemove(Listcell listcell, CreditCardCoa creditCardCoa) {
				Button button = new Button();
				button.setLabel("Remove");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Remove Button Clicked...");
						
						// remove
						getCreditCardCoaDao().delete(creditCardCoa);
						
						// re-list
						listCreditCardCoa();
						
					}
				});
				
				return listcell;
			}
		};
	}

	public void onClick$addAccountMasterButton(Event event) throws Exception {
		
		Window window = 
				(Window) Executions.createComponents("~./secure/coa/Coa_05_MasterDialog.zul", 
						null, null);
		window.doModal();
		window.addEventListener(Events.ON_SELECT, new EventListener<Event>() {

			@Override
			public void onEvent(Event event) throws Exception {
				Coa_05_Master masterCoa = (Coa_05_Master) event.getData();
				log.info(masterCoa.toString());
				
				// create a new creditCardCoa object
				CreditCardCoa creditCardCoa =  createMasterCoaCreditCard(masterCoa);
				
				// save
				getCreditCardCoaDao().save(creditCardCoa);
				
				// re-list
				listCreditCardCoa();
			}
		});
		
	}
	
	protected CreditCardCoa createMasterCoaCreditCard(Coa_05_Master masterCoa) {
		CreditCardCoa creditCardCoa = new CreditCardCoa();
		
		creditCardCoa.setCreateDate(asDate(todayDate, zoneId));
		LocalDateTime currentDateTime = getLocalDateTime(zoneId);
		creditCardCoa.setLastModified(asDateTime(currentDateTime, zoneId));
		creditCardCoa.setMasterCoaName(masterCoa.getMasterCoaName());
		creditCardCoa.setTypeCoaNumber(masterCoa.getTypeCoaNumber());
		creditCardCoa.setGroupCoaNumber(masterCoa.getSubaccount01CoaNumber());
		creditCardCoa.setSubaccount01CoaNumber(masterCoa.getSubaccount01CoaNumber());
		creditCardCoa.setSubaccount02CoaNumber(masterCoa.getSubaccount02CoaNumber());
		creditCardCoa.setMasterCoaNumber(masterCoa.getMasterCoaNumber());
		creditCardCoa.setMasterCoaComp(masterCoa.getMasterCoaComp());
		creditCardCoa.setCreditAccount(masterCoa.isCreditAccount());
		creditCardCoa.setRestricted(masterCoa.isRestricted());
		// default set to active
		creditCardCoa.setActive(true);
		// set ref
		creditCardCoa.setCoa_05_Master(masterCoa);
		
		return creditCardCoa;
	}
	
	public CreditCardCoaDao getCreditCardCoaDao() {
		return creditCardCoaDao;
	}

	public void setCreditCardCoaDao(CreditCardCoaDao creditCardCoaDao) {
		this.creditCardCoaDao = creditCardCoaDao;
	}
	
}
