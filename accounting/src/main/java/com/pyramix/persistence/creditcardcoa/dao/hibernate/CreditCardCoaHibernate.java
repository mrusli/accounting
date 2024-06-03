package com.pyramix.persistence.creditcardcoa.dao.hibernate;

import java.util.List;

import com.pyramix.domain.creditcard.CreditCardCoa;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.creditcardcoa.dao.CreditCardCoaDao;

public class CreditCardCoaHibernate extends DaoHibernate implements CreditCardCoaDao {

	@Override
	public CreditCardCoa findCreditCardCoaById(long id) throws Exception {
		
		return (CreditCardCoa) super.findById(CreditCardCoa.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditCardCoa> findAllCreditCardCoa() throws Exception {
		
		return super.findAll(CreditCardCoa.class);
	}

	@Override
	public void save(CreditCardCoa creditCardCoa) throws Exception {
		
		super.save(creditCardCoa);
	}

	@Override
	public void update(CreditCardCoa creditCardCoa) throws Exception {
		
		super.update(creditCardCoa);
	}

}
