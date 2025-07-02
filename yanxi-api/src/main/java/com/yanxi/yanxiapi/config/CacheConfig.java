package com.yanxi.yanxiapi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类 - 使用Caffeine提供高性能本地缓存
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${app.cache.default-ttl:10}")
    private int defaultTtl;

    @Value("${app.cache.user-ttl:30}")
    private int userTtl;

    @Value("${app.cache.class-ttl:60}")
    private int classTtl;

    /**
     * 配置Caffeine缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // 默认缓存配置
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(2000)
                .expireAfterWrite(defaultTtl, TimeUnit.MINUTES)
                .recordStats() // 启用统计信息
        );
        
        // 预定义缓存名称
        cacheManager.setCacheNames(Arrays.asList(
                "users", "classes", "assignments", "classNames", 
                "userBatch", "classBatch", "submissions"
        ));
        
        return cacheManager;
    }

    /**
     * 用户缓存配置 - 较长的TTL
     */
    @Bean
    public Caffeine<Object, Object> userCaffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(50)
                .maximumSize(500)
                .expireAfterWrite(userTtl, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * 班级缓存配置 - 最长的TTL
     */
    @Bean
    public Caffeine<Object, Object> classCaffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(20)
                .maximumSize(200)
                .expireAfterWrite(classTtl, TimeUnit.MINUTES)
                .recordStats();
    }

    /**
     * 作业缓存配置 - 中等TTL
     */
    @Bean
    public Caffeine<Object, Object> assignmentCaffeineConfig() {
        return Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(15, TimeUnit.MINUTES)
                .recordStats();
    }
} 