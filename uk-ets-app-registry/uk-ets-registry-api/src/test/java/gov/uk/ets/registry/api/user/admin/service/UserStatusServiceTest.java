package gov.uk.ets.registry.api.user.admin.service;

import static gov.uk.ets.registry.api.user.UserServiceTest.ENROLMENT_KEY_EXPIRATION_DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.UserFileRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.printenrolmentletter.PrintEnrolmentLetterTaskService;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.UserActionError;
import gov.uk.ets.registry.api.user.UserActionException;
import gov.uk.ets.registry.api.user.UserConversionService;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import gov.uk.ets.registry.api.user.admin.shared.UserStatusAction;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusActionOptionDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.AccessToken;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author fragkise
 */
@DisplayName("Tests User Change Status functionality")
class UserStatusServiceTest {

    private static final String TEST_TOKEN = "fds$uadsgrf8gd$&f634tuh8dbfv8j";
    private static final String TEST_URID = "UK12345678";
    @Mock
    private UserRepository userRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UserAdministrationService userAdministrationService;

    @Mock
    private PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService;

    @Mock
    private UserConversionService userConversionService;

    @Mock
    private AuthorizationService authorizationService;
    
    @Mock
    private PersistenceService persistenceService;

    @Mock
    private UserWorkContactRepository userWorkContactRepository;

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;
    
    @Mock
    private ARUpdateActionRepository arUpdateActionRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskEventService taskEventService;

    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Mock
    private ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    
    @Mock
    private UserFileRepository userFileRepository;

    @Mock
    private Mapper mapper;

    private UserService userService;

    private UserStatusService userStatusService;

    private User admin;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        AccessToken token = new AccessToken();
        token.setSubject("admin_subject");
        when(authorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn("admin_subject");
        admin = new User();
        when(userRepository.findByIamIdentifier(token.getSubject())).thenReturn(admin);
        userService =
                new UserService(ENROLMENT_KEY_EXPIRATION_DAYS, userRepository, userConversionService, 
                        authorizationService, userAdministrationService, new UserGeneratorService(), 
                        userWorkContactRepository, eventService, persistenceService, uploadedFilesRepository,
                        userFileRepository, printEnrolmentLetterTaskService, taskRepository, taskEventService,
                        serviceAccountAuthorizationService, reportRequestAddRemoveRoleService, arUpdateActionRepository, mapper);
        userStatusService = new UserStatusService(userRepository, eventService, userAdministrationService,
                printEnrolmentLetterTaskService, userService, authorizationService);
    }

    @Test
    @DisplayName("User with status REGISTERED should have 2 options , expected to succeed")
    void changeUserStatusOptionsWithRegisteredUser() {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(UserStatus.REGISTERED);
        user.setPreviousState(null);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);

        List<UserStatusActionOptionDTO> result = userStatusService.getUserStatusAvailableActions(user.getUrid());
        result.sort(Comparator.comparing(UserStatusActionOptionDTO::getValue));

        assertEquals(2, result.size());
        assertEquals(UserStatusAction.VALIDATE, result.get(0).getValue());
        assertEquals(UserStatus.VALIDATED, result.get(0).getNewStatus());
        assertEquals("A print letter with registry activation code task will be created", result.get(0).getMessage());

