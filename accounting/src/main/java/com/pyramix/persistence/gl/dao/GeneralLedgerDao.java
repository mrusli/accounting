package com.pyramix.persistence.gl.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.GeneralLedger;

/**
 * Date: 31/12/2023
 */
public interface GeneralLedgerDao {

	/**
	 * @param id
	 * @return {@link GeneralLedger}
	 * @throws Exception
	 */
	public GeneralLedger findGeneralLedgerById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link GeneralLedger}
	 * @throws Exception
	 */
	public List<GeneralLedger> findAllGeneralLedger() throws Exception;
	
	/**
	 * @param generalLedger
	 * @throws Exception
	 */
	public void save(GeneralLedger generalLedger) throws Exception;
	
	/**
	 * @param generalLedger
	 * @throws Exception
	 */
	public void update(GeneralLedger generalLedger) throws Exception;

	/**
	 * @param selCoaMaster
	 * @return {@link List} of {@link GeneralLedger}
	 * @throws Exception
	 */
	public List<GeneralLedger> findAllGeneralLedgerByCoaMaster(Coa_05_Master selCoaMaster) throws Exception;

	/**
	 * @param coa
	 * @param startDate
	 * @param endDate
	 * @return {@link List} of {@link GeneralLedger}
	 * @throws Exception
	 */
	public List<GeneralLedger> findAllGeneralLedgerByCoaMaster(Coa_05_Master coa_05_Master, Date startDate, Date endDate) throws Exception;

	/**
	 * @param startDate
	 * @param endDate
	 * @return {@link List} of {@link GeneralLedger}
	 * @throws Exception
	 */
	public List<GeneralLedger> findAllGeneralLedgerByStartEndDate(Date startDate, Date endDate) throws Exception;
}
