package com.javainuse.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.javainuse.authentication.AuthenticationFilter;
import com.javainuse.authentication.CustomAuthenticationProvider;
import com.javainuse.authentication.CustomAuthenticationProvider2;

@Configuration
@EnableWebSecurity
public class EmployeeSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthenticationFilter filter;

	@Autowired
	private CustomAuthenticationProvider authenticationProvider;

	@Autowired

	@Qualifier("customAuthenticationProvider2")
	private CustomAuthenticationProvider2 authenticationProvider2;

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/resources/**");
	}

	@Bean
	public FilterRegistrationBean loggingFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();

		registrationBean.setFilter(new AuthenticationFilter());
		registrationBean.addUrlPatterns("/validate");

		return registrationBean;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		

		http.authorizeRequests().antMatchers("/register*").permitAll().antMatchers("/welcome").hasAnyRole("USER", "ADMIN")
				.antMatchers("/getEmployees").hasAnyRole("USER", "ADMIN").antMatchers("/addNewEmployee")
				.hasAnyRole("ADMIN").anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				.and().logout().logoutSuccessUrl("/welcome");

		http.csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.inMemoryAuthentication().withUser("employee").password("employee").authorities("ROLE_USER").and()
				.withUser("javainuse").password("javainuse").authorities("ROLE_USER", "ROLE_ADMIN");

		auth.authenticationProvider(authenticationProvider);
		//auth.authenticationProvider(authenticationProvider2);

	}

	/*
	 * public void configureGlobal(AuthenticationManagerBuilder authenticationMgr)
	 * throws Exception {
	 * 
	 * authenticationMgr.inMemoryAuthentication().withUser("employee").password(
	 * "employee")
	 * .authorities("ROLE_USER").and().withUser("javainuse").password("javainuse")
	 * .authorities("ROLE_USER", "ROLE_ADMIN");
	 * 
	 * 
	 * authenticationMgr.authenticationProvider(authenticationProvider); }
	 */

}