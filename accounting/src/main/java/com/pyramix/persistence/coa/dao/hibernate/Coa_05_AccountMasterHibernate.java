package com.pyramix.persistence.coa.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_05_Master;
import com.pyramix.persistence.coa.dao.Coa_05_AccountMasterDao;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class Coa_05_AccountMasterHibernate extends DaoHibernate implements Coa_05_AccountMasterDao {

	@Override
	public Coa_05_Master findCoa_05_MasterById(long id) throws Exception {

		return (Coa_05_Master) super.findById(Coa_05_Master.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_05_Master> findAllCoa_05_Master() throws Exception {
		
		return super.findAll(Coa_05_Master.class);
	}

	@Override
	public void save(Coa_05_Master coa_05_Master) throws Exception {
		
		super.save(coa_05_Master);
	}

	@Override
	public void update(Coa_05_Master coa_05_Master) throws Exception {
		
		super.update(coa_05_Master);
	}

	@Override
	public void delete(Coa_05_Master coa_05_Master) throws Exception {

		super.delete(coa_05_Master);
	}

	@Override
	public Coa_05_Master findCoa_01_AccountType_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_05_Master accMaster =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accMaster.getAccountType());
			
			return accMaster;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}

	@Override
	public Coa_05_Master findCoa_02_AccountGroup_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_05_Master accMaster =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accMaster.getAccountGroup());
			
			return accMaster;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Coa_05_Master findCoa_03_SubAccount01_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_05_Master accMaster =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accMaster.getSubAccount01());
			
			return accMaster;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}	

	@Override
	public Coa_05_Master findCoa_04_SubAccount02_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_05_Master accMaster =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accMaster.getSubAccount02());
			
			return accMaster;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}
	
	@Override
	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_OrderBy_MasterCoaComp() throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("active"), true));
				// no limit on COA
				// criteriaBuilder.equal(root.get("creditAccount"), creditAccount));
		// https://stackoverflow.com/questions/18389378/jpa-criteria-query-api-and-order-by-two-columns
		criteriaQuery.orderBy(
				criteriaBuilder.asc(root.get("typeCoaNumber")),
				criteriaBuilder.asc(root.get("groupCoaNumber")),
				criteriaBuilder.asc(root.get("subaccount01CoaNumber")),
				criteriaBuilder.asc(root.get("subaccount02CoaNumber")),
				criteriaBuilder.asc(root.get("masterCoaNumber")));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Coa_05_Master> find_ActiveOnly_Coa_05_Master_by_AccountType(int accountTypeNo) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("active"), true),
				criteriaBuilder.equal(root.get("typeCoaNumber"), accountTypeNo));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public List<Coa_05_Master> find_All_Coa_05_Master_by_AccountType(Coa_01_AccountType accountType) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_05_Master> criteriaQuery =
				criteriaBuilder.createQuery(Coa_05_Master.class);
		Root<Coa_05_Master> root = criteriaQuery.from(Coa_05_Master.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("typeCoaNumber"), accountType.getAccountTypeNumber()));
		
		try {
			
			return session.createQuery(criteriaQuery).getResultList();
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}


}
