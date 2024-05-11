
package com.pyramix.persistence.coa.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_03_SubAccount01;
import com.pyramix.persistence.coa.dao.Coa_03_SubAccount01Dao;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class Coa_03_SubAccount01Hibernate extends DaoHibernate implements Coa_03_SubAccount01Dao {

	@Override
	public Coa_03_SubAccount01 findCoa_03_SubAccount01ById(long id) throws Exception {
		
		return (Coa_03_SubAccount01) super.findById(Coa_03_SubAccount01.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_03_SubAccount01> findAllCoa_03SubAccount01() throws Exception {
		
		return super.findAll(Coa_03_SubAccount01.class);
	}

	@Override
	public void save(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception {
		
		super.save(coa_03_SubAccount01);
	}

	@Override
	public void update(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception {
		
		super.update(coa_03_SubAccount01);
	}

	@Override
	public void delete(Coa_03_SubAccount01 coa_03_SubAccount01) throws Exception {
		
		super.delete(coa_03_SubAccount01);
	}

	@Override
	public Coa_03_SubAccount01 findCoa_02_AccountGroup_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_03_SubAccount01> criteriaQuery = criteriaBuilder.createQuery(Coa_03_SubAccount01.class);
		Root<Coa_03_SubAccount01> root = criteriaQuery.from(Coa_03_SubAccount01.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));

		Coa_03_SubAccount01 subAccount01 =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(subAccount01.getAccountGroup());
			
			return subAccount01;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Coa_03_SubAccount01 findCoa_03_SubAccount02s_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_03_SubAccount01> criteriaQuery = criteriaBuilder.createQuery(Coa_03_SubAccount01.class);
		Root<Coa_03_SubAccount01> root = criteriaQuery.from(Coa_03_SubAccount01.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));

		Coa_03_SubAccount01 subAccount01 =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(subAccount01.getSubAccount02s());
			
			return subAccount01;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}	
}
