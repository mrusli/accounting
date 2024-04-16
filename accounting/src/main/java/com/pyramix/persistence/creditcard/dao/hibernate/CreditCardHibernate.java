package com.pyramix.persistence.creditcard.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.creditcard.CreditCard;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.creditcard.dao.CreditCardDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CreditCardHibernate extends DaoHibernate implements CreditCardDao {

	@Override
	public CreditCard findCreditCardById(long id) throws Exception {
		
		return (CreditCard) super.findById(CreditCard.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<CreditCard> findAllCreditCard() throws Exception {
		
		return super.findAll(CreditCard.class);
	}

	@Override
	public void save(CreditCard creditCard) throws Exception {
		
		super.save(creditCard);
	}

	@Override
	public void update(CreditCard creditCard) throws Exception {
		
		super.update(creditCard);
	}

	@Override
	public CreditCard findCreditCardUserCreateByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<CreditCard> criteriaQuery = criteriaBuilder.createQuery(CreditCard.class);
		Root<CreditCard> root = criteriaQuery.from(CreditCard.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("id"), id));
		
		CreditCard creditCard =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(creditCard.getUserCreate());
			
			return creditCard;
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<CreditCard> findAllCreditCardByDate(Coa_05_Master creditCardAccount, Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<CreditCard> criteriaQuery = criteriaBuilder.createQuery(CreditCard.class);
		Root<CreditCard> root = criteriaQuery.from(CreditCard.class);
		
		Predicate preCreditCardAccount = 
				criteriaBuilder.equal(root.get("creditCardMasterCoa"), creditCardAccount);
		Predicate preTransactionDate = 
				criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);
		
		criteriaQuery.select(root).where(criteriaBuilder.and(preCreditCardAccount, preTransactionDate));
		criteriaQuery.orderBy(
				criteriaBuilder.desc(root.get("transactionDate")));		

		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
