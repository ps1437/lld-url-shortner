package com.syscho.lld.urlShortener.common.utils;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.syscho.lld.urlShortener.common.dao.UrlRepository;
import org.springframework.stereotype.Component;

@Component
public class ShortCodeGenerator {

    private final UrlRepository urlRepository;

    public ShortCodeGenerator(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public String generateUniqueShortCode(int maxAttempts) {
        int attempt = 0;
        String shortCode;

        do {
            shortCode = NanoIdUtils.randomNanoId();
            attempt++;
            if (attempt > maxAttempts) {
                throw new RuntimeException("Failed to generate unique short code after " + maxAttempts + " attempts");
            }
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }
}
