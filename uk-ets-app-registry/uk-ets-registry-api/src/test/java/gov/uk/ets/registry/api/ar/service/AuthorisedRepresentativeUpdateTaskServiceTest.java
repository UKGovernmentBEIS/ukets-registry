package gov.uk.ets.registry.api.ar.service;

import static gov.uk.ets.registry.api.account.domain.types.AccountAccessRight.INITIATE_AND_APPROVE;
import static gov.uk.ets.registry.api.ar.domain.ARUpdateActionType.ADD;
import static gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome.APPROVED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.authz.AccountAuthorizationServiceImpl;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.ContactDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.publication.PublicationRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsTaskService;
import gov.uk.ets.registry.api.task.domain.TaskARStatus;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.printenrolmentletter.PrintEnrolmentLetterTaskService;
import gov.uk.ets.registry.api.task.repository.TaskARStatusRepository;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.AuthoriseRepresentativeTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.KeycloakUser;
import gov.uk.ets.registry.api.user.UserConversionService;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthorisedRepresentativeUpdateTaskServiceTest {


    private static final String ADD_AR =
        "{\"type\":\"ADD\",\"urid\":\"UK566650437068\",\"accountAccessRight\":\"INITIATE_AND_APPROVE\"}";
    private static final String REMOVE_AR = "{\"type\":\"REMOVE\",\"urid\":\"UK566650437068\"}";
    private static final String SUSPEND_AR = "{\"type\":\"SUSPEND\",\"urid\":\"UK566650437068\"}";
    private static final String CHANGE_ACCESS_RIGHTS_AR =
        "{\"type\":\"CHANGE_ACCESS_RIGHTS\",\"urid\":\"UK566650437068\",\"accountAccessRight\":\"READ_ONLY\"}";
    private static final String restoreAR = "{\"type\":\"RESTORE\",\"urid\":\"UK566650437068\"}";
    private static final String replaceRepresentative =
        "{\"type\":\"REPLACE\",\"urid\":\"UK405681794859\",\"toBeReplacedUrid\":\"UK566650437068\",\"accountAccessRight\":\"READ_ONLY\"}";

    @Mock
    private AccountService accountService;
    @Mock
    private UserConversionService userConversionService;
    @Mock
    private UserAdministrationService userAdministrationService;
    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    @Mock
    private AccountAccessRepository accountAccessRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserService userService;
    @Mock
    private PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService;
    @Mock
    AccountAuthorizationServiceImpl accountAuthorizationService;
    @Mock
    private UserStatusService userStateService;
    @Mock
    private ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    @Mock
    private PublicationRequestAddRemoveRoleService publicationRequestAddRemoveRoleService;
    @Mock
    private RequestedDocsTaskService requestedDocsTaskService;
    @Mock
    private Mapper mapper;
    @Mock
    private TaskARStatusRepository taskARStatusRepository;

    ObjectMapper jacksonMapper = new ObjectMapper();

    @InjectMocks
    private AuthorizedRepresentativeService authorizedRepresentativeService;

    AuthorisedRepresentativeUpdateTaskService authorisedRepresentativeUpdateTaskService;

    AccountInfo accountInfo;
    User user;
    Account account;

    @BeforeEach
    public void setup() {
        authorisedRepresentativeUpdateTaskService = new AuthorisedRepresentativeUpdateTaskService(accountService,
            userConversionService, userAdministrationService, accountAccessRepository, userService, taskRepository,
            authorizedRepresentativeService, userStateService, requestedDocsTaskService, mapper, taskARStatusRepository);
    }

    @DisplayName("Retrieve authorise representative update values successfully.")
    @Test
    void deserializeAuthoriseRepresentativeRequest() throws JsonProcessingException {
        String diff = "{\"type\":\"ADD\",\"urid\":\"UK566650437068\",\"accountAccessRight\":\"INITIATE_AND_APPROVE\"}";
        ARUpdateAction dto =
            new ObjectMapper().readValue(diff, ARUpdateAction.class);
        Assertions.assertEquals(INITIATE_AND_APPROVE, dto.getAccountAccessRight());
        Assertions.assertEquals("UK566650437068", dto.getUrid());
        Assertions.assertEquals(ADD, dto.getType());
    }

    @DisplayName("In case of Approval initial state of the rules should change " +
        "but on Rejection the initial state should not change.")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {1} - {0} - AR State {3}")
    void testAuthoriseRepresentativeUpdateInCaseOfApprovalOrRejection(TaskOutcome outcome, RequestType requestType,
                                                                      String difference, UserStatus arStatus)
        throws JsonProcessingException {

        createMocks(arStatus);

        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(requestType);
        taskDetailsDTO.setDifference(difference);
        taskDetailsDTO.setAccountNumber(Objects.toString(accountInfo.getIdentifier(), null));

         when(mapper.convertToPojo(taskDetailsDTO.getDifference(), ARUpdateAction.class))
            .thenReturn(jacksonMapper.readValue(taskDetailsDTO.getDifference(), ARUpdateAction.class));

        if (RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST.equals(requestType)) {
            User newAuthRep = new User();
            newAuthRep.setUrid("UK405681794859");
            newAuthRep.setIamIdentifier("56a496d2-aaaa-4444-aaaa-078e76203dc6");
            newAuthRep.setState(arStatus);
            newAuthRep.setFirstName("Current rep Name");
            newAuthRep.setLastName("Current rep lastName");
            when(userService.getUserByUrid("UK405681794859"))
                .thenReturn(newAuthRep);

            UserDTO newUserDTO = new UserDTO();
            newUserDTO.setUrid(newAuthRep.getUrid());
            newUserDTO.setKeycloakId(newAuthRep.getIamIdentifier());
            newUserDTO.setStatus(newAuthRep.getState());
            newUserDTO.setFirstName(newAuthRep.getFirstName());
            newUserDTO.setLastName(newAuthRep.getLastName());
            when(userConversionService.convert(newAuthRep))
                .thenReturn(newUserDTO);

            ContactDTO newUserContact = new ContactDTO();
            when(userAdministrationService.findWorkContactDetailsByIamId(newUserDTO.getKeycloakId()))
                .thenReturn(newUserContact);

            when(userService.getUserByUrid(newAuthRep.getUrid())).thenReturn(newAuthRep);
        }

        AuthoriseRepresentativeTaskDetailsDTO dto =
            authorisedRepresentativeUpdateTaskService.getDetails(taskDetailsDTO);
        authorisedRepresentativeUpdateTaskService.complete(dto, outcome, null);

        if (APPROVED.equals(outcome)
            && requestType.equals(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST)) {
            Assertions.assertEquals(1, account.getAccountAccesses().size());
            Assertions
                .assertEquals(INITIATE_AND_APPROVE, account.getAccountAccesses().get(0).getRight());
            Assertions.assertEquals(AccountAccessState.ACTIVE, account.getAccountAccesses().get(0).getState());
        } else if (APPROVED.equals(outcome)
            && requestType.equals(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST)) {
            Assertions.assertEquals(1, account.getAccountAccesses().size());
            Assertions.assertEquals(AccountAccessRight.APPROVE, account.getAccountAccesses().get(0).getRight());
            Assertions.assertEquals(AccountAccessState.REMOVED, account.getAccountAccesses().get(0).getState());
        } else if (APPROVED.equals(outcome)
            && requestType.equals(RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST)) {
            Assertions.assertEquals(1, account.getAccountAccesses().size());
            Assertions.assertEquals(AccountAccessRight.APPROVE, account.getAccountAccesses().get(0).getRight());
            Assertions.assertEquals(AccountAccessState.SUSPENDED, account.getAccountAccesses().get(0).getState());
        } else if (APPROVED.equals(outcome)
            && requestType.equals(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST)) {
            Assertions.assertEquals(1, account.getAccountAccesses().size());
            Assertions.assertEquals(AccountAccessRight.READ_ONLY, account.getAccountAccesses().get(0).getRight());
            Assertions.assertEquals(AccountAccessState.REQUESTED, account.getAccountAccesses().get(0).getState());
        } else if (APPROVED.equals(outcome)
            && requestType.equals(RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST)) {
            Assertions.assertEquals(1, account.getAccountAccesses().size());
            Assertions.assertEquals(AccountAccessRight.APPROVE, account.getAccountAccesses().get(0).getRight());
            Assertions.assertEquals(AccountAccessState.ACTIVE, account.getAccountAccesses().get(0).getState());
        } else if (APPROVED.equals(outcome)
            && requestType.equals(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST)) {
            Assertions.assertEquals(1, account.getAccountAccesses().size());
            Assertions.assertEquals(AccountAccessRight.APPROVE, account.getAccountAccesses().get(0).getRight());
            Assertions.assertEquals(AccountAccessState.REMOVED, account.getAccountAccesses().get(0).getState());
            verify(accountAccessRepository, Mockito.times(1)).save(any());
        } else {
            Assertions.assertEquals(AccountAccessState.REQUESTED, account.getAccountAccesses().get(0).getState());
            Assertions.assertEquals(user, account.getAccountAccesses().get(0).getUser());
        }
    }

    @Test
    void shouldRemoveKeycloakRoleWhenUserNotArInOtherAccounts() throws JsonProcessingException {
        createMocks(UserStatus.VALIDATED);
        account.getAccountAccesses().add(createAccountAccess(1L, AccountAccessState.ACTIVE));

        TaskDetailsDTO taskDetailsDTO = new TaskDetailsDTO();
        taskDetailsDTO.setTaskType(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST);
        taskDetailsDTO.setDifference(REMOVE_AR);
        taskDetailsDTO.setReferredUserURID("UK12345");
        taskDetailsDTO.setAccountNumber(Objects.toString(accountInfo.getIdentifier(), null));

        when(mapper.convertToPojo(taskDetailsDTO.getDifference(), ARUpdateAction.class))
            .thenReturn(jacksonMapper.readValue(taskDetailsDTO.getDifference(), ARUpdateAction.class));

        UserRepresentation userRepresentation = new UserRepresentation();
        KeycloakUser keycloakUser = new KeycloakUser(userRepresentation);
        when(userStateService.getKeycloakUser(taskDetailsDTO.getReferredUserURID())).thenReturn(keycloakUser);
        AuthoriseRepresentativeTaskDetailsDTO dto =
            authorisedRepresentativeUpdateTaskService.getDetails(taskDetailsDTO);

        when(accountAccessRepository.findByUser_Urid(user.getUrid())).thenReturn(account.getAccountAccesses());

        authorisedRepresentativeUpdateTaskService.complete(dto, APPROVED, null);
        verify(serviceAccountAuthorizationService, times(1))
            .removeUserRole(user.getIamIdentifier(), UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral());

        verify(reportRequestAddRemoveRoleService, times(1))
            .requestReportsApiRemoveRole(user.getIamIdentifier());

        verify(publicationRequestAddRemoveRoleService, times(1))
                .requestPublicationApiRemoveRole(user.getIamIdentifier());
    }


    static Stream<Arguments> getArguments() {

        List<UserStatus> userStatuses = new ArrayList<>();
        userStatuses.add(UserStatus.REGISTERED);
        userStatuses.add(UserStatus.ENROLLED);
        return Stream.of(TaskOutcome.values()).flatMap(outcome ->
            userStatuses.stream().map(status ->
                Stream.of(
                    Arguments.of(outcome, RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, ADD_AR, status),
                    Arguments.of(outcome, RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, REMOVE_AR, status),
                    Arguments.of(outcome, RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST,
                        CHANGE_ACCESS_RIGHTS_AR, status),
                    Arguments
                        .of(outcome, RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, replaceRepresentative,
                            status)
                )
            )
        ).flatMap(Function.identity());
    }

    private void createMocks(UserStatus arStatus) {
        accountInfo = AccountInfo.builder()
            .accountHolderName("Account Holder Name")
            .accountName("Account Name")
            .fullIdentifier("GB-100-1004-1-89")
            .identifier(1004L)
            .registryCode("GB")
            .build();
        when(accountService.getAccountInfo(1004L))
            .thenReturn(accountInfo);

        user = new User();
        user.setUrid("UK566650437068");
        user.setIamIdentifier("56a496d2-ab9b-460c-a376-078e76203dc6");
        user.setState(arStatus);
        user.setFirstName("First Name");
        user.setLastName("Last Name");
        when(userService.getUserByUrid("UK566650437068"))
            .thenReturn(user);

        User currentUser = new User();
        currentUser.setUrid("UK766630437065");
        currentUser.setFirstName("Current First Name");
        currentUser.setLastName("Current Last Name");
        when(userService.getCurrentUser()).thenReturn(currentUser);

        when(userService.getUserByUrid(user.getUrid())).thenReturn(user);

        account = new Account();
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setTransfersToAccountsNotOnTheTrustedListAreAllowed(false);
        account.setIdentifier(accountInfo.getIdentifier());
        account.setFullIdentifier(accountInfo.getFullIdentifier());
        account.setRegistryCode(accountInfo.getRegistryCode());
        List<AccountAccess> initialAccountAccesses = new ArrayList<>();
        AccountAccess accountAccess1 = createAccountAccess(1L, AccountAccessState.REQUESTED);
        initialAccountAccesses.add(accountAccess1);
        account.setAccountAccesses(initialAccountAccesses);
        when(accountService.getAccount(1004L))
            .thenReturn(account);

        UserDTO userDTO = new UserDTO();
        userDTO.setUrid(user.getUrid());
        userDTO.setKeycloakId(user.getIamIdentifier());
        userDTO.setStatus(user.getState());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        when(userConversionService.convert(user))
            .thenReturn(userDTO);

        ContactDTO workContact = new ContactDTO();
        when(userAdministrationService.findWorkContactDetailsByIamId(userDTO.getKeycloakId()))
            .thenReturn(workContact);
    }

    @NotNull
    private AccountAccess createAccountAccess(long id, AccountAccessState state) {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setId(id);
        accountAccess.setState(state);
        accountAccess.setUser(user);
        accountAccess.setRight(AccountAccessRight.APPROVE);
        accountAccess.setAccount(account);
        return accountAccess;
    }
}
