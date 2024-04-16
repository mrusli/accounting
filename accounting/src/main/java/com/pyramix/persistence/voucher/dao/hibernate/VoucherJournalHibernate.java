package com.pyramix.persistence.voucher.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.pyramix.domain.voucher.VoucherJournal;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.voucher.dao.VoucherJournalDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class VoucherJournalHibernate extends DaoHibernate implements VoucherJournalDao {

	@Override
	public VoucherJournal findVoucherJournalById(long id) throws Exception {
		
		return (VoucherJournal) super.findById(VoucherJournal.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VoucherJournal> findAllVoucherJournal() throws Exception {

		return super.findAll(VoucherJournal.class);
	}

	@Override
	public Long save(VoucherJournal voucherJournal) throws Exception {
		
		super.save(voucherJournal);
		
		Session session =
				super.getSessionFactory().openSession();
		
		Transaction transaction = session.beginTransaction();
		
		try {
			VoucherJournal sessVoucherJournal = 
					session.get(voucherJournal.getClass(), voucherJournal.getId());
			// commit
			transaction.commit();
			// return
			return sessVoucherJournal.getId();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}

	@Override
	public void update(VoucherJournal voucherJournal) throws Exception {
		
		super.update(voucherJournal);
	}

	@Override
	public VoucherJournal findVoucherJournalUserCreateByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<VoucherJournal> criteriaQuery = criteriaBuilder.createQuery(VoucherJournal.class);
		Root<VoucherJournal> root = criteriaQuery.from(VoucherJournal.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("id"), id));
		
		VoucherJournal voucherJournal =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(voucherJournal.getUserCreate());
			
			return voucherJournal;
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<VoucherJournal> findAllVoucherJournalByDate(Date startDate, Date endDate) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<VoucherJournal> criteriaQuery = criteriaBuilder.createQuery(VoucherJournal.class);
		Root<VoucherJournal> root = criteriaQuery.from(VoucherJournal.class);
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
