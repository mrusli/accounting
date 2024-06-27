package com.pyramix.web.security;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	private static final Logger log = Logger.getLogger(SecurityConfig.class);

	private static final String ZUL_FILES = "/zkau/web/secure/**";
	
    private static final String[] ZK_RESOURCES = {
            "/zkau/web/js/*",
            "/zkau/web/css/*",
            "/zkau/web/font/*",
            "/zkau/web/img/*",
            "/zkau/web/error/*"
    };
    
    private static final String REMOVE_DESKTOP_REGEX = "/zkau\\?dtid=.*&cmd_0=rmDesktop&.*";
    
    @Autowired
    private UserSecurityControl userSecurityControl;

	public SecurityConfig() {
		super();
		
		log.info("Configuration - WebSecurity - SecurityConfiguration Started");
	}

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	log.info("Define Security FilterChain");

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers(ZUL_FILES).denyAll()
                        .requestMatchers(HttpMethod.GET, ZK_RESOURCES).permitAll()
                        .requestMatchers(HttpMethod.GET, REMOVE_DESKTOP_REGEX).permitAll()
                        .requestMatchers(req -> "rmDesktop".equals(req.getParameter("cmd_0"))).permitAll()
                        .requestMatchers("/pyramix/excel").permitAll()
                        .requestMatchers("/success").authenticated()
                        .requestMatchers("/secure/**").authenticated()
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(login -> login
                        .loginPage("/login").permitAll()
                        .defaultSuccessUrl("/success", true)
                        .failureUrl("/login?error=true"))
                .rememberMe(me -> me.key("secretkey"));
        http
        	.headers(headers -> headers
        			.frameOptions().sameOrigin()
        			.httpStrictTransportSecurity().disable());
        http
        	.authenticationManager(authManager(http));

        return http.build();
    }

	@Bean
	protected AuthenticationProvider authenticationProvider() {
		log.info("Define the authenticationProvider with UserSecurityControl and BCryptPasswordEncoder");
		
		DaoAuthenticationProvider provider
			= new DaoAuthenticationProvider();
		provider.setUserDetailsService(getUserSecurityControl());
		provider.setPasswordEncoder(new BCryptPasswordEncoder());
		
		return provider;
	}

	
	@Bean 
	AuthenticationManager authManager(HttpSecurity http) throws Exception {
		log.info("AuthenticationManager setup using authenticationProvider()");
		
		AuthenticationManagerBuilder authenticationManagerBuilder =
				http.getSharedObject(AuthenticationManagerBuilder.class);
		authenticationManagerBuilder.authenticationProvider(authenticationProvider());
	  
	  return authenticationManagerBuilder.build(); 
	}
	
	public UserSecurityControl getUserSecurityControl() {
		return userSecurityControl;
	}

	public void setUserSecurityControl(UserSecurityControl userSecurityControl) {
		this.userSecurityControl = userSecurityControl;
	}
    
    
}
