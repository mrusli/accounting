package com.pyramix.persistence.gl.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.domain.gl.GeneralLedger;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.gl.dao.GeneralLedgerDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class GeneralLedgerHibernate extends DaoHibernate implements GeneralLedgerDao {

	@Override
	public GeneralLedger findGeneralLedgerById(long id) throws Exception {
		
		return (GeneralLedger) super.findById(GeneralLedger.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GeneralLedger> findAllGeneralLedger() throws Exception {
		
		return super.findAll(GeneralLedger.class);
	}

	@Override
	public void save(GeneralLedger generalLedger) throws Exception {
		
		super.save(generalLedger);
	}

	@Override
	public void update(GeneralLedger generalLedger) throws Exception {
		
		super.update(generalLedger);
	}

	@Override
	public List<GeneralLedger> findAllGeneralLedgerByCoaMaster(Coa_05_Master selCoaMaster) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<GeneralLedger> criteriaQuery = criteriaBuilder.createQuery(GeneralLedger.class);
		Root<GeneralLedger> root = criteriaQuery.from(GeneralLedger.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("masterCoa"), selCoaMaster));
		criteriaQuery.orderBy(
				criteriaBuilder.asc(root.get("transactionDate")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<GeneralLedger> findAllGeneralLedgerByCoaMaster(Coa_05_Master coa_05_Master, Date startDate,
			Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<GeneralLedger> criteriaQuery = criteriaBuilder.createQuery(GeneralLedger.class);
		Root<GeneralLedger> root = criteriaQuery.from(GeneralLedger.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("masterCoa"), coa_05_Master),
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

	@Override
	public List<GeneralLedger> findAllGeneralLedgerByStartEndDate(Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<GeneralLedger> criteriaQuery = criteriaBuilder.createQuery(GeneralLedger.class);
		Root<GeneralLedger> root = criteriaQuery.from(GeneralLedger.class);
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
