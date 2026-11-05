package gov.uk.ets.registry.api.common.reporting.metrics.messaging.events;

import java.time.LocalDateTime;

import gov.uk.ets.registry.api.common.reporting.metrics.types.ReportingMetricsEventType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountClaimCompletionEvent extends AbstractReportingMetricsEvent {

    private static final long serialVersionUID = 1L;
    private Long accountIdentifier;
    private Long requestIdentifier;
    private String accountClaimCode;
    private LocalDateTime accountClaimCompletionDateTime;
    private TaskOutcome taskOutcome;
    

    @Override
    public ReportingMetricsEventType getType() {
        return ReportingMetricsEventType.ACCOUNT_CLAIM_COMPLETION;
    }

    @Override
    public String getIdentifier() {
        return Long.toString(accountIdentifier);
    }

}
