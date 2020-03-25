package io.guruinfotech.coronavirustracker.controllers;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.guruinfotech.coronavirustracker.interceptor.MaintenanceInterceptor;

@Controller
public class MaintenanceController {

	@Autowired
	private MaintenanceInterceptor interceptor;
	
	@Value("${smsstart}")
	private int smsStart;
	@Value("${smsend}")
	private int smsend;
	
	public int getSmsStart() {
		return smsStart;
	}

	public void setSmsStart(int smsStart) {
		this.smsStart = smsStart;
	}

	public int getSmsend() {
		return smsend;
	}

	public void setSmsend(int smsend) {
		this.smsend = smsend;
	}

	@RequestMapping("/maintenance/start/{start}/end/{end}")
	public String maintenance1(@PathVariable("start") String start,@PathVariable("end") String end) throws ServletException, IOException {
		try {
		interceptor.setMaintenanceStartTime(Integer.parseInt(start));
		interceptor.setMaintenanceEndTime(Integer.parseInt(end));
		}catch (NumberFormatException e) {
			return "error";
		}
		return "maintenanceupdate";
	}
	
	@RequestMapping("/sms/start/{smsstart}/end/{smsend}")
	public String sms(@PathVariable("smsstart") String start,@PathVariable("smsend") String end) throws ServletException, IOException {
		try {
		this.smsStart=Integer.parseInt(start);
		this.smsend=Integer.parseInt(end);
		}catch (NumberFormatException e) {
			return "smsurlfailed";
		}
		return "smsurl";
		
	   
	}
	
	
}
