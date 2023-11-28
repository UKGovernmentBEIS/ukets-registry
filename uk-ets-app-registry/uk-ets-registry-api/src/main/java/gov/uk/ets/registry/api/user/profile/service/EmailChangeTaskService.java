package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.EmailChangeTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link TaskTypeService} implementation for tasks of type of email change request.
 */
@Service
@RequiredArgsConstructor
public class EmailChangeTaskService implements TaskTypeService<EmailChangeTaskDetailsDTO> {
    private final UserAdministrationService userAdministrationService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailChangeChecker emailChangeChecker;
    private final TaskRepository taskRepository;
    private final Mapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.REQUESTED_EMAIL_CHANGE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmailChangeTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {

        EmailChangeTaskDetailsDTO dto = new EmailChangeTaskDetailsDTO(taskDetailsDTO);
        EmailChangeDTO emailChangeRequest;
        try {
            emailChangeRequest = mapper.convertToPojo(dto.getDifference(),
                EmailChangeDTO.class);            
        } catch (Exception e) {
            //Old cases where the initiator is also the user we are trying to change email.
            //Just in case there is a pending email change in progress during deployment
            UserDTO userToChangeEmail = userService.getUser(taskDetailsDTO.getInitiatorUrid());
            String initiatorKeycloakId = userToChangeEmail.getKeycloakId();
            UserRepresentation userRepresentation = userAdministrationService.findByIamId(initiatorKeycloakId);
            emailChangeRequest = EmailChangeDTO
                .builder()
                .oldEmail(userRepresentation.getEmail())
                .newEmail(dto.getDifference())
                .requesterUrid(taskDetailsDTO.getInitiatorUrid())
                .urid(taskDetailsDTO.getInitiatorUrid())
                .build();
        }
        
        dto.setUserCurrentEmail(emailChangeRequest.getOldEmail());
        dto.setUserNewEmail(emailChangeRequest.getNewEmail());
        dto.setUserUrid(emailChangeRequest.getUrid());
        Optional<UserRepresentation> userRepresentationOpt = 
            userAdministrationService.findByEmail(emailChangeRequest.getOldEmail());
        if (userRepresentationOpt.isPresent()) {
            UserRepresentation userRepresentation = userRepresentationOpt.get();
            dto.setUserFirstName(userRepresentation.getFirstName());
            dto.setUserLastName(userRepresentation.getLastName());                      
        }
        
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @EmitsGroupNotifications(GroupNotificationType.EMAIL_CHANGE_TASK_COMPLETED)
    @Protected(
        {
            FourEyesPrincipleRule.class,
            OnlyRegistryAdminCanCompleteTaskRule.class,
        }
    )
    @Transactional
    @Override
    public TaskCompleteResponse complete(EmailChangeTaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        if (taskOutcome == TaskOutcome.APPROVED) {
            if (emailChangeChecker.otherUserHasNewEmailAsWorkingEmail(taskDTO.getUserNewEmail())) {
                throw new IllegalArgumentException("The email change aborted because the new email is used.");
            }
            userAdministrationService.updateUserEmail(taskDTO.getUserCurrentEmail(), taskDTO.getUserNewEmail());
            User user = userRepository.findByUrid(taskDTO.getUserUrid());
            if (user == null) {
               throw new IllegalArgumentException("User not found, aborting task completion.");
            }

            // update the user email in registry as well
            user.setEmail(taskDTO.getUserNewEmail());
            userRepository.save(user);

            Task task = taskRepository.findByRequestId(taskDTO.getRequestId());
            task.setDifference(mapper.convertToJson(EmailChangeDTO.builder()
                    .newEmail(taskDTO.getUserNewEmail())
                    .oldEmail(taskDTO.getUserCurrentEmail())
                    .build()));
            taskRepository.save(task);
        }
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
        OnlySeniorOrJuniorCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {

    }

    @Protected({
        OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
    })
    @Override
    public void checkForInvalidAssignPermissions() {

    }
}
