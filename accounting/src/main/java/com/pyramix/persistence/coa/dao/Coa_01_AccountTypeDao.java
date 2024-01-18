package com.pyramix.persistence.coa.dao;

import java.util.List;

import com.pyramix.domain.coa.Coa_01_AccountType;

public interface Coa_01_AccountTypeDao {
	/**
	 * @param id
	 * @return {@link Coa_01_AccountType}
	 * @throws Exception
	 */
	public Coa_01_AccountType findCoa_01_AccountTypeById(Long id) throws Exception;

	/** 
	 * @return {@link List} of {@link Coa_01_AccountType} (table: mm_coa_01_accounttype)
	 * @throws Exception
	 */
	public List<Coa_01_AccountType> findAllCoa_01_AccountType() throws Exception;

	/**
	 * Save the new coa_01_AccountType to the database
	 * 
	 * @param Coa_01_AccountType
	 * @throws Exception
	 */
	public void save(Coa_01_AccountType coa_01_AccountType) throws Exception;

	/**
	 * Update the existing Coa_01_AccountType
	 * 
	 * @param coa_01_AccountType
	 * @throws Exception
	 */
	public void update(Coa_01_AccountType coa_01_AccountType) throws Exception;

	/**
	 * Delete the existing Coa_01_AccountType
	 * 
	 * @param coa_01_AccountType
	 * @throws Exception
	 */
	public void delete(Coa_01_AccountType coa_01_AccountType) throws Exception;
	

	/**
	 * @param id
	 * @return {@link Coa_01_AccountType}
	 * @throws Exception
	 */
	public Coa_01_AccountType findCoa_01_AccountTypeAccountGroupsByProxy(Long id) throws Exception;
}
