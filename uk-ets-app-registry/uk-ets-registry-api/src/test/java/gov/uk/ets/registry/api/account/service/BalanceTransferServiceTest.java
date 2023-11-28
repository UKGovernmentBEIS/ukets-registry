package gov.uk.ets.registry.api.account.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.InstallationOwnership;
import gov.uk.ets.registry.api.account.messaging.UKTLAccountOpeningAnswer;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.transaction.TransactionService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.repository.UnitBlockRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DisplayName("Testing balance transfer related service")
@ExtendWith(SpringExtension.class)
public class BalanceTransferServiceTest {

    @MockBean
    private TransactionService transactionService;
    @MockBean
    private UnitBlockRepository unitBlockRepository;
    @MockBean
    private InstallationOwnershipRepository installationOwnershipRepository;
    @MockBean
    private AccountService accountService;

    private BalanceTransferService balanceTransferService;

    @BeforeEach
    public void setUp() {
        balanceTransferService = new BalanceTransferService(transactionService, unitBlockRepository,
                                                            installationOwnershipRepository,
                                                            accountService);
    }

    @Test
    void testFailedAccountValidationForBalanceTransfer() {
        given(installationOwnershipRepository
                  .findByInstallationAndStatusOrderByOwnershipDateDesc(any(), any())).willReturn(new ArrayList<>());
        given(installationOwnershipRepository
                  .findByAccountIdentifierAndStatus(any(), any())).willReturn(new ArrayList<>());
        Assertions.assertFalse(balanceTransferService.createBalanceTransferTransaction(constructAccountOpeningAnswer()));
    }

    @Test
    void testSuccessfulAccountValidationForBalanceTransfer() {

        List<InstallationOwnership> ownerships = new ArrayList<>();
        InstallationOwnership installationOwnership = new InstallationOwnership();
        Account account = new Account();
        account.setIdentifier(100001L);
        installationOwnership.setAccount(account);
        ownerships.add(installationOwnership);


        given(installationOwnershipRepository
                  .findByInstallationAndStatusOrderByOwnershipDateDesc(any(), any())).willReturn(ownerships);
        given(transactionService.proposeTransaction(any(),anyBoolean())).willReturn(new BusinessCheckResult());
        given(installationOwnershipRepository
                  .findByAccountIdentifierAndStatus(any(), any())).willReturn(new ArrayList<>());
        Assertions.assertTrue(balanceTransferService.createBalanceTransferTransaction(constructAccountOpeningAnswer()));
    }

    private UKTLAccountOpeningAnswer constructAccountOpeningAnswer() {
        UKTLAccountOpeningAnswer answer = new UKTLAccountOpeningAnswer();
        answer.setIdentifier(100002L);
        answer.setFullIdentifier("UK-100-100001-0-90");
        answer.setOldIdentifier(100001L);
        return answer;
    }
}
