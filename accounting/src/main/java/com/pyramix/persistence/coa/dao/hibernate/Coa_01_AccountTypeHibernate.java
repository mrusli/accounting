package com.pyramix.persistence.coa.dao.hibernate;

import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.coa.Coa_01_AccountType;
import com.pyramix.persistence.coa.dao.Coa_01_AccountTypeDao;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class Coa_01_AccountTypeHibernate extends DaoHibernate implements Coa_01_AccountTypeDao {

	@Override
	public Coa_01_AccountType findCoa_01_AccountTypeById(Long id) throws Exception {
		
		return (Coa_01_AccountType) super.findById(Coa_01_AccountType.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Coa_01_AccountType> findAllCoa_01_AccountType() throws Exception {
		
		return super.findAll(Coa_01_AccountType.class);
	}

	@Override
	public void save(Coa_01_AccountType coa_01_AccountType) throws Exception {
		
		super.save(coa_01_AccountType);
	}

	@Override
	public void update(Coa_01_AccountType coa_01_AccountType) throws Exception {
		
		super.update(coa_01_AccountType);
	}

	@Override
	public void delete(Coa_01_AccountType coa_01_AccountType) throws Exception {

		super.delete(coa_01_AccountType);
	}

	@Override
	public Coa_01_AccountType findCoa_01_AccountTypeAccountGroupsByProxy(Long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<Coa_01_AccountType> criteriaQuery =
				criteriaBuilder.createQuery(Coa_01_AccountType.class);
		Root<Coa_01_AccountType> root = criteriaQuery.from(Coa_01_AccountType.class);
		criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("id"), id));
		
		Coa_01_AccountType accType =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(accType.getAccountGroups());
			
			return accType;
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}

}
