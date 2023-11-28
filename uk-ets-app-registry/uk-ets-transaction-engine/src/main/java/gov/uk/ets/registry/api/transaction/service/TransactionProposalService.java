package gov.uk.ets.registry.api.transaction.service;

import static java.lang.String.format;

import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.itl.notice.ITLNoticeService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.AcquiringAccountInfo;
import gov.uk.ets.registry.api.transaction.domain.data.CandidateAcquiringAccounts;
import gov.uk.ets.registry.api.transaction.domain.data.ReturnExcessAllocationTransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ItlNotificationSummary;
import gov.uk.ets.registry.api.transaction.domain.data.ProposedTransactionType;
import gov.uk.ets.registry.api.transaction.domain.data.ReversedAccountInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionBlockSummary;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionAcquiringAccountMode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import gov.uk.ets.registry.api.transaction.repository.AccountHoldingRepository;
import gov.uk.ets.registry.api.transaction.repository.AcquiringAccountRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for facilitating transaction proposals.
 */
@Service
@RequiredArgsConstructor
public class TransactionProposalService {

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * Repository for account holdings.
     */
    private final AccountHoldingRepository accountHoldingRepository;

    /**
     * Repository for acquiring accounts.
     */
    private final AcquiringAccountRepository acquiringAccountRepository;

    /**
     * Service for projects.
     */
    private final ProjectService projectService;

    /**
     * Service for accounts.
     */
    private final TransactionAccountService transactionAccountService;

    /**
     * Service for allocations.
     */
    private final AllocationYearCapService allocationYearCapService;

    /**
     * Service for ITL notifications.
     */
    private final ITLNoticeService itlNoticeService;

    /**
     * Retrieves the transaction types which can be initiated by the provided account.
     *
     * @param accountIdentifier The account identifier.
     * @param isSeniorAdmin     if the account belongs to a senior administrator
     * @return some transaction types.
     */
    public List<ProposedTransactionType> getAvailableTransactionTypes(Long accountIdentifier, boolean isSeniorAdmin) {

        AccountSummary account = transactionPersistenceService.getAccount(accountIdentifier);
        if (account == null) {
            return new ArrayList<>();
        }
        Set<ProposedTransactionType> proposedTransactionTypes = new LinkedHashSet<>();

        Set<TransactionType> transactionTypes = filterAccordingToTransferringAccount(isSeniorAdmin, account.getType(),
                                                                                     account.getAccountStatus());
        transactionTypes = filterBasedOnUnits(accountIdentifier, transactionTypes);
        transactionTypes = filterBasedOnRole(isSeniorAdmin, transactionTypes);

        for (TransactionType type : transactionTypes) {
            if (type.hideFromProposalWizard() || type.isLegacy()) {
                continue;
            }
            ProposedTransactionType proposal = new ProposedTransactionType();
            proposal.setType(type);
            proposal.setDescription(type.getDescription());
            proposal.setHint(type.getHint());
            proposal.setEnabled(type.getProposalEnabled());
            proposal.setSkipAccountStep(!type.has(TransactionAcquiringAccountMode.EXPLICIT));
            proposedTransactionTypes.add(proposal);
        }

        return proposedTransactionTypes.stream()
            .sorted(Comparator.comparing(ProposedTransactionType::getDescription))
            .collect(Collectors.toList());
    }

    /**
     * Enriches the transaction summary so it's ready for signing.
     *
     * @param transactionSummary the partial transaction summary as defined by the user in the client wizard
     * @return A complete transactionSummary.
     */
    public TransactionSummary getTransactionSummaryForSigning(final TransactionSummary transactionSummary) {
        TransactionType transactionType = transactionSummary.getType();
        transactionSummary.setIdentifier(format("%s%s", Constants.getRegistryCode(transactionType.isKyoto()),
            transactionPersistenceService.getNextIdentifier()));
        //TODO: add missing information from ParentTransactionProcessor.createInitialTransaction acquiring and transferring account.
        return transactionSummary;
    }
    
    
    /**
     * Assigns the Transaction Identifiers so it's ready for signing.
     *
     * @param transactionSummary the partial transaction summary as defined by the user in the client wizard
     * @return A complete transactionSummary.
     */
    public ReturnExcessAllocationTransactionSummary getReturnExcessAllocationTransactionSummaryForSigning(final ReturnExcessAllocationTransactionSummary transactionSummary) {
        TransactionType transactionType = transactionSummary.getType();
        transactionSummary.setNatReturnTransactionIdentifier(format("%s%s", Constants.getRegistryCode(transactionType.isKyoto()),
            transactionPersistenceService.getNextIdentifier()));
        transactionSummary.setNerReturnTransactionIdentifier(format("%s%s", Constants.getRegistryCode(transactionType.isKyoto()),
            transactionPersistenceService.getNextIdentifier()));
        //TODO Add logic as required
        return transactionSummary;
    }

