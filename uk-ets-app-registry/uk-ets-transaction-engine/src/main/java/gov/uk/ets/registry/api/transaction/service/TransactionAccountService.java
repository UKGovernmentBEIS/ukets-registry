package gov.uk.ets.registry.api.transaction.service;

import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckError;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckException;
import gov.uk.ets.registry.api.transaction.domain.AccountIdentifier;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionAcquiringAccountMode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.repository.AccountTotalRepository;
import gov.uk.ets.registry.api.transaction.repository.AcquiringAccountRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Service for accounts.
 */
@Service
@AllArgsConstructor
public class TransactionAccountService {

    /**
     * Persistence service.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Repository for accounts.
     */
    private final AccountTotalRepository accountTotalRepository;

    /**
     * Predefined acquiring accounts.
     */
    private final PredefinedAcquiringAccountsProperties predefinedAcquiringAccountsProperties;

    /**
     * Acquiring accounts.
     */
    private final AcquiringAccountRepository acquiringAccountRepository;

    /**
     * Service for ITL notifications.
     */
    private final ITLNoticeService itlNoticeService;

    public AccountStatus populateAcquiringAccountStatus(String acquiringAccountIdentifier) {
        AccountSummary account = transactionPersistenceService.getAccount(acquiringAccountIdentifier);
        return account != null ? account.getAccountStatus() : null;
    }

    /**
     * Prepares the acquiring account details for this transaction.
     *
     * @param transaction The transaction.
     * @return an account.
     */
    public AccountSummary populateAcquiringAccount(TransactionSummary transaction) {
        AccountSummary account = null;

        if (transaction.getType().isExternal() && !Constants.isInboundTransaction(transaction)
                && transaction.getItlNotification() == null) {
            account = populateExternalAccount(transaction);

        } else if (transaction.getType().hasITLNotification() && transaction.getItlNotification() != null
                && !transaction.getType().has(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)) {
            // The acquiring account may be overridden, if this transaction is meant to fulfil an ITL Notification.
            if (transaction.getAcquiringAccountCommitmentPeriod() == null) {
                transaction.setAcquiringAccountCommitmentPeriod(transaction.getBlocks().get(0).getApplicablePeriod().getCode());
            }
            Optional<AccountSummary> notificationAccount = itlNoticeService.getAcquiringAccount(
                    transaction.getItlNotification().getNotificationIdentifier(), transaction.getType(), transaction.getAcquiringAccountCommitmentPeriod());
            account = notificationAccount.orElseGet(() -> populateInternalAccount(transaction));
        } else {
            account = populateInternalAccount(transaction);
        }

        return account;
    }

    /**
     * Populates information about an external account.
     *
     * @param transaction The transaction.
     * @return an account.
     */
    private AccountSummary populateExternalAccount(TransactionSummary transaction) {
        AccountSummary account = null;

        TransactionType type = transaction.getType();
        if (type.has(TransactionAcquiringAccountMode.PREDEFINED_OUTSIDE_REGISTRY)) {
            // acquiring account is predefined (e.g. CDM SOP account)
            account = AccountSummary.parse(predefinedAcquiringAccountsProperties.getAccount(type.getExternalAccount()),
                RegistryAccountType.NONE, AccountStatus.OPEN);

        } else if (type.has(TransactionAcquiringAccountMode.EXPLICIT)) {
            // acquiring account is hosted to another registry (e.g. JP)
            account = AccountSummary.parse(transaction.getAcquiringAccountFullIdentifier(),
                RegistryAccountType.NONE, AccountStatus.OPEN);
        }

        return account;
    }

