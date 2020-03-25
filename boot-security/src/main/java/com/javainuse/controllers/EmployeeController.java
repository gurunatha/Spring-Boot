package com.javainuse.controllers;

import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.javainuse.authentication.CustomAuthenticationProvider;
import com.javainuse.model.Employee;
import com.javainuse.model.Register;
import com.javainuse.model.TotpValidation;
import com.javainuse.service.EmployeeService;

@Controller
public class EmployeeController {
	
	@Value("${totp.key}")
	private String secreateKey;

	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	private CustomAuthenticationProvider customAuthenticationProvider;

	@RequestMapping("/welcome")
	public ModelAndView firstPage() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("Loged in user :"+name);
		List<com.javainuse.model.User> users = customAuthenticationProvider.getUsers();
		System.out.println(users);
		for(com.javainuse.model.User u:users) {
			System.out.println(name+"========"+u.is2FAEnable());
			if(u.getName().equals(name)&&u.is2FAEnable()) {
				TotpValidation totp = new TotpValidation();
				return new ModelAndView("validatetotp", "totp", totp);
			}else if(u.getName().equals(name)) {
				return new ModelAndView("welcome");
			}
		}
		
		return new ModelAndView("login");
	}
	
	

	@RequestMapping(value = "/addNewEmployee", method = RequestMethod.GET)
	public ModelAndView show() {
		return new ModelAndView("addEmployee", "emp", new Employee());
	}
	
	@RequestMapping(value = "/validate", method = RequestMethod.POST)
	public ModelAndView processRequest(@ModelAttribute("totp") TotpValidation otp,HttpServletRequest request) throws ServletException {
		
		boolean verify;
		try {
			Totp t = new Totp(secreateKey);
			System.out.println("-----------------");
			verify = t.verify(otp.getOtp());
			System.out.println("===============");
			if(verify) {
				System.out.println("OTP Verified");
				return new ModelAndView("welcome");
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		
			SecurityContextHolder.getContext().setAuthentication(null);
		return new ModelAndView("welcome");
	}
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() throws ServletException {
		
		return new ModelAndView("register");
	}
	
	@RequestMapping(value = "/registerpage", method = RequestMethod.POST)
	public ModelAndView registerPage(@ModelAttribute("regis") Register register,@RequestParam(value="is2FAEnabled",required = false) String value) throws ServletException {
		if(value!=null) {
			register.set2FAEnabled(true);
			System.out.println(register);
		customAuthenticationProvider.getUsers().add(new com.javainuse.model.User(register.getUsername(), register.getPassword(), "ROLE_ADMIN",register.is2FAEnabled()));
		}else {
			customAuthenticationProvider.getUsers().add(new com.javainuse.model.User(register.getUsername(), register.getPassword(), "ROLE_ADMIN"));
			System.out.println(register);
		}
		System.out.println(register);
		System.out.println("Registration Success");
		return new ModelAndView("welcome");
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, String error, String logout) {
        if (error != null)
            model.addAttribute("errorMsg", "Your username and password are invalid.");

        if (logout != null)
            model.addAttribute("msg", "You have been logged out successfully.");

        return "login";
    }
	
	@RequestMapping(value = "/invalid")
	public ModelAndView invalid() throws ServletException {
		
			SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
		return new ModelAndView("welcome");
	}

	@RequestMapping(value = "/addNewEmployee", method = RequestMethod.POST)
	public ModelAndView processRequest(@ModelAttribute("emp") Employee emp) {
		
		employeeService.insertEmployee(emp);
		List<Employee> employees = employeeService.getAllEmployees();
		ModelAndView model = new ModelAndView("getEmployees");
		model.addObject("employees", employees);
		return model;
	}

	@RequestMapping("/getEmployees")
	public ModelAndView getEmployees() {
		List<Employee> employees = employeeService.getAllEmployees();
		ModelAndView model = new ModelAndView("getEmployees");
		model.addObject("employees", employees);
		return model;
	}

}
