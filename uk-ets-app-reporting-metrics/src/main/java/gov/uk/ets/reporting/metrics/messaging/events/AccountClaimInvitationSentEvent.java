package gov.uk.ets.reporting.metrics.messaging.events;

import java.time.LocalDateTime;

import gov.uk.ets.reporting.metrics.types.ReportingMetricsEventType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AccountClaimInvitationSentEvent extends AbstractReportingMetricsEvent {

    private static final long serialVersionUID = 1L;
    private Long accountIdentifier;
    private String accountClaimCode;
    /** True if the invitation was triggered manually via the UI, false if triggered as part of an automated process. */
    private Boolean manual;
    private LocalDateTime accountClaimInvitationSendDateTime;

    @Override
    public ReportingMetricsEventType getType() {
        return ReportingMetricsEventType.ACCOUNT_CLAIM_INVITATION_SENT;
    }

    @Override
    public String getIdentifier() {
        return Long.toString(accountIdentifier);
    }

}
