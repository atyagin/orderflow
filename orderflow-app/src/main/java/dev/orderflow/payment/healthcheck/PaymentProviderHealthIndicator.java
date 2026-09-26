package dev.orderflow.payment.healthcheck;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClient;

@Component
public class PaymentProviderHealthIndicator  extends AbstractHealthIndicator {

    private final RestClient restClient;

    public PaymentProviderHealthIndicator(@Qualifier("paymentRestClient") RestClient restClient) {
        this.restClient = restClient;

    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            ResponseEntity<String> response = restClient
                .get()
                .uri("/health")
                .retrieve()
                .toEntity(String.class);

            stopWatch.stop();

            builder.up()
                .withDetail("latency", stopWatch.getTotalTimeMillis());

        } catch (Exception e) {
            stopWatch.stop();
            builder.down()
                .withDetail("latency", stopWatch.getTotalTimeMillis());
        }
    }

}
