package gov.uk.ets.commons.cloudwatch.metrics;

import io.micrometer.cloudwatch2.CloudWatchConfig;
import io.micrometer.cloudwatch2.CloudWatchMeterRegistry;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;

@Configuration
public class MetricsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloudwatch.metrics.region}")
    private String region;

    @Value("${cloudwatch.metrics.awsEndpointUrl}")
    private String awsEndpointUrl;

    /**
     * Actual CloudWatch async client for aws env.
     */
    @Bean
    @ConditionalOnProperty(name = "cloudwatch.metrics.enable", havingValue = "true")
    public CloudWatchAsyncClient cloudWatchAsyncClient() {
        return CloudWatchAsyncClient.builder()
                .region(Region.of(region))
                .build();
    }

    /**
     * CloudWatch async client for local emulator (localstack).
     */
    @Bean
    @ConditionalOnProperty(name = "cloudwatch.metrics.enable", havingValue = "false", matchIfMissing = true)
    public CloudWatchAsyncClient cloudWatchAsyncClientEmulator() {
        AwsCredentialsProvider
            credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        return CloudWatchAsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .endpointOverride(URI.create(awsEndpointUrl))
                .build();
    }
    
    /**
     * Sets up a CloudWatch Meter Registry.
     */
    @Bean
    public MeterRegistry getMeterRegistry(CloudWatchAsyncClient cloudWatchAsyncClient) {
        CloudWatchConfig cloudWatchConfig = setupCloudWatchConfig();

        return new CloudWatchMeterRegistry(cloudWatchConfig, Clock.SYSTEM,
                cloudWatchAsyncClient);
    }

    /**
     * Sets up a configuration for cloudwatch exporting
     * cloudwatch.namespace: the namespace where these metrics will be pushed, mandatory
     * cloudwatch.step: how often the metrics are being published to cloudwatch, optional
     * cloudwatch.batch: how many metrics will be sent in a single batch, optional, default: 20
     */
    private CloudWatchConfig setupCloudWatchConfig() {
        return new CloudWatchConfig() {
            private Map<String, String> configuration
                = Map.of("cloudwatch.namespace", "health-check",
                       "cloudwatch.step", Duration.ofMinutes(1).toString());

            @Override
            public String get(String key) {
                return configuration.get(key);
            }
        };
    }
}
