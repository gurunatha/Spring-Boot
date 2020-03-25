package io.guruinfotech.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import io.guruinfotech.coronavirustracker.models.LocationStats;

@Service
public class CoronavirusSmsService {
	
	@Autowired
	private CoronaVirusDataService coronaVirusDataService;
	
	private static String VIRUS_DATA_INDIA_URL = "https://raw.githubusercontent.com/gurunatha/coronavirus/master/coronavirus_india.csv";
	private static String VIRUS_DATA_INDIA_RECOVERED_URL = "https://raw.githubusercontent.com/gurunatha/coronavirus/master/coronavirus_india_recover.csv";
	
	private final static String ACCOUNT_ID = "AC799eed7fa48bd9e63a85a8345333f814";
	private final static String AUTH_TOKEN = "1598851e27ec73243a91a856832dee90";
	
	
	public String sendSmsCoronavirusData(String fromMobileNumber) throws MalformedURLException, ProtocolException, IOException {
		try {
		StringReader csvBodyReader = coronaVirusDataService.getRawDate(VIRUS_DATA_INDIA_URL);
		StringReader csvBodyReaderRecovered = coronaVirusDataService.getRawDate(VIRUS_DATA_INDIA_RECOVERED_URL);
		
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
		
		
		boolean isFirst = false;
		StringBuilder builder = new StringBuilder();
		builder.append("Total Coronavirus Positive Cases In India");
		for(LocationStats l: positivateData) {
			if(!isFirst) {
				builder.append("\n");
				builder.append(l.getState()+"  "+l.getLatestTotalCases()+",");
				builder.append("\n");
				isFirst=true;
			}else {
			builder.append(l.getState()+"  "+l.getLatestTotalCases()+",");
			builder.append("\n");
			}
		}
		
		builder.append("Total Number Of Cases"+total).append(",").append("\n");
		builder.append("Recovered Cases :"+recoverd).append(",").append("\n");
		builder.append("Foreign Cases :"+foreign).append(",").append("\n");
		builder.append("Death Cases :"+deathCases).append(",").append("\n");
		builder.append("Number Of Active Cases in India :"+activeCases);
		
		
		Twilio.init(ACCOUNT_ID,AUTH_TOKEN);
			System.out.println("Twilio try to send sms :"+fromMobileNumber);
			StringBuilder phonenumber = new StringBuilder();
			phonenumber.append("+91");
			phonenumber.append(fromMobileNumber);
		//	System.out.println("Sms Service :"+phonenumber.toString());
			Message.creator(new PhoneNumber(phonenumber.toString()), new PhoneNumber("+12816773899"), builder.toString()).create();
			return "Smssent";
		} catch (Exception e) {
			e.printStackTrace();
			return "Smsfail";
		}
		
	}

}