    /**
     * Gets the candidate acquiring accounts for the provided account and transaction type.
     *
     * @param accountIdentifier the account business identifier.
     * @param transactionType   the transaction type.
     * @return some candidate acquiring accounts.
     */
    public CandidateAcquiringAccounts getCandidateAcquiringAccounts(Long accountIdentifier,
                                                                    TransactionType transactionType) {
        List<AcquiringAccountInfo> predefinedCandidateAcquiringAccounts =
            this.getPredefinedCandidateAcquiringAccounts(accountIdentifier, transactionType);

        return CandidateAcquiringAccounts.builder()
            .accountId(accountIdentifier)
            .trustedAccountsUnderTheSameHolder(!predefinedCandidateAcquiringAccounts.isEmpty() ? new ArrayList<>() :
                this.getTrustedAccountsUnderTheSameHolder(accountIdentifier))
            .otherTrustedAccounts(!predefinedCandidateAcquiringAccounts.isEmpty() ? new ArrayList<>() :
                this.getOtherTrustedAccounts(accountIdentifier))
            .predefinedCandidateAccounts(predefinedCandidateAcquiringAccounts)
            .predefinedCandidateAccountsDescription(!predefinedCandidateAcquiringAccounts.isEmpty() ?
                getPredefinedCandidateAcquiringAccountsDescription(transactionType) : null)
            .build();
    }

    /**
     * Retrieves the available acquiring accounts under the same account holder.
     *
     * @param accountIdentifier The account identifier.
     * @return some accounts.
     */
    public List<AcquiringAccountInfo> getTrustedAccountsUnderTheSameHolder(Long accountIdentifier) {
        return acquiringAccountRepository.retrieveEligibleAcquiringAccountsUnderTheSameAccountHolder(accountIdentifier);
    }

    /**
     * Retrieves the trusted accounts as acquiring accounts.
     *
     * @param accountIdentifier The account identifier.
     * @return some accounts.
     */
    public List<AcquiringAccountInfo> getOtherTrustedAccounts(Long accountIdentifier) {
        return acquiringAccountRepository.retrieveOtherTrustedAccountsAsAcquiringAccounts(accountIdentifier);
    }

