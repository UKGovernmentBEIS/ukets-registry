package gov.uk.ets.commons.cloudwatch.metrics;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.CloudWatchException;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

@Service
@Log4j2
public class MetricsPublisher {

    private CloudWatchAsyncClient cloudWatchAsyncClient;

    @Autowired
    public MetricsPublisher(CloudWatchAsyncClient cloudWatchAsyncClient) {
        super();
        this.cloudWatchAsyncClient = cloudWatchAsyncClient;
    }

    /**
     * To publish metrics to CloudWatch we call the CloudWatchClient’s putMetricData method with a PutMetricDataRequest 
     * @see <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/examples-cloudwatch-publish-custom-metrics.html">CloudWatch Metrics</a> 
     */
    public void putMetricData(final String nameSpace, 
            final String metricName, 
            final Double dataPoint,
            final List<MetricTag> metricTags) {

        try {
            List<Dimension> dimensions = metricTags
                    .stream()
                    .map((metricTag)-> {
                        return Dimension
                             .builder()
                             .name(metricTag.getName())
                             .value(metricTag.getValue())
                             .build();
                    }).collect(Collectors.toList());			

            // Set an Instant object
            String time = ZonedDateTime
                       .now(ZoneOffset.UTC)
                       .format(DateTimeFormatter.ISO_INSTANT);
            Instant instant = Instant.parse(time);

            MetricDatum datum = MetricDatum
                     .builder()
                     .metricName(metricName)
                     .unit(StandardUnit.NONE)
                     .value(dataPoint)
                     .timestamp(instant)
                     .dimensions(dimensions)
                     .build();

            PutMetricDataRequest request = 
                       PutMetricDataRequest
                       .builder()
                       .namespace(nameSpace)
                       .metricData(datum)
                       .build();

            cloudWatchAsyncClient.putMetricData(request);

        } catch (CloudWatchException e) {
            log.error("Error while creating metric data", e.awsErrorDetails().errorMessage());
        }
    }
}
