package io.guruinfotech.coronavirustracker.controllers;

import java.io.IOException;
import java.util.Calendar;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.guruinfotech.coronavirustracker.interceptor.MaintenanceInterceptor;
import io.guruinfotech.coronavirustracker.models.HelplineNumbers;
import io.guruinfotech.coronavirustracker.models.LocationStats;
import io.guruinfotech.coronavirustracker.models.LocationStatsDeath;
import io.guruinfotech.coronavirustracker.models.LocationStatsRecovered;
import io.guruinfotech.coronavirustracker.services.CoronaVirusDataService;
import io.guruinfotech.coronavirustracker.services.CoronavirusSmsService;
import io.swagger.annotations.Api;

@Controller
@Api(value = "Home Controller",description = "Getting Virus Data and update url and scrolling message")
public class HomeController implements ErrorController {

	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	@Autowired
	private CoronavirusSmsService coronavirusSmsService;
	@Autowired
	private MaintenanceInterceptor interceptor;
	@Autowired
	private MaintenanceController maintenanceController;

	private String date;
	private String numbers;
	
	public static String CONFIRMED_GLOBAL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	public static String DEATH_GLOBAL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	public static String VIRUS_RECOVERED_DATA = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	
	@GetMapping("/confirmurl**")
	public String urlUpdate(@RequestParam(name = "url",required = true) String url) {
		this.CONFIRMED_GLOBAL = url;
		return "update";
	}
	
	@GetMapping("/recoverurl**")
	public String recoverUrl(@RequestParam(name = "url",required = true) String url) {
		this.VIRUS_RECOVERED_DATA = url;
		return "update";
	}
	
	@GetMapping("/deathurl**")
	public String urlDeathUrl(@RequestParam("url") String url) {
		this.DEATH_GLOBAL = url;
		return "update";
	}
	
	@GetMapping("/contact**")
	public String contact(Model model) {
		return "contact";
	}

	@GetMapping("/symptoms**")
	public String symtoms(Model model) {
		return "symptoms";
	}

	@GetMapping("/incubation**")
	public String incubation(Model model) {
		return "incubation";
	}

	@GetMapping("/deathrate**")
	public String deathrate(Model model) {
		return "deathrate";
	}

	@RequestMapping(value = { "/helpline**" }, method = { RequestMethod.GET })
	public String getMuNumbers(Model model) {
		List<HelplineNumbers> numbers = coronaVirusDataService.getNumbers();
		model.addAttribute("locationStats", numbers);
		System.out.println("Return data");
		return "helpline";
	}

	@RequestMapping(value = { "/indian/start/{time}/end/{number}" }, method = { RequestMethod.GET })
	public String getMuNumbers(@PathVariable("time") String time, @PathVariable("number") String number, Model model) {
		if (time.equalsIgnoreCase("null")) {
			this.date=null;
			this.numbers=null;
		} else {
			this.date = time;
			this.numbers = number;
		}
		System.out.println(time + "=======" + number);
		model.addAttribute("date", date);
		model.addAttribute("numbers", numbers);
		return "update";
	}

	@RequestMapping(value = { "/", "/index**" }, method = { RequestMethod.GET })
	public String home(Model model) throws IOException, InterruptedException {

		coronaVirusDataService.fetchVirusData();
		coronaVirusDataService.getRecoveredData();
		Set<LocationStatsDeath> deathData = coronaVirusDataService.getDeathData();
		
		// Map<String, Integer> collect = deathData.stream().collect(Collectors.groupingBy(LocationStatsDeath::getCountry, Collectors.summingInt(LocationStatsDeath::getDeath)));
	//	System.out.println(collect);
		Map<String, Integer> indianData = coronaVirusDataService.getIndianData();

		int totalPositiveCases = coronaVirusDataService.getTotalPositiveCases();
		int totalRecoverCases = coronaVirusDataService.getTotalRecoverCases();
		int totalDeathCases = coronaVirusDataService.getTotalDeathCases();

		int totalActiveCases = totalPositiveCases - (totalRecoverCases + totalDeathCases);

		model.addAttribute("totalPositiveCases", totalPositiveCases);
		model.addAttribute("totalRecoverCases", totalRecoverCases);
		model.addAttribute("totalDeathCases", totalDeathCases);
		model.addAttribute("totalActiveCases", totalActiveCases);
		model.addAttribute("india", indianData);
		model.addAttribute("date", date);
		model.addAttribute("numbers", numbers);
		return "index";
	}

