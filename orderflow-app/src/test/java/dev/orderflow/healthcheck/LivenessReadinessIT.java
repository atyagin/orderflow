package dev.orderflow.healthcheck;

import com.fasterxml.jackson.databind.JsonNode;
import dev.orderflow.TestcontainersConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class LivenessReadinessIT {

    @Autowired
    TestRestTemplate rest;

    @Test
    void livenessHealthIsUp() {
        ResponseEntity<JsonNode> response = rest.getForEntity("/actuator/health/readiness", JsonNode.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().path("status").asText()).isEqualTo("UP");
    }

    @Test
    void readinessHealthIsUp() {
        ResponseEntity<JsonNode> response = rest.getForEntity("/actuator/health/readiness", JsonNode.class);

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().path("status").asText()).isEqualTo("UP");
    }

}
