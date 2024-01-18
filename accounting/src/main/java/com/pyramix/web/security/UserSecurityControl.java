package com.pyramix.web.security;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.pyramix.domain.user.User;
import com.pyramix.persistence.user.dao.UserDao;

@Service
public class UserSecurityControl implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	private static final Logger log = Logger.getLogger(UserSecurityControl.class);
	
	public UserSecurityControl(UserDao userDao) {
		super();
		this.userDao = userDao;
		
		log.info("UserSecurityControl implementing Spring-Security-UserDetailsService using UserDao");
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = null;
		
		try {
			log.info("Attempt to find User by Username : "+username);
			
			user = getUserDao().findUserByUsername(username);
		
		} catch (Exception e) {
			throw new UsernameNotFoundException("User not found");
		}
		
		return new UserSecurityDetails(user);
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

}
