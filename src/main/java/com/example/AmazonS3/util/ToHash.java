package com.example.AmazonS3.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ToHash {

    public static String toHexString(String input) throws Exception {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashInBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

        // bytes to hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashInBytes) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }
}
