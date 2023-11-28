package gov.uk.ets.registry.api.ar;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.ar.service.dto.AuthorizedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.web.model.ReplaceARRequest;
import gov.uk.ets.registry.api.ar.web.model.UpdateARRequest;
import gov.uk.ets.registry.api.ar.web.model.UpdateARRightsRequest;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCanSubmitUpdateWhenUserStatusIsEnrolled;
import gov.uk.ets.registry.api.authz.ruleengine.features.ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsOrSameUserCanRequestUserDetailsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthoritiesWithAccountAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CannotSubmitRequestWhenAccountIsTransferPendingStatusRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ForbidUsersWithDeactivationPendingOrDeactivatedRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.OnlyNonAdminUsersCanBeAddedAsArRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount;
import gov.uk.ets.registry.api.authz.ruleengine.features.RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ExistingARsCannotBeAddedRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCanBeReplacedWhenHeHasTheProperStatus;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountAR;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountActiveAR;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARCandidateShouldBeAccountSuspendedAR;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.AuthorityCannotBeRequestedUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.OnlyNonAdminUserCanBeAddedAsArRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.UserCannotAddedWithStatusDeactivationPendingOrDeactivatedRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.ar.rules.AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static gov.uk.ets.commons.logging.RequestParamType.ACCOUNT_ID;
import static gov.uk.ets.commons.logging.RequestParamType.URID;

