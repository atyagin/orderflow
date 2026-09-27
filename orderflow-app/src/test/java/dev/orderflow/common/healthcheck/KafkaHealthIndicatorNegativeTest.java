package dev.orderflow.common.healthcheck;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;


class KafkaHealthIndicatorNegativeIT {

    private AdminClient adminClient;
    private KafkaHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:50000");
        properties.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 500);
        properties.put(AdminClientConfig.DEFAULT_API_TIMEOUT_MS_CONFIG, 500);
        adminClient = AdminClient.create(properties);
        healthIndicator = new KafkaHealthIndicator(adminClient);
    }

    @AfterEach
    void tearDown() {
        adminClient.close(Duration.ofMillis(100));
    }

    @Test
    void shouldReturnDownStatusWhenKafkaIsUnavailable() {
        Health health = assertTimeoutPreemptively(Duration.ofSeconds(2),
            () -> healthIndicator.health(), "Health indicator took longer than 2 seconds");
        assertEquals(Status.DOWN, health.getStatus());
    }
}
