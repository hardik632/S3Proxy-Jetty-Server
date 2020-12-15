package com.example.AmazonS3.API;


import com.example.AmazonS3.SignatureCalculation.CanonicalRequestCalculation;
import com.example.AmazonS3.SignatureCalculation.SignatureCalculation;
import com.example.AmazonS3.SignatureCalculation.StringToSignCalculation;
import com.example.AmazonS3.util.Crypto;
import com.example.AmazonS3.util.HttpUtils;
import com.example.AmazonS3.util.ToHash;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class S3Api {

    static String service = "s3";
    static String algorithm = "AWS4-HMAC-SHA256"; // algorithm for string to sign
    static String amz_date;
    static String today_date;
    static String response;
    static String signed_headers;
    static Map<String, String> headers = new HashMap<>();
    static String method;
    static String host;
    static String scope;
    static String salt = "itisthesecretkey";
    static String token = "";

    public static String Signature(String method, String host, String key, String bucket, String region, String payload, String secret_key, String access_key, String request_parameter) throws Exception {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(utc);
        amz_date = (sdf.format(today));

        Date today1 = new Date();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        sdf1.setTimeZone(utc);
        today_date = (sdf1.format(today1));
        scope = today_date + '/' + region + '/' + service + '/' + "aws4_request";

        signed_headers = "host;x-amz-content-sha256;x-amz-date;x-amz-security-token";
        String canonical_headers = "host:" + host + '\n' + "x-amz-content-sha256:" + payload + '\n' + "x-amz-date:" + amz_date + '\n' + "x-amz-security-token:" + token +"\n";
        String canonical_request = CanonicalRequestCalculation.canonicalRequest(method, key, bucket, region, payload, signed_headers, canonical_headers, request_parameter);
        String string_to_sign = StringToSignCalculation.stringToSign(algorithm, scope, canonical_request);
        String signature_hash = SignatureCalculation.signature(secret_key, region, service, string_to_sign);
        String authorization = algorithm + ' ' + "Credential=" + access_key + '/' + scope + ", "
                + "SignedHeaders=" + signed_headers + ", " + "Signature=" + signature_hash;

//        System.out.println("-------------------------------");
//        System.out.println(canonical_request);
//        System.out.println(string_to_sign);
//        System.out.println(signature_hash);
//        System.out.println(authorization);
//        System.out.println("-------------------------------");

        return authorization;
    }

    public static String getS3Object(String key, String bucket, String region, String access_key, String secret_key, Map<String, String> headers1, String request_parameter)
            throws Exception {

        TimeZone utc = TimeZone.getTimeZone("UTC");
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(utc);
        amz_date = (sdf.format(today));

        method = "GET";
        host = bucket + ".s3." + "amazonaws.com";
        URL endpoint = new URL("https://" + host + "/" + key+ "?" + request_parameter);
        String payload = ToHash.toHexString("");
        String authorization = Signature(method, host, key, bucket, region, payload, secret_key, access_key, request_parameter);

        headers.put("x-amz-date", amz_date);
        headers.put("x-amz-content-sha256", payload);
        headers.put("Authorization", authorization);
        headers.put("x-amz-security-token" , token);

        response = HttpUtils.invokeHttpRequest(endpoint, "GET", headers, null);
        String decryptText=Crypto.decrypt(response,salt);
        return decryptText;

    }

    public static void putS3Object(String key, String bucket, String content, String region, String access_key,
                                   String secret_key, Map<String, String> headers1, String request_parameter) throws Exception {

        TimeZone utc = TimeZone.getTimeZone("UTC");
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(utc);
        amz_date = (sdf.format(today));

        method = "PUT";
        host = bucket + ".s3." + "amazonaws.com";
        URL endpoint = new URL("https://" + host + "/" + key + "?" + request_parameter);

        scope = today_date + '/' + region + '/' + service + '/' + "aws4_request";
        String payload = "UNSIGNED-PAYLOAD";
        String authorization = Signature(method, host, key, bucket, region, payload, secret_key, access_key
                , request_parameter);

        headers.put("x-amz-date", amz_date);
        headers.put("x-amz-content-sha256", payload);
        headers.put("Authorization", authorization);
        headers.put("x-amz-security-token" , token);

        String encryptText = Crypto.encrypt(content,salt);
        response = HttpUtils.invokeHttpRequest(endpoint, "PUT", headers, encryptText);

    }

}

