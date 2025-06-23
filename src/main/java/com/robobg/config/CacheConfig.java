package com.robobg.config;


import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("youtubeVideosCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(100, TimeUnit.MINUTES) // cache expires in 100 minutes
                .maximumSize(100));
        return cacheManager;
    }
}