    /**
     * Retrieves the predefined candidate acquiring accounts based on the transaction type.
     *
     * @param accountIdentifier The account identifier.
     * @param transactionType   The transaction type.
     * @return some accounts.
     */
    public List<AcquiringAccountInfo> getPredefinedCandidateAcquiringAccounts(Long accountIdentifier,
                                                                              TransactionType transactionType) {
        if (TransactionType.CentralTransferAllowances.equals(transactionType)) {
            return acquiringAccountRepository
                .retrievePredefinedCandidateEtsAcquiringAccounts(
                    accountIdentifier, transactionType.getAcquiringAccountTypes()
                                                      .stream()
                                                      .map(AccountType::getRegistryType)
                                                      .collect(Collectors.toList()));
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Retrieves the predefined candidate acquiring accounts description.
     *
     * @param transactionType the transaction type.
     * @return the description.
     */
    public String getPredefinedCandidateAcquiringAccountsDescription(TransactionType transactionType) {
        String result = null;
        if (TransactionType.CentralTransferAllowances.equals(transactionType)) {
            result = "Central accounts under HMG";
        }
        return result;
    }

    /**
     * Retrieves details of the acquiring account specified by the user.
     *
     * @param transferringAccountIdentifier  The transferring account identifier.
     * @param acquiringAccountFullIdentifier The acquiring account full identifier.
     * @return an acquiring Account info
     */
    public AcquiringAccountInfo getUserDefinedAcquiringAccount(Long transferringAccountIdentifier,
                                                               String acquiringAccountFullIdentifier,
                                                               TransactionType transactionType,
                                                               String commitmentPeriod,
                                                               Long itlNotificationIdentifier,
                                                               String allocationType) {


        String fullIdentifier = acquiringAccountFullIdentifier;
        if (StringUtils.isBlank(fullIdentifier)) {
            AccountSummary account = transactionAccountService
                .populateAcquiringAccount(TransactionSummary.transactionSummaryBuilder()
                    .transferringAccountIdentifier(transferringAccountIdentifier)
                    .itlNotification(ItlNotificationSummary.builder().notificationIdentifier(itlNotificationIdentifier).build())
                    .type(transactionType)
                    .acquiringAccountCommitmentPeriod(!StringUtils.isEmpty(commitmentPeriod) ?
                        CommitmentPeriod.valueOf(commitmentPeriod).getCode() : null)
                    .allocationType(AllocationType.parse(allocationType))
                    .build());
            if (account != null) {
                fullIdentifier = account.getFullIdentifier();
            }
        }
        AcquiringAccountInfo.AcquiringAccountInfoBuilder builder = AcquiringAccountInfo.acquiringAccountInfoBuilder();
        builder.fullIdentifier(fullIdentifier);
        AccountInfo accountInfo =
            acquiringAccountRepository.retrieveUserDefinedAccount(fullIdentifier);
        if (accountInfo != null) {
            // the account lies inside the registry.
            builder
                .identifier(accountInfo.getIdentifier())
                .accountName(accountInfo.getAccountName())
                .accountHolderName(accountInfo.getAccountHolderName())
                .registryCode(accountInfo.getRegistryCode())
                .trusted(transactionAccountService
                    .isTrustedAccount(transferringAccountIdentifier, accountInfo,
                        accountInfo.getRegistryCode()));
        }
        return builder.build();
    }

    /**
     * Retrieves details of the transferring and acquiring accounts and reverses them
     * for the needs of transaction reversals.
     *
     * @param transferringAccountIdentifier  The transferring account identifier.
     * @param acquiringAccountIdentifier The acquiring account identifier.
     * @return the reversed account info.
     */
    public ReversedAccountInfoDTO populateAccountInfoForReversal(String transferringAccountIdentifier,
                                                                 String acquiringAccountIdentifier) {
        return ReversedAccountInfoDTO
            .builder()
            .transferringAccountInfo(acquiringAccountRepository.retrieveAccountForReversedTransactions(acquiringAccountIdentifier))
            .acquiringAccountInfo(acquiringAccountRepository.retrieveAccountForReversedTransactions(transferringAccountIdentifier))
            .build();
    }

    public <T extends TransactionBlockSummary> List<T> getAvailableUnits(Long accountIdentifier,
                                                                         TransactionType transactionType,
                                                                         Long notificationIdentifier) {
        List<T> result;
        if (notificationIdentifier != null) {
            result = itlNoticeService.getAvailableUnitsForNotification(notificationIdentifier, accountIdentifier,
                transactionType);
            if (result.isEmpty()) {
                throw new IllegalStateException("There are no eligible units for this ITL Notification");
            }
        } else {
            result = getAvailableUnits(accountIdentifier, transactionType);
        }
        return result;
    }

    /**
     * Retrieves the available account units for a specific transaction.
     *
     * @param accountIdentifier The account identifier.
     * @param transactionType   The transaction type.
     * @return some units.
     */
    public <T extends TransactionBlockSummary> List<T> getAvailableUnits(Long accountIdentifier,
                                                                         TransactionType transactionType) {
        List<T> result;
        if (TransactionType.IssueAllowances.equals(transactionType)) {
            result = (List<T>) allocationYearCapService.getCapsForCurrentPhase();

        } else if (transactionType.getUnitsApplicableCommitmentPeriod() != null) {
            if (transactionType.isSubjectToSOP() != null && transactionType.isSubjectToSOP()) {
                result = (List<T>) accountHoldingRepository
                    .getHoldingsForTransaction(accountIdentifier, transactionType.getUnits(),
                        transactionType.getUnitsApplicableCommitmentPeriod(), transactionType.isSubjectToSOP());
            } else if (transactionType.getOriginatingCountryCode() != null) {
                result = (List<T>) accountHoldingRepository
                    .getHoldingsForTransactionWithSpecificOriginatingCountryCode(accountIdentifier,
                        transactionType.getUnits(),
                        transactionType.getUnitsApplicableCommitmentPeriod(),
                        transactionType.getOriginatingCountryCode());
            } else {
                result = (List<T>) accountHoldingRepository
                    .getHoldingsForTransaction(accountIdentifier, transactionType.getUnits(),
                        transactionType.getUnitsApplicableCommitmentPeriod());
            }
        } else {
            result = (List<T>) accountHoldingRepository
                .getHoldingsForTransaction(accountIdentifier, transactionType.getUnits());
        }
        populateAdditionalInformation(accountIdentifier, transactionType.getUnitsApplicableCommitmentPeriod(), result,
            transactionType);
        return result;
    }

    /**
     * Populates additional information, namely the projects & environmental activities where applicable.
     *
     * @param accountIdentifier The account identifier.
     * @param commitmentPeriod  The commitment period.
     * @param blocks            The unit blocks.
     */
    private void populateAdditionalInformation(Long accountIdentifier,
                                               CommitmentPeriod commitmentPeriod,
                                               List<? extends TransactionBlockSummary> blocks,
                                               TransactionType transactionType) {
        for (TransactionBlockSummary block : blocks) {
            if (Boolean.TRUE.equals(block.getType().getRelatedWithProject())) {
                block.setProjectNumbers(
                    projectService.retrieveProjects(accountIdentifier, block.getType(), commitmentPeriod));
            }
            if (Boolean.TRUE.equals(block.getType().getRelatedWithEnvironmentalActivity())) {
                block.setEnvironmentalActivities(projectService
                    .retrieveEnvironmentalActivities(accountIdentifier, block.getType(), commitmentPeriod));
            }
        }
        if (!TransactionType.TransferToSOPForConversionOfERU.equals(transactionType)) {
            blocks.removeIf(block ->
                (block.getType().equals(UnitType.ERU_FROM_AAU) || block.getType().equals(UnitType.ERU_FROM_RMU)) &&
                    block.getSubjectToSop());
        }
    }

    /**
     * Filters the transaction types according to the units of the account.
     *
     * @param accountIdentifier The account identifier.
     * @param transactionTypes  The available transaction types.
     * @return some
     */
    private Set<TransactionType> filterBasedOnUnits(Long accountIdentifier, Set<TransactionType> transactionTypes) {
        Set<TransactionType> result = new HashSet<>();
        List<TransactionBlockSummary> holdings = accountHoldingRepository.getHoldingsOverviewForProposal(accountIdentifier);
        if (!CollectionUtils.isEmpty(holdings)) {
            for (TransactionBlockSummary block : holdings) {
                for (TransactionType type : transactionTypes) {
                    boolean unitsValid = type.getUnits().contains(block.getType());
                    boolean originalPeriodValid = type.getUnitsOriginalCommitmentPeriod() == null ||
                        type.getUnitsOriginalCommitmentPeriod().equals(block.getOriginalPeriod());
                    boolean applicablePeriodValid = type.getUnitsApplicableCommitmentPeriod() == null ||
                        type.getUnitsApplicableCommitmentPeriod().equals(block.getApplicablePeriod());
                    boolean subjectToSop =
                        type.isSubjectToSOP() == null || type.isSubjectToSOP().equals(block.getSubjectToSop());
                    boolean originatingCountryCodeValid = StringUtils.isEmpty(type.getOriginatingCountryCode()) ||
                        type.getOriginatingCountryCode().equals(block.getOriginatingCountryCode());

                    if (unitsValid && originalPeriodValid && applicablePeriodValid && subjectToSop
                        && originatingCountryCodeValid) {
                        result.add(type);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Filters the transaction types according to the role of the current user.
     *
     * @param transactionTypes The available transaction types.
     * @param isSeniorAdmin    A flag to disclose if the user is a senior registry admin or not.
     * @return some transaction types.
     */
    private Set<TransactionType> filterBasedOnRole(boolean isSeniorAdmin, Set<TransactionType> transactionTypes) {
        if (!isSeniorAdmin) {
            return transactionTypes.stream()
                .filter(TransactionType::isAccessibleToAR)
                .collect(Collectors.toSet());
        }
        return transactionTypes;
    }

    /**
     * Filters transaction types according to the transferring account type.
     *
     * @param isSeniorAdmin If current user is a senior admin.
     * @param accountType The account type.
     * @param accountStatus The account status.
     * @return some transaction types.
     */
    private Set<TransactionType> filterAccordingToTransferringAccount(boolean isSeniorAdmin,
                                                                      AccountType accountType,
                                                                      AccountStatus accountStatus) {
        Set<TransactionType> typesAccordingToTransferringAccountType = new HashSet<>();
        if (accountStatus.allTransactionsRestricted()) {
            return typesAccordingToTransferringAccountType;
        }
        for (TransactionType type : TransactionType.values()) {
            if (!CollectionUtils.isEmpty(type.getTransferringAccountTypes()) &&
                type.getTransferringAccountTypes().contains(accountType)) {
                typesAccordingToTransferringAccountType.add(type);
            }
        }
        if (!isSeniorAdmin && accountStatus.isTransferPending()) {
            typesAccordingToTransferringAccountType.removeIf(
                transactionType -> !transactionType.isAllowedFromTransferPendingAccount());
        }
        if(accountStatus.isTransferPending()) {
            typesAccordingToTransferringAccountType.remove(TransactionType.ClosureTransfer);
        }
        if (accountStatus.someTransactionsRestricted()) {
            if (!isSeniorAdmin) {
                typesAccordingToTransferringAccountType.removeIf(
                    transactionType -> !transactionType.isAllowedFromPartiallyTransactionRestrictedAccount());
            }
            typesAccordingToTransferringAccountType.remove(TransactionType.DeletionOfAllowances);
        }
        if (!isSeniorAdmin && accountStatus.isSuspendedOrPartiallySuspended()) {
            typesAccordingToTransferringAccountType.removeIf(
                transactionType -> !transactionType.isAllowedFromSuspendedOrPartiallySuspendedAccount());
        }
        return typesAccordingToTransferringAccountType;
    }
}
