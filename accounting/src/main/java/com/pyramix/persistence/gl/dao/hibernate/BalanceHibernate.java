package com.pyramix.persistence.gl.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.Balance;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.gl.dao.BalanceDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class BalanceHibernate extends DaoHibernate implements BalanceDao {

	@Override
	public Balance findAccountBalanceById(long id) throws Exception {
		
		return (Balance) super.findById(Balance.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Balance> findAllAccountBalance() throws Exception {
		
		return super.findAll(Balance.class);
	}

	@Override
	public void save(Balance accountClose) throws Exception {
		
		super.save(accountClose);
	}

	@Override
	public void update(Balance accountClose) throws Exception {

		super.update(accountClose);
	}

	@Override
	public void delete(Balance accountClose) throws Exception {

		super.delete(accountClose);
	}


	@Override
	public Balance findAccountBalanceByCoa(Coa_05_Master coa) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Balance> criteriaQuery = criteriaBuilder.createQuery(Balance.class);
		Root<Balance> root = criteriaQuery.from(Balance.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("coa_05_Master"), coa));
		
		try {
			
			return session.createQuery(criteriaQuery).getSingleResult();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}	
	
	@Override
	public List<Balance> findAccountBalanceByCoa_ClosingDate(Coa_05_Master coa, Date closingDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Balance> criteriaQuery = criteriaBuilder.createQuery(Balance.class);
		Root<Balance> root = criteriaQuery.from(Balance.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("coa_05_Master"), coa),
				criteriaBuilder.equal(root.get("closingDate"), closingDate));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
