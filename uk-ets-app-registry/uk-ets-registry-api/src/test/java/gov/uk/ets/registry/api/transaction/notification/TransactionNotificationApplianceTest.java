package gov.uk.ets.registry.api.transaction.notification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.notification.GroupNotificationClient;
import gov.uk.ets.registry.api.notification.NotificationService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TransactionNotificationApplianceTest {

    @Mock
    GroupNotificationClient groupNotificationClient;
    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TransactionNotificationBuilder transactionNotificationBuilder;
    @Mock
    NotificationService notificationService;
    @Mock
    AccountService accountService;
    @InjectMocks
    TransactionNotificationAppliance transactionNotificationAppliance;

    @Mock
    JoinPoint joinPoint;
    @Mock
    EmitsGroupNotifications notificationAnnotation;
    
    @ParameterizedTest
    @EnumSource(value = AccountType.class, names = {"UK_TOTAL_QUANTITY_ACCOUNT", "UK_ALLOCATION_ACCOUNT", "UK_GENERAL_HOLDING_ACCOUNT", "UK_AUCTION_ACCOUNT","UK_NEW_ENTRANTS_RESERVE_ACCOUNT","UK_MARKET_STABILITY_MECHANISM_ACCOUNT"})
    void shouldIncludeAuthorityUsersForCentralTransferOfAllowancesAndAnyCentralAccount(AccountType accountType) {
        assertTrue(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.CentralTransferAllowances, accountType));
    }
    
    @Test
    void shouldIncludeAuthorityUsersForIssuanceAndTotalQuantityAccount() {
        assertTrue(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.IssueAllowances, AccountType.UK_TOTAL_QUANTITY_ACCOUNT));
    }
    
    /**
     * General Holding Account transfers (as per the spec).
     */
    @Test    
    void shouldIncludeAuthorityUsersForTransferOfAllowancesAndGeneralHoldingAccount() {
        assertTrue(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.TransferAllowances, AccountType.UK_GENERAL_HOLDING_ACCOUNT));
    }
    
    /**
     * Authority user is the initiator of this transaction.
     */
    @Test    
    void shouldIncludeAuthorityUsersForAuctionDeliveryOfAllowancesAndAuctionAccount() {
        assertTrue(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.AuctionDeliveryAllowances, AccountType.UK_AUCTION_ACCOUNT));
    }
    
    /**
     * Returning unsold allowance back the UK Auction Account.
     * Authority user is the recipient (a.k.a acquiring) of this transaction.
     */
    @Test    
    void shouldIncludeAuthorityUsersForExcessAuctionOfAllowancesAndAuctionDeliveryAccount() {
        assertTrue(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.ExcessAuction, AccountType.UK_AUCTION_ACCOUNT));
    }
    

    @Test    
    void shouldNotIncludeAuthorityUsersForTransferOfAllowancesAndAllocationAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.TransferAllowances, AccountType.UK_ALLOCATION_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForTransferOfAllowancesAndSurrenderAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.TransferAllowances, AccountType.UK_SURRENDER_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForTransferOfAllowancesAndDeletionAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.TransferAllowances, AccountType.UK_DELETION_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForSurrenderOfAllowancesAndSurrenderAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.SurrenderAllowances, AccountType.UK_SURRENDER_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForSurrenderOfAllowancesAndReversalSurrenderAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.ReverseSurrenderAllowances, AccountType.UK_SURRENDER_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForDeletionOfAllowancesAndDeletionAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.DeletionOfAllowances, AccountType.UK_DELETION_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForDeletionOfAllowancesAndReversalDeletionAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.ReverseDeletionOfAllowances, AccountType.UK_DELETION_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForAllocateOfAllowancesAndAllocationAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.AllocateAllowances, AccountType.UK_ALLOCATION_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForReverseAllocateOfAllowancesAndAllocationAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.ReverseAllocateAllowances, AccountType.UK_ALLOCATION_ACCOUNT));
    }
    
    @Test    
    void shouldNotIncludeAuthorityUsersForExcessAllocationOfAllowancesAndAllocationAccount() {
        assertFalse(transactionNotificationAppliance.shouldIncludeAuthorityUsers(TransactionType.ExcessAllocation, AccountType.UK_ALLOCATION_ACCOUNT));
    }
}
