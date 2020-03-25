package com.guruinfotech.twilio;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@SpringBootApplication
public class SpringBootTwilioApplication  implements CommandLineRunner{
	
	private final static String ACCOUNT_ID = "AC799eed7fa48bd9e63a85a8345333f814";
	private final static String AUTH_TOKEN = "1598851e27ec73243a91a856832dee90";

	public static void main(String[] args) {
		SpringApplication.run(SpringBootTwilioApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Twilio.init(ACCOUNT_ID,AUTH_TOKEN);
		Message.creator(new PhoneNumber("+918332851353"), new PhoneNumber("+12816773899"), "Hi This is Test Message n.....").create();
	}

	
}
