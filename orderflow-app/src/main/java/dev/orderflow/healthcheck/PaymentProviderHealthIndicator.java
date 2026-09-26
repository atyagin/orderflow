package dev.orderflow.healthcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestClient;


@Component
public class PaymentProviderHealthIndicator  extends AbstractHealthIndicator {

    private final String url;
    private final RestClient restClient;

    public PaymentProviderHealthIndicator(@Value("${orderflow.payment-provider.base-url}") String url, RestClient restClient) {
        this.url = url;
        this.restClient = restClient;

    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        StopWatch stopWatch = new StopWatch();

        try {
            stopWatch.start();
            ResponseEntity<String> response = restClient
                .get()
                .uri(url + "/health")
                .retrieve()
                .toEntity(String.class);

            stopWatch.stop();

            builder.up()
                .withDetail("latancy", stopWatch.getTotalTimeMillis());

        } catch (Exception e) {
            builder.down()
                .withException(e);
        }
    }

}
