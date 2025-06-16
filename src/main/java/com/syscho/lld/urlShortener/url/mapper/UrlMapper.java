package com.syscho.lld.urlShortener.url.mapper;

import com.syscho.lld.urlShortener.common.dao.entity.UrlMappingEntity;
import com.syscho.lld.urlShortener.url.model.UrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    @Value("${app.base-url:http://localhost}")
    private String baseUrl;

    @Value("${server.port:8080}")
    private String port;

    public UrlResponse toResponse(UrlMappingEntity url) {
        var res = new UrlResponse();
        res.setShortUrl(baseUrl + ":" + port + "/" + url.getShortCode());
        res.setOriginalUrl(url.getOriginalUrl());
        res.setClickCount(url.getClickCount());
        res.setActive(url.isActive());
        res.setExpiryTime(url.getExpiryTime());
        return res;
    }
}
