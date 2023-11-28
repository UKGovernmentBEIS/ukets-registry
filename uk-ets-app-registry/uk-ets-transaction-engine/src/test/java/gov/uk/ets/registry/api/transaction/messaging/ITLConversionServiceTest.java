package gov.uk.ets.registry.api.transaction.messaging;

import gov.uk.ets.registry.api.transaction.domain.AccountBasicInfo;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionBlock;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.EnvironmentalActivity;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.repository.ProjectRepository;
import gov.uk.ets.registry.api.transaction.service.ProjectService;
import uk.gov.ets.lib.commons.kyoto.types.NotificationRequest;
import uk.gov.ets.lib.commons.kyoto.types.ProposalRequest;
import uk.gov.ets.lib.commons.kyoto.types.TransactionUnitBlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class ITLConversionServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectService projectService;

    private ITLConversionService itlConversionService;

    private ITLBlockConversionService itlBlockConversionService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        projectService = new ProjectService(projectRepository);
        itlBlockConversionService = new ITLBlockConversionService(projectService);
        itlConversionService = new ITLConversionService(itlBlockConversionService);
    }

    @Test
    void acceptProposal() {
        Transaction transaction = new Transaction();
        transaction.setStatus(TransactionStatus.PROPOSED);
        transaction.setIdentifier("GB123456");
        transaction.setStarted(new Date());
        AccountBasicInfo transferringAccount = new AccountBasicInfo();
        transferringAccount.setAccountIdentifier(100_000L);
        transferringAccount.setAccountRegistryCode("GB");
        transferringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transaction.setTransferringAccount(transferringAccount);
        AccountBasicInfo acquiringAccount = new AccountBasicInfo();
        acquiringAccount.setAccountIdentifier(100_001L);
        acquiringAccount.setAccountRegistryCode("JP");
        acquiringAccount.setAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        transaction.setAcquiringAccount(acquiringAccount);
        transaction.setType(TransactionType.ExternalTransfer);

        TransactionBlock block = new TransactionBlock();
        block.setType(UnitType.CER);
        block.setProjectNumber("JP1000");
        block.setProjectTrack(ProjectTrack.TRACK_2);
        block.setOriginatingCountryCode("GB");
        block.setOriginalPeriod(CommitmentPeriod.CP1);
        block.setApplicablePeriod(CommitmentPeriod.CP2);
        block.setStartBlock(1_000_000_000L);
        block.setEndBlock(1_000_000_100L);

        TransactionBlock block2 = new TransactionBlock();
        block2.setType(UnitType.RMU);
        block2.setOriginatingCountryCode("GB");
        block2.setOriginalPeriod(CommitmentPeriod.CP1);
        block2.setEnvironmentalActivity(EnvironmentalActivity.REVEGETATION);
        block2.setApplicablePeriod(CommitmentPeriod.CP1);
        block2.setStartBlock(1_000L);
        block2.setEndBlock(1_001L);

        ProposalRequest request = itlConversionService.prepareAcceptProposal(transaction, Arrays.asList(block, block2));
        TransactionUnitBlock[] blocks = request.getProposedTransaction().getProposalUnitBlocks();

        assertNotNull(request);
        assertEquals("GB", request.getFrom());
        assertEquals(1, request.getMajorVersion());
        assertEquals(1, request.getMinorVersion());
        assertEquals("ITL", request.getTo());
        assertNotNull(request.getProposedTransaction());

        assertTrue(request.getProposedTransaction().getProposalUnitBlocks().length > 0);
        assertEquals("JP", request.getProposedTransaction().getAcquiringRegistryCode());
        assertEquals("GB", request.getProposedTransaction().getTransferringRegistryCode());
        assertEquals(3, request.getProposedTransaction().getTransactionType());
        assertNull(request.getProposedTransaction().getSuppTransactionType());
        assertEquals("GB123456", request.getProposedTransaction().getTransactionIdentifier());

        ProposalRequest request1 = itlConversionService.prepareAcceptProposal(transaction, null);
        assertNull(request1.getProposedTransaction().getProposalUnitBlocks());

        request1 = itlConversionService.prepareAcceptProposal(transaction, new ArrayList<>());
        assertNull(request1.getProposedTransaction().getProposalUnitBlocks());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            itlConversionService.parseAcceptProposal(null);
        });

        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setFrom("TR");
        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setFrom("ITL");
        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setTo("GR");
        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setTo("GB");
        request1.setMajorVersion(7);
        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setMinorVersion(7);
        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setMajorVersion(1);
        assertNull(itlConversionService.parseAcceptProposal(request1));
        request1.setMinorVersion(1);
        assertNotNull(itlConversionService.parseAcceptProposal(request1));
        request1.getProposedTransaction().setProposalUnitBlocks(blocks);

        TransactionSummary request2 = itlConversionService.parseAcceptProposal(request1);
        assertNotNull(request2);
        assertNotNull(request2.getBlocks());

    }

    @Test
    void test_prepareNotificationRequestForTransactionCompletion() {
        NotificationRequest notificationRequest = itlConversionService
                .prepareNotificationRequestForTransactionCompletion("GB12345");
        assertEquals(TransactionStatus.COMPLETED.getCode(), notificationRequest.getTransactionStatus());
        assertEquals(Constants.KYOTO_REGISTRY_CODE, notificationRequest.getFrom());
        assertEquals(Constants.ITL_TO, notificationRequest.getTo());
        assertEquals(Constants.ITL_MAJOR_VERSION.intValue(), notificationRequest.getMajorVersion());
        assertEquals(Constants.ITL_MINOR_VERSION.intValue(), notificationRequest.getMinorVersion());
        assertEquals("GB12345", notificationRequest.getTransactionIdentifier());
    }
}