package com.syscho.lld.urlShortener.audit;

import com.syscho.lld.urlShortener.url.model.UrlAnalyticsResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "URL Shortener Analytics", description = "APIs for tracking and retrieving analytics for shortened URLs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlAnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        return ResponseEntity.ok(analyticsService.getAnalytics(shortCode));
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(analyticsService.getGlobalStats());
    }

}