    /**
     * Populates information about an internal account.
     *
     * @param transaction The transaction.
     * @return an account.
     */
    private AccountSummary populateInternalAccount(TransactionSummary transaction) {
        AccountSummary account = null;

        TransactionType transactionType = transaction.getType();

        if (transactionType.has(TransactionAcquiringAccountMode.EXPLICIT)) {
            if (transaction.getAcquiringAccountIdentifier() != null) {
                account = transactionPersistenceService.getAccount(transaction.getAcquiringAccountIdentifier());

            } else if (AccountSummary.parseIdentifier(transaction.getAcquiringAccountFullIdentifier()) != null) {
                account = transactionPersistenceService
                    .getAccount(AccountSummary.parseIdentifier(transaction.getAcquiringAccountFullIdentifier()));
                if (account != null) {
                    account.setFullIdentifier(transaction.getAcquiringAccountFullIdentifier());
                }
            }

        } else if (transactionType.has(TransactionAcquiringAccountMode.SAME_AS_TRANSFERRING)) {
            account = transactionPersistenceService.getAccount(transaction.getTransferringAccountIdentifier());

        } else if (transactionType.has(TransactionAcquiringAccountMode.PREDEFINED_INSIDE_REGISTRY)) {
            AccountType accountType = getAccountTypeByAllocationType(transactionType,transaction.getAllocationType());
            account = transactionPersistenceService.getAccount(accountType.getRegistryType(),
            accountType.getKyotoType(), List.of(AccountStatus.OPEN, AccountStatus.SOME_TRANSACTIONS_RESTRICTED),
                                                               getProperCommitmentPeriod(transaction, transactionType));
            checkForEligiblePredefinedAcquiringAccount(account);
        } else if (transactionType.has(TransactionAcquiringAccountMode.REVERSED)) {
            Transaction initialTransaction =
                transactionPersistenceService.getTransaction(transaction.getReversedIdentifier());
            account = transactionPersistenceService.getAccount(
                initialTransaction.getTransferringAccount().getAccountIdentifier());
        }
        return account;
    }

    private void checkForEligiblePredefinedAcquiringAccount(AccountSummary accountSummary) {
        if (Optional.ofNullable(accountSummary).isEmpty()) {
            throw new BusinessCheckException(
                new BusinessCheckError(2018, "No eligible acquiring account was found"));
        }
    }

    private CommitmentPeriod getProperCommitmentPeriod(TransactionSummary summary, TransactionType type) {
        CommitmentPeriod cp = null;
        if (type.getPredefinedAccountCommitmentPeriod() != null) {
            cp = type.getPredefinedAccountCommitmentPeriod();
        } else if (summary.getAcquiringAccountCommitmentPeriod() != null) {
            cp = CommitmentPeriod.findByCode(summary.getAcquiringAccountCommitmentPeriod());
        } else if (!CollectionUtils.isEmpty(summary.getBlocks())) {
            cp = summary.getBlocks().get(0).getApplicablePeriod();
        }
        return cp;
    }

    /**
     * Prepares the transferring account details for this transaction.
     *
     * @param transaction The transaction.
     * @return an account.
     */
    public AccountSummary populateTransferringAccount(TransactionSummary transaction) {
        AccountSummary account;

        if (StringUtils.hasLength(transaction.getReversedIdentifier())) {
            Transaction initialTransaction = transactionPersistenceService.getTransaction(transaction.getReversedIdentifier());
            return transactionPersistenceService.getAccount(initialTransaction.getAcquiringAccount().getAccountIdentifier());
        }

        if (transaction.getTransferringAccountIdentifier() == null &&
            !CollectionUtils.isEmpty(transaction.getType().getTransferringAccountTypes())) {
            throw new IllegalArgumentException("Transferring account identifier is empty.");
        }

        if (Constants.isInternalRegistry(transaction.getTransferringRegistryCode())) {
            account = transactionPersistenceService.getAccount(transaction.getTransferringAccountIdentifier());

        } else {
            // transferring account is outside the registry
            account = AccountSummary.parse(transaction.getTransferringAccountFullIdentifier(), RegistryAccountType.NONE,
                AccountStatus.OPEN);
        }

        return account;
    }
    
    /**
     * Prepares the transferring account details for this transaction.
     *
     * @param transaction The transaction.
     * @return an account.
     */
    public Optional<AccountSummary> populateToBeReplacedUnitsAccount(TransactionSummary transaction) {

        if (transaction.getToBeReplacedBlocksAccountFullIdentifier() == null) {
            return Optional.empty();
        }

        return Optional.of(transactionPersistenceService.getAccount(transaction.getToBeReplacedBlocksAccountFullIdentifier()));
    }    

    /**
     * Locks the account to reserve or transfer units.
     *
     * @param accountIdentifier The account identifier.
     */
    @Transactional
    public void lockAccount(Long accountIdentifier) {
        accountTotalRepository.lockOnAccount(accountIdentifier);
    }

    /**
     * Returns the full identifier of SOP Adaptation Fund Account.
     *
     * @return an account full identifier.
     */
    public String getSopAccountFullIdentifier() {
        return predefinedAcquiringAccountsProperties.getCdmSopAccount();
    }

    /**
     * Retrieves whether the approval of a second Authorised Representative is required.
     *
     * @param accountIdentifier The account identifier.
     * @return false/true
     */
    public boolean approvalOfSecondAuthorisedRepresentativeIsRequired(Long accountIdentifier) {
        return accountTotalRepository.approvalOfSecondAuthorisedRepresentativeIsRequired(accountIdentifier);
    }

