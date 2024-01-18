package com.pyramix.persistence.gl.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;

public interface BalanceDao {

	/**
	 * @param id
	 * @return {@link Balance}
	 * @throws Exception
	 */
	public Balance findAccountBalanceById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link Balance}
	 * @throws Exception
	 */
	public List<Balance> findAllAccountBalance() throws Exception;
	
	/**
	 * @param accountClose
	 * @throws Exception
	 */
	public void save(Balance accountBalance) throws Exception;
	
	/**
	 * @param accountClose
	 * @throws Exception
	 */
	public void update(Balance accountBalance) throws Exception;
	
	/**
	 * @param accountClose
	 * @throws Exception
	 */
	public void delete(Balance accountBalance) throws Exception;

	/**
	 * @param coa
	 * @return {@link Balance}
	 * @throws Exception
	 */
	public Balance findAccountBalanceByCoa(Coa_05_Master coa) throws Exception;
		
	/**
	 * @param coa
	 * @param closingDate
	 * @return {@link List} of {@link Balance}
	 * @throws Exception
	 */
	public List<Balance> findAccountBalanceByCoa_ClosingDate(Coa_05_Master coa, Date closingDate) throws Exception;
	
}
