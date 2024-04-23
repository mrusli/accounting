package com.pyramix.persistence.coa.dao;

import java.util.List;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_02_AccountGroup;
import com.pyramix.domain.coa.Coa_03_SubAccount01;
import com.pyramix.domain.coa.Coa_04_SubAccount02;
import com.pyramix.domain.coa.Coa_05_Master;

public interface Coa_05_AccountMasterDao {

	/**
	 * @param id
	 * @return {@link Coa_05_Master}
	 * @throws Exception
	 */
	public Coa_05_Master findCoa_05_MasterById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link Coa_05_Master}
	 * @throws Exception
	 */
	public List<Coa_05_Master> findAllCoa_05_Master() throws Exception;
	
	/**
	 * @param coa_05_Master
	 * @throws Exception
	 */
	public void save(Coa_05_Master coa_05_Master) throws Exception;
	
	/**
	 * @param coa_05_Master
	 * @throws Exception
	 */
	public void update(Coa_05_Master coa_05_Master) throws Exception;

	/**
	 * @param coa_05_Master
	 * @throws Exception
	 */
	public void delete(Coa_05_Master coa_05_Master) throws Exception;

	/**
	 * Return {@link Coa_05_Master} as a proxy to access the {@link Coa_01_AccountType} object.
	 * 
	 * @param id - from current {@link Coa_05_Master}
	 * @return {@link Coa_05_Master}
	 * @throws Exception
	 */
	public Coa_05_Master findCoa_01_AccountType_ByProxy(long id) throws Exception;	

	/**
	 * Return {@link Coa_05_Master} as a proxy to access the {@link Coa_02_AccountGroup} object.
	 * 
	 * @param id - from current {@link Coa_05_Master}
	 * @return {@link Coa_05_Master}
	 * @throws Exception
	 */
	public Coa_05_Master findCoa_02_AccountGroup_ByProxy(long id) throws Exception;	
	
	/**
	 * Return {@link Coa_05_Master} as a proxy to access the {@link Coa_03_SubAccount01} object.
	 * 
	 * @param id - from current {@link Coa_05_Master}
	 * @return {@link Coa_05_Master}
	 * @throws Exception
	 */
	public Coa_05_Master findCoa_03_SubAccount01_ByProxy(long id) throws Exception;	

	/**
	 * Return {@link Coa_05_Master} as a proxy to access the {@link Coa_04_SubAccount02} object.
	 * 
	 * @param id - from current {@link Coa_05_Master}
	 * @return {@link Coa_05_Master}
	 * @throws Exception
	 */
	public Coa_05_Master findCoa_04_SubAccount02_ByProxy(long id) throws Exception;	

	/** 
	 * @return {@link List} of {@link Coa_05_Master}
	 * @throws Exception
	 */
	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp() throws Exception;
	
	/**
	 * @param coaAccountType
	 * @return {@link List} of {@link Coa_05_Master}
	 * @throws Exception
	 */
	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_by_AccountType(int coaAccountTypeNo) throws Exception;

	public List<Coa_05_Master> find_All_Coa_05_Master_by_AccountType(Coa_01_AccountType accountType) throws Exception;

}
