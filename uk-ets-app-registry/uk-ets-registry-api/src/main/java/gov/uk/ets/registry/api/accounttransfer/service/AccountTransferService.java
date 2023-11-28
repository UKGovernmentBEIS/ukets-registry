package gov.uk.ets.registry.api.accounttransfer.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.PermitDTO;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferAction;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferRequestDTO;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AccountTransferService {

    private final UserService userService;
    private final AccountService accountService;
    private final PersistenceService persistenceService;
    private final EventService eventService;
    private final TaskEventService taskEventService;
    private final Mapper mapper;
    private final ConversionService conversionService;
    

    @Transactional
    public Long accountTransfer(AccountTransferRequestDTO request) {
        Task task = generateAccountTransferTask(request);
        return task.getRequestId();
    }


    private Task generateAccountTransferTask(AccountTransferRequestDTO dto) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        Account account = accountService.getAccount(dto.getAccountIdentifier());
        task.setDifference(mapper.convertToJson(toAccountTransferAction(
                dto, account.getAccountStatus(), account.getCompliantEntity())));
        task.setType(RequestType.ACCOUNT_TRANSFER);


        account.setAccountStatus(AccountStatus.TRANSFER_PENDING);

        User currentUser = userService.getCurrentUser();
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        // emit here so that account event is not emitted (we emit it manually later)
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        task.setAccount(account);

        persistenceService.save(account);
        persistenceService.save(task);

        publishAccountEvent(dto, task, account, currentUser);

        return task;
    }

    private AccountTransferAction toAccountTransferAction(
            AccountTransferRequestDTO request, AccountStatus status, CompliantEntity compliantEntity) {
        AccountTransferAction action = new AccountTransferAction();
        action.setType(request.getAccountTransferType());
        action.setAccountHolderDTO(request.getAcquiringAccountHolder());
        action.setAccountHolderContactInfo(request.getAcquiringAccountHolderContactInfo());
        action.setPreviousAccountStatus(status);
        action.setInstallationDetails(toDto((Installation) compliantEntity));
        return action;
    }

    private void publishAccountEvent(AccountTransferRequestDTO dto, Task task, Account account, User currentUser) {
        String oldHolderName = account.getAccountHolder().actualName();
        String newHolderName = dto.getAcquiringAccountHolder().actualName();
        String comment = String.format("From ‘%s’ to ‘%s’", oldHolderName, newHolderName);
        String what = "Request to transfer account to another AH";

        eventService.createAndPublishEvent(task.getAccount().getIdentifier().toString(), currentUser.getUrid(), comment,
            EventType.ACCOUNT_TASK_REQUESTED, what);
    }
    
    InstallationOrAircraftOperatorDTO toDto(Installation entity) {
        InstallationOrAircraftOperatorDTO details = new InstallationOrAircraftOperatorDTO();
        details.setIdentifier(entity.getIdentifier());
        details.setType(OperatorType.INSTALLATION.name());
        details.setActivityType(InstallationActivityType.valueOf(entity.getActivityType()));
        details.setFirstYear(entity.getStartYear());
        details.setLastYear(entity.getEndYear());
        details.setName(entity.getInstallationName());
        PermitDTO permitDTO = new PermitDTO();
        permitDTO.setId(entity.getPermitIdentifier());
        details.setPermit(permitDTO);
        details.setRegulator(entity.getRegulator());
        details.setChangedRegulator(entity.getChangedRegulator());

        return details;
    }
}
