package com.pyramix.persistence.voucher.dao;

import java.util.List;

import com.pyramix.domain.voucher.VoucherJournal;

/**
 * Date: 31/12/2023
 */
public interface VoucherJournalDao {

	/**
	 * @param id
	 * @return {@link VoucherJournal}
	 * @throws Exception
	 */
	public VoucherJournal findVoucherJournalById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link VoucherJournal}
	 * @throws Exception
	 */
	public List<VoucherJournal> findAllVoucherJournal() throws Exception;
	
	/**
	 * @param voucherJOurnal
	 * @throws Exception
	 */
	public void save(VoucherJournal voucherJOurnal) throws Exception;

	/**
	 * @param voucherJournal
	 * @throws Exception
	 */
	public void update(VoucherJournal voucherJournal) throws Exception;

	/**
	 * @param id
	 * @return {@link VoucherJournal}
	 * @throws Exception
	 */
	public VoucherJournal findVoucherJournalUserCreateByProxy(long id) throws Exception;

}
