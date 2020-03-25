package com.guruinfotech.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private SecurityFilter filter;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		

		http.authorizeRequests().antMatchers("/index*").permitAll().antMatchers("/welcome").hasAnyRole("USER", "ADMIN")
				.antMatchers("/getEmployees").hasAnyRole("USER", "ADMIN").antMatchers("/addNewEmployee")
				.hasAnyRole("ADMIN").anyRequest().authenticated().and().formLogin().permitAll()
				.and().logout().logoutUrl("/logout").logoutSuccessUrl("/welcome");

		http.csrf().disable();
	}
	
	@Bean
	public FilterRegistrationBean<SecurityFilter> loggingFilter() {
		FilterRegistrationBean<SecurityFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(filter);
		registrationBean.addUrlPatterns("/index");

		return registrationBean;
	}

}
