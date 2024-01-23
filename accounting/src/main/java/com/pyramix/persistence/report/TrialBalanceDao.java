package com.pyramix.persistence.report;

import java.util.Date;
import java.util.List;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.report.TrialBalance;

public interface TrialBalanceDao {

	/**
	 * @param id
	 * @return {@link TrialBalance}
	 * @throws Exception
	 */
	public TrialBalance findTrialBalanceById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link TrialBalance}
	 * @throws Exception
	 */
	public List<TrialBalance> findAllTrialBalance() throws Exception;
	
	/**
	 * @param trialBalance
	 * @throws Exception
	 */
	public void save(TrialBalance trialBalance) throws Exception;
	
	/**
	 * @param trialBalance
	 * @throws Exception
	 */
	public void update(TrialBalance trialBalance) throws Exception;
	
	/**
	 * @param trialBalance
	 * @throws Exception
	 */
	public void delete(TrialBalance trialBalance) throws Exception;

	/**
	 * @param coa
	 * @param reportEndDate
	 * @return {@link TrialBalance}
	 */
	public TrialBalance findTrialBalanceByCoa_ReportEndDate(Coa_05_Master coa, Date reportEndDate);

	/**
	 * @param reportEndDate
	 * @return {@link List} of {@link TrialBalance}
	 */
	public List<TrialBalance> findTrialBalanceBy_ReportEndDate(Date reportEndDate);
	
}
