package gov.uk.ets.registry.api.transaction;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.features.*;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.CentralTransferCanBeProposedOnlyByAuthority;
import gov.uk.ets.registry.api.transaction.domain.data.*;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryLevelType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.service.IssueUnitsService;
import gov.uk.ets.registry.api.transaction.service.TransactionProposalService;
import gov.uk.ets.registry.api.transaction.web.model.RegistryLevelDTO;
import gov.uk.ets.registry.api.transaction.web.model.TransactionBlockSummariesDTO;
import gov.uk.ets.registry.api.transaction.web.model.TransactionTypesDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;
import static gov.uk.ets.commons.logging.RequestParamType.TRANSACTION_ID;
import static gov.uk.ets.registry.api.authz.ruleengine.RuleInputType.*;

/**
 * A controller to facilitate the transaction proposal UI.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class TransactionWizardController {

    private final TransactionProposalService transactionProposalService;

    private final AuthorizationService authService;

    private final IssueUnitsService issueUnitsService;

    private final AccountAccessService accountAccessService;

    /**
     * Get a list of Proposed Transaction Types for /transaction-types screen, based on the provided Account
     * business identifier.
     *
     * @return a list of proposed transaction types.
     */
    @GetMapping(path = "/transaction-wizard.get.transaction-types")
    @Protected({
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public TransactionTypesDTO getTransactionTypes(@RuleInput(ACCOUNT_ID) @RequestParam
                                                       @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId) {
        List<ProposedTransactionType> transactionTypes =
            transactionProposalService
                .getAvailableTransactionTypes(accountId, authService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN));

        if (accountAccessService.hasSurrenderRight(accountId)) {
            transactionTypes = transactionTypes.stream()
                .filter(transactionType -> transactionType.getType().isOptionAvailableToSurrenderAR())
                .toList();
        }

        return TransactionTypesDTO.builder()
            .accountId(String.valueOf(accountId))
            .result(transactionTypes)
            .build();

    }

    /**
     * Get a list of Transaction Summaries for /allowance.
     *
     * @return a wrapped list of transaction block summaries
     */
    @GetMapping(path = "/transaction-wizard.get.allowance-block-summaries")
    public TransactionBlockSummariesDTO getAllowanceBlockSummaries(
        @RequestParam("transactionType")
            TransactionType transactionType) {

        List<? extends TransactionBlockSummary> availableUnits =
            transactionProposalService.getAvailableUnits(null, transactionType);
        return TransactionBlockSummariesDTO.builder()
            .transactionType(transactionType)
            .result(availableUnits)
            .build();
    }

    /**
     * Get a list of Transaction Summaries for /select-unit-types-quantity screen.
     *
     * @return a wrapped list of transaction block summaries
     */
    @GetMapping(path = "/transaction-wizard.get.transaction-block-summaries")
    @Protected({
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        CentralTransferCanBeProposedOnlyByAuthority.class,
        AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public TransactionBlockSummariesDTO getTransactionBlockSummaries(
        @RuleInput(ACCOUNT_ID) @RequestParam("accountId") @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId,
        @RuleInput(TRANSACTION_TYPE) @RequestParam("transactionType")
            TransactionType transactionType,
        @RequestParam(value = "itlNotificationIdentifier", required = false) Long notificationIdentifier) {

        List<? extends TransactionBlockSummary> availableUnits =
            transactionProposalService.getAvailableUnits(accountId, transactionType, notificationIdentifier);
        return TransactionBlockSummariesDTO.builder()
            .transactionType(transactionType)
            .accountId(accountId)
            .result(availableUnits)
            .build();
    }

    /**
     * Get a list of the candidate acquiring accounts for /specify-acquiring-accounts screen.
     *
     * @return a list of candidate acquiring accounts
     */
    @GetMapping(path = "/transaction-wizard.get.candidate-acquiring-accounts")
    @Protected({
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        CentralTransferCanBeProposedOnlyByAuthority.class,
        AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public CandidateAcquiringAccounts getCandidateAcquiringAccounts(
        @RuleInput(ACCOUNT_ID) @RequestParam @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId,
        @RuleInput(TRANSACTION_TYPE) @RequestParam(required = false) TransactionType transactionType) {
        return this.transactionProposalService.getCandidateAcquiringAccounts(accountId, transactionType);
    }

    /**
     * Get an acquiring account based on the user defined full identifier.
     *
     * @param accountId                      the transferring account identifier
     * @param acquiringAccountFullIdentifier the acquiring account full identifier
     * @return the user defined acquiring account.
     */
    @GetMapping(path = "/transaction-wizard.get.user-defined-acquiring-account")
    @Protected({
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public AcquiringAccountInfo getUserDefinedAcquiringAccount(
        @RuleInput(ACCOUNT_ID) @RequestParam("accountId") @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId,
        @RequestParam("acquiringAccountFullIdentifier")
            String acquiringAccountFullIdentifier) {
        return transactionProposalService.getUserDefinedAcquiringAccount(accountId,
            acquiringAccountFullIdentifier, null, null, null, null);
    }

    @GetMapping(path = "/transaction-wizard.get.reversed-accounts")
    @Protected({SeniorAdminRule.class})
    public ReversedAccountInfoDTO getAccountsForReversalTransactions(
            @RuleInput(ACCOUNT_FULL_ID) @RequestParam("transferringAccountFullIdentifier")
            @MDCParam(TRANSACTION_ID) String transferringAccountFullIdentifier,
            @RequestParam("acquiringAccountFullIdentifier") String acquiringAccountFullIdentifier) {
        return transactionProposalService.populateAccountInfoForReversal(transferringAccountFullIdentifier,
            acquiringAccountFullIdentifier);
    }


    /**
     * Get an acquiring account for allowances.
     *
     * @return the acquiring account.
     */
    @GetMapping(path = "/transaction-wizard.get.allowances-acquiring-account")
    @Protected({AuthorityUserRule.class})
    public AcquiringAccountInfo getUserDefinedAcquiringAccount() {
        return transactionProposalService.getUserDefinedAcquiringAccount(null,
            null, TransactionType.IssueAllowances, null, null, null);
    }

    /**
     * Get an acquiring account based on the skip acquiring account step scenario.
     *
     * @param accountId       the transferring account identifier
     * @param transactionType the transferring account's transaction type
     * @return the populated acquiring account.
     */
    @GetMapping(path = "/transaction-wizard.get.acquiring-account-details")
    @Protected({
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public AcquiringAccountInfo getPopulatedAcquiringAccount(
        @RuleInput(ACCOUNT_ID) @RequestParam("accountId") @MDCParam(RequestParamType.ACCOUNT_ID) Long accountId,
        @RequestParam("proposedTransactionType") TransactionType transactionType,
        @RequestParam("commitmentPeriod") String commitmentPeriod,
        @RequestParam(value = "itlNotificationIdentifier", required = false) Long itlNotificationIdentifier,
        @RequestParam(value = "allocationType", required = false) String allocationType
    ) {
        return transactionProposalService.getUserDefinedAcquiringAccount(accountId,
            null, transactionType, commitmentPeriod, itlNotificationIdentifier, allocationType);
    }

    /**
     * Gets the issuance accounts for the given commitment period.
     *
     * @param commitmentPeriodCode the commitment period code.
     * @return a list of accounts for the given commitment period.
     */
    @GetMapping(value = "/transaction-wizard.get.issuance-accounts")
    @ResponseBody
    public ResponseEntity<List<AccountInfo>> accountsForCommitmentPeriod(
        @RequestParam("commitmentPeriodCode") int commitmentPeriodCode) {
        CommitmentPeriod commitmentPeriod = CommitmentPeriod.findByCode(commitmentPeriodCode);
        if (commitmentPeriod != null) {
            return ResponseEntity.ok(issueUnitsService.getAccountForCommitmentPeriod(
                commitmentPeriod));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Gets the registry levels for the given commitment period and registry level type.
     *
     * @param commitmentPeriodCode the commitment period code.
     * @param registryLevelType    the registry level type.
     * @return A wrapper RegistryLevelDTO result
     */
    @GetMapping(value = "/transaction-wizard.get.registry-levels")
    @ResponseBody
    public ResponseEntity<RegistryLevelDTO> registryLevels(
        @RequestParam("commitmentPeriodCode") int commitmentPeriodCode,
        @RequestParam("registryLevelType")
            RegistryLevelType registryLevelType) {
        return ResponseEntity
            .ok(RegistryLevelDTO.builder().result(issueUnitsService.getUnitTypesForCommitmentPeriod(
                CommitmentPeriod.findByCode(commitmentPeriodCode), registryLevelType)).build());
    }


    /**
     * Gets the registry levels for the given commitment period and registry level type.
     *
     * @param transactionSummary the partial transaction summary as defined by the user
     * @return A complete transactionSummary with all the generated information ready to be signed.
     */
    @PostMapping(value = "/transaction-wizard.get.complete-transaction-summary", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TransactionSummary> signTransaction(
        @RequestBody @MDCParam(DTO) TransactionSummary transactionSummary) {
        TransactionSummary completeTransactionSummary =
            transactionProposalService.getTransactionSummaryForSigning(transactionSummary);
        return ResponseEntity
            .ok(completeTransactionSummary);
    }
    
    /**
     * Gets the transaction summary for Return of Excess Allocation.
     *
     * @param transactionSummary the partial transaction summary as defined by the user
     * @return A complete transactionSummary with all the generated information ready to be signed.
     */
    @PostMapping(value = "/transaction-wizard.get.excess-allocation-nat-and-ner.complete-transaction-summary", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ReturnExcessAllocationTransactionSummary> signReturnExcessAllocationNatAndNerTransaction(
        @RequestBody @MDCParam(DTO) ReturnExcessAllocationTransactionSummary transactionSummary) {
        ReturnExcessAllocationTransactionSummary completeTransactionSummary = 
            transactionProposalService.getReturnExcessAllocationTransactionSummaryForSigning(transactionSummary);
        return ResponseEntity
            .ok(completeTransactionSummary);
    }
}
