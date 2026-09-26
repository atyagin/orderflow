package dev.orderflow.healthcheck;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class KafkaHealthIndicator extends AbstractHealthIndicator {

    private final AdminClient adminClient;

    public KafkaHealthIndicator(KafkaAdmin kafkaAdmin) {
        this.adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        try {
            DescribeClusterResult describeClusterResult = adminClient.describeCluster();

            String clusterId = describeClusterResult.clusterId().get(1, TimeUnit.SECONDS);
            int brokers = describeClusterResult.nodes().get(1, TimeUnit.SECONDS).size();

            builder.up()
                .withDetail("clusterId", clusterId)
                .withDetail("brokers", brokers);
        } catch (Exception e) {
            builder.down()
                .withException(e);
        }
    }

    @PreDestroy
    public void close() {
        adminClient.close(Duration.ofSeconds(2));
    }
}
