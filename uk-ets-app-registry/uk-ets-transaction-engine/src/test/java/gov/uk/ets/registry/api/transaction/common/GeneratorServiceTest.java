package gov.uk.ets.registry.api.transaction.common;

import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import java.security.NoSuchAlgorithmException;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

class GeneratorServiceTest {

    private final GeneratorService generatorService = new GeneratorService();

    @Test
    void getSecureRandom() throws NoSuchAlgorithmException {
        Assert.assertNotNull(generatorService.getSecureRandom());
    }

    @Test
    void calculateCheckDigitsDifferentCommitmentPeriods() {
        Assert.assertNotEquals(
            generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0),
            generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 1));
    }

    @Test
    void calculateCheckDigitsDifferentTypes() {
        Assert.assertNotEquals(
            generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0),
            generatorService.calculateCheckDigits(KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT.getCode(), 1L, 0));
    }

    @Test
    void calculateCheckDigitsDifferentIdentifier() {
        Assert.assertNotEquals(
            generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0),
            generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 2L, 0));
    }

    @Test
    void calculateCheckDigits() {
        for (int index = 0; index < 10; index++) {
            Assert.assertEquals(
                generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0),
                generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0));
        }
    }

    @Test
    void validateCheckDigits() {
        for (int index = 0; index < 10; index++) {
            int checkDigits = generatorService.calculateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0);
            Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.PARTY_HOLDING_ACCOUNT.getCode(), 1L, 0, checkDigits));
        }
    }

    @Test
    void test_governmentKyotoAccounts() {
        // Net source cancellation account for CP1 (GB-210-10000013-1-70) and for CP2 (GB-210-10000014-2-62)
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.NET_SOURCE_CANCELLATION_ACCOUNT.getCode(), 10000013L, 1, 70));
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.NET_SOURCE_CANCELLATION_ACCOUNT.getCode(), 10000014L, 2, 62));

        // Non-compliance cancellation account for CP1 (GB-220-10000015-1-67) and for CP2 (GB-220-10000016-2-59)
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT.getCode(), 10000015L, 1, 67));
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT.getCode(), 10000016L, 2, 59));

        // tCER Replacement account for expiry for CP1 (GB-411-10000017-1-84) and for CP2 (GB-411-10000018-2-76)
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getCode(), 10000017L, 1, 84));
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getCode(), 10000018L, 2, 76));

        // lCER Replacement account for expiry for CP1 (GB-421-10000019-1-81) and for CP2 (GB-421-10000020-2-73)
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getCode(), 10000019L, 1, 81));
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY.getCode(), 10000020L, 2, 73));

        // lCER Replacement account for reversal of storage for CP1 (GB-422-10000021-1-62) and for CP2 (GB-422-10000022-2-54)
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE.getCode(), 10000021L, 1, 62));
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE.getCode(), 10000022L, 2, 54));

        // lCER Replacement account for non-submission of certification report for CP1 (GB-423-10000023-1-43) and for CP2 (GB-423-10000024-2-35)
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT.getCode(), 10000023L, 1, 43));
        Assert.assertTrue(generatorService.validateCheckDigits(KyotoAccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT.getCode(), 10000024L, 2, 35));
    }
}