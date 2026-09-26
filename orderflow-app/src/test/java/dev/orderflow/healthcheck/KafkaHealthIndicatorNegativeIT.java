package dev.orderflow.healthcheck;

import dev.orderflow.common.healthcheck.KafkaHealthIndicator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext
@SpringBootTest(properties = {
    // 1. Указываем локальный хост, но порт, который гарантированно закрыт
    "spring.kafka.bootstrap-servers=localhost:50000",
    // 2. Жестко ограничиваем время ожидания для AdminClient (именно он отвечает за Health Indicator)
    "spring.kafka.admin.properties.max.block.ms=500",
    // 3. Дополнительные сетевые таймауты
    "spring.kafka.properties.request.timeout.ms=500",
    "spring.kafka.properties.default.api.timeout.ms=500"
})
class KafkaHealthIndicatorNegativeIT {

    @Autowired
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Test
    void shouldReturnDownStatusWhenKafkaIsUnavailable() {

        Health health = Assertions.assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            return kafkaHealthIndicator.health();
        }, "Индикатор завис дольше чем на 2 секунды!");

        assertEquals(Status.DOWN, health.getStatus());
    }
}
