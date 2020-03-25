package io.guruinfotech.coronavirustracker.interceptor;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class MaintenanceInterceptor extends HandlerInterceptorAdapter{
	@Value("${start}")
	private int maintenanceStartTime;
	@Value("${end}")
	private int maintenanceEndTime;
	@Value("${mapping}")
	private String maintenanceMapping;
	
	public int getMaintenanceStartTime() {
		return maintenanceStartTime;
	}

	public void setMaintenanceStartTime(int maintenanceStartTime) {
		this.maintenanceStartTime = maintenanceStartTime;
	}

	public int getMaintenanceEndTime() {
		return maintenanceEndTime;
	}

	public void setMaintenanceEndTime(int maintenanceEndTime) {
		this.maintenanceEndTime = maintenanceEndTime;
	}

	public String getMaintenanceMapping() {
		return maintenanceMapping;
	}

	public void setMaintenanceMapping(String maintenanceMapping) {
		this.maintenanceMapping = maintenanceMapping;
	}

	//before the actual handler will be executed
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler)
	    throws Exception {
		TimeZone istTimeZone = TimeZone.getTimeZone("Asia/Kolkata");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(istTimeZone);
		
		int hour = cal.get(cal.HOUR_OF_DAY);
		System.out.println("Hour of the day"+hour);
		if (hour >= maintenanceStartTime && hour <= maintenanceEndTime) {
	//		System.out.println("Maintance :"+maintenanceStartTime+" End :"+maintenanceEndTime);
			//maintenance time, send to maintenance page
			response.sendRedirect(request.getContextPath()+"/maintenance");
			return false;
		} else {
			return true;
		}
		
	}
}