package com.syscho.lld.urlShortener.url.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlAnalyticsResponse {
    private String shortCode;
    private String originalUrl;
    private long clickCount;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime expiryTime;
}
