package gov.uk.ets.registry.api.account;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.RequestParamType;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.domain.UnitBlockFilter;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.validation.AccountValidator;
import gov.uk.ets.registry.api.account.web.mappers.AccountFilterMapper;
import gov.uk.ets.registry.api.account.web.mappers.AccountSearchPageableMapper;
import gov.uk.ets.registry.api.account.web.model.*;
import gov.uk.ets.registry.api.account.web.model.AccountHoldingDetailsDTO.UnitBlockDTO;
import gov.uk.ets.registry.api.account.web.model.search.AccountFiltersDescriptor;
import gov.uk.ets.registry.api.account.web.model.search.AccountSearchCriteria;
import gov.uk.ets.registry.api.account.web.model.search.AccountSearchResult;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewRequestsOnlyForAccountsWithAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ReadOnlyAdministratorsCannotSubmitRequest;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.account.rules.*;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.PageableMapper;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.common.view.RequestDTO;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.UnitBlock;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSearchResultMapper;
import gov.uk.ets.registry.api.transaction.web.mapper.TransactionSortFieldParam;
import gov.uk.ets.registry.api.transaction.web.model.TransactionSearchResult;
import gov.uk.ets.registry.api.user.domain.UserRole;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;
import java.util.stream.Collectors;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static gov.uk.ets.commons.logging.RequestParamType.*;


/**
 * Handles requests for account management.
 */
