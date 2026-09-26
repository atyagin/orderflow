package dev.orderflow.healthcheck;

import dev.orderflow.TestcontainersConfiguration;
import dev.orderflow.common.healthcheck.KafkaHealthIndicator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class KafkaHealthIndicatorIT {

    @Autowired
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Test
    void kafkaIsUpWhenBrokerAvailable() {
        Health health = kafkaHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }
}
