package com.pyramix.persistence.coa.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.domain.coa.Coa_02_AccountGroup;
import com.pyramix.persistence.coa.dao.Coa_02_AccountGroupDao;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class Coa_02_AccountGroupHibernate extends DaoHibernate implements Coa_02_AccountGroupDao {

	@Override
	public Coa_02_AccountGroup findCoa_02_AccountGroupById(long id) throws Exception {
		
		return (Coa_02_AccountGroup) super.findById(Coa_02_AccountGroup.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_02_AccountGroup> findAllCoa_02_AccountGroup() throws Exception {
		
		return super.findAll(Coa_02_AccountGroup.class);
	}

	@Override
	public void save(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception {
		
		super.save(coa_02_AccountGroup);
	}

	@Override
	public void update(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception {
		
		super.update(coa_02_AccountGroup);
	}

	@Override
	public void delete(Coa_02_AccountGroup coa_02_AccountGroup) throws Exception {
		
		super.delete(coa_02_AccountGroup);
	}

	@Override
	public List<Coa_02_AccountGroup> findCoa_02_AccountGroupByAccountType(Coa_01_AccountType coa_01_AccountType)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Coa_02_AccountGroup> findCoa_02_AccountGroup_By_AccountType(int selAccountType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Coa_02_AccountGroup findCoa_01_AccountType_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_02_AccountGroup> criteriaQuery = criteriaBuilder.createQuery(Coa_02_AccountGroup.class);
		Root<Coa_02_AccountGroup> root = criteriaQuery.from(Coa_02_AccountGroup.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
				
		Coa_02_AccountGroup accGroup = 
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accGroup.getAccountType());
			
			return accGroup;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

	@Override
	public Coa_02_AccountGroup findCoa_03_SubAccount01s_ByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_02_AccountGroup> criteriaQuery = criteriaBuilder.createQuery(Coa_02_AccountGroup.class);
		Root<Coa_02_AccountGroup> root = criteriaQuery.from(Coa_02_AccountGroup.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
				
		Coa_02_AccountGroup accGroup = 
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accGroup.getSubAccount01s());
			
			return accGroup;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}
	}

}
