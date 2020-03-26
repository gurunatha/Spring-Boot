package io.guruinfotech.coronavirustracker.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.guruinfotech.coronavirustracker.controllers.HomeController;
import io.guruinfotech.coronavirustracker.models.HelplineNumbers;
import io.guruinfotech.coronavirustracker.models.LocationStats;
import io.guruinfotech.coronavirustracker.models.LocationStatsDeath;
import io.guruinfotech.coronavirustracker.models.LocationStatsRecovered;

@Service
public class CoronaVirusDataService {
	
	@Autowired
	private CoronavirusSmsService coronavirusSmsService;

	private static String VIRUS_DATA_INDIA_URL = "https://raw.githubusercontent.com/gurunatha/coronavirus/master/coronavirus_india.csv";

	private static String VIRUS_DATA_INDIA_RECOVERED_URL = "https://raw.githubusercontent.com/gurunatha/coronavirus/master/coronavirus_india_recover.csv";
	
	private static String INDIA_HELPLINE_NUMBERS ="https://raw.githubusercontent.com/gurunatha/coronavirus/master/coronavirus_helpline.csv";
	private List<LocationStats> allStats = new ArrayList<>();
	private Set<String> affectedCountries = new HashSet<>();
	private Set<String> recoverCountries = new HashSet<>();
	private Set<String> deathCounties = new HashSet<>();
	private int totalPositiveCases;
	private int totalRecoverCases;
	private int totalDeathCases;

	public int getTotalPositiveCases() {
		return totalPositiveCases;
	}

	public int getTotalRecoverCases() {
		return totalRecoverCases;
	}

	public int getTotalDeathCases() {
		return totalDeathCases;
	}

	public List<LocationStats> getAllStats() {
		return allStats;
	}

	public Set<String> totalAffetectedCountries() {
		return affectedCountries;
	}

	public Set<String> recovedCountries() {
		return recoverCountries;
	}

	public Set<String> deathCountries() {
		return deathCounties;
	}

	@PostConstruct
	// @Scheduled(cron = "* * 1 * * *")
	public List<LocationStats> fetchVirusData() throws IOException, InterruptedException {

		Set<LocationStats> newStats = new HashSet<>();

		StringReader csvBodyReader = getRawDate(HomeController.CONFIRMED_GLOBAL);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			int latestCases = 0;
			int prevDayCases = 0;
			LocationStats locationStat = new LocationStats();
			locationStat.setState(record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));
			locationStat.setLat(Float.parseFloat(record.get("Lat")));
			locationStat.setLongt(Float.parseFloat(record.get("Long")));
			
			affectedCountries.add(record.get("Country/Region").trim());
			
