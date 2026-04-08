package gov.uk.ets.reporting.metrics.messaging.events;

import java.time.Year;

import gov.uk.ets.reporting.metrics.types.ReportingMetricsEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EmissionsUpdatedEvent extends AbstractReportingMetricsEvent {


	private static final long serialVersionUID = 1L;
	private Long accountIdentifier;
    private Long oldEmissionsValue;
    private Long newEmissionsValue;
    private Year year;
    
    @Override
    public ReportingMetricsEventType getType() {
        return ReportingMetricsEventType.UPDATE_OF_VERIFIED_EMISSIONS;
    }
    
    @Override
    public String getIdentifier() {
        return Long.toString(accountIdentifier);
    }
    
}
