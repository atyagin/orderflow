package dev.orderflow.healthcheck;

import com.fasterxml.jackson.databind.JsonNode;
import dev.orderflow.TestcontainersConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class LivenessReadinessNegativeIT {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    void healthIsDownWhenStateChanges() {
        //Имитируем сбой готовности (Readiness)
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);

        ResponseEntity<JsonNode> readinessResponse = rest.getForEntity("/actuator/health/readiness", JsonNode.class);
        Assertions.assertThat(readinessResponse.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        Assertions.assertThat(readinessResponse.getBody()).isNotNull();
        Assertions.assertThat(readinessResponse.getBody().path("status").asText()).isEqualTo("OUT_OF_SERVICE");

        //Имитируем критический сбой (Liveness)
        AvailabilityChangeEvent.publish(eventPublisher, this, LivenessState.BROKEN);

        ResponseEntity<JsonNode> livenessResponse = rest.getForEntity("/actuator/health/liveness", JsonNode.class);
        Assertions.assertThat(livenessResponse.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        Assertions.assertThat(livenessResponse.getBody()).isNotNull();
        Assertions.assertThat(livenessResponse.getBody().path("status").asText()).isEqualTo("DOWN");
    }
}
