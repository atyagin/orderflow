package dev.orderflow.common.config;

import dev.orderflow.common.properties.OrderFlowProperties;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfiguration {

    @Bean
    public RestClient paymentRestClient(RestClient.Builder builder, OrderFlowProperties properties) {
        OrderFlowProperties.PaymentProviderProperties providerProps = properties.paymentProvider();

        Duration connectTimeout = DurationStyle.detectAndParse(providerProps.connectTimeout());
        Duration readTimeout = DurationStyle.detectAndParse(providerProps.readTimeout());

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(readTimeout);

        return builder
            .baseUrl(providerProps.baseUrl())
            .requestFactory(requestFactory)
            .build();
    }
}
