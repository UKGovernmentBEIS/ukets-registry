package gov.uk.ets.registry.api.transaction.domain.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TransactionConfigurationTest {

    @Test
    void checkVariousBusinessProperties() {
        assertTrue(TransactionType.ExternalTransfer.isKyoto());
        assertTrue(TransactionType.CarryOver_CER_ERU_FROM_AAU.getTransferringAccountTypes().contains(AccountType.PERSON_HOLDING_ACCOUNT));
        assertEquals(1, TransactionType.CarryOver_AAU.getAcquiringAccountTypes().size());
        assertTrue(TransactionType.CarryOver_AAU.getAcquiringAccountTypes().contains(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT));
        assertEquals(TransactionType.InternalTransfer.getDescription(), TransactionType.ExternalTransfer.getDescription());
        assertTrue(TransactionType.TransferToSOPforFirstExtTransferAAU.isExternal());
        assertTrue(TransactionType.InternalTransfer.getDelayApplies());
        assertFalse(TransactionType.CarryOver_AAU.getDelayApplies());
        assertTrue(TransactionType.ExternalTransfer.getDelayApplies());
        assertFalse(TransactionType.TransferToSOPforFirstExtTransferAAU.getReversalAllowed());
        assertEquals(1, TransactionType.TransferToSOPforFirstExtTransferAAU.getUnits().size());
        assertEquals(CommitmentPeriod.CP1, TransactionType.CarryOver_CER_ERU_FROM_AAU.getUnitsApplicableCommitmentPeriod());
        assertEquals(CommitmentPeriod.CP1, TransactionType.CarryOver_AAU.getUnitsOriginalCommitmentPeriod());
        assertEquals(CommitmentPeriod.CP2, TransactionType.AmbitionIncreaseCancellation.getUnitsOriginalCommitmentPeriod());
        assertEquals(TransactionType.Art37Cancellation.getPrimaryCode(), TransactionType.AmbitionIncreaseCancellation.getPrimaryCode());
        assertEquals(0, TransactionType.ExternalTransfer.getSupplementaryCode());
    }

    @Test
    void checkVariousBusinessProperties2() {
        assertFalse(TransactionType.Art37Cancellation.isAccessibleToAR());
        assertFalse(TransactionType.ConversionA.isAccessibleToAR());
        assertFalse(TransactionType.TransferToSOPForConversionOfERU.isAccessibleToAR());
        assertEquals(ExternalPredefinedAccount.CDM_SOP_ACCOUNT, TransactionType.TransferToSOPForConversionOfERU.getExternalAccount());
        assertTrue(TransactionType.InternalTransfer.getProposalEnabled());
        assertFalse(TransactionType.InternalTransfer.hideFromProposalWizard());
        assertTrue(TransactionType.InternalTransfer.getOrder() < TransactionType.CarryOver_AAU.getOrder());
        assertTrue(TransactionType.CarryOver_AAU.has(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY));
        assertFalse(TransactionType.CarryOver_CER_ERU_FROM_AAU.has(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY));
        assertEquals(CommitmentPeriod.CP2, TransactionType.CarryOver_AAU.getPredefinedAccountCommitmentPeriod());
        assertNotNull(TransactionType.TransferToSOPforFirstExtTransferAAU.getExternalAccount());
    }

}
