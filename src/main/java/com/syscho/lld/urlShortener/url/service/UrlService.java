package com.syscho.lld.urlShortener.url.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.syscho.lld.urlShortener.common.dao.UrlRepository;
import com.syscho.lld.urlShortener.common.dao.entity.UrlMappingEntity;
import com.syscho.lld.urlShortener.common.utils.ShortCodeGenerator;
import com.syscho.lld.urlShortener.url.mapper.UrlMapper;
import com.syscho.lld.urlShortener.url.model.UrlRequest;
import com.syscho.lld.urlShortener.url.model.UrlResponse;
import com.syscho.lld.urlShortener.url.validator.UrlValidatorService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private static final Logger log = LoggerFactory.getLogger(UrlService.class);

    private final UrlRepository urlRepository;
    private final UrlValidatorService urlValidator;
    private final UrlMapper urlMapper;
    private final ShortCodeGenerator shortCodeGenerator;
    private final Cache<Object, Object> shortUrlCache;


    public UrlResponse shortenUrl(UrlRequest request) {
        urlValidator.validateUrl(request.getOriginalUrl());

        String shortCode;
        if (StringUtils.isNoneBlank(request.getCustomAlias())) {
            shortCode = request.getCustomAlias();
            if (urlRepository.existsByShortCode(shortCode)) {
                throw new IllegalArgumentException("Custom alias already exists");
            }
        } else {
            shortCode = shortCodeGenerator.generateUniqueShortCode(request.getLength());
        }

        UrlMappingEntity url = new UrlMappingEntity();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode(shortCode);

        if (request.getExpiryInMinutes() != null) {
            url.setExpiryTime(LocalDateTime.now().plusMinutes(request.getExpiryInMinutes()));
        }

        UrlMappingEntity saved = urlRepository.save(url);
        return urlMapper.toResponse(saved);
    }

    public String getOriginalUrl(String code) {
        UrlMappingEntity url = (UrlMappingEntity) shortUrlCache.getIfPresent(code);

        if (url == null) {
            log.info("Not in Cache Loading from Database: {}", code);

            url = urlRepository.findByShortCode(code)
                    .filter(this::isNotExpired)
                    .orElseThrow(() -> new RuntimeException("URL not found or has expired"));

            shortUrlCache.put(code, url);
        }

        if (!url.isActive() || isExpired(url)) {
            shortUrlCache.invalidate(url);
            log.warn("URL with code {} is expired", code);
            throw new RuntimeException("This short URL has expired.");
        }

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        return url.getOriginalUrl();
    }

    private boolean isExpired(UrlMappingEntity url) {
        return url.getExpiryTime() != null && LocalDateTime.now().isAfter(url.getExpiryTime());
    }

    private boolean isNotExpired(UrlMappingEntity url) {
        return !isExpired(url);
    }

}
