package com.pyramix.web.login;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.pyramix.domain.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

	private static final Logger log = Logger.getLogger(LoginController.class);

	private User user;
	
	public LoginController() {
		super();
		
		log.info("Login and other request Controller activated");
	}

	@GetMapping("/login") 
	public String login(@RequestParam(value="error", required = false) String error) {
		
		if (error != null) {
			log.info("/login detected... [ERROR] but username / password NOT matched");
			return "login";	  
		} else { 
			log.info("/login detected... returning 'login' string...");	
			return "login";	  
		}
		
	}
	
	/**
	 * Handles the "/success" URL.
	 * 
	 * ref: https://stackoverflow.com/questions/31524426/securityconfig-2-success-url-for-different-roles
	 * 
	 * @param request
	 * @return {@link String}
	 */
	@GetMapping("/success")
	public String landing(HttpServletRequest request) {		 
		// UsernamePasswordAuthenticationToken authToken = 
		//	(UsernamePasswordAuthenticationToken) request.getUserPrincipal();
		// log.info("Login Success with " + authToken);
		
		// UserSecurityDetails userSecurityDetails =
		//	(UserSecurityDetails) authToken.getPrincipal();
		//user = userSecurityDetails.getUser();
		// log.info("UserSales from UserSecurityDetails: "+user.toString());
		
		// if (request.isUserInRole("ROLE_ADMIN")) {
		//	return "redirect:/admin/MainAdmin";
		// }
		// return "redirect:/logout";
		return "secure/main";

	}
	
	@GetMapping("/secure/{page}")
	public String secure(@PathVariable String page) {
		log.info("/secure/{page} detected... returning 'secure/"+page+"'"+" string...");
		
		return "secure/"+page;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}	
}
