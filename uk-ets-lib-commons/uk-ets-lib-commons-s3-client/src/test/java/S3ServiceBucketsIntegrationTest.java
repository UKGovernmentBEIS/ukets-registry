import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import gov.uk.ets.commons.s3.client.S3ClientConfiguration;
import gov.uk.ets.commons.s3.client.UkEtsS3Exception;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.s3.model.Bucket;

@SpringBootTest(classes = {S3ClientConfiguration.class})
public class S3ServiceBucketsIntegrationTest extends S3IntegrationTestBase {

    @Test
    public void shouldAutowireService() {
        assertThat(s3ClientService).isNotNull();
    }

    @Test
    public void shouldCreateBucket() {
        String bucketName = generateTestBucketName();

        Bucket bucket = s3ClientService.createBucket(bucketName);

        assertThat(bucket).isNotNull();
        assertThat(bucket.name()).isEqualTo(bucketName);
    }

    @Test
    public void shouldNotCreateExistingBucketButRetrieveExistingOne() {
        String bucketName = createTestBucket();
        Bucket bucket2 = s3ClientService.createBucket(bucketName);

        assertThat(bucket2).isNotNull();
        assertThat(bucket2.name()).isEqualTo(bucketName);
        verify(s3ClientService, times(2)).getBucket(bucketName);
    }

    @Test
    public void shouldThrowWhenRetrievingBucketIfNotFound() {

        String bucketName = generateTestBucketName();

        UkEtsS3Exception exception =
            assertThrows(UkEtsS3Exception.class, () -> s3ClientService.getBucket(bucketName));
        assertThat(exception.getMessage()).contains("S3 bucket '" + bucketName + "' not found");
    }

    @Test
    public void shouldDeleteCreatedBucket() {
        String bucketName = createTestBucket();

        s3ClientService.deleteBucket(bucketName);

        UkEtsS3Exception exception =
            assertThrows(UkEtsS3Exception.class, () -> s3ClientService.getBucket(bucketName));
        assertThat(exception.getMessage()).contains("S3 bucket '" + bucketName + "' not found");
    }

    @Test
    public void shouldThrowWhenDeletingNonExistingBucket() {

        String bucketName = "non-existing-bucket";

        UkEtsS3Exception exception =
            assertThrows(UkEtsS3Exception.class, () -> s3ClientService.deleteBucket(bucketName));

        assertThat(exception.getMessage())
            .contains("AWS error code=NoSuchBucket");
    }
}