    /**
     * Retrieves whether transfers to accounts not on the trusted account list are allowed.
     *
     * @param accountIdentifier The account identifier.
     * @return false/true
     */
    public boolean transfersToAccountsNotOnTheTrustedListAreAllowed(Long accountIdentifier) {
        return accountTotalRepository.transfersToAccountsNotOnTheTrustedListAreAllowed(accountIdentifier);
    }

    /**
     * Retrieves whether a single person approval is required for specific transactions.
     *
     * @param accountIdentifier The account identifier.
     * @return false/true
     */
    public boolean singlePersonApprovalRequired(Long accountIdentifier) {
        return accountTotalRepository.singlePersonApprovalRequired(accountIdentifier);
    }

    /**
     * Retrieves the account holder identifier of the provided account.
     *
     * @param accountIdentifier The account identifier.
     * @return the account holder identifier.
     */
    public Long getAccountHolderIdentifier(Long accountIdentifier) {
        return accountTotalRepository.getAccountHolderIdentifier(accountIdentifier);
    }

    /**
     * Returns whether the accounts in this transaction are trusted.
     *
     * @param transaction             The transaction.
     * @param acquiringAccountSummary The acquiring account summary.
     * @return false/true
     * @deprecated use isTrustedAccount instead
     */
    @Deprecated
    public boolean belongsToTrustedList(TransactionSummary transaction, AccountSummary acquiringAccountSummary) {
        return this.isTrustedAccount(transaction.getTransferringAccountIdentifier(),acquiringAccountSummary, acquiringAccountSummary.getRegistryCode());
    }

    public boolean isTrustedAccount(Long transferringAccountIdentifier, AccountIdentifier acquiringAccountIdentifier,String acquiringAccountRegistryCode) {
        return isInternalTrustedAccount(transferringAccountIdentifier,acquiringAccountIdentifier.getIdentifier(),acquiringAccountRegistryCode) 
            || isExternalTrustedAccount(transferringAccountIdentifier,acquiringAccountIdentifier.getFullIdentifier(),acquiringAccountRegistryCode);
    }
    
    /**
     * Returns whether the accounts in this transaction are trusted.
     *
     * @param transferringAccountIdentifier The transferring account identifier
     * @param acquiringAccountIdentifier    The acquiring account account identifier
     * @param acquiringAccountRegistryCode  The acquiring account account registry code
     * @return false/true
     */
    private boolean isInternalTrustedAccount(Long transferringAccountIdentifier, Long acquiringAccountIdentifier,
                                        String acquiringAccountRegistryCode) {
        boolean result = false;
        if (Constants.isInternalRegistry(acquiringAccountRegistryCode)) {
            Long transferringAccountHolder =
                this.getAccountHolderIdentifier(transferringAccountIdentifier);
            Long acquiringAccountHolder =
                this.getAccountHolderIdentifier(acquiringAccountIdentifier);
            result = Objects.equals(transferringAccountHolder, acquiringAccountHolder);
            if (!result) {
                AccountSummary accountSummary = accountTotalRepository.getAccountSummary(acquiringAccountIdentifier);
                if (accountSummary != null) {
                    result = acquiringAccountRepository
                        .retrieveOtherTrustedAccountsAsAcquiringAccounts(transferringAccountIdentifier)
                        .stream()
                        .map(AccountInfo::getFullIdentifier)
                        .anyMatch(accountSummary.getFullIdentifier()::equals);
                }
            }
        }
        return result;
    }
    
    private boolean isExternalTrustedAccount(Long transferringAccountIdentifier,  String acquiringAccountFullIdentifier,
        String acquiringAccountRegistryCode) {
        boolean result = false;    
        if (!Constants.isInternalRegistry(acquiringAccountRegistryCode)) {
            result = acquiringAccountRepository
                .retrieveOtherTrustedAccountsAsAcquiringAccounts(transferringAccountIdentifier)
                .stream()
                .map(AccountInfo::getFullIdentifier)
                .anyMatch(acquiringAccountFullIdentifier::equals);
        }
        return result;
    }

    private AccountType getAccountTypeByAllocationType(TransactionType transactionType, AllocationType allocationType) {
        if (!transactionType.equals(TransactionType.ExcessAllocation)) {
            return transactionType.getAcquiringAccountTypes().get(0);
        }
        return transactionType.getAcquiringAccountTypes().stream().anyMatch(at -> at.equals(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT))
                && allocationType.equals(AllocationType.NER)
                ? AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT
                : AccountType.UK_ALLOCATION_ACCOUNT;
    }
}
