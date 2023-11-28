package gov.uk.ets.commons.s3.client;

import java.util.List;

/**
 * Abstraction for any S3 bucket attributes that are specific to a microservice.
 */
public interface S3BucketAttributes {
    
    /**
     * returns the S3 bucket names for each microservice that implements this abstraction.
     *
     * @return the bucket names
     */
    List<String> getS3BucketNames();

}
