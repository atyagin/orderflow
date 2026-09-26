package dev.orderflow.healthcheck;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
    // 1. Указываем нероутабельный IP, чтобы тест гарантированно не лез в реальный Docker
    "spring.kafka.bootstrap-servers=254.254.254.254:9999",
    // 2. Выставляем минимальные сетевые таймауты для Kafka, чтобы она сдавалась мгновенно
    "spring.kafka.properties.request.timeout.ms=500",
    "spring.kafka.properties.default.api.timeout.ms=500"
})
class KafkaHealthIndicatorTest {

    @Autowired
    private KafkaHealthIndicator kafkaHealthIndicator;


    @Test
    void shouldReturnDownStatusStrictlyWithinTwoSeconds() {
        Awaitility.given()
            .atMost(Duration.ofSeconds(2))       // Ждем не дольше 2 секунд (условие задачи)
            .pollInterval(Duration.ofMillis(100)) // Проверяем статус каждые 100 миллисекунд
            .await()
            .until(() -> kafkaHealthIndicator.health().getStatus(), equalTo(Status.DOWN));

        // Дополнительно проверяем финальное состояние здоровья ресурса
        Health finalHealth = kafkaHealthIndicator.health();
        assertEquals(Status.DOWN, finalHealth.getStatus());
    }
}
