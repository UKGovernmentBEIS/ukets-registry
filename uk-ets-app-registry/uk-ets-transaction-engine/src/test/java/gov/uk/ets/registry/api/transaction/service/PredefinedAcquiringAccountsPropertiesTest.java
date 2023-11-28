package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.transaction.domain.type.ExternalPredefinedAccount;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = PredefinedAcquiringAccountsProperties.class)
@TestPropertySource(properties = {
    "business.property.transaction.predefined.acquiring.account.cdmSopAccount                                           = CDM-100-1100-0",
    "business.property.transaction.predefined.acquiring.account.cdmExcessIssuanceAccountForCP2                          = CDM-240-2240-2",
    "business.property.transaction.predefined.acquiring.account.ccsNetReversalCancellationAccount                       = CDM-241-2241-2",
    "business.property.transaction.predefined.acquiring.account.ccsNonSubmissionOfVerificationReportCancellationAccount = CDM-242-2242-2"})
class PredefinedAcquiringAccountsPropertiesTest {

    @Autowired
    private PredefinedAcquiringAccountsProperties predefinedAcquiringAccountsProperties;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getAccount() {
        Assert.assertEquals("CDM-100-1100-0", predefinedAcquiringAccountsProperties.getCdmSopAccount());
        Assert.assertEquals("CDM-100-1100-0", predefinedAcquiringAccountsProperties.getAccount(ExternalPredefinedAccount.CDM_SOP_ACCOUNT));

        Assert.assertEquals("CDM-241-2241-2", predefinedAcquiringAccountsProperties.getCcsNetReversalCancellationAccount());
        Assert.assertEquals("CDM-241-2241-2", predefinedAcquiringAccountsProperties.getAccount(ExternalPredefinedAccount.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT));

        Assert.assertEquals("CDM-242-2242-2", predefinedAcquiringAccountsProperties.getCcsNonSubmissionOfVerificationReportCancellationAccount());
        Assert.assertEquals("CDM-242-2242-2", predefinedAcquiringAccountsProperties.getAccount(ExternalPredefinedAccount.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT));

        Assert.assertEquals("CDM-240-2240-2", predefinedAcquiringAccountsProperties.getCdmExcessIssuanceAccountForCP2());
        Assert.assertEquals("CDM-240-2240-2", predefinedAcquiringAccountsProperties.getAccount(ExternalPredefinedAccount.CDM_EXCESS_ISSUANCE_ACCOUNT_FOR_CP2));

        Assertions.assertThrows(NullPointerException.class, () ->
            predefinedAcquiringAccountsProperties.getAccount(null));

    }
}