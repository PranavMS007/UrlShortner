package com.entain.urlshortener.util;

import java.security.SecureRandom;

/**
 * The ShortCodeGenerator class provides functionality to generate random short codes
 * consisting of alphanumeric characters. These codes can be used for cases such as
 * URL shortening or generating unique identifiers.
 */
public class ShortCodeGenerator {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
