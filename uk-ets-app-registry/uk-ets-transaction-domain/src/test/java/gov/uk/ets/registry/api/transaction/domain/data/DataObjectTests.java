package gov.uk.ets.registry.api.transaction.domain.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class DataObjectTests {

    @Test
    public void testAccountSummary() {
        AccountSummary account = AccountSummary.parse("JP-100-12345-0", RegistryAccountType.NONE, AccountStatus.OPEN);
        assertFalse(account.belongsToRegistry());
        assertEquals(AccountType.PARTY_HOLDING_ACCOUNT, account.getType());
        assertEquals("JP", account.getRegistryCode());
        assertEquals(12345L, account.getIdentifier());
        assertEquals(0, account.getCheckDigits());
        assertEquals(0, account.getCommitmentPeriod());

        AccountSummary account2 = new AccountSummary(1L, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, KyotoAccountType.PARTY_HOLDING_ACCOUNT,
                                                     AccountStatus.SOME_TRANSACTIONS_RESTRICTED, Constants.REGISTRY_CODE, "UK-100-1-2-22", 2,
                                                     200L, UnitType.MULTIPLE);

        assertEquals(AccountType.OPERATOR_HOLDING_ACCOUNT, account2.getType());
        assertEquals(200L, account2.getBalance());
        assertEquals(UnitType.MULTIPLE, account2.getUnitType());
        assertNotNull(account2.getRegistryCode());
        assertNotNull(account2.getKyotoAccountType());
        assertNotNull(account2.getRegistryAccountType());
        assertNotNull(account2.getAccountStatus());
        assertNotNull(account2.getFullIdentifier());

        assertNotEquals(account, account2);
        account.setIdentifier(1L);
        assertEquals(account, account2);
    }

    @Test
    public void testProposedTransactionType() {
        ProposedTransactionType proposal = new ProposedTransactionType();
        proposal.setType(TransactionType.CarryOver_AAU);
        proposal.setEnabled(proposal.getType().getProposalEnabled());
        proposal.setDescription(proposal.getType().getDescription());
        proposal.setSupportsNotification(false);
        proposal.setCategory("Kyoto");
        assertNotNull(proposal.getCategory());
        assertNotNull(proposal.getDescription());
        assertNotNull(proposal.getEnabled());
        assertNotNull(proposal.getSupportsNotification());
        assertNotNull(proposal.getType());
        assertNotNull(proposal.toString());

        ProposedTransactionType proposal2 = new ProposedTransactionType();
        proposal2.setType(TransactionType.ExternalTransfer);

        assertNotEquals(proposal, proposal2);

        proposal2.setType(TransactionType.CarryOver_AAU);
        proposal2.setDescription(proposal2.getType().getDescription());
        assertEquals(proposal, proposal2);

        Map<ProposedTransactionType, String> map = new HashMap<>() {{
           put(proposal, null);
           put(proposal2, null) ;
        }};
        assertEquals(1, map.size());

    }

    @Test
    public void testTransactionResponseSummary() {
        TransactionResponseSummary summary = TransactionResponseSummary.builder()
            .dateOccurred(new Date())
            .details("Error in response")
            .errorCode(1515L)
            .transactionBlockId(null)
            .build();

        assertNotNull(summary.getDateOccurred());
        assertNotNull(summary.getDetails());
        assertNotNull(summary.getDateOccurred());
        assertNull(summary.getTransactionBlockId());
        assertNotNull(summary.getErrorCode());

        summary.setDateOccurred(null);
        summary.setDetails("Details");
        summary.setErrorCode(1L);
        summary.setTransactionBlockId(10L);

        Date date = new Date();
        TransactionResponseSummary summary1 = TransactionResponseSummary.builder().errorCode(222L).dateOccurred(date).transactionBlockId(22L).build();
        assertNotEquals(summary, summary1);

        TransactionResponseSummary summary2 = TransactionResponseSummary.builder().errorCode(100L).dateOccurred(date).transactionBlockId(22L).build();
        assertNotEquals(summary1, summary2);

        summary2.setErrorCode(222L);
        assertEquals(summary1, summary2);

        Map<TransactionResponseSummary, String> map = new HashMap<>();
        map.put(summary1, null);
        map.put(summary2, null);
        assertEquals(1, map.size());

        TransactionResponseSummary response = TransactionResponseSummary.builder()
            .errorCode(1L)
            .build();
        TransactionResponseSummary response2 = TransactionResponseSummary.builder()
            .errorCode(1L)
            .build();
        assertEquals(response, response2);

        response.setDateOccurred(date);
        response2.setDateOccurred(date);
        assertEquals(response, response2);

        response.setTransactionBlockId(1L);
        response2.setTransactionBlockId(1L);
        assertEquals(response, response2);

        response2.setTransactionBlockId(2L);
        assertNotEquals(response, response2);

        response2.setTransactionBlockId(1L);
        response2.setDateOccurred(new Date(LocalDate.of(2020, 03, 30).toEpochDay()));
        assertNotEquals(response, response2);

        response2.setDateOccurred(date);
        response2.setErrorCode(444L);
        assertNotEquals(response, response2);


    }

    @Test
    public void testTransactionSummary() {
        TransactionSummary transactionSummary = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer)
            .identifier("GB12345")
            .quantity(100L)
            .status(TransactionStatus.PROPOSED)
            .acquiringAccountCommitmentPeriod(0)
            .acquiringAccountIdentifier(12345L)
            .acquiringAccountRegistryCode("JP")
            .acquiringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .acquiringAccountFullIdentifier("JP-100-12345-0")
            .transferringAccountCommitmentPeriod(2)
            .transferringAccountIdentifier(11L)
            .transferringAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT)
            .transferringAccountFullIdentifier("GB-100-11-2-22")
            .taskIdentifier(100L)
            .unitType(UnitType.MULTIPLE)
            .build();

        TransactionSummary transactionSummary1 = new TransactionSummary();
        transactionSummary1.setIdentifier("GB12345");

        TransactionSummary transactionSummary2 = new TransactionSummary();
        transactionSummary2.setIdentifier("GB2");

        Map<TransactionSummary, String> map = new HashMap<>() {{
           put(transactionSummary, null);
           put(transactionSummary1, null);
           put(transactionSummary2, null);
        }};
        assertEquals(2, map.size());
        assertNotNull(transactionSummary2.toString());

        TransactionBlockSummary block1 = TransactionBlockSummary.builder()
            .applicablePeriod(CommitmentPeriod.CP2)
            .originalPeriod(CommitmentPeriod.CP2)
            .originatingCountryCode("GB")
            .type(UnitType.AAU)
            .quantity("20")
            .build();

        TransactionBlockSummary block2 = TransactionBlockSummary.builder()
            .applicablePeriod(CommitmentPeriod.CP1)
            .originalPeriod(CommitmentPeriod.CP1)
            .originatingCountryCode("GB")
            .type(UnitType.RMU)
            .environmentalActivity(EnvironmentalActivity.DEFORESTATION)
            .startBlock(1L)
            .endBlock(10L)
            .build();


        TransactionBlockSummary block3 = TransactionBlockSummary.builder()
            .applicablePeriod(CommitmentPeriod.CP1)
            .originalPeriod(CommitmentPeriod.CP1)
            .originatingCountryCode("GB")
            .type(UnitType.CER)
            .projectNumber("JP123")
            .quantity("10")
            .build();

        transactionSummary.setBlocks(Arrays.asList(block1, block2, block3));
        assertEquals(40L, transactionSummary.calculateQuantity());
        assertEquals(10L, transactionSummary.calculateQuantity(UnitType.CER));
        assertEquals(10L, transactionSummary.calculateQuantity(UnitType.RMU));
        assertEquals(UnitType.MULTIPLE, transactionSummary.calculateUnitTypes());
        assertNotNull(transactionSummary.toString());

        transactionSummary2.setBlocks(Collections.singletonList(block3));
        assertEquals(UnitType.CER, transactionSummary2.calculateUnitTypes());

        TransactionBlockSummary block5 = new TransactionBlockSummary();
        block5.setStartBlock(100L);
        block5.setEndBlock(109L);
        block5.setOriginatingCountryCode("GB");

        TransactionBlockSummary block6 = new TransactionBlockSummary(
            UnitType.CER, CommitmentPeriod.CP1, CommitmentPeriod.CP1, 50L, false
        );
        block6.setStartBlock(100L);
        block6.setEndBlock(109L);
        block6.setOriginatingCountryCode("GB");

        TransactionBlockSummary block7 = new TransactionBlockSummary(
            UnitType.RMU, CommitmentPeriod.CP1, CommitmentPeriod.CP1, EnvironmentalActivity.FOREST_MANAGEMENT, 50L, null, false, "JP111"
        );

        Map<TransactionBlockSummary, String> mapBlock = new HashMap<>() {{
           put(block1, null);
           put(block5, null);
           put(block6, null);
        }};
        assertEquals(2, mapBlock.size());

        TransactionBlockSummary transactionBlockSummary1 = TransactionBlockSummary.builder().startBlock(1L).endBlock(2L).originatingCountryCode("GB")
            .type(UnitType.AAU).build();
        TransactionBlockSummary transactionBlockSummary2 = TransactionBlockSummary.builder().startBlock(1L).endBlock(2L).originatingCountryCode("GB")
            .type(UnitType.CER).build();
        assertEquals(transactionBlockSummary1, transactionBlockSummary2);

        transactionBlockSummary2.setOriginatingCountryCode("JP");
        assertNotEquals(transactionBlockSummary1, transactionBlockSummary2);

        transactionBlockSummary2.setQuantity("xxx");
        assertEquals(0L, transactionBlockSummary2.calculateQuantity());

        TransactionSummary transactionSummary3 = TransactionSummary.transactionSummaryBuilder()
            .type(TransactionType.ExternalTransfer).build();

        assertNull(transactionSummary3.calculateUnitTypes());
        assertEquals(0L, transactionSummary3.calculateQuantity());

        transactionBlockSummary2.setStartBlock(1L);
        transactionBlockSummary2.setEndBlock(1L);
        transactionSummary3.setBlocks(Collections.singletonList(transactionBlockSummary2));
        assertTrue(transactionSummary3.calculateQuantity() > 0);
        assertEquals(0, (long) transactionSummary3.calculateQuantity(null));
        assertEquals(0, (long) transactionSummary3.calculateQuantity(UnitType.ERU_FROM_AAU));

    }

}
