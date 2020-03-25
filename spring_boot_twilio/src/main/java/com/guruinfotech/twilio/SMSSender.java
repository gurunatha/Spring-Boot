package com.guruinfotech.twilio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Random;

public class SMSSender {
	static String url = "https://www.sms4india.com";
	static String API_KEY = "P748JWAX19S0K6FF5TNA9ZOYHRU3ET4C";
	static String SECREAT_KEY ="Y1QONCOVETSIPUWO";

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		
		/*
		 * URL url = new URL(
		 * "https://2factor.in/API/V1/44ceaab2-6469-11ea-9fa5-0200cd936042/SMS/+918332851353/123456"
		 * ); URLConnection connection = url.openConnection(); BufferedReader
		 * bufferedReader = new BufferedReader(new
		 * InputStreamReader(connection.getInputStream())); String inputLine;
		 * while((inputLine = bufferedReader.readLine()) !=null){
		 * System.out.println(inputLine); } bufferedReader.close();
		 */
		/*
		 * String sendCampaign = sendCampaign(API_KEY, SECREAT_KEY, "stage",
		 * "8332851353", "Hello world ", "SMSIND");
		 * 
		 * System.out.println("--------"+sendCampaign);
		 */
		Random r = new Random();
		System.out.println(((1+r.nextInt(9))*1000)+r.nextInt(1000));
		

	}
	
	public static String sendCampaign(String apiKey, String secretKey, String useType, String phone, String message, String senderId1){
	      try{
	          // Construct data
	          // accesskeys generated ad www.way2sms.com
	          String token = "apikey=" + URLEncoder.encode(apiKey, "UTF-8");
	          String secret = "&secret=" + URLEncoder.encode(secretKey, "UTF-8");
	          // usetype "stage" or "prod" must be given
	          String usetype = "&usetype=" + URLEncoder.encode(useType, "UTF-8");
	          String mobile = "&phone=" + URLEncoder.encode(phone, "UTF-8");
	          String messageText = "&message=" + URLEncoder.encode(message, "UTF-8");
	          String senderId = "&senderid=" + URLEncoder.encode(senderId1, "UTF-8");
	          //send data
	          URL obj = new URL(url + "/api/v1/sendCampaign?"+token+secret+usetype+mobile+messageText+senderId);
	          HttpURLConnection httpConnection = (HttpURLConnection) obj.openConnection();
	          httpConnection.setDoOutput(true);
	          // Get the response
	          BufferedReader bufferedReader = null;
	          if (httpConnection.getResponseCode() == 200) {
	              bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
	          } else {
	              bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getErrorStream()));
	          }
	        
	          StringBuilder content = new StringBuilder();
	          String line;
	          while ((line = bufferedReader.readLine()) != null) {
	              content.append(line).append("\n");
	          }
	          bufferedReader.close();
	          return content.toString();
	      }catch(Exception ex){
	        System.out.println("Exception at:"+ex);
	        return "{'status':500,'message':'Internal Server Error'}";
	      }
	    }
	
		

}
