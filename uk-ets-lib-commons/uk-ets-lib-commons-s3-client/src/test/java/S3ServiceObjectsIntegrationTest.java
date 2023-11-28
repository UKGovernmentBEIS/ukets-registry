import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.uk.ets.commons.s3.client.S3ClientConfiguration;
import gov.uk.ets.commons.s3.client.UkEtsS3Exception;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@SpringBootTest(classes = {S3ClientConfiguration.class})
public class S3ServiceObjectsIntegrationTest extends S3IntegrationTestBase {

    public static final String SAMPLE_FILE_1 = "sample-report-1.xlsx";
    public static final String SAMPLE_FILE_2 = "sample-report-2.txt";
    public static final String SAMPLE_KEY_1 = "sample-report-1";
    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String TEXT_CONTENT_TYPE = "text/plain";

    @Test
    public void shouldUploadFileInBucket() throws URISyntaxException {
        String bucketName = createTestBucket();

        Path path = Paths.get(ClassLoader.getSystemResource(SAMPLE_FILE_1).toURI());

        PutObjectResponse putObjectResponse = s3ClientService.uploadFile(bucketName, SAMPLE_KEY_1, path);

        assertThat(putObjectResponse.sdkHttpResponse().statusCode()).isEqualTo(200);

        ResponseInputStream<GetObjectResponse> responseInputStream =
            s3ClientService.downloadFile(bucketName, SAMPLE_KEY_1);

        assertThat(responseInputStream.response().contentType())
            .isEqualTo(XLSX_CONTENT_TYPE);
    }

    @Test
    public void shouldUploadFileAsByteArrayInBucket() throws URISyntaxException, IOException {
        String bucketName = createTestBucket();

        Path path = Paths.get(ClassLoader.getSystemResource(SAMPLE_FILE_1).toURI());
        byte[] bytes = Files.readAllBytes(path);

        PutObjectResponse putObjectResponse = s3ClientService.uploadXlsxFile(bucketName, SAMPLE_KEY_1, bytes);

        assertThat(putObjectResponse.sdkHttpResponse().statusCode()).isEqualTo(200);

        ResponseInputStream<GetObjectResponse> responseInputStream =
            s3ClientService.downloadFile(bucketName, SAMPLE_KEY_1);

        assertThat(responseInputStream.response().contentType())
            .isEqualTo(XLSX_CONTENT_TYPE);
    }

    @Test
    public void shouldRetrieveFileFromBucket() throws URISyntaxException {
        String bucketName = createTestBucket();

        Path path = Paths.get(ClassLoader.getSystemResource(SAMPLE_FILE_1).toURI());
        s3ClientService.uploadFile(bucketName, SAMPLE_KEY_1, path);

        ResponseInputStream<GetObjectResponse> responseInputStream =
            s3ClientService.downloadFile(bucketName, SAMPLE_KEY_1);

        assertThat(responseInputStream.response().contentType())
            .isEqualTo(XLSX_CONTENT_TYPE);
    }

    @Test
    public void shouldRetrieveAndReadFileFromBucket() throws URISyntaxException, IOException {
        String bucketName = createTestBucket();

        Path path = Paths.get(ClassLoader.getSystemResource(SAMPLE_FILE_2).toURI());
        s3ClientService.uploadFile(bucketName, SAMPLE_FILE_2, path);

        ResponseInputStream<GetObjectResponse> responseInputStream =
            s3ClientService.downloadFile(bucketName, SAMPLE_FILE_2);

        assertThat(responseInputStream.response().contentType())
            .isEqualTo(TEXT_CONTENT_TYPE);

        List<String> lines = IOUtils.readLines(responseInputStream, StandardCharsets.UTF_8);

        assertThat(lines).hasSize(2);
    }

    @Test
    public void shouldRetrieveAndReadFileUploadedAsByteArray() throws URISyntaxException, IOException {
        String bucketName = createTestBucket();

        Path path = Paths.get(ClassLoader.getSystemResource(SAMPLE_FILE_2).toURI());
        byte[] bytes = Files.readAllBytes(path);

        s3ClientService.uploadFile(bucketName, SAMPLE_FILE_2, bytes, TEXT_CONTENT_TYPE);

        ResponseInputStream<GetObjectResponse> responseInputStream =
            s3ClientService.downloadFile(bucketName, SAMPLE_FILE_2);

        assertThat(responseInputStream.response().contentType())
            .isEqualTo(TEXT_CONTENT_TYPE);

        List<String> lines = IOUtils.readLines(responseInputStream, StandardCharsets.UTF_8);

        assertThat(lines).hasSize(2);
    }

    @Test
    public void shouldThrowWhenRetrievingNonExistentFile() {
        String bucketName = createTestBucket();

        UkEtsS3Exception exception =
            assertThrows(UkEtsS3Exception.class, () -> s3ClientService.downloadFile(bucketName, SAMPLE_KEY_1));

        assertThat(exception.getMessage()).contains("AWS error code=NoSuchKey");
    }

    @Test
    public void shouldThrowWhenRetrievingFromNonExistentBucket() {

        UkEtsS3Exception exception = assertThrows(UkEtsS3Exception.class, () -> {
            s3ClientService.downloadFile("non-existent-bucket", SAMPLE_KEY_1);
        });

        assertThat(exception.getMessage()).contains("AWS error code=NoSuchBucket");
    }
}
