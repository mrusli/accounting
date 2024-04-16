package com.pyramix.persistence.projectjournal.dao.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;

import com.pyramix.domain.project.Project;
import com.pyramix.domain.project.ProjectJournal;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.projectjournal.dao.ProjectJournalDao;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class ProjectJournalHibernate extends DaoHibernate implements ProjectJournalDao {

	@Override
	public ProjectJournal findProjectJournalById(long id) throws Exception {
		
		return (ProjectJournal) super.findById(ProjectJournal.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectJournal> findAllProjectJournals() throws Exception {
		
		return super.findAll(ProjectJournal.class);
	}

	@Override
	public void save(ProjectJournal projectJournal) throws Exception {
		
		super.save(projectJournal);
	}

	@Override
	public void update(ProjectJournal projectJournal) throws Exception {
		
		super.update(projectJournal);
	}
	
	@Override
	public List<ProjectJournal> findAllProjectJournalsByDate(Project project, Date startDate, Date endDate)
			throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<ProjectJournal> criteriaQuery = criteriaBuilder.createQuery(ProjectJournal.class);
		Root<ProjectJournal> root = criteriaQuery.from(ProjectJournal.class);
		
		Predicate preProject =
				criteriaBuilder.equal(root.get("project"), project);
		Predicate preTransactionDate = 
				criteriaBuilder.between(root.get("transactionDate"), startDate, endDate);

		criteriaQuery.select(root).where(criteriaBuilder.and(preProject, preTransactionDate));
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
	public ProjectJournal findProjectJournalUserCreateByProxy(long id) throws Exception {
		Session session = super.getSessionFactory().openSession();
		
		CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
		CriteriaQuery<ProjectJournal> criteriaQuery = criteriaBuilder.createQuery(ProjectJournal.class);
		Root<ProjectJournal> root = criteriaQuery.from(ProjectJournal.class);
		criteriaQuery.select(root).where(
				criteriaBuilder.equal(root.get("id"), id));
		
		ProjectJournal projectJournal =
				session.createQuery(criteriaQuery).getSingleResult();
		
		try {
			Hibernate.initialize(projectJournal.getUserCreate());
			
			return projectJournal;
			
		} catch (Exception e) {
			throw e;
		} finally {
			session.close();
		}		
	}

}
