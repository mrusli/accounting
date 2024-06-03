package com.pyramix.persistence.creditcardcoa.dao;

import java.util.List;

import com.pyramix.domain.creditcard.CreditCardCoa;

public interface CreditCardCoaDao {

	/**
	 * @param id
	 * @return {@link CreditCardCoa}
	 * @throws Exception
	 */
	public CreditCardCoa findCreditCardCoaById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link CreditCardCoa}
	 * @throws Exception
	 */
	public List<CreditCardCoa> findAllCreditCardCoa() throws Exception;
	
	/**
	 * @param creditCardCoa
	 * @throws Exception
	 */
	public void save(CreditCardCoa creditCardCoa) throws Exception;
	
	/**
	 * @param creditCardCoa
	 * @throws Exception
	 */
	public void update(CreditCardCoa creditCardCoa) throws Exception;
	
	
	
}
