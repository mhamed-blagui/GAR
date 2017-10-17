package com.gar.system;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class GarSecurityConfig extends WebSecurityConfigurerAdapter {

	
	
	@Autowired
	public void globalConfig(AuthenticationManagerBuilder auth, DataSource dataSource) throws Exception {
		auth.jdbcAuthentication().dataSource(dataSource)
				.usersByUsernameQuery(
						"SELECT usr.USER_NAME as principal, password as credentials, true FROM GAR_USER usr WHERE USER_NAME = ? AND IS_ACTIVE = 1")
				.authoritiesByUsernameQuery(
						"SELECT usr.USER_NAME as principal , usr.PROFIL_NAME as role FROM GAR_USER usr WHERE USER_NAME = ?")
				.rolePrefix("ROLE_");

	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers( "/static/styles/**", "/static/images/**", "/static/scripts/**")
				.permitAll()
		         .antMatchers("/static/views/RH/**").hasAuthority("RH").anyRequest().authenticated() 
		         .antMatchers("/static/views/admin/**").hasAuthority("ADMIN").anyRequest().authenticated() ; 
		http.csrf().disable()
				.authorizeRequests()
				.anyRequest().authenticated().and().formLogin().loginPage("/login")
				.defaultSuccessUrl("/static/index.html").failureUrl("/static/login-error.html").permitAll()

		;

	}
 

}
