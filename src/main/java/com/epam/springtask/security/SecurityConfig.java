package com.epam.springtask.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		logger.debug("Configuring PasswordEncoder with NoOpPasswordEncoder");
		return NoOpPasswordEncoder.getInstance();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		logger.debug("Configuring HttpSecurity");
		http
				.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/users/**").permitAll()
					.antMatchers(HttpMethod.PUT, "/products/**").hasAuthority("ADMIN")
					.antMatchers("/users/**").hasAuthority("ADMIN")
					.antMatchers("/products/**").hasAnyAuthority("USER", "ADMIN")
				.and()
				
				.formLogin()
				.and()
				
				.csrf()
				.disable();
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		logger.debug("Configuring AuthenticationManagerBuilder with " + userDetailsService.getClass().getSimpleName());
		auth.userDetailsService(userDetailsService);
	}
}