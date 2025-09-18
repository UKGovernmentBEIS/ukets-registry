package gov.uk.ets.registry.api.ar.service;

import static java.util.Comparator.comparing;

import gov.uk.ets.registry.api.account.authz.AccountAuthorizationService;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountAccessDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.exception.UserNotFoundException;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.ar.service.dto.AuthorizedRepresentativeDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.publication.PublicationRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArTaskBuilder;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Authorised Representative service.
 */
@Service
@Log4j2
@AllArgsConstructor
public class AuthorizedRepresentativeService {
    public static final String ACCOUNT_AUTHORISED_REPRESENTATIVE_SUSPENDED =
        "Account authorised representative suspended";
    public static final String ACCOUNT_AUTHORISED_REPRESENTATIVE_RESTORED =
        "Account authorised representative restored";
    private final ARAccountAccessRepository arAccountAccessRepository;
    private final ARUpdateActionRepository arUpdateActionRepository;
    private final AccountRepository accountRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final DTOFactory dtoFactory;
    private final EventService eventService;
    private final AuthorizationService authorizationService;
    private final AccountAccessRepository accountAccessRepository;
    private final AccountAuthorizationService accountAuthorizationService;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final TaskEventService taskEventService;
    private final ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    private final PublicationRequestAddRemoveRoleService publicationRequestAddRemoveRoleService;
    private final Mapper mapper;

