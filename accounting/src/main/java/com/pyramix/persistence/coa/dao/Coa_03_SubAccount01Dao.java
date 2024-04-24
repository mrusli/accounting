package com.pyramix.persistence.coa.dao;

import java.util.List;

import com.pyramix.domain.coa.Coa_02_AccountGroup;
import com.pyramix.domain.coa.Coa_03_SubAccount01;

public interface Coa_03_SubAccount01Dao {

	/**
	 * @param id
	 * @return {@link Coa_03_SubAccount01}
	 * @throws Exception
	 */
	public Coa_03_SubAccount01 findCoa_03_SubAccount01ById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link Coa_03_SubAccount01}
	 * @throws Exception
	 */
	public List<Coa_03_SubAccount01> findAllCoa_03SubAccount01() throws Exception;
	
	/**
	 * @param coa_03_SubAccount01
	 * @throws Exception
	 */
	public void save(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception;
	
	/**
	 * @param coa_03_SubAccount01
	 * @throws Exception
	 */
	public void update(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception;

	/**
	 * @param coa_03_SubAccount01
	 * @throws Exception
	 */
	public void delete(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception;
	
	/**
	 * @param id
	 * @return {@link Coa_03_SubAccount01}
	 * @throws Exception
	 */
	public Coa_03_SubAccount01 findCoa_02_AccountGroup_ByProxy(long id) throws Exception;

	/**
	 * @param id
	 * @return {@link Coa_03_SubAccount01}
	 * @throws Exception
	 */
	public Coa_03_SubAccount01 findCoa_03_SubAccount02s_ByProxy(long id) throws Exception;

}
