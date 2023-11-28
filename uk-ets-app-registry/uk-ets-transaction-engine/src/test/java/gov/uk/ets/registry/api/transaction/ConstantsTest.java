package gov.uk.ets.registry.api.transaction;

import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstantsTest {

    @Test
    void isInboundTransaction() {
        TransactionSummary transactionSummary = new TransactionSummary();
        transactionSummary.setType(TransactionType.ExternalTransfer);
        transactionSummary.setTransferringRegistryCode("JP");
        Assertions.assertTrue(Constants.isInboundTransaction(transactionSummary));

        transactionSummary.setTransferringRegistryCode(null);
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));

        transactionSummary.setTransferringRegistryCode("GB");
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));

        transactionSummary.setTransferringRegistryCode("JP");
        transactionSummary.setType(TransactionType.InternalTransfer);
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));
    }

    @Test
    void testIsInboundTransaction() {
        Transaction transactionSummary = new Transaction();
        transactionSummary.setType(TransactionType.ExternalTransfer);
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transactionSummary.setTransferringAccount(transferringAccount);
        transferringAccount.setAccountRegistryCode("JP");
        Assertions.assertTrue(Constants.isInboundTransaction(transactionSummary));

        transferringAccount.setAccountRegistryCode(null);
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));

        transferringAccount.setAccountRegistryCode("GB");
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));

        transferringAccount.setAccountRegistryCode("JP");
        transactionSummary.setType(TransactionType.InternalTransfer);
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));

        transactionSummary.setTransferringAccount(null);
        transactionSummary.setType(TransactionType.ExternalTransfer);
        Assertions.assertFalse(Constants.isInboundTransaction(transactionSummary));
    }

    @Test
    void testAccountIsInternal() {
        Assertions.assertFalse(Constants.accountIsInternal(null));
        Assertions.assertFalse(Constants.accountIsInternal(""));
        Assertions.assertFalse(Constants.accountIsInternal("  "));
        Assertions.assertFalse(Constants.accountIsInternal("G"));
        Assertions.assertTrue(Constants.accountIsInternal("GB"));
        Assertions.assertTrue(Constants.accountIsInternal("UK"));
        Assertions.assertTrue(Constants.accountIsInternal("GB-100-12345-0-12"));
        Assertions.assertTrue(Constants.accountIsInternal("UK-100-12345-0-12"));
        Assertions.assertFalse(Constants.accountIsInternal("JP-100-12345-0-12"));
        Assertions.assertFalse(Constants.accountIsInternal("JP-100-12345"));
    }

    @Test
    void testIsInternalRegistry() {
        Assertions.assertTrue(Constants.isInternalRegistry(null));
        Assertions.assertTrue(Constants.isInternalRegistry("GB"));
        Assertions.assertTrue(Constants.isInternalRegistry("UK"));
        Assertions.assertFalse(Constants.isInternalRegistry("JP"));
    }

    @Test
    void testGetRegistryCode() {
        Assertions.assertEquals("GB", Constants.getRegistryCode(true));
        Assertions.assertEquals("UK", Constants.getRegistryCode(false));
    }

}
