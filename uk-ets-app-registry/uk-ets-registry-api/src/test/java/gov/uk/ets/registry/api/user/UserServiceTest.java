package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionType;
import gov.uk.ets.registry.api.authz.AuthorizationServiceImpl;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.UserDetailsUtil;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.view.DateDTO;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.UserFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.UserFileRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.printenrolmentletter.PrintEnrolmentLetterTaskService;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.shared.UserDetailsUpdateType;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.domain.UserWorkContactRepository;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AuthorizationServiceException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    public static final int ENROLMENT_KEY_EXPIRATION_DAYS = 31;
    private static final List<UserStatus> statuses = List.of(UserStatus.SUSPENDED, UserStatus.UNENROLLED,
        UserStatus.UNENROLLEMENT_PENDING);
    private static final String TEST_EMAIL = "test@test.com";
    private static final String TEST_USER_ID = "asdasd-asfwe2-fwe--g-ewg";
    private static final String TEST_URID = "UK12345678";
    private static final String TEST_KNOWN_AS = "Test Known As name";
    
    private static final String ATTR_URID = "urid";
    private static final String ATTR_WORK_EMAIL = "workEmailAddress";
    private static final String ATTR_COUNTRY_OF_BIRTH = "countryOfBirth";
    private static final String ATTR_BIRTH_DATE = "birthDate";
    private static final String ATTR_ALSO_KNOWN_AS = "alsoKnownAs";

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserConversionService userConversionService;

    @Mock
    private AuthorizationServiceImpl authorizationService;

    @Mock
    private UserAdministrationService userAdministrationService;

    @Mock
    private UserGeneratorService userGeneratorService;

    @Mock
    private UserWorkContactRepository userWorkContactRepository;

    @Mock
    private EventService eventService;

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;
    
    @Mock
    private UserFileRepository userFileRepository;

    @Mock
    private PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService;
    
    @Mock
    private ARUpdateActionRepository arUpdateActionRepository;
   
    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private PersistenceService persistenceService;

    @Mock
    private TaskEventService taskEventService;

    @Mock
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Mock
    private Mapper mapper;

    @Mock
    ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        userService =
            new UserService(ENROLMENT_KEY_EXPIRATION_DAYS, userRepository, userConversionService, authorizationService,
                userAdministrationService,
                userGeneratorService, userWorkContactRepository, eventService, persistenceService, 
                uploadedFilesRepository, userFileRepository, printEnrolmentLetterTaskService, taskRepository, 
                taskEventService, serviceAccountAuthorizationService, reportRequestAddRemoveRoleService, arUpdateActionRepository,
                mapper);

        User user = new User();
        user.setUrid("");
        given(userRepository.findByIamIdentifier(anyString())).willReturn(user);
        AccessToken accessToken = new AccessToken();
        accessToken.setSubject("");
        given(authorizationService.getToken()).willReturn(accessToken);
        Task task = new Task();
        given(printEnrolmentLetterTaskService.create(user, user, null)).willReturn(task);
    }

    @Test
    void test_getUsersByName() {
        String searchTerm = "test";
        userService.getUsersByName(searchTerm);
        Mockito.verify(userRepository, times(1)).getUsersByNameStartingWith(searchTerm);
    }

    @Test
    @DisplayName("Retrieve the enrolment key details, expected to succeed.")
    void retrieveEnrolmentKeyDetails() {
        String urid = "UK123456";
        EnrolmentKeyDTO dto = new EnrolmentKeyDTO(urid, "XXXX-XXXX-XXXX-XXXX-XXXX", new Date());
        given(userRepository.getEnrolmentKeyDetails(urid)).willReturn(dto);

        EnrolmentKeyDTO result = userService.getEnrolmentKeyDetails(urid);
        assertEquals(dto, result);
    }

    @Test
    @DisplayName("Retrieve the enrolment key expiration date and check business rule (expires in 2 months), expected to fail.")
    void retrieveEnrolmentKeyExpirationDate_ExpectedToFail() {
        Date creationDate = new Date();
        Calendar expired = Calendar.getInstance();
        expired.setTime(creationDate);
        expired.add(Calendar.MONTH, 3);
        Date expirationDate = expired.getTime();

        Date result = userService.getEnrolmentKeyExpirationDate(creationDate);
        assertNotEquals(expirationDate, result);
    }

    @Test
    @DisplayName("Retrieve the enrolment key expiration date and check business rule (expires in 31 days), expected to succeed.")
    void retrieveEnrolmentKeyExpirationDate_ExpectedToSucceed() {
        Date creationDate = new Date();
        Calendar expired = Calendar.getInstance();
        expired.setTime(creationDate);
        expired.add(Calendar.DAY_OF_MONTH, ENROLMENT_KEY_EXPIRATION_DAYS);
        Date expirationDate = expired.getTime();

        Date result = userService.getEnrolmentKeyExpirationDate(creationDate);
        assertEquals(expirationDate, result);
    }

    @Test
    @DisplayName("Check URID format validity")
    void checkUridValidity() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUrid("123456");
        assertThrows(UserActionException.class, () -> userService.registerUser(userDTO));
    }

    @Test
    @DisplayName("Check User file list")
    void test_getUserFiles() {
        List<UserFileDTO> files = new ArrayList<>();
        UserFileDTO file1 = new UserFileDTO(1L, "Filename1", "Proof of identity", LocalDateTime.now());
        files.add(file1);
        when(userRepository.getUserFiles("UK12345")).thenReturn(files);

        List<UserFileDTO> userFiles = userService.getUserFiles("UK12345");
        assertEquals(1, userFiles.size());
        assertEquals(files.get(0), userFiles.get(0));

    }

    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - Initial User State {0}")
    @DisplayName("Validate User and Generate events only when the user state is REGISTERED.")
    void test_validateUserAndGenerateEvents(UserStatus status) {
        User registeredUser = new User();
        registeredUser.setUrid("UK123456");
        registeredUser.setState(status);

        User currentUser = new User();
        currentUser.setUrid("UK7891011");

        Task task = new Task();
        task.setRequestId(1000001L);

        UserStatusChangeResultDTO
            userValidated = userService.validateUserAndGenerateEvents(registeredUser, currentUser, task);

        int wantedNumberOfInvocations;
        if (UserStatus.REGISTERED.equals(status)) {
            Assertions.assertNotNull(userValidated);
            wantedNumberOfInvocations = 1;
        } else {
            Assertions.assertNull(userValidated);
            wantedNumberOfInvocations = 0;
        }

        String action = "User validated";
        then(eventService).should(times(wantedNumberOfInvocations))
            .createAndPublishEvent(registeredUser.getUrid(), currentUser.getUrid(),
                "", EventType.USER_VALIDATED, action);
        action = "Registry activation code issued";
        then(eventService).should(times(wantedNumberOfInvocations))
            .createAndPublishEvent(registeredUser.getUrid(), currentUser.getUrid(),
                "", EventType.REGISTRY_ACTIVATION_CODE, action);
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(UserStatus.values()).map(Arguments::of);
    }

    @Test
    void shouldThrowWhenRequestNewRegistryCodeAndExpirationDateIsSameDay() {
        LocalDate localDateCreated = LocalDate.now().minusDays(ENROLMENT_KEY_EXPIRATION_DAYS);
        given(userRepository.getEnrolmentKeyDetails(anyString()))
            .willReturn(generateEnrolmentKeyDTO(localDateCreated));

        assertDoesNotThrow(() -> userService.requestNewRegistryActivationCode());
    }

    @Test
    void shouldNotThrowWhenRequestNewRegistryCodeAndExpirationDateHasPassed() {
        LocalDate localDateCreated = LocalDate.now().minusDays(ENROLMENT_KEY_EXPIRATION_DAYS).minusDays(1);
        given(userRepository.getEnrolmentKeyDetails(anyString()))
            .willReturn(generateEnrolmentKeyDTO(localDateCreated));

        assertDoesNotThrow(() -> userService.requestNewRegistryActivationCode());
    }

    @ParameterizedTest
    @MethodSource("provideFindByEmailTestData")
    void should(String email, UserStatus userStatus, List<UserStatus> excludedStatusList,
                       boolean shouldFindUser) {
        UserRepresentation ur = new UserRepresentation();
        ur.setId(TEST_USER_ID);
        given(userAdministrationService.findByEmail(anyString())).willAnswer(
            invocation -> {
                String emailArgument = (String) invocation.getArguments()[0];
                if (emailArgument.equals(TEST_EMAIL)) {
                    return Optional.of(ur);
                }
                return Optional.empty();
            }
        );
        User user = new User();
        user.setIamIdentifier(TEST_USER_ID);
        user.setState(userStatus);
        given(userRepository.findByIamIdentifier(TEST_USER_ID)).willReturn(user);
        Optional<User> foundUser =
            userService.findByEmailNotInStatuses(email, excludedStatusList.toArray(UserStatus[]::new));


        if (shouldFindUser) {
            assertThat(foundUser).isPresent();
            assertThat(foundUser.get().getIamIdentifier()).isEqualTo(TEST_USER_ID);
        } else {
            assertThat(foundUser).isNotPresent();
        }
    }

    private EnrolmentKeyDTO generateEnrolmentKeyDTO(LocalDate localDateCreated) {
        Date created = Date.from(localDateCreated.atStartOfDay(
            ZoneId.systemDefault()).toInstant());
        return new EnrolmentKeyDTO("", "", created);
    }


    private static Stream<Arguments> provideFindByEmailTestData() {
        return Stream.of(
            Arguments.of(TEST_EMAIL, UserStatus.REGISTERED, statuses, true),
            Arguments.of(TEST_EMAIL, UserStatus.SUSPENDED, statuses, false),
            Arguments.of("not_registered@test.com", UserStatus.REGISTERED, statuses, false),
            Arguments.of("not_registered@test.com", UserStatus.REGISTERED, new ArrayList<>(), false),
            Arguments.of(TEST_EMAIL, UserStatus.REGISTERED, new ArrayList<>(), true)
        );
    }

    @Test
    void testEnrolUser() {
        User user = new User();
        user.setUrid("UK802061511788");
        user.setEnrolmentKey("A4QE-KAMH-XEX8-4R43-D3RB");
        when(userRepository.findByIamIdentifier(any())).thenReturn(user);

        when(userRepository.getEnrolmentKeyDetails(anyString()))
            .thenReturn(generateEnrolmentKeyDTO(
                LocalDate.now().minusDays(ENROLMENT_KEY_EXPIRATION_DAYS)));

        userService.enrolUser("A4QE-KAMH-XEX8-4R43-D3RB");
        userService.enrolUser("A4QE-kamh-xex8-4r43-d3rb");
        userService.enrolUser("a4qe-KAMH-XEX8-4r43-D3RB");
        userService.enrolUser("a4QE-KamH-xEx8-4r43-D3rB");

        UserActionException userActionException =
            assertThrows(UserActionException.class, () -> userService.enrolUser("xxxx-kamh-xex8-4r43-d3rb"));
        assertThat(userActionException.getUserActionError()).isEqualTo(UserActionError.ENROLMENT_KEY_INVALID);


        when(userRepository.getEnrolmentKeyDetails(anyString()))
            .thenReturn(generateEnrolmentKeyDTO(
                LocalDate.now().minusDays(ENROLMENT_KEY_EXPIRATION_DAYS).minusDays(1)));

        userActionException =
            assertThrows(UserActionException.class, () -> userService.enrolUser("A4QE-kamh-xex8-4r43-d3rb"));
        assertThat(userActionException.getUserActionError()).isEqualTo(UserActionError.ENROLMENT_KEY_EXPIRED);

    }
    
    @Test
    @DisplayName("Submit Major User Details Update Request.")
    void test_submitMajorUserDetailsUpdateRequest() {
        User user = new User();
        user.setUrid("urid");
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        Mockito.when(persistenceService.getNextBusinessIdentifier(Task.class)).thenReturn(10001L);
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq(user.getUrid())))
            .thenReturn(Collections.emptyList());
        User currentUser = Mockito.mock(User.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        UserDetailsUpdateDTO dto = createMajorUserDetailsUpdateRequest();

        Long taskIdentifier = userService.submitMajorUserDetailsUpdateRequest(user.getUrid(), dto);
        assertEquals(10001L, taskIdentifier);

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(persistenceService, Mockito.times(1)).save(argument.capture());

        ArgumentCaptor<Task> argument2 = ArgumentCaptor.forClass(Task.class);
        ArgumentCaptor<String> argument3 = ArgumentCaptor.forClass(String.class);
        verify(taskEventService, Mockito.times(1)).createAndPublishTaskAndAccountRequestEvent(argument2.capture(), argument3.capture());
    }
    
    @Test
    @DisplayName("Submit major user details update, user does not exist, expected to fail.")
    void test_updateUserDetailsUserDoesNotExist() {
        User user = new User();
        user.setUrid("urid");
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(null);
        User currentUser = Mockito.mock(User.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        UserDetailsUpdateDTO dto = createMajorUserDetailsUpdateRequest();

        UkEtsException exception = assertThrows(
                UkEtsException.class,
                () -> userService.submitMajorUserDetailsUpdateRequest(user.getUrid(), dto));

        assertTrue(exception.getMessage().contains("which does not exist"));
    }
    
    @Test
    @DisplayName("Submit major user details update, update details request already pending, expected to fail.")
    void test_updateUserDetailsTaskPending() {
        User user = new User();
        user.setUrid("urid");
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq(user.getUrid())))
            .thenReturn(Collections.singletonList(new Task()));
        User currentUser = Mockito.mock(User.class);
        Mockito.when(userService.getCurrentUser()).thenReturn(currentUser);

        UserDetailsUpdateDTO dto = createMajorUserDetailsUpdateRequest();

        BusinessRuleErrorException exception = assertThrows(
                BusinessRuleErrorException.class,
                () -> userService.submitMajorUserDetailsUpdateRequest(user.getUrid(), dto));

        assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
                .contains("Another request to update the user details is pending approval."));
    }
    
    @Test
    @DisplayName("Submit Minor User Details Update Request.")
    void test_submitMinorUserDetailsUpdateRequest() {
        User user = new User();
        user.setUrid(TEST_URID);
        
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq(user.getUrid())))
            .thenReturn(Collections.emptyList());
        
        
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("UserName");
        userRepresentation.setFirstName("firstName");
        userRepresentation.setLastName("SurName");
        userRepresentation.setId(TEST_USER_ID);
        userRepresentation.setEmail(TEST_EMAIL);
        Map<String, List<String>> attributes  = new HashMap<>() {{
                put(ATTR_URID, Collections.singletonList(TEST_URID));
                put(ATTR_WORK_EMAIL, Collections.singletonList("testWorkEmailAddress"));
                put(ATTR_COUNTRY_OF_BIRTH, Collections.singletonList("UK"));
                put(ATTR_BIRTH_DATE, Collections.singletonList("1/1/1960"));
            }};
        userRepresentation.setAttributes(attributes);
            
	    UserDetailsDTO diff = new UserDetailsDTO();
	    diff.setWorkPostCode("12345");
	    
	    UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
	    dto.setDiff(diff);
	    UserDetailsDTO current = new UserDetailsDTO();
	    current.setUsername("UserName");
	    current.setFirstName("firstName");
	    current.setLastName("SurName");
	    current.setCountryOfBirth("UK");
	    dto.setCurrent(current);
	    
	    
        Mockito.when(userAdministrationService.findByEmail(dto.getCurrent().getUsername())).thenReturn(Optional.of(userRepresentation));
        Mockito.when(userRepository.findByIamIdentifier(any(String.class))).thenReturn(user);

        Long taskIdentifier = userService.submitMinorUserDetailsUpdateRequest(user.getUrid(), dto);
        assertNull(taskIdentifier);

        ArgumentCaptor<Task> arg2 = ArgumentCaptor.forClass(Task.class);
        ArgumentCaptor<String> arg3 = ArgumentCaptor.forClass(String.class);
        verify(taskEventService, Mockito.times(0)).createAndPublishTaskAndAccountRequestEvent(arg2.capture(), arg3.capture());
        
        String comment = UserDetailsUtil.generateUserDetailsUpdateComment(dto.getCurrent(), dto.getDiff());
        then(eventService).should(times(1))
            .createAndPublishEvent(user.getUrid(), user.getUrid(), comment, EventType.USER_MINOR_DETAILS_UPDATED, "Update User details");
     }
    
    @Test
    @DisplayName("Update user details, user representation does not exist, expected to fail.")
    void test_updateUserDetailsNoUserRepresentationStored() {
        TaskDetailsDTO taskdetailsDto = new TaskDetailsDTO();
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskdetailsDto);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setUsername(TEST_EMAIL);
        dto.setCurrent(current);
        Optional<UserRepresentation> emptyUserRep = Optional.empty();
        Mockito.when(userAdministrationService.findByEmail(any(String.class))).thenReturn(emptyUserRep);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.updateUserDetails(dto));

        assertTrue(exception.getMessage()
                .contains("Illegal user representation"));
    }

    @Test
    @DisplayName("Update user details with invalid phone code and phone number.")
    void test_InvalidPhoneCodeAndPhoneNumber() {

        Mockito.when(userRepository.findByUrid("urid")).thenReturn(new User());
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq("urid")))
            .thenReturn(Collections.emptyList());

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUsername(TEST_EMAIL);
        userDetailsDTO.setWorkCountryCode("Agargarg");
        userDetailsDTO.setWorkPhoneNumber("ager345t6wrg");

        UserDetailsUpdateDTO userDetailsUpdateDTO = new UserDetailsUpdateDTO();
        userDetailsUpdateDTO.setDiff(userDetailsDTO);
        userDetailsUpdateDTO.setCurrent(new UserDetailsDTO());

        BusinessRuleErrorException exception = assertThrows(
            BusinessRuleErrorException.class,
                () -> userService.submitMajorUserDetailsUpdateRequest("urid", userDetailsUpdateDTO));
        assertTrue(exception.getErrorBody().getErrorDetails().get(0).getMessage()
                .contains("Invalid phone number format"));
    }

    @Test
    @DisplayName("Update user details with invalid phone code.")
    void test_invalidPhoneCode() {

        Mockito.when(userRepository.findByUrid("urid")).thenReturn(new User());
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq("urid")))
            .thenReturn(Collections.emptyList());

        UserDetailsDTO changedDTO = new UserDetailsDTO();
        changedDTO.setUsername(TEST_EMAIL);
        changedDTO.setWorkCountryCode("Agargarg");

        UserDetailsDTO currentDto = new UserDetailsDTO();
        currentDto.setWorkPhoneNumber("6971234567");

        UserDetailsUpdateDTO userDetailsUpdateDTO = new UserDetailsUpdateDTO();
        userDetailsUpdateDTO.setDiff(changedDTO);
        userDetailsUpdateDTO.setCurrent(currentDto);

        BusinessRuleErrorException exception = assertThrows(
            BusinessRuleErrorException.class,
            () -> userService.submitMajorUserDetailsUpdateRequest("urid", userDetailsUpdateDTO));
        assertTrue(exception.getErrorBody().getErrorDetails().get(0).getMessage()
            .contains("Invalid phone number format"));
    }

    @Test
    @DisplayName("Update user details with invalid phone number.")
    void test_invalidPhoneNumber() {

        Mockito.when(userRepository.findByUrid("urid")).thenReturn(new User());
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq("urid")))
            .thenReturn(Collections.emptyList());

        UserDetailsDTO changedDTO = new UserDetailsDTO();
        changedDTO.setUsername(TEST_EMAIL);
        changedDTO.setWorkPhoneNumber("697123456789");

        UserDetailsDTO currentDto = new UserDetailsDTO();
        currentDto.setWorkCountryCode("30");

        UserDetailsUpdateDTO userDetailsUpdateDTO = new UserDetailsUpdateDTO();
        userDetailsUpdateDTO.setDiff(changedDTO);
        userDetailsUpdateDTO.setCurrent(currentDto);

        BusinessRuleErrorException exception = assertThrows(
            BusinessRuleErrorException.class,
            () -> userService.submitMajorUserDetailsUpdateRequest("urid", userDetailsUpdateDTO));
        assertTrue(exception.getErrorBody().getErrorDetails().get(0).getMessage()
            .contains("Invalid phone number given"));
    }

    @Test
    @DisplayName("Update user details with valid phone code and phone number.")
    void test_validPhoneCodeAndPhoneNumber() {

        Mockito.when(userRepository.findByUrid("urid")).thenReturn(new User());
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq("urid")))
            .thenReturn(Collections.emptyList());

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setUsername(TEST_EMAIL);
        userDetailsDTO.setWorkCountryCode("30");
        userDetailsDTO.setWorkPhoneNumber("6999999993");

        UserDetailsUpdateDTO userDetailsUpdateDTO = new UserDetailsUpdateDTO();
        userDetailsUpdateDTO.setDiff(userDetailsDTO);
        userDetailsUpdateDTO.setCurrent(new UserDetailsDTO());

        assertDoesNotThrow(() -> userService.submitMajorUserDetailsUpdateRequest("urid", userDetailsUpdateDTO));
    }

    @Test
    @DisplayName("Update user details with valid phone code.")
    void test_validPhoneCode() {

        Mockito.when(userRepository.findByUrid("urid")).thenReturn(new User());
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq("urid")))
            .thenReturn(Collections.emptyList());

        UserDetailsDTO changedDTO = new UserDetailsDTO();
        changedDTO.setUsername(TEST_EMAIL);
        changedDTO.setWorkCountryCode("30");

        UserDetailsDTO currentDto = new UserDetailsDTO();
        currentDto.setWorkPhoneNumber("6999999993");

        UserDetailsUpdateDTO userDetailsUpdateDTO = new UserDetailsUpdateDTO();
        userDetailsUpdateDTO.setDiff(changedDTO);
        userDetailsUpdateDTO.setCurrent(currentDto);

        assertDoesNotThrow(() -> userService.submitMajorUserDetailsUpdateRequest("urid", userDetailsUpdateDTO));
    }

    @Test
    @DisplayName("Update user details with valid phone number.")
    void test_validPhoneNumber() {

        Mockito.when(userRepository.findByUrid("urid")).thenReturn(new User());
        Mockito.when(taskRepository.findPendingTasksByTypeAndUser(any(RequestType.class), eq("urid")))
            .thenReturn(Collections.emptyList());

        UserDetailsDTO changedDTO = new UserDetailsDTO();
        changedDTO.setUsername(TEST_EMAIL);
        changedDTO.setWorkPhoneNumber("6999999993");;

        UserDetailsDTO currentDto = new UserDetailsDTO();
        currentDto.setWorkCountryCode("30");

        UserDetailsUpdateDTO userDetailsUpdateDTO = new UserDetailsUpdateDTO();
        userDetailsUpdateDTO.setDiff(changedDTO);
        userDetailsUpdateDTO.setCurrent(currentDto);

        assertDoesNotThrow(() -> userService.submitMajorUserDetailsUpdateRequest("urid", userDetailsUpdateDTO));
    }

    @Test
    @DisplayName("Update user details, user exists in keycloak but not in registry DB, expected to fail.")
    void test_updateUserDetailsNoRegistryUser() {
        TaskDetailsDTO taskdetailsDto = new TaskDetailsDTO();
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskdetailsDto);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setUsername(TEST_EMAIL);
        dto.setCurrent(current);
        UserRepresentation userRep = createUserRepresentation();
        Mockito.when(userAdministrationService.findByEmail(any(String.class))).thenReturn(Optional.of(userRep));
        Mockito.when(userRepository.findByIamIdentifier(any(String.class))).thenReturn(null);
        UkEtsException exception = assertThrows(
                UkEtsException.class,
                () -> userService.updateUserDetails(dto));

        assertTrue(exception.getMessage()
                .contains("does not exist in registry DB"));
    }
    
    @Test
    @DisplayName("Update user details, expected to pass.")
    void test_updateUserDetails() {
        TaskDetailsDTO taskdetailsDto = new TaskDetailsDTO();
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskdetailsDto);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setUsername(TEST_EMAIL);
        dto.setCurrent(current);
        UserDetailsDTO changed = new UserDetailsDTO();
        changed.setFirstName("changedName");
        changed.setAlsoKnownAs("");
        changed.setCountryOfBirth("UK");
        DateDTO birthDate = new DateDTO();
        birthDate.setDay(1);
        birthDate.setMonth(12);
        birthDate.setYear(1950);
        changed.setBirthDate(birthDate);
        dto.setChanged(changed);

        UserRepresentation userRep = createUserRepresentation();
        User user = new User();
        user.setFirstName("testName");
        user.setLastName("testLastName");
        Mockito.when(userAdministrationService.findByEmail(any(String.class))).thenReturn(Optional.of(userRep));
        Mockito.when(userRepository.findByIamIdentifier(any(String.class))).thenReturn(user);

        userService.updateUserDetails(dto);
        
        ArgumentCaptor<User> argument1 = ArgumentCaptor.forClass(User.class);
        verify(userRepository, Mockito.times(1)).save(argument1.capture());
        assertEquals(Utils.concat(" ", argument1.getValue().getFirstName(), argument1.getValue().getLastName()), 
                argument1.getValue().getDisclosedName());
        ArgumentCaptor<UserRepresentation> argument2 = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userAdministrationService, Mockito.times(1)).updateUserDetails(argument2.capture());
        assertEquals(changed.getFirstName(), argument2.getValue().getFirstName());
        assertEquals("testLastName", argument2.getValue().getLastName());
        assertEquals("", argument2.getValue().getAttributes().get("alsoKnownAs").get(0));
        assertEquals(changed.getCountryOfBirth(), argument2.getValue().getAttributes().get("countryOfBirth").get(0));
        assertEquals("1/12/1950", argument2.getValue().getAttributes().get("birthDate").get(0));
    }
    
    @Test
    @DisplayName("Update user details, change Known as, disclosed and searchable name should also change.")
    void test_updateUserDetailsChangeKnownAs() {
        TaskDetailsDTO taskdetailsDto = new TaskDetailsDTO();
        UserDetailsUpdateTaskDetailsDTO dto = new UserDetailsUpdateTaskDetailsDTO(taskdetailsDto);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setUsername(TEST_EMAIL);
        dto.setCurrent(current);
        UserDetailsDTO changed = new UserDetailsDTO();
        changed.setAlsoKnownAs(TEST_KNOWN_AS);
        dto.setChanged(changed);

        UserRepresentation userRep = createUserRepresentation();
        User user = new User();
        user.setFirstName("testName");
        user.setLastName("testLastName");
        Mockito.when(userAdministrationService.findByEmail(any(String.class))).thenReturn(Optional.of(userRep));
        Mockito.when(userRepository.findByIamIdentifier(any(String.class))).thenReturn(user);

        userService.updateUserDetails(dto);
        
        ArgumentCaptor<User> argument1 = ArgumentCaptor.forClass(User.class);
        verify(userRepository, Mockito.times(1)).save(argument1.capture());
        assertEquals(TEST_KNOWN_AS, argument1.getValue().getKnownAs());
        assertEquals(TEST_KNOWN_AS, argument1.getValue().getDisclosedName());
        ArgumentCaptor<UserRepresentation> argument2 = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userAdministrationService, Mockito.times(1)).updateUserDetails(argument2.capture());
        assertEquals(TEST_KNOWN_AS, argument2.getValue().getAttributes().get("alsoKnownAs").get(0));
    }

    private UserRepresentation createUserRepresentation() {
        UserRepresentation userRep = new UserRepresentation();
        userRep.setUsername(TEST_URID);
        userRep.setFirstName("testName");
        userRep.setLastName("testLastName");
        userRep.setId(TEST_USER_ID);
        userRep.setEmail(TEST_EMAIL);
        Map<String, List<String>> attributes  = new HashMap<>() {{
                put(ATTR_URID, Collections.singletonList(TEST_URID));
                put(ATTR_ALSO_KNOWN_AS, Collections.singletonList("testAlsoKnownAs"));
                put(ATTR_WORK_EMAIL, Collections.singletonList("testEmailAddress"));
                put(ATTR_COUNTRY_OF_BIRTH, Collections.singletonList("GR"));
                put(ATTR_BIRTH_DATE, Collections.singletonList("1/1/1960"));
            }};

        userRep.setAttributes(attributes);
        return userRep;
    }
    
    private UserDetailsUpdateDTO createMajorUserDetailsUpdateRequest() {
        UserDetailsDTO diff = new UserDetailsDTO();
        diff.setFirstName("FIRST");
        diff.setLastName("LAST");
        diff.setCountryOfBirth("UK");
        UserDetailsUpdateDTO dto = new UserDetailsUpdateDTO();
        dto.setDiff(diff);
        UserDetailsDTO current = new UserDetailsDTO();
        current.setFirstName("FIRSTNAME");
        current.setLastName("LASTNAME");
        current.setCountryOfBirth("UK");
        dto.setCurrent(current);
        return dto;
    }

    @Test
    @DisplayName("Revert from deactivation pending to original status, user does not exist, expected to fail.")
    void test_revertDeactivationPendingUserDoesNotExist() {
        User user = new User();
        user.setUrid("urid");
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(null);

        UkEtsException exception = assertThrows(
                UkEtsException.class,
                () -> userService.revertDeactivationPending(user.getUrid(), "claimantUrid"));

        assertTrue(exception.getMessage().contains("which does not exist"));
    }
    
    @Test
    @DisplayName("Revert from deactivation pending to original status, expected to pass.")
    void test_revertDeactivationPending() {
        User user = new User();
        user.setUrid("urid");
        user.setPreviousState(UserStatus.ENROLLED);
        user.setIamIdentifier(TEST_USER_ID);        
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        userService.revertDeactivationPending(user.getUrid(), "claimantUrid");
        
        verify(userAdministrationService, Mockito.times(1)).updateUserState(user.getIamIdentifier(), UserStatus.ENROLLED);
        ArgumentCaptor<User> argument = ArgumentCaptor.forClass(User.class);
        verify(userRepository, Mockito.times(1)).save(argument.capture());
    }
    
    @Test
    @DisplayName("Deactivate user, pending tasks exist for this user, expected to fail.")
    void test_deactivateUserPendingTasksExist() {
        User user = new User();
        user.setUrid("urid");     
        Mockito.when(taskRepository.findPendingTasksClaimedByUser(eq(user.getUrid())))
            .thenReturn(Collections.singletonList(new Task()));
        Mockito.when(taskRepository.findPendingTasksForUser(eq(user.getUrid())))
            .thenReturn(Collections.singletonList(new Task()));

        BusinessRuleErrorException exception = assertThrows(
                BusinessRuleErrorException.class,
                () -> userService.deactivateUser(user.getUrid(), "claimantUrid"));

        assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString().contains(
                "You cannot approve deactivation as pending tasks exist for this user"));
    }    
    
    @Test
    @DisplayName("Deactivate user, expected to pass.")
    void test_deactivateUser() {
        User user = new User();
        user.setUrid("urid");
        user.setIamIdentifier(TEST_USER_ID);  
        UserRepresentation userRep = createUserRepresentation();
        
        Mockito.when(taskRepository.findPendingTasksForUser(eq(user.getUrid())))
            .thenReturn(Collections.emptyList());
        Mockito.when(taskRepository.findPendingTasksClaimedByUser(eq(user.getUrid())))
            .thenReturn(Collections.emptyList());
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        Mockito.when(userAdministrationService.findByIamId(user.getIamIdentifier())).thenReturn(userRep);
        userService.deactivateUser(user.getUrid(), "claimantUrid");
        
        ArgumentCaptor<UserRepresentation> argument = ArgumentCaptor.forClass(UserRepresentation.class);
        verify(userAdministrationService, Mockito.times(1)).updateUserDetails(argument.capture());
        assertTrue(argument.getValue().getEmail().contains("DEACTIVATED_"));
        assertEquals(TEST_EMAIL, argument.getValue().getAttributes().get("deactivatedEmail").get(0));
        verify(userAdministrationService, Mockito.times(1)).updateUserState(user.getIamIdentifier(), UserStatus.DEACTIVATED);
        ArgumentCaptor<User> argument2 = ArgumentCaptor.forClass(User.class);
        verify(userRepository, Mockito.times(1)).save(argument2.capture());
    }

    @Test
    @DisplayName("Deactivate user, expected to pass.")
    void test_submitDeactivationRequest() {
        User user = new User();
        user.setUrid(TEST_URID);
        user.setState(UserStatus.ENROLLED);
        user.setIamIdentifier(TEST_USER_ID);
        UserRepresentation userRep = createUserRepresentation();
        UserDeactivationDTO dto = constructUserDeactivationDTO();

        Mockito.when(userRepository.findByUrid(anyString())).thenReturn(user);
        Mockito.when(userRepository.getEnrolmentKeyDetails(user.getUrid())).thenReturn(dto.getEnrolmentKeyDetails());
        Mockito.when(userAdministrationService.findByIamId(user.getIamIdentifier())).thenReturn(userRep);
        userService.submitUserDeactivationRequest(user.getUrid(), dto);
        assertEquals(UserStatus.DEACTIVATION_PENDING,user.getState());
    }

    private UserDeactivationDTO constructUserDeactivationDTO() {
        KeycloakUser keycloakUser = new KeycloakUser(createUserRepresentation());
        EnrolmentKeyDTO enrolmentKeyDTO = new EnrolmentKeyDTO(
            keycloakUser.getAttributes().get("urid").get(0), "XXXX-XXXX-XXXX-XXXX-XXXX", new Date());

        List<String> warnings = new ArrayList<>();
        return new UserDeactivationDTO(keycloakUser,enrolmentKeyDTO,"deactivationComment", warnings);
    }
    
    @Test
    @DisplayName("Delete user file, file does not exist, expected to fail.")
    void test_deleteUserFileDoesNotExist() {
        UserFile userFile = new UserFile();
        userFile.setId(1234L);
        User user = new User();
        user.setUrid("urid");
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        Mockito.when(userFileRepository.findByIdAndUser(1234L, user)).thenReturn(null);

        AuthorizationServiceException exception = assertThrows(
                AuthorizationServiceException.class,
                () -> userService.deleteUserFile(user.getUrid(), 1234L));

        assertTrue(exception.getMessage().contains("File not found"));
    }
    
    @Test
    @DisplayName("Delete user file, expected to pass.")
    void test_deleteUserFile() {
        UserFile userFile = new UserFile();
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1234L);
        uploadedFile.setFileName("Test file name");   
        userFile.setUploadedFile(uploadedFile);
        User user = new User();
        user.setUrid("urid");
        Mockito.when(userRepository.findByUrid(user.getUrid())).thenReturn(user);
        Mockito.when(userFileRepository.findByIdAndUser(1234L, user)).thenReturn(userFile);

        userService.deleteUserFile(user.getUrid(), 1234L);
        
        verify(eventService, Mockito.times(1)).createAndPublishEvent(user.getUrid(), "", 
                uploadedFile.getFileName(), EventType.USER_DELETE_SUBMITTED_DOCUMENT, "User documentation deleted");
    }
    
    @Test
    @DisplayName("Try to submit a user update details request, expected to fail due to another pending user update details request.")
	void test_anotheUserDetailsUpdateRequestExistsForUserDeactivationRequest() {
		User user = new User();
		user.setUrid("urid");
		Mockito.when(
				taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DETAILS_UPDATE_REQUEST, user.getUrid()))
				.thenReturn(Collections.singletonList(new Task()));

		BusinessRuleErrorException exception = assertThrows(BusinessRuleErrorException.class, 
				() -> userService.validateUserUpdateRequest(user.getUrid(), UserDetailsUpdateType.UPDATE_USER_DETAILS));

		assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
				.contains("Another request to update user details is pending approval"));
	}
    
    @Test
    @DisplayName("Try to submit a user deactivation request, expected to fail due to another pending user deactivation request.")
	void test_anotherDeactivationRequestExistsForUserDeactivationRequest() {
		User user = new User();
		user.setUrid("urid");
		Mockito.when(
				taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DEACTIVATION_REQUEST, user.getUrid()))
				.thenReturn(Collections.singletonList(new Task()));

		BusinessRuleErrorException exception = assertThrows(BusinessRuleErrorException.class, 
				() -> userService.validateUserUpdateRequest(user.getUrid(), UserDetailsUpdateType.DEACTIVATE_USER));

		assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
				.contains("Another request to deactivate user is pending approval"));
	}
    
    @Test
    @DisplayName("Try to submit a user deactivation request, expected to fail due to a pending approval task.")
	void test_blockingTaskExistsForUserDeactivationRequest() {
		User user = new User();
		user.setUrid("urid");
		Mockito.when(
				taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DEACTIVATION_REQUEST, user.getUrid()))
				.thenReturn(Collections.emptyList());
		
		final List<RequestType> blockingTaskTypes = List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, 
				RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
		
		Mockito.when(taskRepository.findPendingTasksByTypesAndUser(blockingTaskTypes, user.getUrid()))
				.thenReturn(Collections.singletonList(new Task()));

		BusinessRuleErrorException exception = assertThrows(BusinessRuleErrorException.class, 
				() -> userService.validateUserUpdateRequest(user.getUrid(), UserDetailsUpdateType.DEACTIVATE_USER));

		assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
				.contains("There are requests pending approval"));
	}
    

    
    @Test
    @DisplayName("Try to submit a user deactivation request, expected to fail due to a pending ar replacement task.")
	void test_blockingArReplacementTaskExistsForUserDeactivationRequest() {
		User user = new User();
		user.setUrid("urid");
		Mockito.when(
				taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DEACTIVATION_REQUEST, user.getUrid()))
				.thenReturn(Collections.emptyList());
		
		final List<RequestType> blockingTaskTypes = List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, 
				RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
		
		Mockito.when(taskRepository.findPendingTasksByTypesAndUser(blockingTaskTypes, user.getUrid()))
				.thenReturn(Collections.emptyList());
		
		ARUpdateAction arUpdateAction = new ARUpdateAction();
		arUpdateAction.setType(ARUpdateActionType.REPLACE);
		arUpdateAction.setToBeReplacedUrid(user.getUrid());
		arUpdateAction.setUrid("new_urid");
		Mockito.when(arUpdateActionRepository.fetchPendingArUpdateActionsByType(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST))
				.thenReturn(Collections.singletonList(arUpdateAction));
		
		BusinessRuleErrorException exception = assertThrows(BusinessRuleErrorException.class, 
				() -> userService.validateUserUpdateRequest(user.getUrid(), UserDetailsUpdateType.DEACTIVATE_USER));

		assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
				.contains("There are requests pending approval"));
	}
    
    @Test
    @DisplayName("Try to submit a user deactivation request, expected to fail due to a pending account opening task.")
	void test_blockingOpenAccountTaskExistsForUserDeactivationRequest() {
		User user = new User();
		user.setUrid("urid");
		Mockito.when(
				taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DEACTIVATION_REQUEST, user.getUrid()))
				.thenReturn(Collections.emptyList());
		
		final List<RequestType> blockingTaskTypes = List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, 
				RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
		
		Mockito.when(taskRepository.findPendingTasksByTypesAndUser(blockingTaskTypes, user.getUrid()))
				.thenReturn(Collections.emptyList());
		
		Mockito.when(arUpdateActionRepository.fetchPendingArUpdateActionsByType(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST))
				.thenReturn(Collections.emptyList());
		
		Task accountOpeningTask = new Task();
		accountOpeningTask.setDifference("{}");
		AccountDTO accountDTO = new AccountDTO();
        AuthorisedRepresentativeDTO ar = new AuthorisedRepresentativeDTO();
        ar.setUrid(user.getUrid());
		accountDTO.setAuthorisedRepresentatives(List.of(ar));
		Mockito.when(mapper.convertToPojo(accountOpeningTask.getDifference(), AccountDTO.class)).thenReturn(accountDTO);
		Mockito.when(taskRepository.findPendingTasksByType(RequestType.ACCOUNT_OPENING_REQUEST))
				.thenReturn(Collections.singletonList(accountOpeningTask));
		
		BusinessRuleErrorException exception = assertThrows(BusinessRuleErrorException.class, 
				() -> userService.validateUserUpdateRequest(user.getUrid(), UserDetailsUpdateType.DEACTIVATE_USER));

		assertTrue(exception.getErrorBody().getErrorDetails().get(0).toString()
				.contains("There are requests pending approval"));
	}
    
    @Test
    @DisplayName("Try to submit a user deactivation request, expected to pass.")
	void test_NoneBlockingTaskExistsForUserDeactivationRequest() {
		User user = new User();
		user.setUrid("urid");
		Mockito.when(
				taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DEACTIVATION_REQUEST, user.getUrid()))
				.thenReturn(Collections.emptyList());
		final List<RequestType> blockingTaskTypes = List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
				RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, 
				RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
		
		Mockito.when(taskRepository.findPendingTasksByTypesAndUser(blockingTaskTypes, user.getUrid()))
				.thenReturn(Collections.emptyList());
		
		Mockito.when(taskRepository.findPendingTasksByType(RequestType.ACCOUNT_OPENING_REQUEST))
				.thenReturn(Collections.emptyList());
		
		userService.validateUserUpdateRequest(user.getUrid(), UserDetailsUpdateType.DEACTIVATE_USER);
	}
}
