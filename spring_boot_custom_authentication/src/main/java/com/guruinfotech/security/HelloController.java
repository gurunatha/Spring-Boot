package com.guruinfotech.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HelloController {
	
	@RequestMapping(method = RequestMethod.GET,value = "/index")
	public ModelAndView hello() {
		System.out.println("index");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			System.out.println(authentication.getPrincipal()+":"+authentication.isAuthenticated());
			System.out.println(authentication.getAuthorities()+":"+authentication.getCredentials());
		}
		return new ModelAndView("index");
	}
	
	
	
	@RequestMapping(method = RequestMethod.GET,value = "/welcome")
	public ModelAndView welcome() {
		System.out.println("welcome");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication!=null) {
			System.out.println(authentication.getName());
		}
		return new ModelAndView("welcome");
	}

}
