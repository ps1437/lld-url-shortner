package com.syscho.lld.urlShortener.url;

import com.syscho.lld.urlShortener.audit.AnalyticsService;
import com.syscho.lld.urlShortener.url.model.UrlRequest;
import com.syscho.lld.urlShortener.url.model.UrlResponse;
import com.syscho.lld.urlShortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/url")
@RequiredArgsConstructor
@Tag(name = "URL Shortener", description = "API for creating and redirecting short URLs")
public class UrlController {

    private final UrlService urlService;
    private final AnalyticsService analyticsService;

    @Operation(summary = "Create a short URL")
    @PostMapping("/shorten")
    public ResponseEntity<UrlResponse> shortenUrl(@RequestBody UrlRequest request) {
        return ResponseEntity.ok(urlService.shortenUrl(request));
    }


}
