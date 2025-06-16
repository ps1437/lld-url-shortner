package com.syscho.lld.urlShortener.url.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlResponse {
    private String shortUrl;
    private String originalUrl;
    private long clickCount;
    private boolean active;
    private LocalDateTime expiryTime;

}