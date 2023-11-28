package gov.uk.ets.reports.generator.export;

import gov.uk.ets.commons.s3.client.S3BucketAttributes;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@NoArgsConstructor
@Component
public class S3BucketAttributesImpl implements S3BucketAttributes {

    @Value("${aws.s3.reports.bucket.name}")
    private String bucket;
    
    /**
    * Returns S3 bucket names that are needed for the report generator..
    */
    @Override
    public List<String> getS3BucketNames() {
        return List.of(bucket);
    }
}