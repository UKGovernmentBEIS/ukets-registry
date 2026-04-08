package gov.uk.ets.reporting.metrics.messaging.events;

import gov.uk.ets.reporting.metrics.types.ReportingMetricsEventType;
import gov.uk.ets.reporting.metrics.types.TransactionStatus;
import gov.uk.ets.reporting.metrics.types.TransactionType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TransactionFinalisationEvent extends AbstractReportingMetricsEvent {

	private static final long serialVersionUID = 1L;
	private String transactionIdentifier;
    private Long transferringAccountIdentifier;
    private Long acquiringAccountIdentifier;
    private Long amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    
    @Override
    public ReportingMetricsEventType getType() {
        return ReportingMetricsEventType.TRANSACTION_FINALISATION;
    }
    
    @Override
    public String getIdentifier() {
        return transactionIdentifier;
    }
}
