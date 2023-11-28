package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountHolderUpdateService {

    private final UserService userService;
    private final AccountService accountService;
    private final PersistenceService persistenceService;
    private final TaskEventService taskEventService;
    private final Mapper mapper;
    /**
     * Creates a task for the Account holder details update details action.
     *
     * @param dto               The account holder update action
     * @param accountIdentifier The account identifier
     * @return The task request id
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long submitAccountHolderDetailsUpdateRequest(AccountHolderDetailsUpdateDTO dto, Long accountIdentifier) {
        Task task =  generateTask(dto, accountIdentifier);
        return task.getRequestId();
    }

    /**
     * Creates a task for the Account holder contact details update details action.
     *
     * @param dto               The account holder update action
     * @param accountIdentifier The account identifier
     * @return The task request id
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long submitAccountHolderContactUpdateRequest(AccountHolderContactUpdateDTO dto, Long accountIdentifier) {
        Task task = generateTask(dto, accountIdentifier);
        return task.getRequestId();
    }

    /**
     * Generates the {@link Task}
     * @param dto               The account holder update action
     * @param accountIdentifier The account identifier
     * @return                  The generated {@link Task}
     */
    private Task generateTask(Object dto, Long accountIdentifier) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));

        if (dto instanceof AccountHolderDetailsUpdateDTO) {
            AccountHolderDetailsUpdateDTO result = (AccountHolderDetailsUpdateDTO) dto;
            task.setDifference(mapper.convertToJson(result.getAccountHolderDiff()));
            task.setBefore(mapper.convertToJson(result.getCurrentAccountHolder()));
            task.setType(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS);
        } else {
            AccountHolderContactUpdateDTO result = (AccountHolderContactUpdateDTO) dto;
            task.setDifference(mapper.convertToJson(result.getAccountHolderDiff()));
            task.setBefore(mapper.convertToJson(result.getCurrentAccountHolder()));
            task.setType(result.getUpdateType());
        }

        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setAccount(accountService.getAccount(accountIdentifier));

        persistenceService.save(task);

        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        return task;
    }
}
