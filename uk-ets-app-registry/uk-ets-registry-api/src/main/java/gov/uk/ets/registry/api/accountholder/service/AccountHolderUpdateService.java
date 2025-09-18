package gov.uk.ets.registry.api.accountholder.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.shared.InstallationAndAccountTransferError;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderChangeDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderContactUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderDetailsUpdateDiffDTO;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;

import java.beans.FeatureDescriptor;
import java.util.*;
import java.util.stream.Stream;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountHolderUpdateService {

    private final UserService userService;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TaskRepository taskRepository;
    private final AccountHolderRepository holderRepository;
    private final PersistenceService persistenceService;
    private final TaskEventService taskEventService;
    private final Mapper mapper;
    private final TrustedAccountRepository trustedAccountRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;

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

    @Transactional
    //@EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Boolean submitChangeAccountHolderRequest(AccountHolderChangeDTO accountHolderChangeDTO) {
        Long identifier = accountHolderChangeDTO.getAccountIdentifier();
        AccountHolderDTO inputAccountHolderDTO = accountHolderChangeDTO.getAcquiringAccountHolder();
        AccountHolderRepresentativeDTO holderUpdatedValues = accountHolderChangeDTO.getAcquiringAccountHolderContactInfo();

        // validate Business Rules
        Account account = retrieveAccountByIdentifier(identifier);
        validateHasAccountSuspendedAR(identifier);
        validatePendingForApprovalTrustedAccount(identifier);

        validateNotTheSameAccountHolder(account, inputAccountHolderDTO);
        validateOpenTasksForAccountsUnderTheSameAccountHolder(account.getId());
        validateOpenTasksForAccounts(account.getId());

        // update the AccountHolder details
        AccountHolder accountHolderEntity = null;
        if (Objects.nonNull(inputAccountHolderDTO.getId())) {
            accountHolderEntity = holderRepository.getAccountHolder(inputAccountHolderDTO.getId());
        } else {
            accountHolderEntity = accountService.createHolder(inputAccountHolderDTO);
            accountService.insertContact(accountHolderEntity, holderUpdatedValues, true);
        }

        // bind the AccountHolder to the Current Account
        account.setAccountHolder(accountHolderEntity);
        persistenceService.save(account);
        return true;
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

    /**
     * Transfer-BR8 An account opening request with installation transfer cannot be
     * submitted if there are outstanding updates (pending or delayed) for the AH of
     * old (existing) account of the installation originating from the same account.
     * More specifically, if any of the following tasks is outstanding (pending or
     * delayed) for the AH of the old (existing account), the account opening with
     * installation transfer cannot be triggered:
     * <p>
     * ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS ACCOUNT_HOLDER_UPDATE_DETAILS
     * AH_REQUESTED_DOCUMENT_UPLOAD
     */
    private void validateOpenTasksForAccountsUnderTheSameAccountHolder(Long accountId) {
        Long openTasksForAccountsUnderTheSameAccountHolderCount = taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId),
                AccountHolderChangeRules.changeAccountHolderInvalidPendingTasks()
            );

        if (openTasksForAccountsUnderTheSameAccountHolderCount > 0) {
            throw AccountActionException.create(AccountActionError
                    .build(InstallationAndAccountTransferError.OUTSTANDING_TASKS_EXIST_FOR_THE_OLD_AH.getMessage()));
        }
    }

    private void validateOpenTasksForAccounts(Long accountId) {
        List<RequestType> invalidRequests = new java.util.ArrayList<>(RequestType.getARUpdateTasks());
        invalidRequests.addAll(
                AccountHolderChangeRules.changeAccountHolderInvalidRequestTypes()
        );
        Long openTasksForAccountsUnderTheSameAccountHolderCount = taskRepository.countPendingTasksByAccountIdInAndType(
                List.of(accountId), invalidRequests);

        if (openTasksForAccountsUnderTheSameAccountHolderCount > 0) {
            throw AccountActionException.create(AccountActionError
                    .build("You cannot change the AccountHolder if there are pending update Requests"));
        }
    }

    private void validateNotTheSameAccountHolder(Account account, @NotNull AccountHolderDTO accountHolder) {
        boolean hasEqualIds = account.getAccountHolder().getId() != null
                && account.getAccountHolder().getId().equals(accountHolder.getId());

        boolean hasEqualIdentifiers = Objects.nonNull(account.getAccountHolder().getIdentifier()) &&
                account.getAccountHolder().getIdentifier().equals(accountHolder.getId());

        if(hasEqualIds || hasEqualIdentifiers) {
            throw AccountActionException.create(AccountActionError
                    .build("You cannot change the account holder to the same one"));
        }
    }

    private void validateHasAccountSuspendedAR(Long identifier) {
        if(authorizedRepresentativeService.hasSuspendedAR(identifier)) {
            throw AccountActionException.create(AccountActionError
                    .build("You try to change account holder when suspended account representative request exists"));
        }
    }

    private void validatePendingForApprovalTrustedAccount(Long identifier) {
        List<TrustedAccount> trustedAccounts = trustedAccountRepository.findAllByAccountIdentifierAndStatusIn(identifier, List.of(TrustedAccountStatus.PENDING_ACTIVATION));
        if(!trustedAccounts.isEmpty()) {
            throw AccountActionException.create(AccountActionError
                    .build("You try to change account holder when trusted account change exists"));
        }
    }
}
