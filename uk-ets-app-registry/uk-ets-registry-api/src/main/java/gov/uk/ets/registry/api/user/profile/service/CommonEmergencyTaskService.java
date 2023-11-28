package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.User;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommonEmergencyTaskService {

    private final TaskRepository taskRepository;

    private final TaskEventService taskEventService;

    private final Mapper mapper;

    public TokenTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        TokenTaskDetailsDTO result = new TokenTaskDetailsDTO(taskDetailsDTO);
        TokenTaskDetailsDTO temp = mapper.convertToPojo(taskDetailsDTO.getDifference(), TokenTaskDetailsDTO.class);
        result.setEmail(temp.getEmail());
        result.setComment(temp.getComment());
        result.setFirstName(temp.getFirstName());
        result.setLastName(temp.getLastName());
        return result;
    }

    @Transactional
    public String proposeRequest(User user, String email, RequestType requestType) {

        if (!taskRepository.findPendingTasksByTypeAndUser(requestType, user.getUrid()).isEmpty()) {
            throw UserActionException.create(UserActionError.LOST_KEY_TASK_PENDING);
        }

        TokenTaskDetailsDTO details = new TokenTaskDetailsDTO();
        details.setEmail(email);
        details.setFirstName(user.getFirstName());
        details.setLastName(user.getLastName());
        details.setReferredUserURID(user.getUrid());

        Task task = new Task();
        task.setInitiatedBy(user);
        task.setRequestId(taskRepository.getNextRequestId());
        task.setType(requestType);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setDifference(mapper.convertToJson(details));
        task.setInitiatedDate(new Date());
        task.setUser(user);
        taskRepository.save(task);

        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, user.getUrid());

        return task.getRequestId().toString();
    }

    UserStatusChangeDTO createUnsuspendedUserChange(User user) {
        UserStatusChangeDTO statusChange = new UserStatusChangeDTO();
        statusChange.setUrid(user.getUrid());
        statusChange.setUserStatus(user.getPreviousState());
        return statusChange;
    }
}
