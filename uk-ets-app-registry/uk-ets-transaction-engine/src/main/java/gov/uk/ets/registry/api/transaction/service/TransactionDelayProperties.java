package gov.uk.ets.registry.api.transaction.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for the transaction delays.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "business.property.delay")
public class TransactionDelayProperties {

    /**
     * Working hours start.
     */
    @Value("${business.property.delay.workingHoursStart:09:00}")
    private String workingHoursStart;

    /**
     * Working hours end.
     */
    @Value("${business.property.delay.workingHoursEnd:18:00}")
    private String workingHoursEnd;

    /**
     * Transaction delay (in minutes).
     */
    @Value("${business.property.delay.transactionDelay:1}")
    private Integer transactionDelay;

    /**
     * Trusted account list - TAL (in minutes).
     */
    @Value("${business.property.delay.trustedAccountListDelay:7}")
    private Integer trustedAccountListDelay;

    /**
     * Flag to ignore holiday and weekends.
     */
    private Boolean ignoreHolidaysAndWeekends;

    /**
     * When set to true all delays are skipped.
     */
    @Value("${business.property.delay.disableDelays:false}")
    private Boolean disableDelays;

}
