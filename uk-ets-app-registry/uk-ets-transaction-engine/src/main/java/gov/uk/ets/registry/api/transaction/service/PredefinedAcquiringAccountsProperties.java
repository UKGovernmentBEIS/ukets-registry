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
@Deprecated(forRemoval = true)
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
        throw new UnsupportedOperationException(
                "Kyoto ITL services have been permanently decommissioned. This method is no longer supported."
            );
    }

}
