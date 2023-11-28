package gov.uk.ets.registry.api.user.admin;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.*;
import gov.uk.ets.registry.api.authz.ruleengine.features.user.profile.rules.UserCannotBeRestoredManuallyWhenSuspendedByTheSystem;
import gov.uk.ets.registry.api.common.UserDetailsUtil;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.UserDeactivationDTO;
import gov.uk.ets.registry.api.user.admin.service.AuthorityAdministrationService;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.shared.EnrolledUserDTO;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchCriteria;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchPagedResults;
import gov.uk.ets.registry.api.user.admin.shared.UserDetailsUpdateType;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusActionOptionDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Controller for user administration.
 */
@RestController
@RequestMapping(path = "/api-registry/admin", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class UserAdministrationController {

    /**
     * Service for users administration.
     */
    private final UserAdministrationService userAdministrationService;

    /**
     * Service for user state.
     */
    private final UserStatusService userStateService;

    /**
     * The authority administration service.
     */
    private final AuthorityAdministrationService authorityAdministrationService;
    
    /**
     * Service for users.
     */
    private final UserService userService;

    /**
     * Retrieves a user.
     *
     * @param urid The Keycloak (a.ka. iam) identifier.
     * @return a user
     */
    @GetMapping(path = "users.get", produces = MediaType.APPLICATION_JSON_VALUE)
    @Protected(AdminsOrSameUserCanRequestUserDetailsRule.class)
    public ResponseEntity<KeycloakUser> getUser(@RequestParam @RuleInput(RuleInputType.URID) String urid) {
        KeycloakUser keycloakUser = userStateService.getKeycloakUser(urid);
        return keycloakUser != null && keycloakUser.getEmail() != null ?
            new ResponseEntity<>(keycloakUser, HttpStatus.OK) :
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Protected(AnyAdminRule.class)
    @GetMapping(path = "users.list", produces = MediaType.APPLICATION_JSON_VALUE)
    public KeycloakUserSearchPagedResults getUsers(@Valid KeycloakUserSearchCriteria criteria) {
        return userAdministrationService.search(criteria);
    }

    @GetMapping(path = "users.get.statuses")
    public List<UserStatusActionOptionDTO> getUserStatusAvailableActions(@NotNull @RequestParam String urid) {
        return userStateService.getUserStatusAvailableActions(urid);
    }

    /**
     * Updates the status of the user and records an event with a reason.
     *
     * @param patch the input patch
     * @return the new User Status
     */
    @Protected({UserStatusEnrolledRule.class, SeniorAdminRule.class, UserCannotBeRestoredManuallyWhenSuspendedByTheSystem.class})
    @PatchMapping(path = "users.update.status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserStatusChangeResultDTO patchUserStatus(@RuleInput(RuleInputType.NEW_STATUS) @RequestBody @Valid UserStatusChangeDTO patch) {
        return userStateService.changeUserStatus(patch);
    }

    /**
     * Gets enrolled user by unique user business identifier.
     *
     * @param urid The unique user business identifier
     * @return The enrolled user
     */
    @Protected(SystemAdminRule.class)
    @GetMapping("users.get.enrolled")
    public EnrolledUserDTO getEnrolledUser(@RequestParam String urid) {
        return authorityAdministrationService.getEnrolledUser(urid);
    }

    /**
     * Sets user of unique business identifier as authority user.
     *
     * @param urid The unique business identifier.
     */
    @Protected(SystemAdminRule.class)
    @PostMapping(value = "users.add.authority")
    public void addAuthorityUser(@RequestParam String urid) {
        authorityAdministrationService.setAuthorityUser(urid);
    }

    /**
     * Removes user from authority users.
     *
     * @param urid The unique business identifier.
     */
    @Protected(SystemAdminRule.class)
    @PostMapping(value = "users.remove.authority")
    public void removeUserFromAuthorityUsers(@RequestParam String urid) {
        authorityAdministrationService.removeUserFromAuthorityUsers(urid);
    }
    
    
    /**
     * If at least one major field has been submitted for change to the user’s profile, then generate a task in order to update the User personal and work contact details. 
     * Otherwise, if only minor fields are changed, bypass the approval process and update the changes directly to the system.
     * 
     * @param urid
     * @param userDetails
     * @return the task identifier if at least one major field has been submitted for change, otherwise null.
     */
    @Protected(AdminsOrSameUserWithSpecificStatusCanUpdateUserDetailsRule.class)
    @PatchMapping(path = "users.update.details", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> updateUserDetails(@RequestParam @RuleInput(RuleInputType.URID) String urid, @RequestBody @Valid UserDetailsUpdateDTO userDetails) {
        boolean majorUserDetailsUpdateRequested = UserDetailsUtil.majorUserDetailsUpdateRequested(userDetails.getDiff());
		Long taskIdentifier = majorUserDetailsUpdateRequested ?
				  userService.submitMajorUserDetailsUpdateRequest(urid, userDetails)
				: userService.submitMinorUserDetailsUpdateRequest(urid, userDetails);

		return new ResponseEntity<>(taskIdentifier, HttpStatus.OK);
    }

	@Protected(AdminsOrSameUserWithSpecificStatusCanUpdateUserDetailsRule.class)
    @PostMapping(path = "users.deactivate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> deactivateUser(@RequestParam @RuleInput(RuleInputType.URID) String urid, @RequestBody @Valid UserDeactivationDTO dto) {

        Long taskIdentifier = userService.submitUserDeactivationRequest(urid, dto);
        return new ResponseEntity<>(taskIdentifier, HttpStatus.OK);
    }

  	@GetMapping(path = "users.validateUserUpdateRequest")
	public void validateUserUpdateRequest(@NotNull @RequestParam String urid, @NotNull @RequestParam UserDetailsUpdateType userDetailsUpdateType) {
		userService.validateUserUpdateRequest(urid, userDetailsUpdateType);
	}

}
