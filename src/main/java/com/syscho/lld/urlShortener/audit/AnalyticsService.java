package com.syscho.lld.urlShortener.audit;


import com.syscho.lld.urlShortener.common.dao.entity.UrlMappingEntity;
import com.syscho.lld.urlShortener.common.dao.UrlRepository;
import com.syscho.lld.urlShortener.url.model.UrlAnalyticsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AnalyticsService {

    private final UrlRepository urlRepository;

    public AnalyticsService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public UrlAnalyticsResponse getAnalytics(String shortCode) {

        UrlMappingEntity url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));

        UrlAnalyticsResponse response = new UrlAnalyticsResponse();
        response.setShortCode(shortCode);
        response.setOriginalUrl(url.getOriginalUrl());
        response.setClickCount(url.getClickCount());
        response.setActive(url.isActive() &&
                (url.getExpiryTime() == null || LocalDateTime.now().isBefore(url.getExpiryTime())));
        response.setCreatedAt(url.getCreatedAt());
        response.setExpiryTime(url.getExpiryTime());

        return response;
    }

    public Map<String, Long> getGlobalStats() {
        long total = urlRepository.count();

        long active = urlRepository.findAll().stream()
                .filter(u -> u.isActive() &&
                        (u.getExpiryTime() == null || u.getExpiryTime().isAfter(LocalDateTime.now())))
                .count();
        long expired = total - active;

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("expired", expired);
        return stats;
    }
}
