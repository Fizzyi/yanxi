package com.yanxi.yanxiapi.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * 性能监控配置类
 */
@Configuration
public class PerformanceConfig {

    /**
     * 自定义指标注册器
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer() {
        return registry -> registry.config().commonTags("application", "yanxi-api");
    }

    /**
     * 启用@Timed注解支持
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * 自定义健康检查
     */
    @Bean
    public HealthIndicator customHealthIndicator() {
        return () -> {
            // 可以添加自定义健康检查逻辑
            // 例如检查数据库连接、缓存状态等
            return Health.up()
                    .withDetail("status", "Application is running")
                    .withDetail("version", "2.0")
                    .build();
        };
    }
} 