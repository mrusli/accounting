package com.pyramix.persistence.pettycash.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.domain.pettycash.PettyCash;
import com.pyramix.domain.voucher.VoucherJournal;

/**
 * Date: 01/03/2024
 */
public interface PettyCashDao {
	/**
	 * @param id
	 * @return {@link VoucherJournal}
	 * @throws Exception
	 */
	public PettyCash findPettyCashById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link VoucherJournal}
	 * @throws Exception
	 */
	public List<PettyCash> findAllPettyCash() throws Exception;
	
	/**
	 * @param voucherJOurnal
	 * @throws Exception
	 */
	public void save(PettyCash pettyCash) throws Exception;

	/**
	 * @param voucherJournal
	 * @throws Exception
	 */
	public void update(PettyCash pettyCash) throws Exception;

	/**
	 * @param id
	 * @return {@link VoucherJournal}
	 * @throws Exception
	 */
	public PettyCash findPettyCashUserCreateByProxy(long id) throws Exception;

	/**
	 * @param periodStartDate
	 * @param periodEndDate
	 * @return {@link List} of {@link PettyCash}
	 * @throws Exception
	 */
	public List<PettyCash> findAllPettyCashByDate(Date startDate, Date endDate) throws Exception;


}
