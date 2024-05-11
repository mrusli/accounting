package com.pyramix.web.serial;

import java.util.Date;

import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherType;
import com.pyramix.persistence.voucher.dao.VoucherSerialNumberDao;
import com.pyramix.web.common.GFCBaseController;

public class SerialNumberGenerator extends GFCBaseController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8583275231280373663L;

	private VoucherSerialNumberDao voucherSerialNumberDao;

	// private static final Logger log = Logger.getLogger(SerialNumberGenerator.class);
	
	public int getSerialNumber(VoucherType voucherType, Date currentDate) throws Exception {
		int serialNum = 1;
		
		VoucherSerialNumber voucherSerNum = 
			getVoucherSerialNumberDao().findLastVoucherSerialNumberByVoucherType(voucherType);
		// log.info("Previous serialNumber : "+voucherSerNum==null ? 1 : voucherSerNum.toString());
		
		if (voucherSerNum!=null) {
			Date lastDate = voucherSerNum.getSerialDate();
			
			// compare year
			int lastYearValue = getLocalDateYearValue(asLocalDate(lastDate));
			int currYearValue = getLocalDateYearValue(asLocalDate(currentDate));
			// compare month
			int lastMonthValue = getLocalDateMonthValue(asLocalDate(lastDate));
			int currMonthValue = getLocalDateMonthValue(asLocalDate(currentDate));
			
			if (lastYearValue==currYearValue) {
				if (lastMonthValue==currMonthValue) {
					serialNum = voucherSerNum.getSerialNo()+1;
				}
			}
		}
		
		return serialNum;
	}
	
	public VoucherSerialNumberDao getVoucherSerialNumberDao() {
		return voucherSerialNumberDao;
	}

	public void setVoucherSerialNumberDao(VoucherSerialNumberDao voucherSerialNumberDao) {
		this.voucherSerialNumberDao = voucherSerialNumberDao;
	}
	
}
