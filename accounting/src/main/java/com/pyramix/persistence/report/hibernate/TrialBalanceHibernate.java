package com.pyramix.persistence.report.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.report.TrialBalance;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.report.TrialBalanceDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class TrialBalanceHibernate extends DaoHibernate implements TrialBalanceDao {

	@Override
	public TrialBalance findTrialBalanceById(long id) throws Exception {
		
		return (TrialBalance) super.findById(TrialBalance.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<TrialBalance> findAllTrialBalance() throws Exception {
		
		return super.findAll(TrialBalance.class);
	}

	@Override
	public void save(TrialBalance trialBalance) throws Exception {
		
		super.save(trialBalance);
	}

	@Override
	public void update(TrialBalance trialBalance) throws Exception {
		
		super.update(trialBalance);
	}

	@Override
	public void delete(TrialBalance trialBalance) throws Exception {
		
		super.delete(trialBalance);
	}

	@Override
	public TrialBalance findTrialBalanceByCoa_ReportEndDate(Coa_05_Master coa, Date reportEndDate) {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<TrialBalance> criteriaQuery = criteriaBuilder.createQuery(TrialBalance.class);
		Root<TrialBalance> root = criteriaQuery.from(TrialBalance.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("coa_05_Master"), coa),
				criteriaBuilder.equal(root.get("reportEndDate"), reportEndDate));
		
		try {
			
			return session.createQuery(criteriaQuery).getSingleResult();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<TrialBalance> findTrialBalanceBy_ReportEndDate(Date reportEndDate) {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<TrialBalance> criteriaQuery = criteriaBuilder.createQuery(TrialBalance.class);
		Root<TrialBalance> root = criteriaQuery.from(TrialBalance.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("reportEndDate"), reportEndDate));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
