package gov.uk.ets.reporting.metrics.messaging.events;

import gov.uk.ets.reporting.metrics.types.ReportingMetricsEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class InstallationTransferEvent extends AbstractReportingMetricsEvent {

	private static final long serialVersionUID = 1L;
	private Long transferringAccountIdentifier;
    private Long acquiringAccountIdentifier;
    
    @Override
    public ReportingMetricsEventType getType() {
        return ReportingMetricsEventType.INSTALLATION_TRANSFER;
    }

    @Override
    public String getIdentifier() {
        return Long.toString(transferringAccountIdentifier);
    }

}
