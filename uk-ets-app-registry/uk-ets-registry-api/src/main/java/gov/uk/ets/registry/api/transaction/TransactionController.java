package gov.uk.ets.registry.api.transaction;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;
import static gov.uk.ets.commons.logging.RequestParamType.TRANSACTION_ID;
import static gov.uk.ets.registry.api.authz.ruleengine.RuleInputType.TRANSACTION;
import static gov.uk.ets.registry.api.authz.ruleengine.RuleInputType.TRANSACTION_IDENTIFIER;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsCanViewOnlyTransactionsOfAccountsWithAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyNonAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesCanViewOnlyTransactionsOfAccountsWithAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthorityUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdministratorOrAuthorisedRepresentativeInitiateRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.UserStatusEnrolledRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.ARsCanViewOnlyTransactionsOfAccountsWithAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.ARsOfTransferringAccountCanCancelTransactionsOfAccountsWithAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.AuthorityCanPerformTransactionActionsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules.CentralTransferCanBeProposedOnlyByAuthority;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckGroup;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckResult;
import gov.uk.ets.registry.api.transaction.domain.ExcessAllocationTransactionFactory;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.data.*;
import gov.uk.ets.registry.api.transaction.service.TransactionManagementService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.transaction.service.TransactionWithTaskService;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchCriteria;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSearchResultMapper;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSortFieldParam;
import gov.uk.ets.registry.api.transaction.web.model.TransactionFiltersDescriptor;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles end-user requests related to transactions.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class TransactionController {

    /**
     * Service for transactions.
     */
    private final TransactionService transactionService;

    /**
     * The task service for transactions.
     */
    private final TransactionWithTaskService transactionWithTaskService;

    /**
     * The authorization service.
     */
    private final AuthorizationService authorizationService;

    /**
     * The transaction management service.
     */
    private final TransactionManagementService transactionManagementService;

    /**
     * Persistence service for transactions.
     */
    private final TransactionPersistenceService transactionPersistenceService;

    /**
     * The factory that is being used in cases of NAT and NER excess allocation.
     */
    private final ExcessAllocationTransactionFactory excessAllocationTransactionFactory;

    /**
     * The user service.
     */
    private final UserService userService;

    /**
     * The transaction search result mapper.
     */
    private final TransactionSearchResultMapper resultMapper;


    /**
     * Executes business checks before the transaction is actually proposed.
     *
     * @param transaction The transaction
     * @return The check result
     */
    @Protected({
        UserStatusEnrolledRule.class,
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        CentralTransferCanBeProposedOnlyByAuthority.class,
        AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority.class,
        AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule.class,
        AuthorityCanPerformTransactionActionsWithAccountAccessRule.class
    })
    @PostMapping(value = "transactions.validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BusinessCheckResult> validate(
        @RuleInput(TRANSACTION) @RequestBody @MDCParam(DTO) TransactionSummary transaction,
        @RequestParam(required = false) BusinessCheckGroup group) {

        return new ResponseEntity<>(transactionService
            .performChecks(transaction, group, authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)),
            HttpStatus.OK);
    }

    /**
     * Executes business checks before the transaction is actually proposed for
     * return of excess allocation nat and ner.
     *
     * @param returnExcessAllocationTransaction The transaction
     * @return The check result
     */
    @Protected({
        UserStatusEnrolledRule.class,
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule.class,
        AuthorityCanPerformTransactionActionsWithAccountAccessRule.class
    })
    @PostMapping(value = "transactions.excess-allocation-nat-and-ner.validate", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<BusinessCheckResult> validateExcessAllocationNatAndNer(
        @RuleInput(TRANSACTION) @RequestBody @MDCParam(DTO) ReturnExcessAllocationTransactionSummary returnExcessAllocationTransaction,
        @RequestParam(required = false) BusinessCheckGroup group) {
        BusinessCheckResult businessCheckResult = null;

        for (TransactionSummary transaction :
                excessAllocationTransactionFactory.createNatAndNerTransactionSummaries(returnExcessAllocationTransaction)) {
            businessCheckResult = transactionService
                    .performChecks(transaction, group, authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN));
            if (businessCheckResult.getErrors() != null &&
                    !businessCheckResult.getErrors().isEmpty()) {
                return new ResponseEntity<>(businessCheckResult,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(businessCheckResult, HttpStatus.OK);
    }

    /**
     * Proceeds to proposal of the transaction.
     *
     * @param transaction The transaction
     * @return The check result, containing the request identifier and transaction identifier in
     * case of successful proposal.
     */
    @Protected({
        UserStatusEnrolledRule.class,
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        CentralTransferCanBeProposedOnlyByAuthority.class,
        AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority.class,
        AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule.class,
        AuthorityCanPerformTransactionActionsWithAccountAccessRule.class
    })
    @PostMapping(value = "transactions.excess-allocation-nat-and-ner.propose", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BusinessCheckResult proposeExcessAllocationNatAndNer(@RuleInput(TRANSACTION) @RequestBody SignedReturnExcessAllocationTransactionSummary
                                                                            returnExcessAllocationTransaction) {
        BusinessCheckResult businessCheckResult = transactionWithTaskService.
            proposeMultipleTransactions(excessAllocationTransactionFactory.
            createNatAndNerSignedTransactions(returnExcessAllocationTransaction));
        if (businessCheckResult.getErrors() != null &&
                businessCheckResult.getErrors().size() > 0) {
            return businessCheckResult;
        }
        
        return businessCheckResult;
    }


    /**
     * Proceeds to proposal of the transaction.
     *
     * @param transaction The transaction
     * @return The check result, containing the request identifier and transaction identifier in
     * case of successful proposal.
     */
    @Protected({
        UserStatusEnrolledRule.class,
        SeniorAdministratorOrAuthorisedRepresentativeInitiateRule.class,
        CentralTransferCanBeProposedOnlyByAuthority.class,
        AuctionDeliveryAllowancesCanBeProposedOnlyByAuthority.class,
        AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule.class,
        AuthorityCanPerformTransactionActionsWithAccountAccessRule.class
    })
    @PostMapping(value = "transactions.propose", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BusinessCheckResult propose(@RuleInput(TRANSACTION) @RequestBody SignedTransactionSummary transaction) {
        return transactionWithTaskService.proposeTransaction(transaction);
    }

    /**
     * Proceeds to proposal of transaction with type
     * {@link gov.uk.ets.registry.api.transaction.domain.type.TransactionType#IssueAllowances}.
     *
     * @param transaction The transaction
     * @return The check result, containing the request identifier and transaction identifier in
     * case of successful proposal.
     */
    @Protected({
        UserStatusEnrolledRule.class,
        AuthorityUserRule.class,
        AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule.class,
        AuthorityCanPerformTransactionActionsWithAccountAccessRule.class
    })
    @PostMapping(value = "transactions.propose-allowances", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public BusinessCheckResult proposeAllowances(
        @RuleInput(TRANSACTION) @RequestBody SignedTransactionSummary transaction) {
        return transactionWithTaskService.proposeTransaction(transaction);
    }

    /**
     * Returns information about the filters of transaction search. It returns the descriptor which
     * contains the available account type options.
     *
     * @return The {@link TransactionFiltersDescriptor}
     */
    @GetMapping(path = "/transactions.list.filters")
    @ResponseBody
    public TransactionFiltersDescriptor getTransactionFiltersDescriptor() {
        boolean currentUserIsAdmin = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
        boolean isAuthorityUser = authorizationService.hasClientRole(UserRole.AUTHORITY_USER);

        return TransactionFiltersDescriptor.build(currentUserIsAdmin, isAuthorityUser);
    }

    /**
     * Searches for transactions according to the criteria parameter and returns the page of sorted
     * results as the pageParameters argument instructs
     *
     * @param criteria       The {@link TransactionSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} paging info
     * @return The {@link SearchResponse<TransactionSearchResult>} response
     */
    @GetMapping(path = "/transactions.list")
    @ResponseBody
    public SearchResponse<TransactionSearchResult> search(@Valid TransactionSearchCriteria criteria,
                                                          PageParameters pageParameters,
                                                          @RequestHeader(name = "Is-Report", required = false)
                                                              boolean isReport) {
        SearchResponse<TransactionSearchResult> response = new SearchResponse<>();
        PageableMapper pageableMapper =
            new PageableMapper(TransactionSortFieldParam.values(), TransactionSortFieldParam.TRANSACTION_IDENTIFIER);
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<TransactionProjection> page = transactionManagementService.search(criteria, pageable, isReport);
        response.setItems(page.getContent().stream().map(resultMapper::map).collect(Collectors.toList()));
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);
        return response;
    }

    /**
     * Generates the transaction details report.
     * 
     * @param transactionIdentifier the business identifier of the transaction
     * @return the generated report identifier
     */
    @GetMapping(path = "/transactions.generate.details.report")
    public ResponseEntity<Long> requestTransactionDetailsReport(
        @RequestParam @MDCParam(TRANSACTION_ID) String transactionIdentifier) {
      
        Long reportId = transactionManagementService.requestTransactionDetailsReport(transactionIdentifier);
            
        return new ResponseEntity<>(reportId, HttpStatus.OK);            
    }     
    
    /**
     * Retrieves a transaction based on its transaction ID.
     *
     * @param transactionIdentifier The unique business identifier.
     * @return a {@link TransactionSummary}.
     */
    @Protected({
        ARsCanViewOnlyTransactionsOfAccountsWithAccess.class,
        AdminsCanViewOnlyTransactionsOfAccountsWithAccessRule.class,
        AuthoritiesCanViewOnlyTransactionsOfAccountsWithAccessRule.class
    })
    @GetMapping(path = "transactions.get", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TransactionSummary> getTransaction(@RuleInput(TRANSACTION_IDENTIFIER)
                                                             @RequestParam @MDCParam(TRANSACTION_ID) String transactionIdentifier) {

        Transaction transaction = transactionPersistenceService.getTransaction(transactionIdentifier);
        TransactionSummary dto = transactionManagementService.getTransactionSummary(transaction);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Retrieves the history (events & comments) of the provided transaction.
     *
     * @param transactionIdentifier The unique business identifier.
     * @return the task history
     */
    @Protected({
        ARsCanViewOnlyTransactionsOfAccountsWithAccess.class,
        AdminsCanViewOnlyTransactionsOfAccountsWithAccessRule.class,
        AuthoritiesCanViewOnlyTransactionsOfAccountsWithAccessRule.class
    })
    @GetMapping(path = "transactions.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditEventDTO> getTransactionHistory(@RuleInput(TRANSACTION_IDENTIFIER)
                                                     @RequestParam @MDCParam(TRANSACTION_ID) String transactionIdentifier) {
        return transactionManagementService.getTransactionHistory(transactionIdentifier);
    }

    /**
     * Cancels a transaction which is in Delayed Status.
     */
    @Protected({
        ARsOfTransferringAccountCanCancelTransactionsOfAccountsWithAccess.class,
        AnyNonAdminRule.class,
        AdminsCanPerformTransactionActionsWithAccountAccessAndReversalsRule.class,
        AuthorityCanPerformTransactionActionsWithAccountAccessRule.class
    })
    @PostMapping(value = "/transactions.cancel", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void cancel(@RuleInput(TRANSACTION_IDENTIFIER) @RequestParam
                           @MDCParam(TRANSACTION_ID) String transactionIdentifier,
                       @RequestParam String comment) {
        User currentUser = userService.getCurrentUser();
        transactionService.manuallyCancel(transactionIdentifier, comment, currentUser.getUrid());
    }
}
