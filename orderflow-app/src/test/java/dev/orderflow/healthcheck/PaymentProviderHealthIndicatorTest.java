package dev.orderflow.healthcheck;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = {
    "spring.http.client.connect-timeout=500ms",
    "spring.http.client.read-timeout=500ms"
})
class PaymentProviderHealthIndicatorTest {


    private static WireMockServer wireMockServer;
    @Autowired
    private PaymentProviderHealthIndicator paymentHealthIndicator;

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort());
        wireMockServer.start();

        WireMock.configureFor("localhost", wireMockServer.port());
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("orderflow.payment-provider.base-url", () -> "http://localhost:" + wireMockServer.port());
    }

    @AfterEach
    void resetStubs() {
        WireMock.reset();
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void shouldReturnUpWhenPaymentReturns200() {
        stubFor(get(urlEqualTo("/health"))
            .willReturn(aResponse().withStatus(200)));

        Health health = paymentHealthIndicator.health();
        assertEquals(Status.UP, health.getStatus());
    }

    @Test
    void shouldReturnDownWhenPaymentReturns500() {
        stubFor(get(urlEqualTo("/health"))
            .willReturn(aResponse().withStatus(500)));

        Health health = paymentHealthIndicator.health();
        assertEquals(Status.DOWN, health.getStatus());
    }

    @Test
    void shouldReturnDownWhenPaymentExceedsReadTimeout() {
        stubFor(get(urlEqualTo("/health"))
            .willReturn(aResponse()
                .withStatus(200)
                .withFixedDelay(3000)));

        Health health = Assertions.assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            return paymentHealthIndicator.health();
        }, "Индикатор завис дольше чем на 2 секунды!");

        assertEquals(Status.DOWN, health.getStatus());
    }
}
