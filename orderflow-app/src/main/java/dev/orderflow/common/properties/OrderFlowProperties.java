package dev.orderflow.common.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
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
    KafkaProps kafkaProps
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
    public record KafkaProps(
        @Positive(message = "timeout должен быть больше 0")
        int timeout
    ) {}
}
