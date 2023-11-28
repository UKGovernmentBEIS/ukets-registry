package gov.uk.ets.registry.api.user.service;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.SeniorAdminCanClaimTaskInitiatedByAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ClaimantCanCompleteTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CommentMandatoryWhenRejected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlySeniorRegistryAdminCanApproveTask;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RequestCannotBeApprovedByAffectedUser;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDeactivationTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.UserDeactivationDTO;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
public class UserDeactivationTaskService implements TaskTypeService<UserDeactivationTaskDetailsDTO> {

    private final UserService userService;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final EventService eventService;
    private final AccountAccessRepository accountAccessRepository;
    private final Mapper mapper;
    public static final String ACCOUNT_AUTHORISED_REPRESENTATIVE_REMOVED =
        "Authorised representative removed due to user deactivation";
    private static final List<String> WARNING_MESSAGES = List.of("User is associated with one or more accounts.",
        "When you submit this request, the user will no longer have access to the Registry.");

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.USER_DEACTIVATION_REQUEST);
    }

    @Override
    public UserDeactivationTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        UserDeactivationTaskDetailsDTO dto = new UserDeactivationTaskDetailsDTO(taskDetailsDTO);
        UserDeactivationDTO userDeactivationDTO =
                mapper.convertToPojo(dto.getDifference(), UserDeactivationDTO.class);
        List<AuthorisedRepresentativeDTO> ars =
                authorizedRepresentativeService.getARsByUser(dto.getReferredUserURID());
        if(ars != null && ars.size() > 0) {
            userDeactivationDTO.setWarningMessages(WARNING_MESSAGES);
        }
        dto.setChanged(userDeactivationDTO);
        return dto;
    }

    @Protected({
        FourEyesPrincipleRule.class,
        ClaimantCanCompleteTaskRule.class,
        CommentMandatoryWhenRejected.class,
        OnlySeniorRegistryAdminCanApproveTask.class,
        RequestCannotBeApprovedByAffectedUser.class

    })
    @Transactional
    @Override
    @EmitsGroupNotifications({GroupNotificationType.USER_DEACTIVATION_COMPLETED})
    public TaskCompleteResponse complete(UserDeactivationTaskDetailsDTO taskDTO, TaskOutcome taskOutcome,
                                         String comment) {
        String urid = taskDTO.getReferredUserURID();
        if (TaskOutcome.REJECTED.equals(taskOutcome)) {
            userService.revertDeactivationPending(urid, taskDTO.getClaimantURID());
        } else {
            userService.deactivateUser(urid, taskDTO.getClaimantURID());
            //remove AR from all accounts
            List<AuthorisedRepresentativeDTO> ars = authorizedRepresentativeService.getARsByUser(urid);
            ars.forEach(ar -> removeArFromAccount(urid, ar, taskDTO.getRequestId()));
        }
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
        SeniorAdminCanClaimTaskInitiatedByAdminRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    private void removeArFromAccount(String urid, AuthorisedRepresentativeDTO ar, Long requestId) {
        AccountAccess access = accountAccessRepository
            .findArsByAccount_IdentifierAndUser_Urid(ar.getAccountIdentifier(), urid);
        if (access.getState() == AccountAccessState.REMOVED) {
            return;
        }
        access.setState(AccountAccessState.REMOVED);
        accountAccessRepository.save(access);
        authorizedRepresentativeService.removeKeycloakRoleIfNoOtherAccountAccess(
            urid, access.getUser().getIamIdentifier());

        String description = String.format("Task requestId %s", requestId);
        eventService.createAndPublishEvent(ar.getAccountIdentifier().toString(), urid, description,
            EventType.ACCOUNT_AR_REMOVED, ACCOUNT_AUTHORISED_REPRESENTATIVE_REMOVED);
    }
}