	@RequestMapping(value = "/positive", method = { RequestMethod.GET })
	public String positiveCases(Model model) throws IOException, InterruptedException {
		List<LocationStats> allStats = coronaVirusDataService.fetchVirusData();
		int totalReportedCases = coronaVirusDataService.getTotalPositiveCases();
		int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
		long totalCountries = coronaVirusDataService.totalAffetectedCountries().stream().count();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("totalNewCases", totalNewCases);
		model.addAttribute("start", interceptor.getMaintenanceStartTime());
		model.addAttribute("end", interceptor.getMaintenanceEndTime());
		model.addAttribute("totalcountries", totalCountries);

		return "positive";
	}

	@RequestMapping(value = "/recover", method = { RequestMethod.GET })
	public String recoveredData(Model model) throws IOException, InterruptedException {
		Set<LocationStatsRecovered> allStats = coronaVirusDataService.getRecoveredData();
		int totalRecoveredCases = allStats.stream().mapToInt(stat -> stat.getRecoverd()).sum();
		// System.out.println("Total Recoved Cases :" + totalRecoveredCases);
		long totalCountries = coronaVirusDataService.recovedCountries().stream().count();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalRecoveredCases);

		model.addAttribute("start", interceptor.getMaintenanceStartTime());
		model.addAttribute("end", interceptor.getMaintenanceEndTime());
		model.addAttribute("totalcountries", totalCountries);

		return "recovered";
	}

	@RequestMapping(value = "/death", method = { RequestMethod.GET })
	public String deathData(Model model) throws IOException, InterruptedException {
		Set<LocationStatsDeath> allStats = coronaVirusDataService.getDeathData();
		int totalDeathCases = allStats.stream().mapToInt(stat -> stat.getDeath()).sum();

		long totalCountries = coronaVirusDataService.deathCountries().stream().count();
		model.addAttribute("locationStats", allStats);
		model.addAttribute("totalReportedCases", totalDeathCases);

		model.addAttribute("start", interceptor.getMaintenanceStartTime());
		model.addAttribute("end", interceptor.getMaintenanceEndTime());
		model.addAttribute("totalcountries", totalCountries);

		return "death";
	}

	@RequestMapping(value = "/sms", method = { RequestMethod.GET })
	public String sendSmsData(@RequestParam("mobile") String mobileno) throws IOException, InterruptedException {

		int start = maintenanceController.getSmsStart();
		int end = maintenanceController.getSmsend();
		TimeZone istTimeZone = TimeZone.getTimeZone("Asia/Kolkata");
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(istTimeZone);

		int hour = cal.get(cal.HOUR_OF_DAY);
		// System.out.println("start time:"+start+" End time :"+end);
		// System.out.println("Hour of the day :"+hour);
		if (hour >= start && hour <= end) {
			return "smsmaintenance";
		} else {
			String sendSmsData = coronavirusSmsService.sendSmsCoronavirusData(mobileno);
			// System.out.println("Mobile Number:" + mobileno + " Status :" + sendSmsData);

			if (sendSmsData.equalsIgnoreCase("Smssent")) {
				return "smssent";
			} else if (sendSmsData.equalsIgnoreCase("Smsfail")) {
				return "smsfail";
			}
		}
		return "index";
	}

	@RequestMapping("/maintenance")
	public String maintenance() {
		return "maintenance";
	}

	@Override
	public String getErrorPath() {
		return "/error";
	}

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if (statusCode == HttpStatus.NOT_FOUND.value()) {
				return "error";
			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				return "servererror";
			}
		}
		return "error";
	}

	@RequestMapping("/servererror")
	public String handleError() throws Exception {
		throw new Exception("something went wrong in server");
	}

}