        assertEquals(UserStatusAction.SUSPEND, result.get(1).getValue());
        assertEquals(UserStatus.SUSPENDED, result.get(1).getNewStatus());
        assertNull(result.get(1).getMessage());
    }

    @DisplayName("User with status SUSPENDED should have 1 option , expected to succeed.")
    @ParameterizedTest(name = "#{index} - SUSPENDED user should revert to {0}")
    @EnumSource(value = UserStatus.class, names = {"REGISTERED", "VALIDATED", "ENROLLED"})
    void changeUserStatusOptionsWithSuspendedUser(UserStatus status) {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(UserStatus.SUSPENDED);
        user.setPreviousState(status);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);

        List<UserStatusActionOptionDTO> result = userStatusService.getUserStatusAvailableActions(user.getUrid());
        result.sort(Comparator.comparing(UserStatusActionOptionDTO::getValue));


        assertEquals(UserStatusAction.RESTORE, result.get(0).getValue());
        assertEquals(result.get(0).getNewStatus(), status);
        assertNull(result.get(0).getMessage());
    }

    @DisplayName("User with status SUSPENDED and empty previous status throws UserException.")
    @Test
    void changeUserStatusOptionsWithSuspendedUserAndEmptyPreviousStatus() {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(UserStatus.SUSPENDED);
        user.setPreviousState(null);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);

        final String urid = user.getUrid();

        UserActionException exception = Assertions.assertThrows(UserActionException.class, () ->
            userStatusService.getUserStatusAvailableActions(urid));
        assertEquals(UserActionError.PREVIOUS_USER_STATUS_UNDEFINED, exception.getUserActionError());
    }

    @DisplayName("Users with status VALIDATED,ENROLLED  should have only SUSPEND option.")
    @ParameterizedTest(name = "#{index} - {0} user should have only SUSPEND option")
    @EnumSource(value = UserStatus.class, names = {"VALIDATED", "ENROLLED"})
    void changeUserStatusOptions(UserStatus status) {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(status);
        user.setPreviousState(null);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);

        List<UserStatusActionOptionDTO> result = userStatusService.getUserStatusAvailableActions(user.getUrid());
        result.sort(Comparator.comparing(UserStatusActionOptionDTO::getValue));

        assertEquals(1, result.size());
        assertEquals(UserStatusAction.SUSPEND, result.get(0).getValue());
        assertEquals(UserStatus.SUSPENDED, result.get(0).getNewStatus());
        assertNull(result.get(0).getMessage());
    }

    @DisplayName("Change user status should modify the status property of User.")
    @ParameterizedTest(name = "#{index} - {0} user should be updated to {1}")
    @MethodSource("validCurrentStatusAndNewStatusProvider")
    void changeUserStatus(UserStatus currentStatus, UserStatus newStatus) {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(currentStatus);
        user.setPreviousState(null);
        //Task prineEnrollmentLetter
        Task task = new Task();
        task.setRequestId(789L);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        when(printEnrolmentLetterTaskService.create(user, admin, null)).thenReturn(task);
        UserStatusChangeDTO patch = new UserStatusChangeDTO();
        patch.setUrid(user.getUrid());
        patch.setUserStatus(newStatus);
        UserStatusChangeResultDTO result = userStatusService.changeUserStatus(patch);
        assertEquals(newStatus, result.getUserStatus());
        assertEquals(newStatus, user.getState());

        //The previous status must be stored
        assumingThat(UserStatus.SUSPENDED.equals(newStatus),
                () -> {
                    // perform these assertions only when testing SUSPENDED
                    assertEquals(currentStatus, user.getPreviousState());
                    assertTrue(result.getRequestId().isEmpty());
                });
    }

    @DisplayName("Change user status from REGISTERED to VALIDATED should create a task “Print letter with registry activation code” for the administrator.")
    @ParameterizedTest(name = "#{index} - {0} user should be updated to {1}")
    @MethodSource("validCurrentStatusAndNewStatusProvider")
    void changeUserStatusTaskCreation(UserStatus currentStatus, UserStatus newStatus) {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(currentStatus);
        user.setPreviousState(null);
        //Task prineEnrollmentLetter
        Task task = new Task();
        task.setRequestId(789L);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        when(printEnrolmentLetterTaskService.create(user, admin, null)).thenReturn(task);
        UserStatusChangeDTO patch = new UserStatusChangeDTO();
        patch.setUrid(user.getUrid());
        patch.setUserStatus(newStatus);
        UserStatusChangeResultDTO result = userStatusService.changeUserStatus(patch);

        //If the user status changes from REGISTERED to VALIDATED, the system also generates the
        //Registry Activation Code and creates a task “Print letter with registry activation code” for the administrator.
        assumingThat(UserStatus.REGISTERED.equals(currentStatus) && UserStatus.VALIDATED.equals(newStatus),
                () -> {
                    // perform these assertions only when testing REGISTER --> VALIDATED
                    assertNotNull(user.getEnrolmentKey());
                    assertNotNull(user.getEnrolmentKeyDate());
                    assertTrue(result.getRequestId().isPresent());
                });

        assumingThat(!UserStatus.REGISTERED.equals(currentStatus) || !UserStatus.VALIDATED.equals(newStatus),
                () -> {
                    // perform these assertions only when testing any other than REGISTER --> VALIDATED
                    assertFalse(result.getRequestId().isPresent());
                });
    }

    static Stream<Arguments> validCurrentStatusAndNewStatusProvider() {
        return Stream.of(
                Arguments.of(UserStatus.REGISTERED, UserStatus.VALIDATED),
                Arguments.of(UserStatus.REGISTERED, UserStatus.SUSPENDED),
                Arguments.of(UserStatus.VALIDATED, UserStatus.SUSPENDED),
                Arguments.of(UserStatus.ENROLLED, UserStatus.SUSPENDED),
                Arguments.of(UserStatus.SUSPENDED, UserStatus.REGISTERED),
                Arguments.of(UserStatus.SUSPENDED, UserStatus.VALIDATED),
                Arguments.of(UserStatus.SUSPENDED, UserStatus.ENROLLED)
        );
    }


    @DisplayName("Invalid change user statues should fail.")
    @ParameterizedTest(name = "#{index} - {0} user should be updated to {1}")
    @MethodSource("invalidCurrentStatusAndNewStatusProvider")
    void changeUserStatusWithInvalidStatusShouldThrowException(UserStatus currentStatus, UserStatus newStatus) {
        // Prepare user
        User user = new User();
        user.setUrid("UK835132560077");
        user.setState(currentStatus);
        user.setPreviousState(null);
        // Mock userRepository
        when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        UserStatusChangeDTO patch = new UserStatusChangeDTO();
        patch.setUrid(user.getUrid());
        patch.setUserStatus(newStatus);

        UserActionException exception = Assertions.assertThrows(UserActionException.class, () -> {
            userStatusService.changeUserStatus(patch);
        });
        assertEquals(UserActionError.INVALID_USER_STATUS_TRANSITION, exception.getUserActionError());


    }

    @Test
    void shouldCreateEventWithSameUserAndSpecificWhatFieldWhenSystemAction() {
        UserStatusChangeDTO patch = new UserStatusChangeDTO();
        patch.setUserStatus(UserStatus.SUSPENDED);
        patch.setUrid(TEST_URID);
        User user = new User();
        user.setUrid(TEST_URID);
        user.setState(UserStatus.REGISTERED);
        when(userRepository.findByUrid(TEST_URID)).thenReturn(user);

        userStatusService.changeUserStatus(patch, TEST_TOKEN);

        verify(eventService, times(1))
                .createAndPublishEvent(TEST_URID, TEST_URID, UserStatus.SUSPENDED.name(),
                        EventType.USER_STATUS_CHANGED, "Change user status (initiated by system)");
    }

    static Stream<Arguments> invalidCurrentStatusAndNewStatusProvider() {
        return Stream.of(
                Arguments.of(UserStatus.REGISTERED, UserStatus.REGISTERED),
                Arguments.of(UserStatus.REGISTERED, UserStatus.ENROLLED),
                Arguments.of(UserStatus.VALIDATED, UserStatus.VALIDATED),
                Arguments.of(UserStatus.VALIDATED, UserStatus.REGISTERED),
                Arguments.of(UserStatus.VALIDATED, UserStatus.ENROLLED),
                Arguments.of(UserStatus.SUSPENDED, UserStatus.SUSPENDED)
        );
    }
}
