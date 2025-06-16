package com.syscho.lld.urlShortener.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.syscho.lld.urlShortener.common.dao.entity.UrlMappingEntity;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(Cache<Object, Object> cache) {
        CaffeineCache shortUrlCache = new CaffeineCache("shortUrlCache", cache);
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(List.of(shortUrlCache));
        return manager;
    }

    @Bean
    public Cache<Object, Object> cache() {
        return Caffeine.newBuilder()
                .expireAfter(new Expiry<>() {
                    @Override
                    public long expireAfterCreate(Object key, Object value, long currentTime) {
                        if (value instanceof UrlMappingEntity entity && entity.getExpiryTime() != null) {
                            long seconds = Duration.between(LocalDateTime.now(), entity.getExpiryTime()).getSeconds();
                            return TimeUnit.SECONDS.toNanos(Math.max(10, seconds));
                        }
                        return TimeUnit.MINUTES.toNanos(30);
                    }

                    @Override
                    public long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }

                    @Override
                    public long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .maximumSize(5000)
                .build();

    }
}

