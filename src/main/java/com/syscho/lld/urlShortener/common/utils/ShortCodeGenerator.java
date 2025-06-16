package com.syscho.lld.urlShortener.common.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.syscho.lld.urlShortener.common.dao.UrlRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int MAX_ATTEMPTS = 3;

    private final UrlRepository urlRepository;

    public ShortCodeGenerator(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String generateUniqueShortCode(Integer length) {
        int attempt = 0;
        String shortCode;

        do {
            shortCode = (length != null)
                    ? NanoIdUtils.randomNanoId(RANDOM, ALPHABET, length)
                    : NanoIdUtils.randomNanoId();

            attempt++;
            if (attempt > MAX_ATTEMPTS) {
                throw new RuntimeException("Failed to generate unique short code after " + MAX_ATTEMPTS + " attempts");
            }
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }
}


