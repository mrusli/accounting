package com.pyramix.persistence.creditcard.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.creditcard.CreditCard;

public interface CreditCardDao {
	
	/**
	 * @param id
	 * @return {@link CreditCard}
	 * @throws Exception
	 */
	public CreditCard findCreditCardById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link CreditCard}
	 * @throws Exception
	 */
	public List<CreditCard> findAllCreditCard() throws Exception;
	
	/**
	 * @param creditCard
	 * @throws Exception
	 */
	public void save(CreditCard creditCard) throws Exception;
	
	/**
	 * @param creditCard
	 * @throws Exception
	 */
	public void update(CreditCard creditCard) throws Exception;
	
	/**
	 * @param id
	 * @return {@link CreditCard}
	 * @throws Exception
	 */
	public CreditCard findCreditCardUserCreateByProxy(long id) throws Exception;
	
	/**
	 * @param creditCardAccount 
	 * @param startDate
	 * @param endDate
	 * @return {@link List} of {@link CreditCard}
	 * @throws Exception
	 */
	public List<CreditCard> findAllCreditCardByDate(Coa_05_Master creditCardAccount, Date startDate, Date endDate) throws Exception;
}
