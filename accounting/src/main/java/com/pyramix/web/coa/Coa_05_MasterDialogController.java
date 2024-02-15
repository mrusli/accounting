package com.pyramix.web.coa;

import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.web.common.GFCBaseController;

public class Coa_05_MasterDialogController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -304354700323146562L;

	private Coa_05_AccountMasterDao coa_05_AccountMasterDao;
	
	private Window coa_05_MasterDialogWin;
	private Listbox coa_05_MasterListbox;
	private Label statusLabel;
	
	private List<Coa_05_Master> coaMasterList;
	private ListModelList<Coa_05_Master> coaMasterListModelList;
	private boolean creditAccount;
	
	private static final Logger log = Logger.getLogger(Coa_05_MasterDialogController.class);
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		creditAccount = (boolean) arg.get("creditAccount");
	}

	public void onCreate$coa_05_MasterDialogWin(Event event) throws Exception {
		log.info("coa_05_MasterDialogWin created.");
		
		// reset status
		statusLabel.setValue("[Select] Click COA to select");
		
		listCoaMaster();
	}
	
	private void listCoaMaster() throws Exception {
		coaMasterList = 
				getCoa_05_AccountMasterDao().find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp(creditAccount);
		
		coaMasterListModelList = new ListModelList<Coa_05_Master>(coaMasterList);
		
		coa_05_MasterListbox.setModel(coaMasterListModelList);
		coa_05_MasterListbox.setItemRenderer(getCoa_05_MasterListitemRenderer());
	}

	private ListitemRenderer<Coa_05_Master> getCoa_05_MasterListitemRenderer() {
		
		return new ListitemRenderer<Coa_05_Master>() {
			
			@Override
			public void render(Listitem item, Coa_05_Master master, int index) throws Exception {
				Listcell lc;
				
				// No.COA
				lc = new Listcell(master.getMasterCoaComp());
				lc.setParent(item);
				
				// Nama
				lc = new Listcell(master.getMasterCoaName());
				lc.setParent(item);
				
				item.setValue(master);
			}
		};
	}
	
	public void onSelect$coa_05_MasterListbox(Event event) throws Exception {
		Listitem item = coa_05_MasterListbox.getSelectedItem();
		Coa_05_Master master = item.getValue();
		log.info("Selected: "+master.getMasterCoaComp()+"-"
				+master.getMasterCoaName());
		
		statusLabel.setValue("[Select] "+master.getMasterCoaComp()+"-"
				+master.getMasterCoaName()+" "
				+"Selected");
	}

	public void onClick$selectButton(Event event) throws Exception {
		if (coa_05_MasterListbox.getSelectedItem()==null) {
			throw new Exception("COA belum terpilih.  Pilih COA sebelum klik tombol.");
		}
		// selected
		Coa_05_Master master = coa_05_MasterListbox.getSelectedItem().getValue();
		log.info("Selected Master COA : "+master.toString());
		
		Events.sendEvent(Events.ON_SELECT, coa_05_MasterDialogWin, master);
		
		coa_05_MasterDialogWin.detach();
	}
	
	public void onClick$cancelButton(Event event) throws Exception {
		coa_05_MasterDialogWin.detach();
	}

	public Coa_05_AccountMasterDao getCoa_05_AccountMasterDao() {
		return coa_05_AccountMasterDao;
	}

	public void setCoa_05_AccountMasterDao(Coa_05_AccountMasterDao coa_05_AccountMasterDao) {
		this.coa_05_AccountMasterDao = coa_05_AccountMasterDao;
	}
}
