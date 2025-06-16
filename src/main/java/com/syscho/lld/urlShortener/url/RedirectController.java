package com.syscho.lld.urlShortener.url;

import com.syscho.lld.urlShortener.url.service.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlService urlService;

    @Operation(summary = "Redirect to the original URL")
    @GetMapping("/{shortCode}")
    public ResponseEntity<String> redirectToOriginal(
            @PathVariable String shortCode,
            @RequestParam(required = false) String password) {
        String originalUrl = urlService.getOriginalUrl(shortCode, password);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", originalUrl)
                .build();
    }

}
