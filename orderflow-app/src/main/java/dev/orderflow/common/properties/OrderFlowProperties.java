package dev.orderflow.common.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "orderflow")
public record OrderFlowProperties(
    @NotNull
    @Valid
    PaymentProviderProperties paymentProvider,
    @NotNull
    @Valid
    Kafka kafka
) {
    public record PaymentProviderProperties(
        @NotBlank(message = "Base URL не может быть пустым")
        @URL(message = "Base URL должен быть корректным URL-адресом")
        String baseUrl,
        @NotNull (message = "connect-timeout не может быть пустым")
        Duration connectTimeout,
        @NotNull (message = "read-timeout не может быть пустым")
        Duration readTimeout
    ) {}
    public record Kafka(
        @NotNull (message = "timeout не может быть пустым")
        Duration timeout
    ) {}
}
