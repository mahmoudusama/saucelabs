package com.sauceLabs.common.utils.generator;

import java.security.SecureRandom;

public class Generator {
    private static final String NameString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String AlphaNumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz";
    private static final String NumbersString = "0123456789";
    private static final SecureRandom Random = new SecureRandom();

    // Generates a random name with 10 characters
    public static String generateRandomName(int nameLength) {
        StringBuilder sb = new StringBuilder(nameLength);
        for (int i = 0; i < nameLength; i++) {
            int index = Random.nextInt(NameString.length());
            sb.append(NameString.charAt(index));
        }
        return sb.toString();
    }

    // Generates a random alphanumeric string
    public static String generateRandomAlphaNumeric(int alphaNumericLength) {
        StringBuilder sb = new StringBuilder(alphaNumericLength);
        for (int i = 0; i < alphaNumericLength; i++) {
            int index = Random.nextInt(AlphaNumeric.length());
            sb.append(AlphaNumeric.charAt(index));
        }
        return sb.toString();
    }

    // Generates a random number string with the specified length
    public static String generateRandomNumbers(int numberslength) {
        StringBuilder sb = new StringBuilder(numberslength);
        for (int i = 0; i < numberslength; i++) {
            int index = Random.nextInt(NumbersString.length());
            sb.append(NumbersString.charAt(index));
        }
        return sb.toString();
    }

    // Generates a random phone number with the specified country code and length
    public static String generateRandomPhoneNumber(String countryCode, int phoneNumberLength) {
        StringBuilder sb = new StringBuilder(countryCode);
        for (int i = 0; i < phoneNumberLength; i++) {
            int index = Random.nextInt(NumbersString.length());
            sb.append(NumbersString.charAt(index));
        }
        return sb.toString();
    }

    // Generates a random email with the specified domain. Example "gmail.com"
    public static String generateRandomEmail(String domain) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = Random.nextInt(NameString.length());
            sb.append(NameString.charAt(index));
        }
        sb.append("@").append(domain);
        return sb.toString();
    }
}