@Tag(name = "Account Management")
@Log4j2
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class AccountController {

    /**
     * Service for accounts.
     */
    private final AccountService accountService;
    private final AuthorizationService authService;
    private final AccountValidator accountValidator;
    private final AccountOperatorUpdateService accountOperatorUpdateService;
    private final TransactionSearchResultMapper resultMapper;


    /**
     * Executes business checks for the account full identifier.
     *
     * @param accountFullIdentifier The full identifier of the account
     * @return until this is implemented we will not return anything.
     */
    @PostMapping(value = "accounts.validate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<ValidateAccountDTO> validate(@RequestParam @MDCParam(ACCOUNT_FULL_ID) String accountFullIdentifier) {
        Boolean isKyotoAccountType = null;
        Account account = accountService.getAccountFullIdentifier(accountFullIdentifier);
        if (account != null) {
            AccountType accountType = AccountType
                    .get(account.getRegistryAccountType(), account.getKyotoAccountType());
            isKyotoAccountType = accountType.getKyoto();
        }
        return new ResponseEntity<>(ValidateAccountDTO.builder()
                .validAccount(accountService.checkAccountFullIdentifier(accountFullIdentifier))
                .kyotoAccountType(isKyotoAccountType)
                .build(), HttpStatus.OK);
    }

    /**
     * Submits an account opening proposal.
     *
     * @param account The account transfer object.
     */
    @PostMapping(path = "accounts.propose", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDTO proposeAccount(@RequestBody @Valid @MDCParam(DTO) AccountDTO account) {
        accountValidator.validate(account);
        return accountService.proposeAccount(account);
    }

    /**
     * Submits an account closure request.
     *
     * @param fullIdentifier The account full identifier.
     * @param closureDTO     The account closure DTO.
     * @return the task request identifier.
     */
    @Protected({ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
        ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess.class,
        ReadOnlyAdministratorsCannotSubmitRequest.class,
        NotApplicableToETSGovernmentAccountRule.class,
        NonZeroAccountBalanceRule.class,
        AccountWithOutstandingTasksRule.class,
        PendingActivationTrustedAccountsRule.class,
        AccountWithOutstandingExceptDelayedTransactionsRule.class,
        LastYearOfVerifiedEmissionsRule.class,
        TransferPendingWithLinkedInstallation.class,
        MissingEmissionsBetweenFyveAndLyveRule.class,
        PendingTALRequestsRule.class
    })
    @PostMapping(path = "accounts.close", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> closeAccount(
        @RuleInput(RuleInputType.ACCOUNT_FULL_ID)
        @RequestParam @MDCParam(ACCOUNT_FULL_ID) String fullIdentifier,
        @RequestBody @Valid AccountClosureDTO closureDTO) {
        Long taskIdentifier = accountService.closeAccount(fullIdentifier, closureDTO);
        return new ResponseEntity<>(taskIdentifier, HttpStatus.OK);
    }

    /**
     * Updates the account details
     *
     * @param identifier the account identifier
     * @param details    the updated AccountDetails DTO
     */
    @Protected({
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PostMapping(path = "accounts.update.details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccountDTO> updateAccountDetails(
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long identifier,
        @Valid @RequestBody AccountDetailsDTO details) {

        accountValidator.validate(details);
        AccountDTO accountDTO = accountService.updateAccountDetails(identifier, details);
        return new ResponseEntity<>(accountDTO, HttpStatus.OK);
    }

    /**
     * Searches for accounts according to criteria and returns the page of sorted results as
     * the pageParameters argument instructs
     *
     * @param criteria       The {@link AccountSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} paging info
     * @return The {@link SearchResponse<AccountSearchResult>} response.
     */
    @GetMapping(path = "accounts.list")
    public SearchResponse<AccountSearchResult> search(@Valid AccountSearchCriteria criteria,
                                                      PageParameters pageParameters,
                                                      @RequestHeader(name = "Is-Report", required = false)
                                                          boolean isReport) {
        log.info("Incoming request");
        AccountFilter filter = new AccountFilterMapper().map(criteria);
        Pageable pageable = new AccountSearchPageableMapper().get(pageParameters);
        Page<AccountProjection> page = accountService.search(filter, pageable, isReport);
        SearchResponse<AccountSearchResult> response = new SearchResponse<>();
        response.setItems(page.getContent().stream().map(AccountSearchResult::of).collect(
            Collectors.toList()));
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);

        return response;
    }

    /**
     * Returns information about the filters of account search.
     * It returns the descriptor which contains the available account type options and the searchByUrid
     * flag which indicates if the user can search by authorized representative urid filter.
     *
     * @return The {@link AccountFiltersDescriptor}
     */
    @GetMapping(path = "accounts.list.filters")
    public AccountFiltersDescriptor getAccountFiltersDescriptor() {
        boolean currentUserIsAdmin = authService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN);
        boolean isAuthorityUser = authService.hasClientRole(UserRole.AUTHORITY_USER);

        return AccountFiltersDescriptor.build(currentUserIsAdmin, isAuthorityUser);
    }

    /**
     * Returns tha account information for the account with the given business identifier.
     *
     * @param accountId the account business identifier.
     * @return an account
     */
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @GetMapping(path = "accounts.get")
    public AccountDTO getAccount(@RequestParam @RuleInput(RuleInputType.ACCOUNT_ID)
                                     @MDCParam(ACCOUNT_ID) Long accountId) {
        return accountService.getAccountDTO(accountId);
    }

    /**
     * Returns the Operator (Installation or Aircraft) account information for the given business identifier
     *
     * @param accountId the account business identifier
     * @return The Operator information
     */
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @GetMapping(path = "accounts.get.operator")
    public OperatorDTO getAccountOperator(
        @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) Long accountId) {
        return accountService.getInstallationOrAircraftOperatorDTO(accountId);
    }

    /**
     * Generates a task in order to update the Operator (Installation or Aircraft) account information.
     *
     * @param accountIdentifier The account identifier
     * @param details
     * @return
     */
    @Protected({
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class,
        FirstYearOfVerifiedEmissionsCheckAllocationRule.class,
        PendingCompliantEntityUpdateRule.class
    })
    @PostMapping(path = "accounts-operator.update-details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateAccountOperatorDetails(
        @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) @RequestParam Long accountIdentifier,
        @RuleInput(RuleInputType.ACCOUNT_OPERATOR_UPDATE) @Valid @RequestBody
            AccountOperatorDetailsUpdateDTO details) {

        Long taskIdentifier =
            accountOperatorUpdateService.submitAccountOperatorUpdateRequest(accountIdentifier, details);
        return new ResponseEntity<>(taskIdentifier, HttpStatus.OK);
    }

    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @GetMapping(path = "accounts.get.statuses")
    public List<AccountStatusActionOptionDTO> getAccountStatusAvailableActions(
        @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) Long accountId) {
        return accountService.getAccountStatusAvailableActions(accountId);
    }

    /**
     * Updates the status of the account and records an event with a reason.
     *
     * @param accountId the account identifier
     * @param patch     the AccountStatusChangeDTO patch
     * @return the new Account Status
     */
    @Protected({
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    @PatchMapping(path = "accounts.update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public AccountStatus patchAccountStatus(@RuleInput(RuleInputType.ACCOUNT_ID)
                                            @RequestParam @MDCParam(ACCOUNT_ID) Long accountId,
                                            @Valid @RequestBody AccountStatusChangeDTO patch) {

        return accountService.updateAccountStatus(accountId, patch);
    }

    /**
     * Returns the account holdings information for the account with the given business identifier.
     *
     * @param identifier the account business identifier.
     * @return an account
     */
    @GetMapping(path = "accounts.get.holdings")
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public AccountHoldingsSummaryResultDTO getAccountHoldings(
        @NotNull @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID)
        @MDCParam(ACCOUNT_ID) Long identifier) {

        return accountService.getAccountHoldings(identifier);
    }


    /**
     * Returns true/false if aircraft monitoringPlanId exists and account is not closed.
     *
     * @param monitoringPlanId The aircraft monitoring plan Identifier
     * @deprecated used only for validating installations during account opening. move all validations about operators
     * to a single endpoint accounts.validate.operator.
     */

    @Deprecated(forRemoval = true)
    @GetMapping(path = "accounts.get.aircraft-monitoring-plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean aircraftMonitoringPlanIdExists(@RequestParam String monitoringPlanId) {

        if (accountService.aircraftMonitoringPlanIdExists(monitoringPlanId)) {
            String error = "An account with the same aircraft monitoring plan ID already exists. " +
                "A second account is not permitted.";
            throw AccountActionException.create(AccountActionError.builder()
                .code(AccountActionError.MULTIPLE_MONITORING_PLAN_IDS_NOT_ALLOWED)
                .message(error)
                .build());
        }

        return false;
    }

    /**
     * Returns true/false if maritime monitoringPlanId exists and account is not closed.
     *
     * @param monitoringPlanId The maritiem monitoring plan Identifier
     * @deprecated used only for validating installations during account opening. move all validations about operators
     * to a single endpoint accounts.validate.operator.
     */

    @Deprecated(forRemoval = true)
    @GetMapping(path = "accounts.get.maritime-monitoring-plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean maritimeMonitoringPlanIdExists(@RequestParam String monitoringPlanId) {

        if (accountService.maritimeMonitoringPlanIdExists(monitoringPlanId)) {
            String error = "An account with the same maritime monitoring plan ID already exists. " +
                "A second account is not permitted.";
            throw AccountActionException.create(AccountActionError.builder()
                .code(AccountActionError.MULTIPLE_MONITORING_PLAN_IDS_NOT_ALLOWED)
                .message(error)
                .build());
        }

        return false;
    }

    @GetMapping(path = "accounts.get.maritime-imo", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean maritimeImoExists(@RequestParam String imo) {

        if (accountService.maritimeImoExists(imo)) {
            String error = "An account with the same IMO (Company IMO number) already exists. " +
                " A second account is not permitted.";
            throw AccountActionException.create(AccountActionError.builder()
                .code(AccountActionError.MULTIPLE_MARITIME_IMOS_NOT_ALLOWED)
                .message(error)
                .build());
        }

        return false;
    }

    @GetMapping(path = "accounts.get.emitter-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean emitterIdExists(@RequestParam String emitterId,@RequestParam(required = false) Long operatorIdentifier) {
        if(operatorIdentifier == null){
            return accountService.emitterIdExists(emitterId);
        } else{
            return accountService.isExistingEmitterId(emitterId,operatorIdentifier);
        }
    }

    /**
     * Returns true/false if installationPermitId exists and account is not closed.
     *
     * @param installationPermitId The installation permit Identifier
     * @deprecated used only for validating installations during account opening. move all validations about operators
     * to a single endpoint accounts.validate.operator.
     */
    @Deprecated(forRemoval = true)
    @GetMapping(path = "accounts.get.installation-permit-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean installationPermitIdExists(@RequestParam String installationPermitId) {
        accountService.validatePermitId(installationPermitId);
        return false;
    }

    /**
     * Retrieves a list of Operators (Installations) for the given installation identifier
     *
     * @param installationId The installation id to search for
     * @return a list of installations
     */
    @GetMapping(path = "accounts.get.candidate-installation-transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<InstallationSearchResultDTO>> getInstallations(
            @RequestParam @MDCParam(COMPLIANT_ENTITY_ID) String installationId, @RequestParam(required = false) @MDCParam(ACCOUNT_HOLDER_ID) Long excludeAccountHolderIdentifier) {
        List<InstallationSearchResultDTO> installations = accountService.findInstallationById(installationId, Optional.ofNullable(excludeAccountHolderIdentifier));
        ResponseEntity<List<InstallationSearchResultDTO>> result = new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!installations.isEmpty()) {
            result = new ResponseEntity<>(installations, HttpStatus.OK);
        }

        return result;
    }

    /**
     * Used during account opening to validate new  operator requests(installation transfer for now)
     * Should also
     * Returns an error only when the operator is not valid
     *
     * @param operator the
     */
    @PostMapping(path = "account.validate.installation-transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    public OperatorDTO validateOperator(@RequestBody @MDCParam(DTO) OperatorDTO operator) {
        accountService.validateOperator(operator);
        return accountService.getInstallationByIdentifier(operator.getIdentifier());
    }

    /**
     * Returns the holding details according to the passed criteria.
     *
     * @param criteria The {@link AccountHoldingDetailsCriteria} criteria parameters
     * @return The {@link AccountHoldingDetailsDTO} holding details
     */
    @GetMapping(path = "accounts.get.holding-details")
    public AccountHoldingDetailsDTO getAccountHoldingDetails(@Valid AccountHoldingDetailsCriteria criteria) {
        UnitBlockFilter filter = UnitBlockFilter.
            builder()
            .accountIdentifier(criteria.getAccountId())
            .unitType(SearchFiltersUtils.mapToEnum(UnitType.values(), criteria.getUnit()))
            .applicablePeriod(CommitmentPeriod.findByCode(criteria.getApplicablePeriodCode()))
            .originalPeriod(CommitmentPeriod.findByCode(criteria.getOriginalPeriodCode()))
            .subjectToSoap(criteria.getSubjectToSop())
            .build();

        List<UnitBlock> blocks = accountService.getUnitBlocks(filter);

        AccountHoldingDetailsDTO accountHoldingDetailsDTO = AccountHoldingDetailsDTO.create(filter, blocks);

        if (!authService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)) {
            //Non Kyoto units(i.e. allowances)
            if (!SearchFiltersUtils.mapToEnum(UnitType.values(), criteria.getUnit()).isKyoto()) {
                accountHoldingDetailsDTO.getResults().forEach(b -> {
                    b.setSerialNumberStart(null);
                    b.setSerialNumberEnd(null);
                });                
            } else { //Kyoto unit case
                List<UnitBlockDTO> groupedBlocks =
                        groupUnitBlocksByProjectActivityAndReservation(accountHoldingDetailsDTO.getResults());
                accountHoldingDetailsDTO.setResults(groupedBlocks);
            }
        }
        accountHoldingDetailsDTO.setResults(
                accountHoldingDetailsDTO
                             .getResults()
                             .stream()
                             .filter(Objects::nonNull)
                             .sorted(AccountService.accountHoldingDetailsComparator())
                             .toList()
        );
        return accountHoldingDetailsDTO;
    }

    /**
     * Groups unit blocks per project,activity and reservation as specified in UKETS-6904.
     * @param blocks the list of blocks to group
     * @return the list of grouped unit blocks
     */
    protected List<UnitBlockDTO> groupUnitBlocksByProjectActivityAndReservation(List<UnitBlockDTO> blocks) {
        
        List<UnitBlockDTO> groupedBlocks = new ArrayList<>();
        Map<Optional<String>,Map<Optional<String>,Map<Boolean,Long>>> results = blocks
            .stream()
            .collect(
                    Collectors.groupingBy(p -> Optional.ofNullable(p.getProject()),
                    Collectors.groupingBy(a -> Optional.ofNullable(a.getActivity()),
                    Collectors.groupingBy(
                           UnitBlockDTO::isReserved,Collectors.collectingAndThen(
                              Collectors.toList(), list -> list.stream().mapToLong(UnitBlockDTO::getQuantity).sum())
                   ))));
        
        results.forEach((project,unitsPerProject) -> {
            unitsPerProject.forEach((activity,unitsPerActivity) -> {
                unitsPerActivity.forEach((reserved, quantity) -> {
                    UnitBlockDTO block = UnitBlockDTO
                        .builder()
                        .project(project.orElse(null))
                        .activity(activity.orElse(null))
                        .reserved(reserved)
                        .quantity(quantity)
                        .build();
                    groupedBlocks.add(block);
                });
            });
        });    
        
        return groupedBlocks;
    }
    
    /**
     * Returns the account event history for the specific account.
     *
     * @param identifier The account identifier
     * @return A list of {@link AuditEventDTO} events
     */
    @GetMapping(path = "accounts.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class
    })
    public List<AuditEventDTO> getAccountHistory(
        @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(RequestParamType.ACCOUNT_ID) Long identifier) {
        return accountService.getAccountHistory(identifier);
    }

    /**
     * Returns the transactions related to the queried account
     *
     * @param accountFullIdentifier The full identifier of the account
     * @param pageParameters        The {@link PageParameters} paging info
     * @return The {@link SearchResponse<TransactionSearchResult>} response.
     */
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class
    })
    @GetMapping(path = "accounts.get.transactions")
    public SearchResponse<TransactionSearchResult> getAccountTransactions(
        @Valid @RuleInput(RuleInputType.ACCOUNT_FULL_ID) @MDCParam(ACCOUNT_FULL_ID) String accountFullIdentifier,
        PageParameters pageParameters) {
        SearchResponse<TransactionSearchResult> response = new SearchResponse<>();
        PageableMapper pageableMapper =
            new PageableMapper(TransactionSortFieldParam.values(), TransactionSortFieldParam.TRANSACTION_IDENTIFIER);
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<TransactionProjection> page = accountService.getAccountTransactions(accountFullIdentifier, pageable);
        response.setItems(page.getContent().stream().map(resultMapper::map).collect(Collectors.toList()));
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);
        return response;
    }
    
    /**
     * Generates the account transaction report.
     * 
     * @param accountFullIdentifier the business identifier of the account
     * @return the generated report identifier
     */
    @Protected({
        ARsCanViewRequestsOnlyForAccountsWithAccess.class,
        ARsCanViewAccountWhenUserStatusIsEnrolled.class,
        ARsCanViewAccountWhenAccountHasSpecificStatus.class,
        ARsCanViewAccountWhenAccountAccessIsNotSuspended.class
    })
    @GetMapping(path = "/accounts.get.transactions.report")
    public ResponseEntity<Long> requestTransactionDetailsReport(
        @Valid @RuleInput(RuleInputType.ACCOUNT_FULL_ID) @MDCParam(ACCOUNT_FULL_ID) String accountFullIdentifier) {
      
        Long reportId = accountService.requestAccountTransactionDetailsReport(accountFullIdentifier);
            
        return new ResponseEntity<>(reportId, HttpStatus.OK);            
    }  
    
    /**
     * Excludes the account from the billing process.
     * 
     * @param the account identifier
     * 
     */
    @Protected({SeniorAdminRule.class})
    @PostMapping(path = "/accounts.exclude.from.billing", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void excludeAccountFromBillingProcess(
    		@NotNull @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) Long identifier, @RequestBody AccountExclusionFromBillingRequestDTO request) {
        accountService.markAccountExcludedFromBilling(identifier, true, request.getExclusionRemarks());     
    }
    
    /**
     * Includes the account in the billing process.
     * 
     * @param the account identifier
     * 
     */
    @Protected({SeniorAdminRule.class})
    @PostMapping(path = "/accounts.include.in.billing")
    public void includeAccountInBillingProcess(
    		@NotNull @RequestParam @RuleInput(RuleInputType.ACCOUNT_ID) @MDCParam(ACCOUNT_ID) Long identifier) {
        accountService.markAccountExcludedFromBilling(identifier, false, null);
    }
}
