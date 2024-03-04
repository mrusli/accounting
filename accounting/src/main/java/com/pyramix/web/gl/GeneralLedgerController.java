package com.pyramix.web.gl;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;
import com.pyramix.web.common.GFCBaseController;

public class GeneralLedgerController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4132805928767688796L;

	private GeneralLedgerDao generalLedgerDao;
	
	private Listbox generalLedgerListbox;
	
	private List<GeneralLedger> generalLedgers;
	
	private static final Logger log = Logger.getLogger(GeneralLedgerController.class);
	
	public void onCreate$generalLedgerPanel(Event event) throws Exception {
		log.info("generalLedgerPanel created");
		
		listGeneralLedgers();
	}

	private void listGeneralLedgers() throws Exception {
		generalLedgers =
				getGeneralLedgerDao().findAllGeneralLedger();
		
		ListModelList<GeneralLedger> generalLedgerModelList =
				new ListModelList<GeneralLedger>(generalLedgers);
		generalLedgerListbox.setModel(generalLedgerModelList);
		generalLedgerListbox.setItemRenderer(getGeneralLedgerListitemRenderer());
	}

	private ListitemRenderer<GeneralLedger> getGeneralLedgerListitemRenderer() {
		
		return new ListitemRenderer<GeneralLedger>() {
			
			@Override
			public void render(Listitem item, GeneralLedger gl, int index) throws Exception {
				Listcell lc;
				
				// Date
				lc = new Listcell(dateToStringDisplay(asLocalDate(gl.getTransactionDate()),
						"yyyy-MM-dd", getLocale()));
				lc.setParent(item);
				
				// COA
				lc = new Listcell(gl.getMasterCoa().getMasterCoaComp()+"-"
						+gl.getMasterCoa().getMasterCoaName());
				lc.setParent(item);
				
				// Description
				lc = new Listcell(gl.getTransactionDescription());
				lc.setParent(item);
				
				// Debit(Rp.)
				lc = new Listcell(toDecimalFormat(gl.getDebitAmount(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
				
				// Credit(Rp.)
				lc = new Listcell(toDecimalFormat(gl.getCreditAmount(), getLocale(), "###.###.###,-"));
				lc.setParent(item);
			}
		};
	}

	public GeneralLedgerDao getGeneralLedgerDao() {
		return generalLedgerDao;
	}

	public void setGeneralLedgerDao(GeneralLedgerDao generalLedgerDao) {
		this.generalLedgerDao = generalLedgerDao;
	}
}
