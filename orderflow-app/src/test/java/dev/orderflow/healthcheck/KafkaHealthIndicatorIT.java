package dev.orderflow.healthcheck;

import dev.orderflow.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.kafka.KafkaContainer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
class KafkaHealthIndicatorIT {

    @Autowired
    private KafkaContainer kafkaContainer;
    @Autowired
    private KafkaHealthIndicator kafkaHealthIndicator;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static String receivedMessage;

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        receivedMessage = message;
        latch.countDown();
    }

    @Test
    void doHealthCheck() throws InterruptedException {
        String payload = "Testcontainers";
        kafkaTemplate.send("test-topic", payload);

        boolean messageReceived = latch.await(5, TimeUnit.SECONDS);

        assertThat(messageReceived).isTrue();
        assertThat(receivedMessage).isEqualTo(payload);
    }
    @Test
    void kafkaIsUpWhenBrokerAvailable() {
        Health health = kafkaHealthIndicator.health();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

}
