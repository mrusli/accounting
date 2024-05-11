package com.pyramix.persistence.coa.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_04_SubAccount02;
import com.pyramix.persistence.coa.dao.Coa_04_SubAccount02Dao;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class Coa_04_SubAccount02Hibernate extends DaoHibernate implements Coa_04_SubAccount02Dao {

	@Override
	public Coa_04_SubAccount02 findCoa_04_SubAccount02ById(long id) throws Exception {
		
		return (Coa_04_SubAccount02) super.findById(Coa_04_SubAccount02.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_04_SubAccount02> findAllCoa_04_SubAccount02() throws Exception {
		
		return super.findAll(Coa_04_SubAccount02.class);
	}

	@Override
	public void save(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception {
		
		super.save(coa_04_SubAccount02);
	}

	@Override
	public void update(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception {
		
		super.update(coa_04_SubAccount02);
	}

	@Override
	public void delete(Coa_04_SubAccount02 coa_04_SubAccount02) throws Exception {

		super.delete(coa_04_SubAccount02);
	}	

	@Override
	public Coa_04_SubAccount02 findCoa_04_SubAccount01_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_04_SubAccount02> criteriaQuery = criteriaBuilder.createQuery(Coa_04_SubAccount02.class);
		Root<Coa_04_SubAccount02> root = criteriaQuery.from(Coa_04_SubAccount02.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_04_SubAccount02 subAccount02 =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(subAccount02.getSubAccount01());
			
			return subAccount02;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
		
		
	}	
	
	@Override
	public Coa_04_SubAccount02 findCoa_04_AccountMastersByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_04_SubAccount02> criteriaQuery = criteriaBuilder.createQuery(Coa_04_SubAccount02.class);
		Root<Coa_04_SubAccount02> root = criteriaQuery.from(Coa_04_SubAccount02.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_04_SubAccount02 subAccount02 =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(subAccount02.getMasters());
			
			return subAccount02;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}



}