/**
 * RPC endpoint that handles requests related to authorized representatives.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class AuthorizedRepresentativeController {
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    /**
     * Gets the account's ARs with account access state equal with state parameter.
     * If state parameter is not defined then all the ARs of the account are returned.
     *
     * @param accountId The account identifier.
     * @param state     The {@link AccountAccessState} state.
     * @return The list of ARs.
     */
    @GetMapping("authorised-representatives.get.by-account")
    public List<AuthorizedRepresentativeDTO> getAuthorizedRepresentatives(@RequestParam @MDCParam(ACCOUNT_ID) Long accountId,
                                                                          @RequestParam(required = false)
                                                                              AccountAccessState state) {
        return authorizedRepresentativeService.getAuthorizedRepresentatives(accountId, Optional.ofNullable(state));
    }

    /**
     * Retrieves the Authorize Representative list of the specific user.
     *
     * @param urid The user identifier
     * @return The Authorize representative list
     */
    @GetMapping(path = "authorised-representatives.get.by-user/{urid}")
    @Protected(AdminsOrSameUserCanRequestUserDetailsRule.class)
    public ResponseEntity<List<AuthorisedRepresentativeDTO>> getARsByUser(
        @PathVariable @RuleInput(RuleInputType.URID) @MDCParam(URID) String urid) {
        return new ResponseEntity<>(authorizedRepresentativeService.getARsByUser(urid), HttpStatus.OK);
    }

    /**
     * Gets the authorised representative candidate.
     *
     * @param accountId The account identifier.
     * @param urid      The user identifier.
     * @return The {@link AuthorizedRepresentativeDTO} candidate user info.
     */
    @Protected({
        OnlyNonAdminUserCanBeAddedAsArRule.class,
        AdminsWithAccountAccessRule.class,
        AuthoritiesWithAccountAccessRule.class,
        AuthorityCannotBeRequestedUserRule.class,
        ExistingARsCannotBeAddedRule.class
    })
    @GetMapping(value = "authorised-representatives.get.candidate", params = {"accountId", "urid"})
    public AuthorizedRepresentativeDTO getCandidate(@RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam Long accountId,
                                                    @RuleInput(RuleInputType.AR_CANDIDATE_URID) @RequestParam
                                                    @MDCParam(URID) String urid) {
        return authorizedRepresentativeService.getCandidate(urid);
    }

    /**
     * Gets the authorised representative candidate.
     *
     * @param urid The user identifier.
     * @return The {@link AuthorizedRepresentativeDTO} candidate user info.
     */
    @Protected(
        {
            OnlyNonAdminUsersCanBeAddedAsArRule.class, 
            ForbidUsersWithDeactivationPendingOrDeactivatedRule.class, 
            AuthorityCannotBeRequestedUserRule.class
        }
    )
    @GetMapping(value = "authorised-representatives.get.candidate", params = "urid")
    public AuthorizedRepresentativeDTO getCandidate(@RuleInput(RuleInputType.URID) @RequestParam @MDCParam(URID) String urid) {
        return authorizedRepresentativeService.getCandidate(urid);
    }

    /**
     * Gets the possible ARs that can be added to the account AR list.
     *
     * @param accountId The account identifier.
     * @return The ARs.
     */
    @GetMapping("authorised-representatives.get.other-accounts-ars")
    public List<AuthorizedRepresentativeDTO> getOtherAccountsARs(@RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.getOtherAccountsARs(accountId);
    }


    /**
     * Adds a new authorized representative on the account,
     *
     * @param request   The request body.
     * @param accountId The account identifier request param.
     * @return The requestId of the produced {@link gov.uk.ets.registry.api.task.domain.Task} entity.
     */
    @Protected(
        {
            ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            UserCannotAddedWithStatusDeactivationPendingOrDeactivatedRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class,
            AuthorityCannotBeRequestedUserRule.class,
            AccountARsLimitShouldNotBeExceededIncludingPendingTasksRule.class,
            ExistingARsCannotBeAddedRule.class
        }
    )
    @PostMapping(path = "authorised-representatives.add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long addAuthorizedRepresentative(
        @RuleInput(RuleInputType.AR_CANDIDATE) @Valid @RequestBody UpdateARRightsRequest request,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.placeUpdateRequest(ARUpdateActionDTO.builder()
            .accountIdentifier(accountId)
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid(request.getCandidateUrid())
                .right(request.getAccessRight())
                .build())
            .updateType(ARUpdateActionType.ADD)
            .build());
    }

    /**
     * Removes an authorized representative from the account.
     *
     * @param request   The request body.
     * @param accountId The account identifier request param.
     * @return The requestId of the produced {@link gov.uk.ets.registry.api.task.domain.Task} entity.
     */
    @Protected(
        {
            ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARCandidateShouldBeAccountActiveAR.class,
            ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class
        }
    )
    @PostMapping(path = "authorised-representatives.remove", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long removeAuthorizedRepresentative(
        @RuleInput(RuleInputType.AR_CANDIDATE) @Valid @RequestBody UpdateARRequest request,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.placeUpdateRequest(ARUpdateActionDTO.builder()
            .accountIdentifier(accountId)
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid(request.getCandidateUrid())
                .build())
            .updateType(ARUpdateActionType.REMOVE)
            .build());
    }

    /**
     * Suspends an authorized representative.
     *
     * @param request   The request body.
     * @param accountId The account identifier request param.
     * @return The requestId of the produced {@link gov.uk.ets.registry.api.task.domain.Task} entity.
     */
    @Protected(
        {
            SeniorAdminRule.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARCandidateShouldBeAccountActiveAR.class,
            ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class,
        }
    )
    @PostMapping(path = "authorised-representatives.suspend", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long suspendAuthorizedRepresentative(
        @RuleInput(RuleInputType.AR_CANDIDATE) @Valid @RequestBody UpdateARRequest request,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.placeUpdateRequest(ARUpdateActionDTO.builder()
            .accountIdentifier(accountId)
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid(request.getCandidateUrid())
                .build())
            .updateType(ARUpdateActionType.SUSPEND)
            .comment(request.getComment())
            .build());
    }

    /**
     * Adds a new authorized representative
     *
     * @param request   The request body
     * @param accountId The account identifier request param
     * @return The requestId of the produced {@link gov.uk.ets.registry.api.task.domain.Task} entity
     */
    @Protected(
        {
            SeniorAdminRule.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARCandidateShouldBeAccountSuspendedAR.class,
            ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class,
        }
    )
    @PostMapping(path = "authorised-representatives.restore", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long restoreAuthorizedRepresentative(
        @RuleInput(RuleInputType.AR_CANDIDATE) @Valid @RequestBody UpdateARRequest request,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.placeUpdateRequest(ARUpdateActionDTO.builder()
            .accountIdentifier(accountId)
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid(request.getCandidateUrid())
                .build())
            .updateType(ARUpdateActionType.RESTORE)
            .comment(request.getComment())
            .build());
    }

    /**
     * Adds a new authorized representative
     *
     * @param request   The request body
     * @param accountId The account identifier request param
     * @return The requestId of the produced {@link gov.uk.ets.registry.api.task.domain.Task} entity
     */
    @Protected(
        {
            ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARCandidateShouldBeAccountAR.class,
            ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class,
        }
    )
    @PostMapping(path = "authorised-representatives.change.access-rights", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long changeARAccessRights(
        @RuleInput(RuleInputType.AR_CANDIDATE) @Valid @RequestBody UpdateARRightsRequest request,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.placeUpdateRequest(ARUpdateActionDTO.builder()
            .accountIdentifier(accountId)
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid(request.getCandidateUrid())
                .right(request.getAccessRight())
                .build())
            .updateType(ARUpdateActionType.CHANGE_ACCESS_RIGHTS)
            .build());
    }

    /**
     * Replaces an AR with an other user which is AR of an other Account.
     *
     * @param request   The request body.
     * @param accountId The account identifier request param.
     * @return The requestId of the produced {@link gov.uk.ets.registry.api.task.domain.Task} entity
     */
    @Protected(
        {
            ARsCannotSubmitRequestsForAccountsWithReadOnlyAccess.class,
            RegistryAdministratorsCanSubmitRequestsForAnyEndUserAccount.class,
            ARsCanSubmitUpdateWhenAccountAccessIsNotSuspended.class,
            ARsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARsCanSubmitUpdateWhenUserStatusIsEnrolled.class,
            RegistryAdministratorsCanSubmitUpdateWhenAccountHasSpecificStatus.class,
            ARCanBeReplacedWhenHeHasTheProperStatus.class,
            ARUpdateRequestAffectedUsersCannotBeInvolvedInOtherPendingRequest.class,
            CannotSubmitRequestWhenAccountIsTransferPendingStatusRule.class,
            UserCannotAddedWithStatusDeactivationPendingOrDeactivatedRule.class,
            AdminsWithAccountAccessRule.class,
            AuthoritiesWithAccountAccessRule.class,
        }
    )
    @PostMapping(path = "authorised-representatives.replace", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Long replaceAuthorizedRepresentative(
        @RuleInput(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR) @Valid @RequestBody ReplaceARRequest request,
        @RuleInput(RuleInputType.ACCOUNT_ID) @RequestParam @MDCParam(ACCOUNT_ID) Long accountId) {
        return authorizedRepresentativeService.placeUpdateRequest(ARUpdateActionDTO.builder()
            .accountIdentifier(accountId)
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid(request.getCandidateUrid())
                .right(request.getAccessRight())
                .build())
            .replacee(AuthorizedRepresentativeDTO.builder()
                .urid(request.getReplaceeUrid()).build())
            .updateType(ARUpdateActionType.REPLACE)
            .build());
    }
}
