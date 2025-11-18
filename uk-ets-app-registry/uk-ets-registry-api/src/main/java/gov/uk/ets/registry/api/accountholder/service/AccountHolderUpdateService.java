package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeAction;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeActionType;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDiffDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.FeatureDescriptor;
import java.util.Date;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class AccountHolderUpdateService {

    private final UserService userService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final PersistenceService persistenceService;
    private final TaskEventService taskEventService;
    private final Mapper mapper;
    private final AccountHolderService accountHolderService;
    private final EventService eventService;
    private final AccountHolderChangeValidationService accountHolderChangeValidationService;

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
        Task task = generateTask(dto, accountIdentifier);
        return task.getRequestId();
    }

    @Transactional
    public Long accountHolderChange(AccountHolderChangeDTO accountHolderChangeDTO) {
        accountHolderChangeValidationService
                .validateAccountHolderChangeRequestForAccountIdentifier(accountHolderChangeDTO.getAccountIdentifier(),
                        accountHolderChangeDTO.getAcquiringAccountHolder().getId(),
                        AccountHolderChangeActionType.ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER
                                .equals(accountHolderChangeDTO.getAccountHolderChangeActionType()),
                        accountHolderChangeDTO.getAccountHolderDelete());
        Task task = generateAccountHolderChangeTask(accountHolderChangeDTO);
        return task.getRequestId();
    }

    private Task generateAccountHolderChangeTask(AccountHolderChangeDTO accountHolderChangeDTO) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        Account account = retrieveAccountByIdentifier(accountHolderChangeDTO.getAccountIdentifier());
        task.setDifference(mapper.convertToJson(toAccountHolderChangeAction(
                accountHolderChangeDTO)));
        task.setType(RequestType.ACCOUNT_HOLDER_CHANGE);

        AccountHolderDTO currentAccountHolder = accountHolderService.getAccountHolder(account.getAccountHolder().getIdentifier());
        task.setBefore(mapper.convertToJson(currentAccountHolder));

        User currentUser = userService.getCurrentUser();
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());

        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        task.setAccount(account);

        persistenceService.save(account);
        persistenceService.save(task);

        publishAccountEvent(accountHolderChangeDTO, task, account, currentUser);

        return task;
    }

    private AccountHolderChangeAction toAccountHolderChangeAction(
            AccountHolderChangeDTO accountHolderChangeDTO) {
        AccountHolderChangeAction action = new AccountHolderChangeAction();
        action.setType(accountHolderChangeDTO.getAccountHolderChangeActionType());
        action.setAccountHolderDTO(accountHolderChangeDTO.getAcquiringAccountHolder());
        action.setAccountHolderContactInfo(accountHolderChangeDTO.getAcquiringAccountHolderContactInfo());
        action.setAccountHolderDelete(accountHolderChangeDTO.getAccountHolderDelete());
        return action;
    }

    private void publishAccountEvent(AccountHolderChangeDTO dto, Task task, Account account, User currentUser) {
        String oldHolderName = account.getAccountHolder().actualName();
        String newHolderName = dto.getAcquiringAccountHolder().actualName();
        String comment = String.format("From ‘%s’ to ‘%s’", oldHolderName, newHolderName);
        String what = "Request to change account holder to another account holder.";

        eventService.createAndPublishEvent(task.getAccount().getIdentifier().toString(), currentUser.getUrid(), comment,
                EventType.ACCOUNT_TASK_REQUESTED, what);
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
        // run an update operation
        // update
        Task task = generateTask(dto, accountIdentifier);
        return task.getRequestId();
    }

    /**
     * Generates the {@link Task}
     *
     * @param dto               The account holder update action
     * @param accountIdentifier The account identifier
     * @return The generated {@link Task}
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

    private static void copyAccountHolderDetailsProperties(AccountHolderDetailsUpdateDiffDTO src, AccountHolderDTO target) {
        if (src.getAddress() != null) {
            BeanUtils.copyProperties(src.getAddress(), target.getAddress(), getNullPropertyNames(src.getAddress()));
        }
        if (src.getEmailAddress() != null) {
            BeanUtils.copyProperties(src.getEmailAddress(), target.getEmailAddress(), getNullPropertyNames(src.getEmailAddress()));
        }
        if (src.getPhoneNumber() != null) {
            BeanUtils.copyProperties(src.getPhoneNumber(), target.getPhoneNumber(), getNullPropertyNames(src.getPhoneNumber()));
        }
        if (src.getDetails() != null) {
            BeanUtils.copyProperties(src.getDetails(), target.getDetails(), getNullPropertyNames(src.getDetails()));
        }
    }

    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }

    private Account retrieveAccountByIdentifier(Long accountIdentifier) {
        return accountRepository.findByIdentifier(accountIdentifier)
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("You cannot change the AccountHolder - Missing account.")));
    }
}
