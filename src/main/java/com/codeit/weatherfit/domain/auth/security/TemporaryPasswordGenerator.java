package com.codeit.weatherfit.domain.auth.security;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class TemporaryPasswordGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 12;

    private final SecureRandom secureRandom = new SecureRandom();

    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int index = 0; index < PASSWORD_LENGTH; index++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            stringBuilder.append(CHARACTERS.charAt(randomIndex));
        }

        return stringBuilder.toString();
    }
}