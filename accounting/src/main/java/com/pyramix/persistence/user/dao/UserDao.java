package com.pyramix.persistence.user.dao;

import java.util.List;

import com.pyramix.domain.user.User;
import com.pyramix.domain.user.UserRole;

public interface UserDao {
	/**
	 * @param id
	 * @return {@link User}
	 * @throws Exception
	 */
	public User findUserById(Long id) throws Exception;
	
	/**
	 * @param username
	 * @return {@link User}
	 * @throws Exception
	 */
	public User findUserByUsername(String username) throws Exception;

	/**
	 * All users authorized to use the system
	 * 
	 * @return {@link List} of {@link User} (table: auth_user)
	 * @throws Exception
	 */
	public List<User> findAllUsers() throws Exception;

	/**
	 * Save the new user, including the password, to the database
	 * 
	 * @param user 
	 * @throws Exception
	 */
	public void save(User user) throws Exception;

	/**
	 * Update the existing user, including the password and roles
	 * 
	 * @param user
	 * @throws Exception
	 */
	public void update(User user) throws Exception;

	/**
	 * Users who has the userRole ({@link UserRole})
	 * 
	 * @param userRole
	 * @return {@link List} of {@link User}
	 */
	public List<User> findAllUsersByUserRole(UserRole userRole) throws Exception;

}
