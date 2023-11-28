package gov.uk.ets.registry.api.ar.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.authz.AccountAuthorizationService;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.ar.service.dto.ARUpdateActionDTO;
import gov.uk.ets.registry.api.ar.service.dto.AuthorizedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.service.dto.WorkContactDetailsDTO;
import gov.uk.ets.registry.api.auditevent.DomainEvent;
import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.publication.PublicationRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContact;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Builder;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class AuthorizedRepresentativeServiceBDDTest {
    private static Map<ARUpdateActionType, RequestType> arUpdateActionTypeToRequestTypeMap = Stream.of(
        new AbstractMap.SimpleEntry<>(ARUpdateActionType.ADD, RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
        new AbstractMap.SimpleEntry<>(ARUpdateActionType.REMOVE, RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST),
        new AbstractMap.SimpleEntry<>(ARUpdateActionType.REPLACE,
            RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST),
        new AbstractMap.SimpleEntry<>(ARUpdateActionType.CHANGE_ACCESS_RIGHTS,
            RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST),
        new AbstractMap.SimpleEntry<>(ARUpdateActionType.RESTORE,
            RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST),
        new AbstractMap.SimpleEntry<>(ARUpdateActionType.SUSPEND, RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST)
    )
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static Stream<ARUpdateActionType> testDtoDataFor() {
        return Stream.of();
    }

    private AuthorizedRepresentativeService service;

    @Mock
    private ARAccountAccessRepository arAccountAccessRepository;

    @Mock
    private ARUpdateActionRepository arUpdateActionRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private DomainEventEntityRepository domainEventEntityRepository;

    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private AccountAccessRepository accountAccessRepository;

    @Mock
    private AccountAuthorizationService accountAuthorizationService;

    @Mock
    private PublicationRequestAddRemoveRoleService publicationRequestAddRemoveRoleService;

    @Mock
    private Mapper mapper;

    @Mock
    private ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;

    @Captor
    protected ArgumentCaptor<Object> publishEventCaptor;

    @BeforeEach
    public void setup() {
        EventService eventService =
            new EventService(applicationEventPublisher, domainEventEntityRepository);
        TaskEventService taskEventService = new TaskEventService(authorizationService, eventService);
        service = new AuthorizedRepresentativeService(arAccountAccessRepository,
            arUpdateActionRepository,
            accountRepository,
            taskRepository,
            userService,
            new DTOFactory(),
            eventService,
            authorizationService,
            accountAccessRepository, accountAuthorizationService, serviceAccountAuthorizationService, taskEventService,
            reportRequestAddRemoveRoleService, publicationRequestAddRemoveRoleService,
            mapper);
    }

    @Test
    void getPendingActions() throws JsonProcessingException {
        // given
        long accountId = 1000L;
        String urid = "UKETS123123";
        MockARUpdateActionCommand updateActionCommand = MockARUpdateActionCommand.builder()
            .accountAccessRight(AccountAccessRight.INITIATE_AND_APPROVE)
            .authorizedRepresentativeUrid(urid)
            .requestId(10012L)
            .type(ARUpdateActionType.ADD)
            .build();
        ARUpdateAction updateAction = mockARUpdateAction(updateActionCommand);
        given(arUpdateActionRepository.fetchByAccountId(accountId)).willReturn(List.of(updateAction));
        MockPersonalUserInfoCommand personalUserInfoCommand = MockPersonalUserInfoCommand.builder()
            .firstName("John")
            .lastName("Alerton")
            .workBuildingAndStreet("test address 1")
            .workBuildingAndStreetOptional("test address 2")
            .workBuildingAndStreetOptional2("test address 3")
            .workCountry("Greece")
            .workCountryCode("GR")
            .urid(updateAction.getUrid())
            .workPhoneNumber("123123123")
            .workPostCode("123232")
            .workTownOrCity("Athens")
            .workStateOrProvince("Attica")
            .build();
        UserWorkContact userInfo = mockPersonalUserInfo(personalUserInfoCommand);
        given(userService.getUserWorkContacts(Set.of(updateAction.getUrid())))
            .willReturn(List.of(userInfo));

        // when
        List<ARUpdateActionDTO> result = service.getPendingActions(accountId);

        // then
        then(arUpdateActionRepository).should(times(1)).fetchByAccountId(accountId);
        then(userService).should(times(1)).getUserWorkContacts(any());
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userInfo.getUrid(), result.get(0).getCandidate().getUrid());
        assertEquals(userInfo.getFirstName(), result.get(0).getCandidate().getUser().getFirstName());
        assertEquals(userInfo.getLastName(), result.get(0).getCandidate().getUser().getLastName());
        verifyWorkContactDetails(personalUserInfoCommand, result.get(0).getCandidate().getContact());
    }

    @Test
    void getAccountARs() {
        // given
        Optional<AccountAccessState> optionalAccountAccessState = Optional.of(AccountAccessState.ACTIVE);
        Long accountIdentifier = 1001L;
        MockAccountAccessCommand mockReturnedAccountAccessCommand = MockAccountAccessCommand
            .builder()
            .urid("UK123123132")
            .firstName("John")
            .lastName("Owen")
            .right(AccountAccessRight.INITIATE_AND_APPROVE)
            .accountAccessState(AccountAccessState.ACTIVE)
            .userStatus(UserStatus.ENROLLED)
            .build();
        given(arAccountAccessRepository
            .fetchARs(accountIdentifier, optionalAccountAccessState.get()))
            .willReturn(List.of(mockAccountAccess(mockReturnedAccountAccessCommand)));
        MockPersonalUserInfoCommand personalUserInfoCommand = MockPersonalUserInfoCommand.builder()
            .urid(mockReturnedAccountAccessCommand.urid)
            .firstName(mockReturnedAccountAccessCommand.firstName)
            .lastName(mockReturnedAccountAccessCommand.lastName)
            .workBuildingAndStreet("test address 1")
            .workBuildingAndStreetOptional("test address 2")
            .workBuildingAndStreetOptional2("test address 3")
            .workCountry("Greece")
            .workCountryCode("GR")
            .workPhoneNumber("123123123")
            .workPostCode("123232")
            .workTownOrCity("Athens")
            .workStateOrProvince("Attica")
            .build();
        given(userService.getUserWorkContacts(Set.of(mockReturnedAccountAccessCommand.urid)))
            .willReturn(List.of(mockPersonalUserInfo(personalUserInfoCommand)));

        // when
        List<AuthorizedRepresentativeDTO> result =
            service.getAuthorizedRepresentatives(accountIdentifier, optionalAccountAccessState);

        // then
        then(arAccountAccessRepository).should(times(1)).fetchARs(accountIdentifier, optionalAccountAccessState.get());
        then(userService).should(times(1)).getUserWorkContacts(Set.of(mockReturnedAccountAccessCommand.urid));
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(mockReturnedAccountAccessCommand.right, result.get(0).getRight());
        assertEquals(mockReturnedAccountAccessCommand.accountAccessState, result.get(0).getState());
        assertEquals(mockReturnedAccountAccessCommand.urid, result.get(0).getUrid());
        assertEquals(personalUserInfoCommand.firstName, result.get(0).getUser().getFirstName());
        assertEquals(personalUserInfoCommand.lastName, result.get(0).getUser().getLastName());
        assertEquals(mockReturnedAccountAccessCommand.userStatus, result.get(0).getUser().getStatus());
        verifyWorkContactDetails(personalUserInfoCommand, result.get(0).getContact());
    }

    @Test
    @DisplayName("Retrieve the accounts that the user is authorise representative with state active expected to succeed.")
    void retrieveAuthoriseRepresentativesOfAccountsForUser() {
        String urid = "UK123456";
        List<AccountAccess> accountAccessesByUser = new ArrayList<>();
        Stream.of(AccountAccessState.values()).filter(f -> f.equals(AccountAccessState.ACTIVE)).forEach(state ->
            Stream.of(AccountAccessRight.values()).forEach(right ->
                accountAccessesByUser.add(retrieveAccountAccessInfo(state, right))));

        given(accountAccessRepository.findARsInAccountByUser(urid,
            List.of(AccountStatus.CLOSED, AccountStatus.SUSPENDED, AccountStatus.TRANSFER_PENDING,
                AccountStatus.SUSPENDED_PARTIALLY, AccountStatus.PROPOSED))).willReturn(accountAccessesByUser);

        User currentUser = Mockito.mock(User.class);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        given(userService.getCurrentUser().getUrid()).willReturn("UK123456");

        List<AuthorisedRepresentativeDTO> aRsByUser = service.getARsByUser(urid);
        assertEquals(aRsByUser.size(), accountAccessesByUser.stream()
            .filter(f -> AccountAccessState.ACTIVE.equals(f.getState())).count());
    }

    @Test
    @DisplayName("Retrieve the accounts that the user is authorise representative regardless the state, expected to succeed.")
    void retrieveAuthoriseRepresentativesOfAccountsForAdmin() {
        String urid = "UK123456";
        List<AccountAccess> accountAccessesByUser = new ArrayList<>();
        Stream.of(AccountAccessState.values()).forEach(state ->
            Stream.of(AccountAccessRight.values()).forEach(right ->
                accountAccessesByUser.add(retrieveAccountAccessInfo(state, right))));

        given(accountAccessRepository.findARsInAccountByUser(urid)).willReturn(accountAccessesByUser);

        User currentUser = Mockito.mock(User.class);
        when(userService.getCurrentUser()).thenReturn(currentUser);
        given(userService.getCurrentUser().getUrid()).willReturn("UK123");

        List<AuthorisedRepresentativeDTO> aRsByUser = service.getARsByUser(urid);
        assertEquals(aRsByUser.size(), accountAccessesByUser.stream()
            .filter(f ->
                (AccountAccessState.SUSPENDED.equals(f.getState()) ||
                    AccountAccessState.ACTIVE.equals(f.getState()))).count());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getPotentialARs(boolean isAdmin) {
        // given
        Long accountId = 1000L;
        String currentUserUrid = "UK123456789";
        User user = new User();
        user.setUrid(currentUserUrid);
        given(userService.getCurrentUser()).willReturn(user);
        MockAccountAccessCommand accountAccessCommand = MockAccountAccessCommand
            .builder()
            .urid("UK12123213")
            .firstName("John")
            .lastName("Owen")
            .build();
        List<AccountAccess> expectedReturnedAccountAccesses = List.of(mockAccountAccess(accountAccessCommand));
        given(arAccountAccessRepository.fetchArsForAccount(accountId, currentUserUrid))
            .willReturn(expectedReturnedAccountAccesses);
        MockPersonalUserInfoCommand personalUserInfoCommand = MockPersonalUserInfoCommand.builder()
            .urid(accountAccessCommand.urid)
            .firstName(accountAccessCommand.firstName)
            .lastName(accountAccessCommand.lastName)
            .workBuildingAndStreet("test address 1")
            .workBuildingAndStreetOptional("test address 2")
            .workBuildingAndStreetOptional2("test address 3")
            .workCountry("Greece")
            .workCountryCode("GR")
            .workPhoneNumber("123123123")
            .workPostCode("123232")
            .workTownOrCity("Athens")
            .workStateOrProvince("Attica")
            .build();
        given(userService.getUserWorkContacts(Set.of(accountAccessCommand.urid)))
            .willReturn(List.of(mockPersonalUserInfo(personalUserInfoCommand)));

        // when
        List<AuthorizedRepresentativeDTO> result = service.getOtherAccountsARs(accountId);

        // then
        then(arAccountAccessRepository).should(times(1)).fetchArsForAccount(accountId, currentUserUrid);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(result.get(0).getUrid(), accountAccessCommand.urid);
        assertEquals(result.get(0).getUser().getFirstName(), accountAccessCommand.firstName);
        assertEquals(result.get(0).getUser().getLastName(), accountAccessCommand.lastName);
        verifyWorkContactDetails(personalUserInfoCommand, result.get(0).getContact());
    }


    @ParameterizedTest
    @ValueSource(strings = {"ADD", "SUSPEND", "CHANGE_ACCESS_RIGHTS", "REMOVE", "REPLACE", "RESTORE"})
    void placeUpdateRequest(String actionType) throws JsonProcessingException {
        // given
        ARUpdateActionDTO dto = ARUpdateActionDTO.builder()
            .accountIdentifier(1000L)
            .updateType(ARUpdateActionType.valueOf(actionType))
            .candidate(AuthorizedRepresentativeDTO.builder()
                .urid("UK123123231")
                .build())
            .build();
        ARUpdateAction arUpdateAction = new ARUpdateAction();
        arUpdateAction.setUrid("UK123123231");
        arUpdateAction.setType(ARUpdateActionType.valueOf(actionType));
        String arUpdateActionStr = "{ \"urid\" : \"UK123123231\", \"type\" :\"" + actionType + "\"}";

        User currentUser = new User();
        currentUser.setId(100L);
        currentUser.setUrid("UK213123");
        currentUser.setFirstName("Firstname");
        currentUser.setLastName("Lastname");
        given(userService.getCurrentUser()).willReturn(currentUser);
        Account account = new Account();
        account.setIdentifier(dto.getAccountIdentifier());
        given(accountRepository.findByIdentifier(any())).willReturn(Optional.of(account));
        User authorizedRepresentative = new User();
        authorizedRepresentative.setId(200L);
        authorizedRepresentative.setUrid("UK123123231");
        given(userService.getUserByUrid("UK123123231")).willReturn(authorizedRepresentative);

        // when
        when(mapper.convertToJson(arUpdateAction)).thenReturn(arUpdateActionStr);
        service.placeUpdateRequest(dto);

        // then
        then(userService).should(times(1)).getCurrentUser();
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        then(taskRepository).should(times(1)).save(taskArgumentCaptor.capture());
        Task task = taskArgumentCaptor.getValue();
        assertNotNull(task);
        assertEquals(arUpdateActionTypeToRequestTypeMap.get(dto.getUpdateType()), task.getType());
        assertNotNull(task.getAccount());
        assertEquals(dto.getAccountIdentifier(), task.getAccount().getIdentifier());
        assertEquals(currentUser, task.getInitiatedBy());
        assertEquals(authorizedRepresentative, task.getUser());
        assertNotNull(task.getInitiatedDate());
        assertEquals(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, task.getStatus());
        String actionInJson = task.getDifference();
        assertNotNull(actionInJson);
        ARUpdateAction action = new ObjectMapper().readValue(actionInJson, ARUpdateAction.class);
        assertEquals(dto.getCandidate().getUrid(), action.getUrid());
        if (dto.getReplacee() == null) {
            assertNull(action.getToBeReplacedUrid());
        } else {
            assertEquals(dto.getReplacee().getUrid(), action.getToBeReplacedUrid());
        }
        if (dto.getCandidate().getRight() == null) {
            assertNull(action.getAccountAccessRight());
        } else {
            assertEquals(dto.getCandidate().getRight(), action.getAccountAccessRight().name());
        }

        then(applicationEventPublisher).should(times(2)).publishEvent(publishEventCaptor.capture());
        List<Object> capturedEvents = publishEventCaptor.getAllValues();
        DomainEvent domainEventFirst =
            (DomainEvent) capturedEvents.get(0);
        Assert.assertEquals(currentUser.getUrid(), domainEventFirst.getWho());
        assertNotNull(domainEventFirst.getDescription());
    }

    @Test
    @DisplayName("Calculate AR counters without account transfer task.")
    void calculateAuthoriseRepresentativeCounterWithoutTransfer() {
        // given
        long accountIdentifier = 123456L;
        AccountAccess active = buildAccountAccess(1L, "urid1", AccountAccessState.ACTIVE);
        AccountAccess removed = buildAccountAccess(2L, "urid2", AccountAccessState.REMOVED);
        List<AccountAccess> accountAccesses = List.of(active, removed);
        when(accountAccessRepository.finARsByAccount_Identifier(accountIdentifier))
            .thenReturn(accountAccesses);

        QTask task = QTask.task;
        when(taskRepository.findAll(task.account.identifier.eq(accountIdentifier)
            .and(task.type.in(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
                RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
                RequestType.ACCOUNT_TRANSFER))
            .and(task.status.eq(RequestStateEnum.APPROVED))))
            .thenReturn(List.of(buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, removed.getUser(), new Date())));

        when(taskRepository.findAll(task.user.id.in(Set.of(1L, 2L))
            .and(task.type.in(RequestType.USER_DEACTIVATION_REQUEST))
            .and(task.status.eq(RequestStateEnum.APPROVED))))
            .thenReturn(List.of(buildTask(RequestType.USER_DEACTIVATION_REQUEST, removed.getUser(), new Date())));

        // when
        Map<RequestType, Integer> result = service.calculateCounters(accountIdentifier);

        // then
        Assertions.assertEquals(2, result.get(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST));
        Assertions.assertEquals(1, result.get(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST));
    }

    @Test
    @DisplayName("Calculate AR counters with account transfer task.")
    void calculateAuthoriseRepresentativeCounterWithTransfer() {
        // given
        long accountIdentifier = 123456L;
        AccountAccess removed1 = buildAccountAccess(1L, "urid1", AccountAccessState.REMOVED);
        AccountAccess removed2 = buildAccountAccess(2L, "urid2", AccountAccessState.REMOVED);
        AccountAccess newActive = buildAccountAccess(3L, "urid3", AccountAccessState.ACTIVE);
        List<AccountAccess> accountAccesses = List.of(removed1, removed2, newActive);
        when(accountAccessRepository.finARsByAccount_Identifier(accountIdentifier))
            .thenReturn(accountAccesses);

        Date now = new Date();
        Date afterNow = new Date(now.getTime() + 1);
        Task task1 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, removed2.getUser(), now);
        Task task2 = buildTask(RequestType.ACCOUNT_TRANSFER, null, now);
        Task task3 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, newActive.getUser(), afterNow);

        QTask task = QTask.task;
        when(taskRepository.findAll(task.account.identifier.eq(accountIdentifier)
            .and(task.type.in(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
                RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
                RequestType.ACCOUNT_TRANSFER))
            .and(task.status.eq(RequestStateEnum.APPROVED))))
            .thenReturn(List.of(task1, task2, task3));

        when(taskRepository.findAll(task.user.id.in(Set.of(1L, 2L, 3L))
            .and(task.type.in(RequestType.USER_DEACTIVATION_REQUEST))
            .and(task.status.eq(RequestStateEnum.APPROVED))))
            .thenReturn(List.of(buildTask(RequestType.USER_DEACTIVATION_REQUEST, removed2.getUser(), afterNow)));

        // when
        Map<RequestType, Integer> result = service.calculateCounters(accountIdentifier);

        // then
        Assertions.assertEquals(1, result.get(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST));
        Assertions.assertNull(result.get(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST));
    }

    @Test
    @DisplayName("Calculate original ARs of an account.")
    void calculateOriginalAuthorisedRepresentatives() {
        // given
        long accountIdentifier = 123456L;
        AccountAccess originalAR1 = buildAccountAccess(1L, "urid1", AccountAccessState.REMOVED);
        AccountAccess originalAR2 = buildAccountAccess(2L, "urid2", AccountAccessState.ACTIVE);
        AccountAccess originalAR3 = buildAccountAccess(3L, "urid3", AccountAccessState.ACTIVE);
        AccountAccess originalAR4 = buildAccountAccess(4L, "urid4", AccountAccessState.ACTIVE);
        AccountAccess newAR = buildAccountAccess(5L, "urid5", AccountAccessState.ACTIVE);

        List<AccountAccess> accountAccesses = List.of(originalAR1, originalAR2, originalAR3, originalAR4, newAR);
        when(accountAccessRepository.finARsByAccount_Identifier(accountIdentifier))
            .thenReturn(accountAccesses);

        Date now = new Date();
        Date beforeNow = new Date(now.getTime() - 1);
        Date afterNow = new Date(now.getTime() + 1);
        String task2Difference = "{\"toBeReplacedUrid\":\"urid3\"}";
        ARUpdateAction updateAction = new ARUpdateAction();
        updateAction.setToBeReplacedUrid("urid3");
        when(mapper.convertToPojo(task2Difference, ARUpdateAction.class))
            .thenReturn(updateAction);

        Task task1 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, originalAR2.getUser(), beforeNow);
        Task task2 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, newAR.getUser(), beforeNow);
        task2.setDifference(task2Difference);
        Task task3 = buildTask(RequestType.ACCOUNT_TRANSFER, null, now);
        Task task4 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, newAR.getUser(), afterNow);
        Task task5 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, originalAR2.getUser(), afterNow);
        Task task6 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, originalAR3.getUser(), afterNow);
        Task task7 = buildTask(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, originalAR4.getUser(), afterNow);

        QTask task = QTask.task;
        when(taskRepository.findAll(task.account.identifier.eq(accountIdentifier)
            .and(task.type.in(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
                RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
                RequestType.ACCOUNT_TRANSFER))
            .and(task.status.eq(RequestStateEnum.APPROVED))))
            .thenReturn(List.of(task1, task2, task3, task4, task5, task6, task7));

        when(taskRepository.findAll(task.user.id.in(Set.of(1L, 2L, 3L, 4L, 5L))
            .and(task.type.in(RequestType.USER_DEACTIVATION_REQUEST))
            .and(task.status.eq(RequestStateEnum.APPROVED))))
            .thenReturn(List.of(buildTask(RequestType.USER_DEACTIVATION_REQUEST, originalAR1.getUser(), afterNow)));

        // when
        List<User> originalARs = service.calculateOriginalARs(accountIdentifier);

        // then
        Assertions.assertEquals(List.of("urid1", "urid2", "urid3"), originalARs.stream().map(User::getUrid).toList());
    }

    private AccountAccess buildAccountAccess(long userId, String urid, AccountAccessState state) {
        AccountAccess accountAccess = new AccountAccess();
        User user = new User();
        user.setId(userId);
        user.setUrid(urid);
        accountAccess.setUser(user);
        accountAccess.setState(state);

        return accountAccess;
    }

    private Task buildTask(RequestType requestType, User user, Date completedDate) {
        Task task = new Task();
        task.setType(requestType);
        task.setCompletedDate(completedDate);
        task.setUser(user);

        return task;
    }


    private AccountAccess mockAccountAccess(MockAccountAccessCommand command) {
        AccountAccess accountAccess = new AccountAccess();
        User user = new User();
        user.setUrid(command.urid);
        user.setFirstName(command.firstName);
        user.setLastName(command.lastName);
        user.setState(command.userStatus);
        accountAccess.setUser(user);
        accountAccess.setRight(command.right);
        accountAccess.setState(command.accountAccessState);
        return accountAccess;
    }

    private ARUpdateAction mockARUpdateAction(MockARUpdateActionCommand command) {
        ARUpdateAction arUpdateAction = new ARUpdateAction();
        arUpdateAction.setType(command.type);
        arUpdateAction.setUrid(command.authorizedRepresentativeUrid);
        arUpdateAction.setAccountAccessRight(command.accountAccessRight);
        arUpdateAction.setToBeReplacedUrid(command.replaceeUrid);

        return arUpdateAction;
    }

    private UserWorkContact mockPersonalUserInfo(MockPersonalUserInfoCommand command) {
        UserWorkContact personalUserInfo = new UserWorkContact();
        personalUserInfo.setUrid(command.urid);
        personalUserInfo.setFirstName(command.firstName);
        personalUserInfo.setLastName(command.lastName);
        personalUserInfo.setWorkBuildingAndStreet(command.workBuildingAndStreet);
        personalUserInfo.setWorkBuildingAndStreetOptional2(command.workBuildingAndStreetOptional2);
        personalUserInfo.setWorkBuildingAndStreetOptional(command.workBuildingAndStreetOptional);
        personalUserInfo.setWorkCountry(command.workCountry);
        personalUserInfo.setWorkCountryCode(command.workCountryCode);
        personalUserInfo.setWorkTownOrCity(command.workTownOrCity);
        personalUserInfo.setWorkStateOrProvince(command.workStateOrProvince);
        personalUserInfo.setEmail(command.workEmailAddress);
        personalUserInfo.setWorkPostCode(command.workPostCode);
        personalUserInfo.setWorkPhoneNumber(command.workPhoneNumber);
        return personalUserInfo;
    }

    private void verifyWorkContactDetails(MockPersonalUserInfoCommand expected,
                                          WorkContactDetailsDTO workContactDetailsDTO) {
        assertNotNull(workContactDetailsDTO);
        assertEquals(expected.workBuildingAndStreet, workContactDetailsDTO.getWorkBuildingAndStreet());
        assertEquals(expected.workBuildingAndStreetOptional, workContactDetailsDTO.getWorkBuildingAndStreetOptional());
        assertEquals(expected.workBuildingAndStreetOptional2,
            workContactDetailsDTO.getWorkBuildingAndStreetOptional2());
        assertEquals(expected.workTownOrCity, workContactDetailsDTO.getWorkTownOrCity());
        assertEquals(expected.workStateOrProvince, workContactDetailsDTO.getWorkStateOrProvince());
        assertEquals(expected.workCountry, workContactDetailsDTO.getWorkCountry());
        assertEquals(expected.workCountryCode, workContactDetailsDTO.getWorkCountryCode());
        assertEquals(expected.workEmailAddress, workContactDetailsDTO.getWorkEmailAddress());
        assertEquals(expected.workPhoneNumber, workContactDetailsDTO.getWorkPhoneNumber());
        assertEquals(expected.workPostCode, workContactDetailsDTO.getWorkPostCode());
    }

    @Builder
    private static class MockAccountAccessCommand {
        private AccountAccessState accountAccessState;
        private String urid;
        private String firstName;
        private String lastName;
        private AccountAccessRight right;
        private UserStatus userStatus;
    }

    @Builder
    private static class MockARUpdateActionCommand {
        private long requestId;
        private ARUpdateActionType type;
        private String authorizedRepresentativeUrid;
        private String replaceeUrid;
        private AccountAccessRight accountAccessRight;
    }

    @Builder
    private static class MockPersonalUserInfoCommand {
        private String urid;
        private String firstName;
        private String lastName;
        private String workBuildingAndStreet;
        private String workBuildingAndStreetOptional;
        private String workBuildingAndStreetOptional2;
        private String workPostCode;
        private String workTownOrCity;
        private String workStateOrProvince;
        private String workCountry;
        private String workCountryCode;
        private String workPhoneNumber;
        private String workEmailAddress;
    }

    private AccountAccess retrieveAccountAccessInfo(AccountAccessState state,
                                                    AccountAccessRight right) {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setState(state);
        accountAccess.setRight(right);
        return accountAccess;
    }
}
