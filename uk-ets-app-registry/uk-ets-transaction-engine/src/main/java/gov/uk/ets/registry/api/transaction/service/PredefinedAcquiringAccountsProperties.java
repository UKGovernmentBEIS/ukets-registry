package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.type.ExternalPredefinedAccount;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Encapsulates the predefined acquiring accounts which are hosted outside the registry.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "business.property.transaction.predefined.acquiring.account")
public class PredefinedAcquiringAccountsProperties {

    /**
     * CDM SOP adaptation fund account.
     */
    private String cdmSopAccount;

    /**
     * CDM CCS net reversal cancellation account=.
     */
    private String ccsNetReversalCancellationAccount;

    /**
     * CDM non submission of verification cancellation account.
     */
    private String ccsNonSubmissionOfVerificationReportCancellationAccount;

    /**
     * CDM excess issuance account for CP2.
     */
    private String cdmExcessIssuanceAccountForCP2;

    /**
     * Retrieves the account full identifier based on the provided value.
     * @param externalPredefinedAccount The provided value.
     * @return an account full identifier.
     */
    public String getAccount(ExternalPredefinedAccount externalPredefinedAccount) {
        String result = null;
        switch (externalPredefinedAccount) {
            case CDM_SOP_ACCOUNT:
                result = cdmSopAccount;
                break;

            case CDM_EXCESS_ISSUANCE_ACCOUNT_FOR_CP2:
                result = cdmExcessIssuanceAccountForCP2;
                break;

            case CCS_NET_REVERSAL_CANCELLATION_ACCOUNT:
                result = ccsNetReversalCancellationAccount;
                break;

            case CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT:
                result = ccsNonSubmissionOfVerificationReportCancellationAccount;
        }

        return result;
    }

}
