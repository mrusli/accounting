package com.pyramix.persistence.coa.dao;

import java.util.List;

import com.pyramix.domain.coa.Coa_03_SubAccount01;
import com.pyramix.domain.coa.Coa_04_SubAccount02;

public interface Coa_04_SubAccount02Dao {

	/**
	 * @param id
	 * @return {@link Coa_04_SubAccount02}
	 * @throws Exception
	 */
	public Coa_04_SubAccount02 findCoa_04_SubAccount02ById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link Coa_04_SubAccount02}
	 * @throws Exception
	 */
	public List<Coa_04_SubAccount02> findAllCoa_04_SubAccount02() throws Exception;
	
	/**
	 * @param coa_04_SubAccount02
	 * @throws Exception
	 */
	public void save(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception;
	
	/**
	 * @param coa_04_SubAccount02
	 * @throws Exception
	 */
	public void update(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception;

	/**
	 * @param coa_04_SubAccount02
	 * @throws Exception
	 */
	public void delete(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception;
	
	/**
	 * @param id
	 * @return {@link Coa_04_SubAccount02}
	 * @throws Exception
	 */
	public Coa_04_SubAccount02 findCoa_04_SubAccount01_ByProxy(long id) throws Exception;;

	/**
	 * @param id
	 * @return {@link Coa_04_SubAccount02}
	 * @throws Exception
	 */
	public Coa_04_SubAccount02 findCoa_04_AccountMastersByProxy(Long id) throws Exception;



}
