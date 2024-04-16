package com.pyramix.persistence.project.dao;

import java.util.List;

import com.pyramix.domain.project.Project;

public interface ProjectDao {

	/**
	 * @param id
	 * @return {@link Project}
	 * @throws Exception
	 */
	public Project findProjectById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link Project}
	 * @throws Exception
	 */
	public List<Project> findAllProjects() throws Exception;
	
	/**
	 * @param project
	 * @throws Exception
	 */
	public void save(Project project) throws Exception;
	
	/**
	 * @param project
	 * @throws Exception
	 */
	public void update(Project project) throws Exception;
	
}
