package com.pyramix.persistence.projectjournal.dao;

import java.util.Date;
import java.util.List;

import com.pyramix.domain.project.Project;
import com.pyramix.domain.project.ProjectJournal;

public interface ProjectJournalDao {

	/**
	 * @param id
	 * @return {@link ProjectJournal}
	 * @throws Exception
	 */
	public ProjectJournal findProjectJournalById(long id) throws Exception;
	
	/**
	 * @return {@link List} of {@link ProjectJournal}
	 * @throws Exception
	 */
	public List<ProjectJournal> findAllProjectJournals() throws Exception;
	
	/**
	 * @param projectJournal
	 * @throws Exception
	 */
	public void save(ProjectJournal projectJournal) throws Exception;
	
	/**
	 * @param projectJournal
	 * @throws Exception
	 */
	public void update(ProjectJournal projectJournal) throws Exception;

	/**
	 * @param id
	 * @return {@link ProjectJournal}
	 * @throws Exception
	 */
	public ProjectJournal findProjectJournalUserCreateByProxy(long id) throws Exception;
	
	/**
	 * @param project
	 * @param startDate
	 * @param endDate
	 * @return {@link List} of {@link ProjectJournal}
	 * @throws Exception
	 */
	public List<ProjectJournal> findAllProjectJournalsByDate(Project project, 
			Date startDate, Date endDate) throws Exception;
	
}
