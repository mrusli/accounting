package com.pyramix.persistence.creditcardcoa.dao.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.creditcard.CreditCardCoa;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.creditcardcoa.dao.CreditCardCoaDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

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

	@Override
	public void delete(CreditCardCoa creditCardCoa) throws Exception {
		
		super.delete(creditCardCoa);
	}

	@Override
	public List<CreditCardCoa> findAllActiveCreditCardCoa() throws Exception {
		Session session = getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<CreditCardCoa> criteriaQuery = criteriaBuilder.createQuery(CreditCardCoa.class);
		Root<CreditCardCoa> root = criteriaQuery.from(CreditCardCoa.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("active"), true));
		
		try {

			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
			
		} finally {
			session.close();

		}
	}
	
}
