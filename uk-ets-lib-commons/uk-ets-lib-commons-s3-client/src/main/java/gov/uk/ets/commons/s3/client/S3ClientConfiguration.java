package gov.uk.ets.commons.s3.client;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class S3ClientConfiguration {

    @Value("${aws.s3.credentials.access-key}")
    private String accessKey;

    @Value("${aws.s3.credentials.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.awsEndpointUrl}")
    private String awsEndpointUrl;
    
    private final S3BucketAttributes bucketAttributes;

    /**
     * This is the default S3 Client, connecting to a real S3 instance.
     */
    @Bean
    @ConditionalOnProperty(name = "aws.s3.enable", havingValue = "true")
    public S3Client s3Client() {
        return commonS3ClientBuilder()
            .build();
    }

    /**
     * This is the S3 client used in development and in tests, connection to a localstack S3 emulator.
     * We need to override the credential provider here, to be able to use the test aws keys
     * (retrieved from application properties or env variables).
     */
    @Bean
    @ConditionalOnProperty(name = "aws.s3.enable", havingValue = "false", matchIfMissing = true)
    public S3Client s3ClientEmulator() {
        AwsCredentialsProvider
            credentialsProvider = StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        return commonS3ClientBuilder()
            .endpointOverride(URI.create(awsEndpointUrl))
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .forcePathStyle(true)
            .build();
    }


    /**
     * Create S3 bucket in S3 emulator (only for dev purposes).
     */
    @Bean
    @ConditionalOnProperty(name = "aws.s3.enable", havingValue = "false", matchIfMissing = true)
    public CommandLineRunner s3BucketCreator(S3ClientService s3ClientService, ApplicationContext context) {
        return args -> {
            try {
                for (String bucket: bucketAttributes.getS3BucketNames()) {
                    s3ClientService.createBucket(bucket);
                }
            } catch (UkEtsS3Exception e) {
                log.error(e);
                log.warn("Application will shutdown");
                // If the bucket creation fails, we do not want the application to start
                // since it will probably be unusable
                int exitCode = SpringApplication.exit(context);
                System.exit(exitCode);
            }
        };
    }

    @Bean
    public S3ClientService s3ClientService(S3Client s3Client) {
        return new S3ClientService(s3Client);
    }

    /**
     * We need the default credentials provider in all non-dev/test environments.
     * So the only common property left is the region.
     */
    private S3ClientBuilder commonS3ClientBuilder() {
        return S3Client.builder()
            .region(Region.of(region));
    }
}
