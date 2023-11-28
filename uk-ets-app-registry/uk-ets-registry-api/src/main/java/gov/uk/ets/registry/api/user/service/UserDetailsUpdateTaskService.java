package gov.uk.ets.registry.api.user.service;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ClaimantCanCompleteTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CommentMandatoryWhenRejected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.RequestCannotBeApprovedByAffectedUser;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
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
public class UserDetailsUpdateTaskService implements TaskTypeService<UserDetailsUpdateTaskDetailsDTO> {
    
    private final UserService userService;
    private final UserStatusService userStateService;
    private final RequestedDocsTaskService requestedDocsTaskService;
	private final Mapper mapper;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.USER_DETAILS_UPDATE_REQUEST);
    }

    @Override
    public UserDetailsUpdateTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskDetailsDTO);
        dto.setCurrent(mapper.convertToPojo(dto.getBefore(), UserDetailsDTO.class));
        dto.setChanged(mapper.convertToPojo(dto.getDifference(), UserDetailsDTO.class));
        if (dto.getReferredUserURID() != null) {
            dto.setUserDetails(List.of(userStateService.getKeycloakUser(dto.getReferredUserURID())));
        }
        return dto;
    }


    @Protected({
        FourEyesPrincipleRule.class,
        ClaimantCanCompleteTaskRule.class,
        CommentMandatoryWhenRejected.class,
        OnlyRegistryAdminCanCompleteTaskRule.class,
        RequestCannotBeApprovedByAffectedUser.class
        
    })
    @Transactional
    @Override
    @EmitsGroupNotifications(GroupNotificationType.USER_DETAILS_UPDATE_COMPLETED)
    public TaskCompleteResponse complete(UserDetailsUpdateTaskDetailsDTO taskDTO, TaskOutcome taskOutcome,
            String comment) {
        if (TaskOutcome.APPROVED.equals(taskOutcome)) {
            userService.updateUserDetails(taskDTO);
        }
        requestedDocsTaskService.completeChildRequestedDocumentTasks(taskDTO.getRequestId(), taskOutcome);
        return defaultResponseBuilder(taskDTO).build();
    }


    @Protected({
            OnlySeniorOrJuniorCanClaimTaskRule.class
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
}
