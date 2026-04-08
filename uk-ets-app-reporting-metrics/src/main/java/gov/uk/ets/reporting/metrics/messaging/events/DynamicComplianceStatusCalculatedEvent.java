package gov.uk.ets.reporting.metrics.messaging.events;

import gov.uk.ets.reporting.metrics.types.ComplianceStatus;
import gov.uk.ets.reporting.metrics.types.ReportingMetricsEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DynamicComplianceStatusCalculatedEvent extends AbstractReportingMetricsEvent {

	private static final long serialVersionUID = 1L;
	private Long accountIdentifier;
    private ComplianceStatus dynamicComplianceStatus;
    
    @Override
    public ReportingMetricsEventType getType() {
        return ReportingMetricsEventType.RECALCULATE_DYNAMIC_STATUS;
    }
    
    @Override
    public String getIdentifier() {
        return Long.toString(accountIdentifier);
    }
    
}
