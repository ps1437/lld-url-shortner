package com.syscho.lld.urlShortener.common.dao;

import com.syscho.lld.urlShortener.common.dao.entity.UrlMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMappingEntity, Long> {
    Optional<UrlMappingEntity> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
}
