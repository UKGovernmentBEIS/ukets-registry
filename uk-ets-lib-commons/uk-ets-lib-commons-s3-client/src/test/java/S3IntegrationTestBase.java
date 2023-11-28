import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

import gov.uk.ets.commons.s3.client.S3BucketAttributes;
import gov.uk.ets.commons.s3.client.S3ClientService;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

@ContextConfiguration(initializers = S3IntegrationTestBase.Initializer.class)
public class S3IntegrationTestBase {

    @SpyBean
    protected S3ClientService s3ClientService;
    
    @MockBean
    protected S3BucketAttributes s3BucketAttributes;

    protected static final DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.12.8");
    private static final LocalStackContainer s3_CONTAINER;
    private static final String TEST_BUCKET_NAME = "test-bucket";

    static {

        s3_CONTAINER = new LocalStackContainer(localstackImage)
            .withReuse(true)
            .withServices(S3)
            .withEnv(Map.of("LS_LOG ", "DEBUG"))
        ;
        s3_CONTAINER.start();

    }

    public static class Initializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            ConfigurableEnvironment environment = configurableApplicationContext.getEnvironment();

            String s3EndpointUrl =
                "aws.s3.awsEndpointUrl=" + s3_CONTAINER.getEndpointOverride(S3);
            String accessKey = "aws.s3.credentials.access-key=" + s3_CONTAINER.getAccessKey();
            String secretKey = "aws.s3.credentials.secret-key=" + s3_CONTAINER.getSecretKey();
            String region = "aws.s3.region=" + s3_CONTAINER.getRegion();

            TestPropertySourceUtils
                .addInlinedPropertiesToEnvironment(
                        environment, s3EndpointUrl, accessKey, secretKey,region);
        }
    }

    protected String createTestBucket() {
        String bucketName = generateTestBucketName();
        s3ClientService.createBucket(bucketName);
        return bucketName;
    }

    protected String generateTestBucketName() {
        UUID uuid = UUID.randomUUID();
        return TEST_BUCKET_NAME + "-" + uuid;
    }


    @AfterEach
    void tearDown() {
        s3ClientService.getAllBuckets().forEach(b -> s3ClientService.deleteBucket(b.name()));
    }
}
