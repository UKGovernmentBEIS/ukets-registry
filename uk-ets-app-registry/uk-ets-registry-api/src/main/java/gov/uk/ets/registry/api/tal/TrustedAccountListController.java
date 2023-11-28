package gov.uk.ets.registry.api.tal;


import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitRequestsOnlyForAccountsWithInitiateOrInitiateAndApproveAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitTalUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosed;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanViewAccountWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenCandidateAccountIsInPendingRequestRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanViewAnyAccount;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountFilter;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountListType;
import gov.uk.ets.registry.api.tal.service.TrustedAccountListService;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.tal.web.model.mappers.TrustedAccountFilterMapper;
import gov.uk.ets.registry.api.tal.web.model.search.TALProjection;
import gov.uk.ets.registry.api.tal.web.model.search.TALSearchCriteria;
import gov.uk.ets.registry.api.tal.web.model.search.TALSearchPageableMapper;
import gov.uk.ets.registry.api.tal.web.model.search.TALSearchResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;

/**
 * Handles requests for trusted account list management.
 */
@Tag(name = "Trusted Account List Management")
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class TrustedAccountListController {
    /**
     * The service for trusted accounts.
     */
    private final TrustedAccountListService trustedAccountListService;

    public TrustedAccountListController(
        TrustedAccountListService trustedAccountListService) {
        this.trustedAccountListService = trustedAccountListService;
    }

    /**
     * Retrieves the Trusted Account List of the specified account.
     *
     * @param accountId the account business identifier
     * @param type      the trusted account lis type.
     * @return the trusted account list.
     */
    @Protected(
        {
            ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
            RegistryAdministratorsCanViewAnyAccount.class,
            ARsCanViewAccountWhenAccountAccessIsNotSuspended.class,
            ARsCanViewAccountWhenAccountHasSpecificStatus.class,
            ARsCanViewAccountWhenUserStatusIsEnrolled.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class
        }
    )
    @GetMapping(path = "tal.get")
    @ResponseBody
    public ResponseEntity<Set<TrustedAccountDTO>> getTalByAccountAndType(
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId,
        @RequestParam(required = false) TrustedAccountListType type) {
        return new ResponseEntity<>(trustedAccountListService.getTrustedAccounts(accountId, type), HttpStatus.OK);
    }

    /**
     * Adds a trusted account to the trusted account list of the specified host account.
     *
     * @param trustedAccount the trusted account to be added
     * @param accountId      the business identifier of the host account.
     * @return the generated task business identifier.
     */
    @Protected(
        {
        ARsCanSubmitRequestsOnlyForAccountsWithInitiateOrInitiateAndApproveAccess.class,
        RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
        ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
        ARsCanSubmitTalUpdateWhenAccountHasSpecificStatus.class,
        ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
        RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
        CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
        ARsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosed.class,
        RegistryAdministratorsCanSubmitTalUpdateWhenCandidateAccountHasStatusClosedOrClosurePending.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class,
        CannotSubmitRequestWhenCandidateAccountAlreadyInTalRule.class,
        CannotSubmitRequestWhenCandidateAccountIsInPendingRequestRule.class
        }
    )
    @PostMapping(path = "tal.add", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Long> addTrustedAccount(
        @RuleInput(RuleInputType.TRUSTED_ACCOUNT_ID) @RequestBody @Valid TrustedAccountDTO trustedAccount,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return new ResponseEntity<>(trustedAccountListService.addTrustedAccount(trustedAccount, accountId),
            HttpStatus.OK);
    }

    /**
     * Removes trusted accounts from the trusted account list of the specified host account.
     *
     * @param trustedAccounts the trusted accounts to be removed
     * @param accountId       the business identifier of the host account.
     * @return the generated task business identifier.
     */
    @Protected(
        {
            ARsCanSubmitRequestsOnlyForAccountsWithInitiateOrInitiateAndApproveAccess.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitTalUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class
        }
    )
    @PostMapping(path = "tal.remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Long> removeTrustedAccounts(
        @RequestBody @NotEmpty List<TrustedAccountDTO> trustedAccounts,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return new ResponseEntity<>(trustedAccountListService.removeTrustedAccounts(trustedAccounts, accountId),
            HttpStatus.OK);
    }

    /**
     * Updates trusted account from a trusted account the specified host account.
     *
     * @param trustedAccountDTO the trusted account to be updated
     * @param accountId         the business identifier of the host account.
     * @return the updated {@link TrustedAccountDTO trusted account details}
     */
    @Protected(
        {
            ARsCanSubmitRequestsOnlyForAccountsWithInitiateAndApproveAccess.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitTalUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class
        }
    )
    @PostMapping(path = "tal.update", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<TrustedAccountDTO> updateTrustedAccountDescription(
        @RequestBody @NotNull @Valid TrustedAccountDTO trustedAccountDTO,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return new ResponseEntity<>(trustedAccountListService.updateTrustedAccount(trustedAccountDTO, accountId),
            HttpStatus.OK);
    }

    /**
     * Searches for trusted accounts according to criteria and returns the page of sorted results as
     * the pageParameters argument instructs
     *
     * @param criteria       The {@link TALSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} paging info
     * @return The {@link SearchResponse < TALSearchResult >} response.
     */
    @GetMapping(path = "tal.list")
    public SearchResponse<TALSearchResult> search(@Valid TALSearchCriteria criteria,
                                                  PageParameters pageParameters) {
        TrustedAccountFilter filter = new TrustedAccountFilterMapper().map(criteria);
        Pageable pageable = new TALSearchPageableMapper().get(pageParameters);
        Page<TALProjection> page = trustedAccountListService.search(filter, pageable);
        SearchResponse<TALSearchResult> response = new SearchResponse<>();
        response.setItems(page.getContent().stream().map(TALSearchResult::of).collect(
                Collectors.toList()));
        pageParameters.setTotalResults(page.getTotalElements());
        response.setPageParameters(pageParameters);
        return response;
    }

    @PostMapping("tal.cancel.pending")
    public ResponseEntity<?> cancelPending(@RequestParam Long accountIdentifier, @RequestParam String trustedAccountFullIdentifier) {
        trustedAccountListService.cancelTrustedAccount(accountIdentifier, trustedAccountFullIdentifier);
        return ResponseEntity.noContent().build();
    }
}
