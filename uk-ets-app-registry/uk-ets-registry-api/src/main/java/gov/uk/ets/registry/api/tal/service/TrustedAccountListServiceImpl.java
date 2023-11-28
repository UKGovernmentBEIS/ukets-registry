package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.exception.NotFoundException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountFilter;
import gov.uk.ets.registry.api.tal.domain.TrustedAccountTaskDifference;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountListType;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.error.TrustedAccountListActionError;
import gov.uk.ets.registry.api.tal.error.TrustedAccountListActionException;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import gov.uk.ets.registry.api.tal.web.model.TrustedAccountDTO;
import gov.uk.ets.registry.api.tal.web.model.search.TALProjection;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrustedAccountListServiceImpl implements TrustedAccountListService {

    public static final String NO_VALUE = "no value";
    private final TrustedAccountRepository trustedAccountRepository;

    private final UserService userService;

    private final AccountRepository accountRepository;

    private final PersistenceService persistenceService;

    private final TrustedAccountConversionService trustedAccountConversionService;

    private final EventService eventService;

    private final TaskEventService taskEventService;

    private final Mapper mapper;

    @Override
    public Set<TrustedAccountDTO> getTrustedAccounts(Long identifier) {
        return Stream.concat(trustedAccountRepository.findAllByAccountIdentifier(identifier).stream()
                .map(trustedAccountConversionService::convertTrustedAccount),
            accountRepository.findAccountsOfTheSameAccountHolder(identifier).stream()
                .map(trustedAccountConversionService::convertTrustedAccount)).collect(Collectors.toSet());
    }

    @Override
    public Set<TrustedAccountDTO> getTrustedAccounts(Long identifier, TrustedAccountListType type) {
        if (type == null) {
            return getTrustedAccounts(identifier);
        } else {
            switch (type) {
                case SAME_ACCOUNT_HOLDER:
                    return accountRepository.findAccountsOfTheSameAccountHolder(identifier).stream()
                        .map(trustedAccountConversionService::convertTrustedAccount).collect(Collectors.toSet());
                case OTHER_APPROVED_OR_ACTIVATED:
                    return trustedAccountRepository.findAllByAccountIdentifierAndStatusIn(identifier,
                        Arrays.asList(TrustedAccountStatus.ACTIVE,
                            TrustedAccountStatus.PENDING_ACTIVATION, TrustedAccountStatus.PENDING_REMOVAL_APPROVAL)).stream()
                        .map(trustedAccountConversionService::convertTrustedAccount).collect(Collectors.toSet());
                case ELIGIBLE_FOR_REMOVAL:
                    return trustedAccountRepository.findAllByAccountIdentifierAndStatusIn(identifier,
                        Arrays.asList(TrustedAccountStatus.ACTIVE,
                            TrustedAccountStatus.PENDING_ACTIVATION)).stream()
                        .map(trustedAccountConversionService::convertTrustedAccount).collect(Collectors.toSet());
                case OTHER_PENDING_APPROVAL:
                    return trustedAccountRepository
                        .findAllByAccountIdentifierAndStatusIn(identifier,
                            Collections.singletonList(TrustedAccountStatus.PENDING_ADDITION_APPROVAL))
                        .stream()
                        .map(trustedAccountConversionService::convertTrustedAccount).collect(Collectors.toSet());
                default:
                    return trustedAccountRepository.findAllByAccountIdentifier(identifier).stream()
                        .map(trustedAccountConversionService::convertTrustedAccount).collect(Collectors.toSet());
            }
        }
    }

    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long addTrustedAccount(TrustedAccountDTO trustedAccountDTO, Long accountId) {

        Account account = accountRepository.findByIdentifier(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found: " + accountId));

        if (trustedAccountDTO.getAccountFullIdentifier().equals(account.getFullIdentifier())) {
            throw TrustedAccountListActionException.create(TrustedAccountListActionError.builder()
                    .code(TrustedAccountListActionError.ACCOUNT_CANNOT_BE_ADDED_TO_ITS_OWN_TRUSTED_ACCOUNT_LIST)
                    .message(TrustedAccountListActionError.ACCOUNT_CANNOT_BE_ADDED_TO_ITS_OWN_TRUSTED_ACCOUNT_LIST)
                    .build());
        }

        TrustedAccount trustedAccount = trustedAccountRepository
                        .save(trustedAccountConversionService.convertTrustedAccount(trustedAccountDTO, accountId));
        Task task = createAddTask(trustedAccount);
        return task.getRequestId();
    }

    /**
     * Searches for trusted accounts.
     */
    @Override
    public Page<TALProjection> search(TrustedAccountFilter filter, Pageable pageable) {
        return trustedAccountRepository.search(filter, pageable);
    }

    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.TRUSTED_ACCOUNT_UPDATE_DESCRIPTION)
    public TrustedAccountDTO updateTrustedAccount(TrustedAccountDTO trustedAccountDto, Long accountId) {

        if (trustedAccountDto == null) {
            throw TrustedAccountListActionException.create(TrustedAccountListActionError.builder()
                .code(TrustedAccountListActionError.ACCOUNT_NOT_INITIALIZED)
                .message(TrustedAccountListActionError.ACCOUNT_NOT_INITIALIZED)
                .build());
        }

        if (trustedAccountDto.getAccountFullIdentifier() == null) {
            throw TrustedAccountListActionException.create(TrustedAccountListActionError.builder()
                .code(TrustedAccountListActionError.TRUSTED_ACCOUNT_NOT_INITIALIZED)
                .message(TrustedAccountListActionError.TRUSTED_ACCOUNT_NOT_INITIALIZED)
                .build());
        }

        TrustedAccount trustedAccount =
            trustedAccountRepository.findByAccountAndTrustedAccountFullIdentifierAndStatus(accountId,
                trustedAccountDto.getAccountFullIdentifier(), TrustedAccountStatus.ACTIVE);

        if (trustedAccount != null) {
            String previousDescription =
                trustedAccount.getDescription() != null && !"".equals(trustedAccount.getDescription()) ?
                    trustedAccount.getDescription() : NO_VALUE;
            String currentDescription =
                trustedAccountDto.getDescription() != null && !"".equals(trustedAccountDto.getDescription()) ?
                    trustedAccountDto.getDescription() : NO_VALUE;
            trustedAccount.setDescription(trustedAccountDto.getDescription());
            TrustedAccount trustedAccountUpdated = trustedAccountRepository.saveAndFlush(trustedAccount);

            // currentUser is null in case of a system action (e.g. emergency OTP change) where there is no logged in user
            User currentUser = retrieveCurrentUserIfAvailable(null);

            if (currentUser != null) {
                eventService
                    .createAndPublishEvent(String.valueOf(accountId), currentUser.getUrid(),
                        String.format("Change description (description) from %s to %s", previousDescription,
                            currentDescription),
                        EventType.ACCOUNT_TRUSTED_ACCOUNT_UPDATED, "Change description (description)");
            }
            return trustedAccountConversionService.convertTrustedAccount(trustedAccountUpdated);
        }

        return null;
    }

    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long removeTrustedAccounts(List<TrustedAccountDTO> trustedAccountDtos, Long accountId) {
        List<String> trustedAccountsFullIdentifiers =
            trustedAccountDtos.stream().map(TrustedAccountDTO::getAccountFullIdentifier).toList();

        List<TrustedAccount> trustedAccounts =
            trustedAccountRepository.findAllByAccountIdentifierAndStatusIn(accountId,
                    List.of(TrustedAccountStatus.ACTIVE, TrustedAccountStatus.PENDING_ACTIVATION))
                .stream()
                .filter(trustedAccount -> trustedAccountsFullIdentifiers.contains(
                    trustedAccount.getTrustedAccountFullIdentifier()))
                .toList();

        trustedAccounts.forEach(trustedAccount -> trustedAccount.setStatus(TrustedAccountStatus.PENDING_REMOVAL_APPROVAL));
        Task task = createRemoveTask(trustedAccounts);
        return task.getRequestId();
    }

    /**
     * Cancels a pending account addition in TAL.
     */
    @Override
    @Transactional
    public void cancelTrustedAccount(Long accountIdentifier, String trustedAccountFullIdentifier) {

        TrustedAccount trustedAccountToCancel = trustedAccountRepository
            .findByAccountAndTrustedAccountFullIdentifierAndStatus(accountIdentifier,
                trustedAccountFullIdentifier, TrustedAccountStatus.PENDING_ACTIVATION);

        if (trustedAccountToCancel == null) {
            throw new BusinessRuleErrorException(
                ErrorBody.from("Requested trusted account not found or it is not in pending activation status."));
        }

        trustedAccountToCancel.setStatus(TrustedAccountStatus.CANCELLED);
        trustedAccountRepository.save(trustedAccountToCancel);

        User currentUser = retrieveCurrentUserIfAvailable(null);

        if (currentUser != null) {
            eventService
                .createAndPublishEvent(String.valueOf(accountIdentifier), currentUser.getUrid(),
                    String.valueOf(accountIdentifier), EventType.ACCOUNT_TRUSTED_ACCOUNT_CANCELLED,
                    "Account addition to TAL cancelled");
        }
    }

    /**
     * Creates an ADD_TRUSTED_ACCOUNT_REQUEST task.
     *
     * @param trustedAccount The trusted account to be added.
     */
    private Task createAddTask(TrustedAccount trustedAccount) {
        Date insertDate = new Date();
        User currentUser = userService.getCurrentUser();

        Task task =
            createTask(trustedAccount.getAccount(), RequestType.ADD_TRUSTED_ACCOUNT_REQUEST, insertDate, currentUser);
        TrustedAccountTaskDifference difference =
            TrustedAccountTaskDifference.builder().id(trustedAccount.getId()).build();
        task.setDifference(mapper.convertToJson(difference));

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        return task;
    }

    /**
     * Creates a DELETE_TRUSTED_ACCOUNT_REQUEST task.
     *
     * @param trustedAccounts The list of trusted account to be removed.
     */
    private Task createRemoveTask(List<TrustedAccount> trustedAccounts) {
        Date insertDate = new Date();
        User currentUser = userService.getCurrentUser();

        Task task =
            createTask(trustedAccounts.get(0).getAccount(), RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST, insertDate,
                currentUser);
        TrustedAccountTaskDifference difference =
            TrustedAccountTaskDifference.builder()
                .ids(trustedAccounts.stream().map(TrustedAccount::getId).collect(Collectors.toList())).build();
        task.setDifference(mapper.convertToJson(difference));

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());

        return task;
    }

    private Task createTask(Account account, RequestType type, Date insertDate, User currentUser) {
        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        task.setType(type);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(insertDate);
        task.setAccount(account);
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);

        return task;
    }

    private User retrieveCurrentUserIfAvailable(String token) {
        return token == null ? userService.getCurrentUser() : null;
    }
}
