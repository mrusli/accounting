package com.pyramix.web.admin;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	
	private static final Logger log = Logger.getLogger(AdminController.class);
	
	public void onCreate$adminPanel(Event event) throws Exception {
		log.info("adminPanel Created");
		
		listCreditCardCoa();
	}

	private void listCreditCardCoa() throws Exception {
		List<CreditCardCoa> creditCardCoaList =
				getCreditCardCoaDao().findAllCreditCardCoa();
		
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
				lc = initRemove(new Listcell());
				lc.setParent(item);
				
			}

			private Listcell initRemove(Listcell listcell) {
				Button button = new Button();
				button.setLabel("Remove");
				button.setParent(listcell);
				button.addEventListener(Events.ON_CLICK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						log.info("Remove Button Clicked...");
						
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
			}
		});
		
	}
	
	public CreditCardCoaDao getCreditCardCoaDao() {
		return creditCardCoaDao;
	}

	public void setCreditCardCoaDao(CreditCardCoaDao creditCardCoaDao) {
		this.creditCardCoaDao = creditCardCoaDao;
	}
	
}
