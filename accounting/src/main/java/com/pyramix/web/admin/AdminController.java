package com.pyramix.web.admin;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Label;

import com.pyramix.domain.voucher.VoucherJournal;
import com.pyramix.persistence.voucher.dao.VoucherJournalDao;
import com.pyramix.web.common.GFCBaseController;

public class AdminController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4986270943283931280L;

	private VoucherJournalDao voucherJournalDao;
	
	private Label resetVoucherJournalStatusLabel;
	
	private static final Logger log = Logger.getLogger(AdminController.class);
	public void onCreate$adminPanel(Event event) throws Exception {
		log.info("adminPanel Created");
	}
	
	public void onClick$resetVoucherJournalButton(Event event) throws Exception {
		log.info("resetVoucherJournalButton click...");
		resetVoucherJournalStatusLabel.setValue("Deleting...");
		
		List<VoucherJournal> voucherJournalList = 
				voucherJournalDao.findAllVoucherJournal();
		Iterator<VoucherJournal> itList = voucherJournalList.iterator();
		while (itList.hasNext()) {
			VoucherJournal voucherJournal = (VoucherJournal) itList.next();

			// delete
			voucherJournalDao.delete(voucherJournal);			
		}
		
		resetVoucherJournalStatusLabel.setValue("");

	}

	public VoucherJournalDao getVoucherJournalDao() {
		return voucherJournalDao;
	}

	public void setVoucherJournalDao(VoucherJournalDao voucherJournalDao) {
		this.voucherJournalDao = voucherJournalDao;
	}
}
