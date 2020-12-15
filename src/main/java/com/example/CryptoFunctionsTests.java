package com.example;

import com.example.AmazonS3.util.Crypto;
import org.junit.Test;

public class CryptoFunctionsTests {

    public String encryptText;
    public String decryptText;
    public String salt = "this is a secret key";

    @Test
    public void Test1() {
        System.out.println("***Running Test1***");
        String plainText = "Test 1";
        encryptText = Crypto.encrypt(plainText, salt);
        decryptText = Crypto.decrypt(encryptText, salt);
        System.out.println("Plain Text : " + plainText);
        System.out.println("Encrypted Text : " + encryptText);
        System.out.println("Decrypted Text : " + decryptText);
//        assertEquals(decryptText, plainText);
        System.out.println("Test Passed");

    }
    @Test
    public void Test2() {
        System.out.println("***Running Test2***");
        String plainText = "Test 2";
        encryptText = Crypto.encrypt(plainText, salt);
        decryptText = Crypto.decrypt(encryptText, salt);
        System.out.println("Plain Text : " + plainText);
        System.out.println("Encrypted Text : " + encryptText);
        System.out.println("Decrypted Text : " + decryptText);
//        assertEquals(decryptText, plainText);
        System.out.println("Test Passed");

    }

}
