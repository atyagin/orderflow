package dev.orderflow;

import org.springframework.boot.SpringApplication;

/**
 * Запуск приложения локально поверх Testcontainers, без docker-compose:
 * {@code ./mvnw -pl orderflow-app spring-boot:test-run} или просто Run этого класса в IDE.
 */
public class TestOrderFlowApplication {

    public static void main(String[] args) {
        SpringApplication.from(OrderFlowApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
