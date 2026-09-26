package dev.orderflow.common.healthcheck;

import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.KafkaFuture;
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
    protected void doHealthCheck(Health.Builder builder) {
        try {

            DescribeClusterOptions options = new DescribeClusterOptions().timeoutMs(1500);
            DescribeClusterResult describeClusterResult = adminClient.describeCluster(options);

            String clusterId = describeClusterResult.clusterId().get(2, TimeUnit.SECONDS);
            int brokers = describeClusterResult.nodes().get().size();

            builder.up()
                .withDetail("clusterId", clusterId)
                .withDetail("brokers", brokers);
        } catch (Exception e) {
            builder.down();
        }
    }

    @PreDestroy
    public void close() {
        if (adminClient != null) {
            adminClient.close(Duration.ofSeconds(2));
        }
    }
}
