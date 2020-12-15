package com.example.AmazonS3.SignatureCalculation;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SignatureCalculation {
	public static String signature(String secret_key, String region, String service, String string_to_sign)
			throws Exception {

		//Dates dates = new Dates();
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		TimeZone utc = TimeZone.getTimeZone("UTC");
		sdf.setTimeZone(utc);
		String date = (sdf.format(today));

		byte[] sign_key = getSignatureKey(secret_key, date, region, service);
		byte[] signature = HmacSHA256(string_to_sign, sign_key);
		StringBuilder signature_hex = new StringBuilder();
		for (byte b : signature) {
			signature_hex.append(String.format("%02x", b));
		}
		return signature_hex.toString();
	}

	static byte[] HmacSHA256(String data, byte[] key) throws Exception {
		String algorithm = "HmacSHA256";
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	}

	static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName)
			throws Exception {
		byte[] kSecret = ("AWS4" + key).getBytes(StandardCharsets.UTF_8);
		byte[] kDate = HmacSHA256(dateStamp, kSecret);
		byte[] kRegion = HmacSHA256(regionName, kDate);
		byte[] kService = HmacSHA256(serviceName, kRegion);
		return HmacSHA256("aws4_request", kService);
	}
}
