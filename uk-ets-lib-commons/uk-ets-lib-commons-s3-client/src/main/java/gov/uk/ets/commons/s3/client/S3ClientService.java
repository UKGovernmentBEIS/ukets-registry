package gov.uk.ets.commons.s3.client;


import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

@Service
@RequiredArgsConstructor
@Log4j2
public class S3ClientService {
    public static final String XLSX_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String HTML_CONTENT_TYPE = "text/html";
    public static final String PDF_CONTENT_TYPE = "application/pdf";

    private final S3Client s3Client;

    /**
     * Uploads a file in S3 in specified bucket with specified file key.
     */
    public PutObjectResponse uploadFile(String bucketName, String key, Path path) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        try {
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, path);
            log.info("File with key '{}' was created in S3 bucket '{}'", key, bucketName);
            return putObjectResponse;
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String.format("saving file with key '%s' in S3 bucket '%s' rejected by S3. AWS error code=%s", key,
                    bucketName,
                    ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String.format("saving file with key '%s' in S3 bucket '%s' failed, S3 internal error", key, bucketName),
                se);
        }
    }

    /**
     * Uploads a byte array in specified bucket for specified key.
     * It seems that s3 won't return the correct content type when downloading if it is not specified during upload.
     */
    public PutObjectResponse uploadFile(String bucketName, String key, byte[] bytes, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();
            PutObjectResponse putObjectResponse = s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
            log.info("File with key '{}' was created in S3 bucket '{}'", key, bucketName);
            return putObjectResponse;
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String.format("saving file with key '%s' in S3 bucket '%s' rejected by S3. AWS error code=%s", key,
                    bucketName,
                    ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String.format("saving file with key '%s' in S3 bucket '%s' failed, S3 internal error", key, bucketName),
                se);
        }
    }

    public PutObjectResponse uploadXlsxFile(String bucketName, String key, byte[] bytes) {
        return uploadFile(bucketName, key, bytes, XLSX_CONTENT_TYPE);
    }

    public PutObjectResponse uploadHtmlFile(String bucketName, String key, byte[] bytes) {
        return uploadFile(bucketName, key, bytes, HTML_CONTENT_TYPE);
    }
    
    public PutObjectResponse uploadPdfFile(String bucketName, String key, byte[] bytes) {
        return uploadFile(bucketName, key, bytes, PDF_CONTENT_TYPE);
    }

    /**
     * Downloads a file from S3 from specified bucket and file key.
     */
    public ResponseInputStream<GetObjectResponse> downloadFile(String bucketName, String key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(key)
            .build();
        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            log.info("File with key '{}' was retrieved from S3 bucket '{}'", key, bucketName);
            return responseInputStream;
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String
                    .format("retrieving file with key '%s' from S3 bucket '%s' rejected by S3. AWS error code=%s", key,
                        bucketName,
                        ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String.format("retrieving file with key '%s' from S3 bucket '%s' failed, S3 internal error", key,
                    bucketName),
                se);
        }
    }

    /**
     * Deletes a file from S3 form specific bucket and file key.
     */
    public void deleteFile(String bucketName, String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("File with key '{}' was deleted from S3 bucket '{}'", key, bucketName);
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String.format("deleting file with key '%s' from S3 bucket '%s' rejected by S3. AWS error code=%s", key,
                    bucketName,
                    ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String
                    .format("deleting file with key '%s' from S3 bucket '%s' failed, S3 internal error", key,
                        bucketName),
                se);
        }
    }
    
    /**
     * Copies a file from a source bucket to a destination bucket.
     */
    public void copyFile(String fromBucketName, String fromKey, String destBucketName, String destKey) {
        try {
            // copySource should be the name of the source bucket and the key of the source object, 
            // separated by a slash (/)
            String copySource = fromBucketName + "/" + fromKey;
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .copySource(copySource)
                    .destinationBucket(destBucketName)
                    .destinationKey(destKey)
                    .build();
            s3Client.copyObject(copyObjectRequest);
            log.info("File with key '{}' was copied from S3 bucket '{}' to bucket '{}' with key '{}'", 
            		fromKey, fromBucketName, destBucketName, destKey);
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String.format("copying a file from S3 bucket '%s' with key '%s' to bucket '%s' with key '%s' "
                        + "rejected by S3. AWS error code=%s", 
                        fromBucketName, fromKey, destBucketName, destKey, ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String.format("copying a file from S3 bucket '%s' with key '%s' to bucket '%s' with key '%s' failed, "
                        + "S3 internal error",
                            fromBucketName, fromKey, destBucketName, destKey), se);
        }
    }

    //******************************** BUCKET OPERATIONS --- MOSTLY NEEDED FOR TEST/DEV ********************************


    public Bucket createBucket(String bucketName) {
        if (bucketExists(bucketName)) {
            log.warn("S3 bucket name {} already exists", bucketName);
            return getBucket(bucketName);
        }
        try {
            S3Waiter s3Waiter = s3Client.waiter();

            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();

            s3Client.createBucket(bucketRequest);

            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                .bucket(bucketName)
                .build();

            // Wait until the bucket is created and print out the response
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(log::info);
            log.info("S3 bucket {} is ready", bucketName);

            return getBucket(bucketName);
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String.format("creation of S3 bucket '%s' rejected by S3. AWS error code=%s", bucketName,
                    ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String.format("creation of S3 bucket '%s' failed, S3 internal error", bucketName),
                se);
        }
    }

    public Bucket getBucket(String bucketName) {
        return getAllBuckets().stream()
            .filter(b -> b.name().equals(bucketName))
            .findFirst()
            .orElseThrow(() -> new UkEtsS3Exception(String.format("S3 bucket '%s' not found", bucketName)));
    }

    public List<Bucket> getAllBuckets() {
        return s3Client.listBuckets().buckets();
    }

    public void deleteBucket(String bucketName) {
        try {
            ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(bucketName).build();
            s3Client.listObjects(listObjectsRequest).contents().forEach(o -> deleteFile(bucketName, o.key()));
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder().bucket(bucketName).build();
            s3Client.deleteBucket(deleteBucketRequest);
            log.info("S3 bucket '{}' was deleted", bucketName);
        } catch (AwsServiceException ase) {
            throw new UkEtsS3Exception(
                String.format("deletion of S3 bucket '%s' rejected by S3. AWS error code=%s", bucketName,
                    ase.awsErrorDetails().errorCode()), ase);
        } catch (SdkException se) {
            throw new UkEtsS3Exception(
                String.format("deletion of S3 bucket '%s' failed, S3 internal error", bucketName),
                se);
        }
    }

    private boolean bucketExists(String bucketName) {
        HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
            .bucket(bucketName)
            .build();
        try {
            HeadBucketResponse headBucketResponse = s3Client.headBucket(headBucketRequest);
            int statusCode = headBucketResponse.sdkHttpResponse().statusCode();
            if (statusCode == 403) {
                log.warn("The S3 bucket exists but you do not have permission to access it");
            }
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
}
