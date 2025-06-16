package com.syscho.lld.urlShortener.url.validator;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UrlValidatorService {

    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://)?[\\w.-]+(:\\d+)?(/.*)?$");

    public void validateUrl(String url) {
        if (url == null || !URL_PATTERN.matcher(url).matches()) {
            throw new IllegalArgumentException("Invalid URL format.");
        }
    }
}
