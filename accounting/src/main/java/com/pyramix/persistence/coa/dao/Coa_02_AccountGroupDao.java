package com.pyramix.persistence.coa.dao;

import java.util.List;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_02_AccountGroup;

public interface Coa_02_AccountGroupDao {
	
	/**
	 * @param id
	 * @return {@link Coa_02_AccountGroup}
	 * @throws Exception
	 */
	public Coa_02_AccountGroup findCoa_02_AccountGroupById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link Coa_02_AccountGroup}
	 * @throws Exception
	 */
	public List<Coa_02_AccountGroup> findAllCoa_02_AccountGroup() throws Exception;
	
	/**
	 * @param coa_02_AccountGroup
	 * @throws Exception
	 */
	public void save(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception;
	
	/**
	 * @param coa_02_AccountGroup
	 * @throws Exception
	 */
	public void update(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception;

	/**
	 * @param coa_02_AccountGroup
	 * @throws Exception
	 */
	public void delete(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception;

	/**
	 * Return {@link Coa_02_AccountGroup} as a proxy to access the {@link Coa_01_AccountType} object.
	 * 
	 * @param id - from current {@link Coa_02_AccountGroup}
	 * @return {@link Coa_02_AccountGroup}
	 * @throws Exception
	 */
	public Coa_02_AccountGroup findCoa_01_AccountType_ByProxy(long id) throws Exception;

	/**
	 * @param id
	 * @return {@link Coa_02_AccountGroup}
	 * @throws Exception
	 */
	public Coa_02_AccountGroup findCoa_02_SubAccount01s_ByProxy(long id) throws Exception;
	
	/**
	 * @param coa_01_AccountType
	 * @return {@link List} of {@link Coa_02_AccountGroup}
	 * @throws Exception
	 */
	public List<Coa_02_AccountGroup> findCoa_02_AccountGroupByAccountType(Coa_01_AccountType coa_01_AccountType) throws Exception;

	/**
	 * @param selAccountType
	 * @return {@link List} of {@link Coa_02_AccountGroup}
	 * @throws Exception
	 */
	public List<Coa_02_AccountGroup> findCoa_02_AccountGroup_By_AccountType(int selAccountType) throws Exception;

}
