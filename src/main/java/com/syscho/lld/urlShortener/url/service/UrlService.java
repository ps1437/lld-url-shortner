package com.syscho.lld.urlShortener.url.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.syscho.lld.urlShortener.common.dao.UrlRepository;
import com.syscho.lld.urlShortener.common.dao.entity.UrlMappingEntity;
import com.syscho.lld.urlShortener.common.utils.PasswordUtils;
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

        if (StringUtils.isNotBlank(request.getPassword())) {
            url.setPassword(PasswordUtils.encode(request.getPassword()));
        }

        if (request.getExpiryInMinutes() != null) {
            url.setExpiryTime(LocalDateTime.now().plusMinutes(request.getExpiryInMinutes()));
        }

        UrlMappingEntity saved = urlRepository.save(url);
        return urlMapper.toResponse(saved);
    }

    public String getOriginalUrl(String code, String password) {
        UrlMappingEntity existingUrl = (UrlMappingEntity) shortUrlCache.getIfPresent(code);

        if (existingUrl == null) {
            log.info("Not in Cache Loading from Database: {}", code);

            existingUrl = urlRepository.findByShortCode(code)
                    .filter(this::isNotExpired)
                    .orElseThrow(() -> new RuntimeException("URL not found or has expired"));

            shortUrlCache.put(code, existingUrl);
        }

        if (!existingUrl.isActive() || isExpired(existingUrl)) {
            shortUrlCache.invalidate(existingUrl);
            log.warn("URL with code {} is expired", code);
            throw new RuntimeException("This short URL has expired.");
        }

        if (StringUtils.isNotBlank(existingUrl.getPassword())) {
            if (StringUtils.isBlank(password) ||
                    !PasswordUtils.checkPassword(password, existingUrl.getPassword())) {
                throw new RuntimeException("Password required or incorrect");
            }
        }


        existingUrl.setClickCount(existingUrl.getClickCount() + 1);
        urlRepository.save(existingUrl);

        return existingUrl.getOriginalUrl();
    }

    private boolean isExpired(UrlMappingEntity url) {
        return url.getExpiryTime() != null && LocalDateTime.now().isAfter(url.getExpiryTime());
    }

    private boolean isNotExpired(UrlMappingEntity url) {
        return !isExpired(url);
    }

}
