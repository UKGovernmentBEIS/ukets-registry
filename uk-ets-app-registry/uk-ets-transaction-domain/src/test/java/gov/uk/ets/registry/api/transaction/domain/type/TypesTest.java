package gov.uk.ets.registry.api.transaction.domain.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class TypesTest {

    @Test
    void checkPrimaryCodeOfKyotoUnitTypes() {
        assertTrue(Stream.of(UnitType.values()).filter(unitType -> unitType.isKyoto())
            .allMatch(unitType -> unitType.getPrimaryCode() > 0));
    }

    @Test
    @Disabled
    void checkSupplementaryCodeOfKyotoUnitTypes() {
        assertTrue(Stream.of(UnitType.values()).filter(unitType -> unitType.isKyoto())
            .allMatch(unitType -> unitType.getSupplementaryCode() == 0));
    }

    @Test
    void checkKyoto() {
        assertTrue(Stream.of(UnitType.values()).filter(unitType -> unitType.getPrimaryCode() > 0)
            .allMatch(unitType -> unitType.isKyoto()));
    }

    @Test
    void checkSubjectToSop() {
        assertTrue(UnitType.AAU.isSubjectToSop());
        assertTrue(UnitType.ERU_FROM_AAU.isSubjectToSop());
        assertTrue(UnitType.ERU_FROM_RMU.isSubjectToSop());
        assertFalse(UnitType.CER.isSubjectToSop());
        assertFalse(UnitType.AAU.isTransferredToSop());
        assertTrue(UnitType.ERU_FROM_RMU.isTransferredToSop());
        assertTrue(UnitType.ERU_FROM_AAU.isTransferredToSop());
    }

    @Test
    void testKyotoAccountTypes() {
        assertEquals(KyotoAccountType.PARTY_HOLDING_ACCOUNT, KyotoAccountType.parse(100));
        assertEquals(null, KyotoAccountType.parse(999));
        assertEquals(null, KyotoAccountType.parse((Integer) null));
        assertEquals(null, KyotoAccountType.parse(-1));
        assertEquals(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE,
            KyotoAccountType.parse("LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE"));
        assertEquals(null, KyotoAccountType.parse("ZZZZZZ"));
        assertTrue(Stream.of(KyotoAccountType.values()).filter(type -> type.isGovernment())
            .allMatch(type -> type.getCode() >= 130 || type.getCode() == 100));
    }

    @Test
    void testRegistryAccountTypes() {
        assertEquals(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
            RegistryAccountType.parse("OPERATOR_HOLDING_ACCOUNT"));
        assertEquals(null, RegistryAccountType.parse("ZZZZZZ"));
        assertTrue(Stream.of(RegistryAccountType.values()).filter(type -> type.isGovernment()).allMatch(
            type -> type.toString().startsWith("UK") || RegistryAccountType.NATIONAL_HOLDING_ACCOUNT.equals(type)));
    }

    @Test
    void testCP() {
        assertEquals(1, CommitmentPeriod.CP1.getCode());
        assertEquals(CommitmentPeriod.CP2, CommitmentPeriod.findByCode(2));
        assertNotNull(CommitmentPeriod.getCurrentPeriod());
        assertNull(CommitmentPeriod.findByCode(9));
    }

    @Test
    void testAccountStatus() {
        assertTrue(Stream.of(AccountStatus.values()).allMatch(accountStatus -> accountStatus.ordinal() >= 0));
    }

    @Test
    void testEnvironmentalActivities() {
        assertEquals("CM", EnvironmentalActivity.CROPLAND_MANAGEMENT.getAbbreviation());
        assertEquals(Integer.valueOf(6), EnvironmentalActivity.REVEGETATION.getCode());
    }

    @Test
    void testMinorTypes() {
        assertEquals(Integer.valueOf(2), ProjectTrack.TRACK_2.getCode());
        assertNotEquals(ProjectTrack.TRACK_1.getCode(), ProjectTrack.TRACK_2.getCode());
        assertFalse(
            Stream.of(RegistryLevelType.values()).allMatch(registryLevelType -> registryLevelType.ordinal() > 0));
        assertFalse(Stream.of(TaskOutcome.values()).allMatch(type -> type.ordinal() > 0));
    }

    @Test
    void testAccountType() {
        assertEquals(AccountType.OPERATOR_HOLDING_ACCOUNT,
            AccountType.get(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, KyotoAccountType.PARTY_HOLDING_ACCOUNT));
        assertNull(AccountType
            .get(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, KyotoAccountType.MANDATORY_CANCELLATION_ACCOUNT));
        assertEquals(AccountType.OPERATOR_HOLDING_ACCOUNT, AccountType.parse("OPERATOR_HOLDING_ACCOUNT"));
        assertEquals(RegistryAccountType.NATIONAL_HOLDING_ACCOUNT,
            AccountType.NATIONAL_HOLDING_ACCOUNT.getRegistryType());
        assertEquals(RegistryAccountType.NONE, AccountType.VOLUNTARY_CANCELLATION_ACCOUNT.getRegistryType());
        assertEquals(RegistryAccountType.NONE, AccountType.RETIREMENT_ACCOUNT.getRegistryType());
        assertEquals(KyotoAccountType.RETIREMENT_ACCOUNT, AccountType.RETIREMENT_ACCOUNT.getKyotoType());
        assertEquals(KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT,
            AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT.getKyotoType());
        assertEquals(null, AccountType.parse("ZZZ"));
        assertEquals(null, AccountType.parse(null));
        assertTrue(AccountType.AMBITION_INCREASE_CANCELLATION_ACCOUNT.getKyoto());
        assertEquals(Integer.valueOf(130), AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT.getKyotoCode());
        assertNotNull(AccountType.getAllRegistryGovernmentTypes());
        assertTrue(
            AccountType.getAllKyotoGovernmentTypes().contains(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT));
        assertEquals(Constants.REGISTRY_CODE, AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.getRegistryCode());
        assertEquals(Constants.KYOTO_REGISTRY_CODE,
            AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT.getRegistryCode());
        assertEquals("KP - Mandatory cancellation account", AccountType.MANDATORY_CANCELLATION_ACCOUNT.getLabel());
        assertTrue(
            AccountType.of(KyotoAccountType.PARTY_HOLDING_ACCOUNT).contains(AccountType.OPERATOR_HOLDING_ACCOUNT));

        assertEquals(RegistryAccountType.NONE, AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getRegistryType());
        assertEquals(KyotoAccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,
            AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getKyotoType());
        assertEquals(RegistryAccountType.NONE, AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getRegistryType());
        assertEquals(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY,
            AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getKyotoType());
        assertEquals(RegistryAccountType.NONE,
            AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE.getRegistryType());
        assertEquals(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE,
            AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE.getKyotoType());
        assertEquals(RegistryAccountType.NONE,
            AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT.getRegistryType());
        assertEquals(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT,
            AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT.getKyotoType());
    }

    @Test
    void testTransactionStatus() {
        assertEquals(3, TransactionStatus.CHECKED_DISCREPANCY.getCode());
        assertEquals(20, TransactionStatus.REVERSED.getCode());
        assertEquals(TransactionStatus.DELAYED, TransactionStatus.parse(30));
        assertNull(TransactionStatus.parse(200));
    }

    @Test
    void testUnitTypesRelatedWithProjectOrActivities() {
        assertTrue(UnitType.CER.getRelatedWithProject());
        assertFalse(UnitType.AAU.getRelatedWithProject());
        assertTrue(UnitType.RMU.getRelatedWithEnvironmentalActivity());
        assertFalse(UnitType.TCER.getRelatedWithEnvironmentalActivity());
    }

    @Test
    void testGovernmentAccounts() {
        assertFalse(AccountType.UK_AUCTION_DELIVERY_ACCOUNT.isGovernmentAccount());
        assertTrue(AccountType.UK_AUCTION_ACCOUNT.isGovernmentAccount());
        assertFalse(AccountType.OPERATOR_HOLDING_ACCOUNT.isGovernmentAccount());
        assertTrue(AccountType.PARTY_HOLDING_ACCOUNT.isGovernmentAccount());
        assertFalse(AccountType.PERSON_HOLDING_ACCOUNT.isGovernmentAccount());
    }

    @Test
    void testRegistryCodes() {
        assertEquals(RegistryCode.UK, RegistryCode.parse("UK"));
        assertEquals(RegistryCode.GB, RegistryCode.parse("GB"));
        assertNull(RegistryCode.parse(null));
        assertNull(RegistryCode.parse(""));
        assertNull(RegistryCode.parse("ZZ"));
        assertTrue(RegistryCode.isValidRegistryCode("GR"));
        assertFalse(RegistryCode.isValidRegistryCode("ZZ"));
        assertEquals("Greece", RegistryCode.GR.getName());
        assertEquals("300", RegistryCode.GR.getCode());
    }

    @Test
    void testOf() {
        assertEquals(UnitType.AAU, UnitType.of(1, 0));
        assertEquals(UnitType.ERU_FROM_RMU, UnitType.of(4, 0));
        assertNull(UnitType.of(9, 9));

        assertEquals(ProjectTrack.TRACK_1, ProjectTrack.of(1));
        assertEquals(ProjectTrack.TRACK_2, ProjectTrack.of(2));
        assertNull(ProjectTrack.of(3));

        assertEquals(TransactionType.ExternalTransfer, TransactionType.of(3, 0));
        assertEquals(TransactionType.TransferToSOPforFirstExtTransferAAU, TransactionType.of(3, 47));
        assertNull(TransactionType.of(9, 9));

        assertEquals(EnvironmentalActivity.CROPLAND_MANAGEMENT, EnvironmentalActivity.of(4));
        assertEquals(EnvironmentalActivity.FOREST_MANAGEMENT, EnvironmentalActivity.of(3));
        assertNull(EnvironmentalActivity.of(15));

    }

    @Test
    void testUKAllowances() {
        assertFalse(UnitType.ALLOWANCE.isKyoto());
        assertFalse(UnitType.ALLOWANCE.getRelatedWithEnvironmentalActivity());
        assertFalse(UnitType.ALLOWANCE.getRelatedWithProject());
        assertFalse(UnitType.ALLOWANCE.isSubjectToSop());
        assertFalse(UnitType.ALLOWANCE.isTransferredToSop());
        assertFalse(UnitType.ALLOWANCE.isTransferredToSop());
    }

    @Test
    public void testCpIndependedAccounts() {
        assertTrue(AccountType.isCpIndependent(AccountType.PERSON_HOLDING_ACCOUNT));
        assertTrue(AccountType.isCpIndependent(AccountType.OPERATOR_HOLDING_ACCOUNT));
        assertTrue(AccountType.isCpIndependent(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT));
        assertTrue(AccountType.isCpIndependent(AccountType.TRADING_ACCOUNT));
        assertFalse(AccountType.isCpIndependent(AccountType.PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT));
        assertFalse(AccountType.isCpIndependent(AccountType.PARTY_HOLDING_ACCOUNT));
        assertFalse(AccountType.isCpIndependent(null));
    }
}