    /**
     * Gets all the pending Authorized Representative update actions of an account.
     *
     * @param accountId The unique business identifier of the account
     * @return The list of {@link ARUpdateActionDTO} pending actions
     */
    @Transactional(readOnly = true)
    public List<ARUpdateActionDTO> getPendingActions(long accountId) {
        List<ARUpdateAction> arUpdateActions = arUpdateActionRepository.fetchByAccountId(accountId);
        if (arUpdateActions.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> urids = arUpdateActions.stream().map(ARUpdateAction::getUrid).collect(Collectors.toSet());
        List<UserWorkContact> userWorkContacts = userService.getUserWorkContacts(urids);
        return dtoFactory.createARUpdateActionDTOs(arUpdateActions, userWorkContacts);
    }

    /**
     * Gets the ARs of the {@link ARUpdateActionDTO#getAccountIdentifier()} account.
     * Optionally, it filters the results by their {@link AccountAccessState} if the accountAccessState
     * parameter has a value.
     *
     * @param accountIdentifier  The account identifier
     * @param accountAccessState The optional {@link AccountAccessState} account access state
     * @return The list of the {@link AuthorizedRepresentativeDTO} authorized representatives.
     */
    @Transactional(readOnly = true)
    public List<AuthorizedRepresentativeDTO> getAuthorizedRepresentatives(Long accountIdentifier,
                                                                          Optional<AccountAccessState> accountAccessState) {
        List<AccountAccess> arAccountAccesses = arAccountAccessRepository
            .fetchARs(accountIdentifier, accountAccessState.orElse(null));
        if (arAccountAccesses.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> urids = arAccountAccesses.stream().map(accountAccess -> accountAccess.getUser().getUrid()).collect(
            Collectors.toSet());
        List<UserWorkContact> userWorkContacts = userService.getUserWorkContacts(urids);
        return dtoFactory.createAuthorizedRepresentativeDTOs(arAccountAccesses, userWorkContacts);
    }

    /**
     * Gets the ARs of the other accounts that the current user is an AR or the ARs of every other account
     * if the current user is an administrator.
     * The returned other accounts' ARs must not be ARs of the {@link ARUpdateActionDTO#getAccountIdentifier()} account.
     *
     * @param accountId The account identifier.
     * @return The ARs that could be added or replace an AR of the account of the passed identifier.
     */
    @Transactional(readOnly = true)
    public List<AuthorizedRepresentativeDTO> getOtherAccountsARs(Long accountId) {
        String urid = authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ANY_ADMIN)
            ? null
            : userService.getCurrentUser().getUrid();

        List<AccountAccess> accountAccesses = arAccountAccessRepository.fetchArsForAccount(accountId, urid);

        if (accountAccesses.isEmpty()) {
            return new ArrayList<>();
        }
        List<UserWorkContact> userWorkContacts = getUserWorkContacts(accountAccesses);
        return userWorkContacts.stream()
            .map(dtoFactory::createAuthorizedRepresentativeDTO)
            .sorted(
                comparing((AuthorizedRepresentativeDTO d) -> d.getUser().getAlsoKnownAs(), ObjectUtils::compare)
                .thenComparing((AuthorizedRepresentativeDTO d) -> d.getUser().getFirstName(), ObjectUtils::compare)
            )
            .collect(Collectors.toList());
    }

    /**
     * Creates a task for the passed AR update action.
     *
     * @param dto The AR update action
     * @return The unique business identifier of the new task.
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long placeUpdateRequest(ARUpdateActionDTO dto) {
        try {
            User currentUser = userService.getCurrentUser();
            Account account = accountRepository.findByIdentifier(dto.getAccountIdentifier()).orElseThrow(
                () -> new IllegalArgumentException("no Account exists with identifier " + dto.getAccountIdentifier()));

            // UKETS-4595: An SRA can suspend/restore an AR immediately
            boolean isSeniorAdmin = authorizationService.hasClientRole(UserRole.SENIOR_REGISTRY_ADMINISTRATOR);
            boolean isSuspension =
                RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST.equals(dto.getUpdateType().getTaskType());
            boolean isRestoration =
                RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST.equals(dto.getUpdateType().getTaskType());

            if (isSeniorAdmin && (isSuspension || isRestoration)) {
                changeARAccountAccess(dto, isSuspension ? AccountAccessState.SUSPENDED : AccountAccessState.ACTIVE);

                String action = isSuspension ? ACCOUNT_AUTHORISED_REPRESENTATIVE_SUSPENDED :
                    ACCOUNT_AUTHORISED_REPRESENTATIVE_RESTORED;

                eventService.createAndPublishEvent(account.getIdentifier().toString(), currentUser.getUrid(),
                    dto.getComment(),
                    isSuspension ? EventType.ACCOUNT_AR_SUSPENDED : EventType.ACCOUNT_AR_RESTORED, action);

                return null;
            } else {
                Task task = createUpdateTask(dto, account, currentUser);
                taskRepository.save(task);
                taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());
                return task.getRequestId();
            }
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * Persist the list of bulk AR tasks as batches.
     *
     * @param dtos the BulkArTaskBuilder object.
     */
    @Transactional
    public List<Task> placeBulkArTasks(List<BulkArTaskBuilder> dtos) {
        try {
            User currentUser = userService.getCurrentUser();
            List<Task> tasks = dtos.stream().map(d -> createBulkArTasks(d, currentUser)).collect(Collectors.toList());
            taskRepository.saveAll(tasks);
            return tasks;
        } catch (Exception exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    /**
     * Propagates the emails for every task created.
     *
     * @param task the task.
     * @return the task's request ID.
     */
    @EmitsGroupNotifications(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
    public Long propagateEmailNotifications(Task task) {
        return task.getRequestId();
    }

    private Task createBulkArTasks(BulkArTaskBuilder dto, User currentUser) {
        Account account = accountRepository.findByFullIdentifier(dto.getAccountFullIdentifier()).orElseThrow(
            () -> new IllegalArgumentException("Failed to retrieve account with ID " +
                dto.getAccountFullIdentifier()));
        Task task = new Task();
        task.setRequestId(taskRepository.getNextRequestId());
        ARUpdateAction arUpdateAction = new ARUpdateAction();
        arUpdateAction.setUrid(dto.getUserUrid());
        arUpdateAction.setAccountAccessRight(dto.getPermissions());
        arUpdateAction.setType(ARUpdateActionType.ADD);
        task.setDifference(mapper.convertToJson(arUpdateAction));
        task.setAccount(account);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setType(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST);
        User authorizedRepresentative = userService.getUserByUrid(dto.getUserUrid());
        task.setUser(authorizedRepresentative);
        return task;
    }


    /**
     * Gets the AR candidate.
     *
     * @param urid The user identifier of candidate.
     * @return The {@link AuthorizedRepresentativeDTO} of AR candidate.
     */
    public AuthorizedRepresentativeDTO getCandidate(String urid) {
        List<UserWorkContact> userWorkContacts = userService.getUserWorkContacts(Set.of(urid));
        User candidate = userService.getUserByUrid(urid);
        if (CollectionUtils.isEmpty(userWorkContacts) || candidate == null) {
            throw new UserNotFoundException("No user with urid " + urid + " exists.", "userIdInput");
        }
        return dtoFactory.createAuthorizedRepresentativeDTO(userWorkContacts.get(0), candidate);
    }


    /**
     * Removes the AR role from the keycloak user, if the user is not AR in other accounts.
     */
    public void removeKeycloakRoleIfNoOtherAccountAccess(String urid, String keycloakId) {
        List<AccountAccessDTO> accountAccesses =
            accountAuthorizationService.getActiveOrSuspendedAccountAccessesForUrid(urid);
        if (accountAccesses.isEmpty()) {
            serviceAccountAuthorizationService
                .removeUserRole(keycloakId,
                    UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral());

            // request to remove reports-user role from reports-api
            reportRequestAddRemoveRoleService.requestReportsApiRemoveRole(keycloakId);
            publicationRequestAddRemoveRoleService.requestPublicationApiRemoveRole(keycloakId);
        }
    }
    public boolean hasSuspendedAR(Long identifier) {
        return !arAccountAccessRepository.fetchARs(identifier, AccountAccessState.SUSPENDED).isEmpty();
    }

    private List<UserWorkContact> getUserWorkContacts(List<AccountAccess> accountAccesses) {
        Set<String> urids = accountAccesses.stream()
            .map(accountAccess -> accountAccess.getUser().getUrid())
            .collect(Collectors.toSet());
        return userService.getUserWorkContacts(urids);
    }

    private void changeARAccountAccess(ARUpdateActionDTO dto, AccountAccessState state) {
        AccountAccess access = accountAccessRepository
            .findArsByAccount_IdentifierAndUser_Urid(dto.getAccountIdentifier(), dto.getCandidate().getUrid());
        access.setState(state);

        accountAccessRepository.save(access);
    }

    private Task createUpdateTask(ARUpdateActionDTO dto, Account account, User currentUser) {
        Task task = new Task();

        task.setRequestId(taskRepository.getNextRequestId());

        ARUpdateAction arUpdateAction = new ARUpdateAction();
        arUpdateAction.setUrid(dto.getCandidate().getUrid());
        arUpdateAction.setAccountAccessRight(dto.getCandidate().getRight());
        arUpdateAction.setType(dto.getUpdateType());
        if (dto.getReplacee() != null) {
            arUpdateAction.setToBeReplacedUrid(dto.getReplacee().getUrid());
        }

        task.setDifference(mapper.convertToJson(arUpdateAction));
        task.setAccount(account);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setType(dto.getUpdateType().getTaskType());
        User authorizedRepresentative = userService.getUserByUrid(arUpdateAction.getUrid());
        task.setUser(authorizedRepresentative);

        return task;
    }

    /**
     * Retrieves a list of accounts that the user is Authorize Representative.
     * In My profile page the AR can see only the Active accounts
     * but on the other hand the admin in the User administration section can see all the accounts of an AR regardless
     * the state (Active, Suspended etc)
     *
     * @param urid The user identifier
     * @return An Authorize representative list
     */
    @Transactional(readOnly = true)
    public List<AuthorisedRepresentativeDTO> getARsByUser(String urid) {
        List<AuthorisedRepresentativeDTO> results = new ArrayList<>();
        List<AccountAccess> accountAccessesByUser;
        if (userService.getCurrentUser().getUrid().equals(urid)) {
            // this is the case of my profile page where we show the same Accounts to AR as in Account Search
            accountAccessesByUser = accountAccessRepository.findARsInAccountByUser(urid,
                List.of(AccountStatus.CLOSED, AccountStatus.SUSPENDED, AccountStatus.TRANSFER_PENDING,
                    AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.PROPOSED));
        } else {
            // this is the case of user details page where we want to show all Accounts to admin
            accountAccessesByUser = accountAccessRepository.findARsInAccountByUser(urid);
        }
        accountAccessesByUser.stream()
            .filter(isAccountAccessVisible(urid))
            .forEach(accountAccess -> {
                AuthorisedRepresentativeDTO dto = new AuthorisedRepresentativeDTO();
                dto.setRight(accountAccess.getRight());
                dto.setState(accountAccess.getState());
                dto.setUrid(urid);
                Account account = accountAccess.getAccount();
                if (account != null) {
                    dto.setAccountIdentifier(account.getIdentifier());
                    dto.setAccountName(account.getAccountName());
                    dto.setAccountFullIdentifier(account.getFullIdentifier());
                    if (account.getAccountHolder() != null) {
                        dto.setAccountHolderName(account.getAccountHolder().getName());
                    }
                }
                results.add(dto);

            });
        return results;
    }

    /**
     * Calculates the total number of AR additions and removals.
     * A user is allowed to have multiple transitions.
     * The sequence of the events is taken into account in order to avoid inconsistencies.
     *
     * @param accountIdentifier The unique business identifier of the account
     * @return A map that contains two entries, one for additions and one for removals.
     */
    public Map<RequestType, Integer> calculateCounters(long accountIdentifier) {

        // Get ARs including those that have been removed.
        List<User> users = accountAccessRepository.finARsByAccount_Identifier(accountIdentifier)
            .stream()
            .map(AccountAccess::getUser)
            .toList();

        // Retrieve all tasks related with ARs addition/removal sorted.
        List<Task> allTasks = retrieveTasksForCountersSorted(accountIdentifier, users);

        // Get tasks after the last account transfer.
        Function<Date, List<Task>> getTasksAfterDate =
            date -> allTasks.stream().filter(t -> date.compareTo(t.getCompletedDate()) < 0).toList();

        List<Task> relatedTasks = allTasks.stream()
            .filter(t -> t.getType() == RequestType.ACCOUNT_TRANSFER)
            .max(comparing(Task::getCompletedDate))
            .map(Task::getCompletedDate)
            .map(getTasksAfterDate)
            .orElse(allTasks);

        // Build transition history for each user based on the relative tasks.
        Map<User, Deque<RequestType>> usersHistory = new HashMap<>();
        users.forEach(u -> computeTransitions(relatedTasks, u, usersHistory));

        if (allTasks.size() == relatedTasks.size()) {
            // Account transfer task does not exist.
            // Add the original ARs.
            List<User> originalArs = calculateOriginalARs(users, usersHistory);
            originalArs.forEach(user -> addFirstEvent(usersHistory, user));

            // Validate counter against account opening task.
            Set<String> originalArsUrid = originalArs.stream().map(User::getUrid).collect(Collectors.toSet());
            Set<String> originalARsUridFromTask = retrieveARsFromAccountOpeningTask(accountIdentifier);
            if (!originalARsUridFromTask.containsAll(originalArsUrid) || !originalArsUrid.containsAll(originalARsUridFromTask)) {
                log.warn("Unable to calculate AR counters correctly for account: {}", accountIdentifier);
            }
        } else {
            // An account transfer task exists, handle deactivation cases
            usersHistory.values()
                .stream()
                .filter(deque -> deque.peekFirst() == RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST)
                .forEach(Deque::pollFirst);
        }

        // Build the counters.
        return usersHistory.values()
            .stream()
            .flatMap(Collection::stream)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(v -> 1)));
    }

    public List<User> calculateOriginalARs(long accountIdentifier) {
        // Get ARs including those that have been removed.
        List<User> users = accountAccessRepository.finARsByAccount_Identifier(accountIdentifier)
            .stream()
            .map(AccountAccess::getUser)
            .toList();

        // Retrieve all tasks related with ARs addition/removal sorted.
        List<Task> allTasks = retrieveTasksForCountersSorted(accountIdentifier, users);

        Map<User, Deque<RequestType>> usersHistory = new HashMap<>();
        users.forEach(u -> computeTransitions(allTasks, u, usersHistory));

        return calculateOriginalARs(users, usersHistory);
    }

    private List<Task> retrieveTasksForCountersSorted(long accountIdentifier, List<User> users) {
        QTask task = QTask.task;
        Iterable<Task> tasks = taskRepository.findAll(
            task.account.identifier.eq(accountIdentifier)
                .and(task.type.in(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                    RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
                    RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
                    RequestType.ACCOUNT_TRANSFER))
                .and(task.status.eq(RequestStateEnum.APPROVED)));

        Set<Long> userIds = users.stream().map(User::getId).collect(Collectors.toSet());
        Iterable<Task> deactivationTasks = taskRepository.findAll(
            task.user.id.in(userIds)
                .and(task.type.in(RequestType.USER_DEACTIVATION_REQUEST))
                .and(task.status.eq(RequestStateEnum.APPROVED)));

        return Stream.concat(StreamSupport.stream(tasks.spliterator(), false),
                StreamSupport.stream(deactivationTasks.spliterator(), false))
            .sorted(comparing(Task::getCompletedDate))
            .toList();
    }

    private void computeTransitions(List<Task> tasks, User user, Map<User, Deque<RequestType>> usersHistory) {

        for (Task task : tasks) {

            if (Objects.equals(task.getUser(), user)) {
                Stream.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST)
                    .filter(type -> type == task.getType())
                    .findFirst()
                    .ifPresent(type -> addTransition(usersHistory, user, RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST));

                Stream.of(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, RequestType.USER_DEACTIVATION_REQUEST)
                    .filter(type -> type == task.getType())
                    .findFirst()
                    .ifPresent(type -> addTransition(usersHistory, user, RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST));
            } else if (task.getType() == RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST) {
                ARUpdateAction updateAction = mapper.convertToPojo(task.getDifference(), ARUpdateAction.class);
                if (Objects.equals(updateAction.getToBeReplacedUrid(), user.getUrid())) {
                    addTransition(usersHistory, user, RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST);
                }
            }

            // In the case of account transfer, we are not able to identify if this user was before the account transfer.
            // We ignore users that does not have tasks before account transfer.
            if (task.getType() == RequestType.ACCOUNT_TRANSFER && usersHistory.containsKey(user)) {
                addTransition(usersHistory, user, RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST);
            }
        }
    }

    private void addTransition(Map<User, Deque<RequestType>> usersHistory, User user, RequestType transition){
        Deque<RequestType> currentList = usersHistory.computeIfAbsent(user, k -> new LinkedList<>());
        if (currentList.peekLast() != transition) {
            currentList.add(transition);
        }
    }

    private void addFirstEvent(Map<User, Deque<RequestType>> usersHistory, User user){
        usersHistory.computeIfAbsent(user, k -> new LinkedList<>())
            .addFirst(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST);
    }

    private Set<String> retrieveARsFromAccountOpeningTask(long accountIdentifier) {
        Optional<Task> accountOpeningTask = taskRepository
            .findByAccount_IdentifierAndTypeAndStatus(accountIdentifier,
                RequestType.ACCOUNT_OPENING_REQUEST,
                RequestStateEnum.APPROVED);
        //We also need to check if the account is from an installation transfer
        if (accountOpeningTask.isEmpty()) {
            accountOpeningTask = taskRepository
                .findByAccount_IdentifierAndTypeAndStatus(accountIdentifier,
                    RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST,
                    RequestStateEnum.APPROVED);
        }

        if (accountOpeningTask.isPresent()) {
            Optional<String> diff = Optional.ofNullable(accountOpeningTask.get().getDifference());
            if (diff.isPresent() && !diff.get().isBlank()) {
                AccountDTO openingAccountDTO = mapper.convertToPojo(diff.get(), AccountDTO.class);
                //From account opening
                return openingAccountDTO.getAuthorisedRepresentatives()
                    .stream()
                    .map(AuthorisedRepresentativeDTO::getUrid)
                    .collect(Collectors.toSet());
            }
        }

        return Collections.emptySet();
    }

    private List<User> calculateOriginalARs(List<User> users, Map<User, Deque<RequestType>> usersHistory) {
        Predicate<User> originalARPredicate = user ->
            Optional.ofNullable(usersHistory.get(user))
                .map(Deque::peekFirst)
                .filter(requestType -> requestType == RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)
                .isEmpty();
        return users.stream().filter(originalARPredicate).toList();
    }
    
    private Predicate<AccountAccess> isAccountAccessVisible(String urid) {
        Predicate<AccountAccess> isAccountActive = aa -> AccountAccessState.ACTIVE.equals(aa.getState());
        if (!userService.getCurrentUser().getUrid().equals(urid)) {
            // this is the case of user details page where we want to show suspended ARs
            return isAccountActive.or(aa -> AccountAccessState.SUSPENDED.equals(aa.getState()));
        }
        // this is the case of my profile page where we show only active ARs
        return isAccountActive;
    }
}
