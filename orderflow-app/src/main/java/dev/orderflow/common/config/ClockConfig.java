package dev.orderflow.common.config;

import java.time.Clock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Всё время в приложении берём из инжектируемого {@link Clock}, а не из {@code Instant.now()}.
 * В тестах его можно подменить на {@code Clock.fixed(...)}.
 */
@Configuration(proxyBeanMethods = false)
class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
