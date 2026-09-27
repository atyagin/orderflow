package dev.orderflow.common.healthcheck;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeClusterOptions;
import org.apache.kafka.clients.admin.DescribeClusterResult;
import org.apache.kafka.common.Node;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Component
public class KafkaHealthIndicator extends AbstractHealthIndicator {

    private final AdminClient adminClient;

    public KafkaHealthIndicator(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try {

            DescribeClusterOptions options = new DescribeClusterOptions().timeoutMs(1500);
            DescribeClusterResult describeClusterResult = adminClient.describeCluster(options);

            CompletableFuture<String> clusterIdFuture = describeClusterResult.clusterId().toCompletionStage().toCompletableFuture();
            CompletableFuture<Collection<Node>> nodesFuture = describeClusterResult.nodes().toCompletionStage().toCompletableFuture();

            CompletableFuture.allOf(clusterIdFuture, nodesFuture).get(); // Один общий .get()

            String clusterId = clusterIdFuture.join();
            Collection<Node> brokers = nodesFuture.join();

            builder.up()
                .withDetail("clusterId", clusterId)
                .withDetail("brokers", brokers.size());
        } catch (Exception e) {
            builder.down()
                .withDetail("error", e.getMessage());
            if (e instanceof InterruptedException || e.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
