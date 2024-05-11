package com.pyramix.web.admin;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;

import com.pyramix.persistence.voucher.dao.VoucherJournalDao;
import com.pyramix.web.common.GFCBaseController;

public class AdminController extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4986270943283931280L;

	private VoucherJournalDao voucherJournalDao;
	
	private static final Logger log = Logger.getLogger(AdminController.class);
	
	public void onCreate$adminPanel(Event event) throws Exception {
		log.info("adminPanel Created");
	}
	
	public VoucherJournalDao getVoucherJournalDao() {
		return voucherJournalDao;
	}

	public void setVoucherJournalDao(VoucherJournalDao voucherJournalDao) {
		this.voucherJournalDao = voucherJournalDao;
	}
}
