package com.pyramix.persistence.voucher.dao;

import java.util.List;

import com.pyramix.domain.voucher.VoucherSerialNumber;
import com.pyramix.domain.voucher.VoucherType;

/**
 * Date : 31/12/2023
 */
public interface VoucherSerialNumberDao {

	/**
	 * @param id
	 * @return VoucherSerialNumber
	 * @throws Exception
	 */
	public VoucherSerialNumber findVoucherSerialNumberById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link VoucherSerialNumber}
	 * @throws Exception
	 */
	public List<VoucherSerialNumber> findAllVoucherSerialNumber() throws Exception;
	
	/**
	 * @param voucherSerialNumber
	 * @throws Exception
	 */
	public void save(VoucherSerialNumber voucherSerialNumber) throws Exception;
	
	/**
	 * @param voucherSerialNumber
	 * @throws Exception
	 */
	public void update(VoucherSerialNumber voucherSerialNumber) throws Exception;
	
	/**
	 * @param voucherType
	 * @return {@link VoucherSerialNumber}
	 * @throws Exception
	 */
	public VoucherSerialNumber findLastVoucherSerialNumberByVoucherType(VoucherType voucherType) throws Exception;

}
