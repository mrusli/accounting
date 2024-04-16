package com.pyramix.persistence.pettycash.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.pettycash.PettyCash;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.pettycash.dao.PettyCashDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class PettyCashHibernate extends DaoHibernate implements PettyCashDao {

	@Override
	public PettyCash findPettyCashById(long id) throws Exception {
		
		return (PettyCash) super.findById(PettyCash.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PettyCash> findAllPettyCash() throws Exception {
		
		return super.findAll(PettyCash.class);
	}

	@Override
	public void save(PettyCash pettyCash) throws Exception {
		
		super.save(pettyCash);
	}

	@Override
	public void update(PettyCash pettyCash) throws Exception {
		
		super.update(pettyCash);
	}

	@Override
	public PettyCash findPettyCashUserCreateByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();

		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<PettyCash> criteriaQuery = criteriaBuilder.createQuery(PettyCash.class);
		Root<PettyCash> root = criteriaQuery.from(PettyCash.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("id"), id));
		
		PettyCash pettyCash =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(pettyCash.getUserCreate());
			
			return pettyCash;
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<PettyCash> findAllPettyCashByDate(Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();

		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<PettyCash> criteriaQuery = criteriaBuilder.createQuery(PettyCash.class);
		Root<PettyCash> root = criteriaQuery.from(PettyCash.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.between(root.get("transactionDate"), startDate, endDate));
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
