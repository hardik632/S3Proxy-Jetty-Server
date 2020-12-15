package com.example.AmazonS3.SignatureCalculation;

import com.example.AmazonS3.util.ToHash;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class StringToSignCalculation {

    public static String stringToSign(String algorithm, String scope, String canonical_request)
            throws Exception {

        //Dates dates = new Dates();

        String amz_date;
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        TimeZone utc = TimeZone.getTimeZone("UTC");
        sdf.setTimeZone(utc);
        amz_date = (sdf.format(today));

        return algorithm + '\n' + amz_date + '\n' + scope + '\n'
                + ToHash.toHexString(canonical_request);

    }

}
