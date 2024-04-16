package com.pyramix.persistence.project.dao.hibernate;

import java.util.List;

import com.pyramix.domain.project.Project;
import com.pyramix.persistence.common.dao.hibernate.DaoHibernate;
import com.pyramix.persistence.project.dao.ProjectDao;

public class ProjectHibernate extends DaoHibernate implements ProjectDao {

	@Override
	public Project findProjectById(long id) throws Exception {
		
		return (Project) super.findById(Project.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Project> findAllProjects() throws Exception {
		
		return super.findAll(Project.class);
	}

	@Override
	public void save(Project project) throws Exception {
		
		super.save(project);
	}

	@Override
	public void update(Project project) throws Exception {
		
		super.update(project);
	}

}