			int latest =0;
			for (int i = record.size() - 1; i >= 4; i--) {
				if (record.get(i).trim().equals("")) {
					continue;
				}else if(record.get(i).trim().equals("")&&i==4) {
					latestCases = Integer.parseInt(String.valueOf("0"));
					break;
				}else {
					latest = i;
					latestCases = Integer.parseInt(String.valueOf(record.get(i)));
					break;
				}
			}
			int privous = latest-1;
			for (int i = privous; i >= 4; i--) {
				if (record.get(i).trim().equals("")) {
					continue;
				}else if(record.get(i).trim().equals("")&&i==4) {
					prevDayCases = Integer.parseInt(String.valueOf("0"));
					break;
				}else {
					prevDayCases = Integer.parseInt(String.valueOf(record.get(i)));
					break;
				}
			}
			
			
			locationStat.setLatestTotalCases(latestCases);
			locationStat.setDiffFromPrevDay(latestCases - prevDayCases);
			newStats.add(locationStat);
		}
		
		List l = new ArrayList<>(newStats);
		this.allStats = l;
		getUpdateIndiaCases(this.allStats);

		return this.allStats;
	}

	private void getUpdateIndiaCases(List<LocationStats> newStats) throws MalformedURLException, IOException {

		StringReader csvBodyReader = getRawDate(VIRUS_DATA_INDIA_URL);
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		List<LocationStats> countryList = new ArrayList<>();
		for (CSVRecord record : records) {
			LocationStats locationStat = new LocationStats();
			locationStat.setState(record.get("state"));
			locationStat.setCountry("India");
			locationStat.setLatestTotalCases(Integer.parseInt(record.get("totalcases")));
			countryList.add(locationStat);
		}
		int totalCases = countryList.stream().mapToInt(m -> m.getLatestTotalCases()).sum();
		for (int i = 0; i < newStats.size(); i++) {
			if (newStats.get(i).getCountry().equalsIgnoreCase("india")) {
				LocationStats locationStats=null;
				
					int total = newStats.get(i).getLatestTotalCases();
					 locationStats = allStats.get(i);
					locationStats.setLatestTotalCases(totalCases);
					int diff = locationStats.getDiffFromPrevDay() + (totalCases - total);
					locationStats.setDiffFromPrevDay(diff);
					this.allStats.set(i, locationStats);
					this.allStats.addAll(i, countryList);
					
				
				if(locationStats!=null) {
				this.totalPositiveCases = this.allStats.stream().mapToInt(m -> m.getLatestTotalCases()).sum()
						- locationStats.getLatestTotalCases();
				
				}else {
					this.totalPositiveCases = this.allStats.stream().mapToInt(m -> m.getLatestTotalCases()).sum();
				}
				
				break;
			}
		}

	}

	public StringReader getRawDate(String url) throws IOException, MalformedURLException, ProtocolException {
		HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
		httpClient.setRequestMethod("GET");
		StringBuilder response = new StringBuilder();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {
			String line;
			while ((line = in.readLine()) != null) {
				response.append(line);
				response.append("\n");
			}
		}
		httpClient.disconnect();
		StringReader csvBodyReader = new StringReader(response.toString());
		return csvBodyReader;
	}

	@PostConstruct
	public Set<LocationStatsRecovered> getRecoveredData()
			throws MalformedURLException, ProtocolException, IOException {
		Set<LocationStatsRecovered> recoveredDate = new HashSet<LocationStatsRecovered>();
		StringReader csvBodyReader = getRawDate(HomeController.VIRUS_RECOVERED_DATA);

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

		for (CSVRecord record : records) {
			LocationStatsRecovered locationStat = new LocationStatsRecovered();
			locationStat.setState(record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));
			locationStat.setLat(Float.parseFloat(record.get("Lat")));
			locationStat.setLongt(Float.parseFloat(record.get("Long")));
			for (int i = record.size() - 1; i >= 4; i--) {
				if (record.get(i).trim().equals("")) {
					continue;
				} else {
					locationStat.setRecoverd(Integer.parseInt(record.get(i)));
					break;
				}
			}

			for (int i = record.size() - 1; i >= 4; i--) {
				if (record.get(i).trim().equals("")) {
					continue;
				} else if (Integer.parseInt(record.get(i)) > 0) {
					recoverCountries.add(record.get("Country/Region").trim());
					break;
				} else {
					break;
				}
			}

			recoveredDate.add(locationStat);
		}
		this.totalRecoverCases = recoveredDate.stream().mapToInt(m -> m.getRecoverd()).sum();
		return recoveredDate;
	}

	@PostConstruct
	public Set<LocationStatsDeath> getDeathData() throws MalformedURLException, ProtocolException, IOException {
		Set<LocationStatsDeath> deathData = new HashSet<LocationStatsDeath>();
		StringReader csvBodyReader = getRawDate(HomeController.DEATH_GLOBAL);

		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {

			LocationStatsDeath locationStat = new LocationStatsDeath();
			locationStat.setState(record.get("Province/State"));
			locationStat.setCountry(record.get("Country/Region"));
			locationStat.setLat(Float.parseFloat(record.get("Lat")));
			locationStat.setLongt(Float.parseFloat(record.get("Long")));
			for (int i = record.size() - 1; i >= 4; i--) {
				if (record.get(i).trim().equals("")) {
					continue;
				} else {
					locationStat.setDeath(Integer.parseInt(record.get(i)));
					
					break;
				}
			}

			for (int i = record.size() - 1; i >= 4; i--) {
				if (record.get(i).trim().equals("")) {
					continue;
				} else if (Integer.parseInt(record.get(i)) > 0) {
					deathCounties.add(record.get("Country/Region").trim());
					break;
				} else {
					break;
				}
			}
			deathData.add(locationStat);

		}
		
		this.totalDeathCases = deathData.stream().mapToInt(m -> m.getDeath()).sum();
	
		return deathData;
	}
	
	public String sendSmsData(String frommobilenumber) {
		try {
			coronavirusSmsService.sendSmsCoronavirusData(frommobilenumber);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Sms Sent Successfully";
	}
	
	public List<HelplineNumbers> getNumbers(){
		
		try {
			StringReader csvBodyReader = getRawDate(INDIA_HELPLINE_NUMBERS);
			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
			List<HelplineNumbers> list = new ArrayList<>();
			for (CSVRecord record : records) {
				HelplineNumbers locationStat = new HelplineNumbers();
				locationStat.setState(record.get("state"));
				locationStat.setNumber(record.get("number"));
				list.add(locationStat);
				
			}
			return list;
		} catch (IOException e) {
			System.out.println("Exception Helpline numbers");
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	public Map<String,Integer> getIndianData(){
		try {
			StringReader csvBodyReader = getRawDate(VIRUS_DATA_INDIA_URL);
			StringReader csvBodyReaderRecovered =getRawDate(VIRUS_DATA_INDIA_RECOVERED_URL);
			
			List<LocationStats> positivateData = new ArrayList<LocationStats>();
			

			Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
			Iterable<CSVRecord> recordsRecovered = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReaderRecovered);

			for (CSVRecord record : records) {
				LocationStats locationStat = new LocationStats();
				locationStat.setState(record.get("state"));
				locationStat.setLatestTotalCases(Integer.parseInt(record.get("totalcases")));
				positivateData.add(locationStat);
			}
			String recoverd="";
			String foreign="";
			String death="";
			for (CSVRecord record : recordsRecovered) {
				recoverd = record.get("recovered");
				foreign = record.get("foreign");
				death = record.get("death");
			}
			int total = positivateData.stream().mapToInt(m->m.getLatestTotalCases()).sum();
			int recoverCases = Integer.parseInt(recoverd);
			int foreignCases = Integer.parseInt(foreign);
			int deathCases = Integer.parseInt(death);
			int activeCases = total-(recoverCases+deathCases);
			Map<String,Integer> map = new HashMap<>();
			map.put("total", total);
			map.put("recover", recoverCases);
			map.put("foreign", foreignCases);
			map.put("death", deathCases);
			map.put("active", activeCases);
			return map;
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new HashMap<>();
		
	}

}
