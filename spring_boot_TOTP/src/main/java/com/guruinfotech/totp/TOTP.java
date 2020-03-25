package com.guruinfotech.totp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

import org.apache.commons.codec.binary.Base32;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;


@RestController
public class TOTP {
	private String key="";
	private String path ="C:\\Users\\guru\\Desktop\\guru\\key.png";

	@GetMapping("/2FA")
	public String generateSecreateKey() throws WriterException, IOException {

		String secreatKey = generateSecretKey();
		this.key=secreatKey;
		String googleAuthenticatorBarCode = getGoogleAuthenticatorBarCode(secreatKey, "guru", "Test Company");
		
		createQRCode(googleAuthenticatorBarCode, path, 300, 300);
		return secreatKey;
		
	}
	@GetMapping("/2FA/validate/{code}")
	public String validateKey(@PathVariable("code") String code) {
		Totp t = new Totp(this.key);
		boolean verify = t.verify(code);
		if(verify) {
			System.out.println("OTP Verified");
			return "Validated Successfully";
		}
		return "Failed";
	}
	
	public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
	    String normalizedBase32Key = secretKey.replace(" ", "").toUpperCase();
	    try {
	        return "otpauth://totp/"
	        + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
	        + "?secret=" + URLEncoder.encode(normalizedBase32Key, "UTF-8").replace("+", "%20")
	        + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
	    } catch (UnsupportedEncodingException e) {
	        throw new IllegalStateException(e);
	    }
	}
	
	public static void createQRCode(String barCodeData, String filePath, int height, int width)
            throws WriterException, IOException {
    BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
            width, height);
    try (FileOutputStream out = new FileOutputStream(filePath)) {
        MatrixToImageWriter.writeToStream(matrix, "png", out);
    }
}
	
	public static String generateSecretKey() {
	    SecureRandom random = new SecureRandom();
	    byte[] bytes = new byte[20];
	    random.nextBytes(bytes);
	    Base32 base32 = new Base32();
	    return base32.encodeToString(bytes);
	}
}