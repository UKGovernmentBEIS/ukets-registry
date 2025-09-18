package gov.uk.ets.registry.api.user.service;


import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.authz.AuthorizationService;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.UserDetailsUtil;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.common.exception.BusinessRuleErrorException;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.UserFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.UserFileRepository;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.printenrolmentletter.PrintEnrolmentLetterTaskService;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.UserDetailsUpdateTaskDetailsDTO;
import gov.uk.ets.registry.api.user.*;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.shared.UserDetailsUpdateType;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsUpdateDTO;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeResultDTO;
import gov.uk.ets.registry.api.user.domain.*;
import gov.uk.ets.registry.api.user.profile.recovery.web.RecoveryMethodUpdateRequest;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

/**
 * Implementation for user service.
 */
@Service
@Log4j2
public class UserService {
    private final int enrolmentKeyExpirationDays;
    private static final String CHANGE_USER_STATUS = "Change user status";

    private final UserRepository userRepository;
    private final UserConversionService userConversionService;
    private final AuthorizationService authorizationService;
    private final UserAdministrationService userAdministrationService;
    private final UserGeneratorService userGeneratorService;
    private final UserWorkContactRepository userWorkContactRepository;
    private final EventService eventService;
    private final PersistenceService persistenceService;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final UserFileRepository userFileRepository;
    private final PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService;
    private final TaskRepository taskRepository;
    private final TaskEventService taskEventService;
    private final ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    private final ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    private final ARUpdateActionRepository arUpdateActionRepository;
    private final Mapper mapper;

    /**
     * Constructor injection needed for @Value properties.
     **/
    public UserService(@Value("${enrolment.key.expiration.days:31}") int enrolmentKeyExpirationDays,
                       UserRepository userRepository,
                       UserConversionService userConversionService,
                       AuthorizationService authorizationService,
                       UserAdministrationService userAdministrationService,
                       UserGeneratorService userGeneratorService,
                       UserWorkContactRepository userWorkContactRepository,
                       EventService eventService,
                       PersistenceService persistenceService,
                       UploadedFilesRepository uploadedFilesRepository,
                       UserFileRepository userFileRepository,
                       PrintEnrolmentLetterTaskService printEnrolmentLetterTaskService,
                       TaskRepository taskRepository, TaskEventService taskEventService,
                       ServiceAccountAuthorizationService serviceAccountAuthorizationService,
                       ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService, 
                       ARUpdateActionRepository arUpdateActionRepository,
                       Mapper mapper) {
        this.enrolmentKeyExpirationDays = enrolmentKeyExpirationDays;
        this.userRepository = userRepository;
        this.userConversionService = userConversionService;
        this.authorizationService = authorizationService;
        this.userAdministrationService = userAdministrationService;
        this.userGeneratorService = userGeneratorService;
        this.userWorkContactRepository = userWorkContactRepository;
        this.eventService = eventService;
        this.persistenceService = persistenceService;
        this.uploadedFilesRepository = uploadedFilesRepository;
        this.userFileRepository = userFileRepository;
        this.printEnrolmentLetterTaskService = printEnrolmentLetterTaskService;
        this.taskRepository = taskRepository;
        this.taskEventService = taskEventService;
        this.serviceAccountAuthorizationService = serviceAccountAuthorizationService;
        this.reportRequestAddRemoveRoleService = reportRequestAddRemoveRoleService;
        this.arUpdateActionRepository = arUpdateActionRepository;
        this.mapper = mapper;
    }

    /**
     * Registers the provided user.
     *
     * @param userDTO The user.
     */
    @Transactional
    public void registerUser(UserDTO userDTO) {
        if (!userGeneratorService.validateURID(userDTO.getUrid())) {
            throw UserActionException.create(UserActionError.INVALID_URID);
        }
        User user = new User();
        user.setUrid(userDTO.getUrid());
        user.setEmail(userDTO.getEmail());
        user.setState(UserStatus.REGISTERED);
        user.setIamIdentifier(userDTO.getKeycloakId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setKnownAs(userDTO.getAlsoKnownAs());
        user.setDisclosedName(!StringUtils.isEmpty(userDTO.getAlsoKnownAs()) ? userDTO.getAlsoKnownAs() 
                : Utils.concat(" ", userDTO.getFirstName(), userDTO.getLastName()));
        userRepository.save(user);
        String action = "User registered";
        eventService.createAndPublishEvent(user.getUrid(), user.getUrid(), "",
            EventType.USER_REGISTERED, action);
    }

    /**
     * Retrieves the user by its URID.
     *
     * @param urid The user URID.
     * @return the user transfer object
     */
    public UserDTO getUser(String urid) {
        UserDTO result = null;
        User user = userRepository.findByUrid(urid);
        if (user != null) {
            result = userConversionService.convert(user);
        }
        return result;
    }

    /**
     * Retrieves the list of the files that belong to the user.
     *
     * @param urid The user URID
     * @return the list of files
     */
    public List<UserFileDTO> getUserFiles(String urid) {
        return userRepository.getUserFiles(urid);
    }

    /**
     * Returns the currently logged in user.
     *
     * @return a user.
     */
    public User getCurrentUser() {
        // Retrieve the currently logged in user
        User user = userRepository.findByIamIdentifier(authorizationService.getClaim(OAuth2ClaimNames.SUBJECT));
        if (user == null) {
            throw new AuthorizationServiceException(
                String.format("The current user %s does not exist in DB", authorizationService.getClaim(OAuth2ClaimNames.SUBJECT)));
        }
        return user;
    }

    /**
     * Retrieves the unique list of users related to this account holder plus the current user based on flag.
     * @param accountHolderIdentifier The account holder identifier.
     * @param includeCurrentUser      Whether the current user must be included or not.
     * @return A list of users.
     */
    public List<UserDTO> getAuthorisedRepresentatives(Long accountHolderIdentifier, boolean includeCurrentUser) {
        final var currentUser = getCurrentUser();
        final var isCurrentUserAdmin = hasAnyAdminRole(currentUser.getIamIdentifier());

        // Only provide userId if the user is NOT an admin
        final var userId = isCurrentUserAdmin ? null : currentUser.getId();
        List<UserDTO> associatedUsers = userRepository.getAccountHolderArs(accountHolderIdentifier, userId);

        if (includeCurrentUser && !isCurrentUserAdmin) {
            associatedUsers.add(userConversionService.convert(currentUser));
        }

        //performing sorting outside of query as we might add the current user
        return associatedUsers.stream()
            .sorted(
                comparing(UserDTO::getAlsoKnownAs, ObjectUtils::compare)
                    .thenComparing(UserDTO::getFirstName, ObjectUtils::compare)
            ).collect(Collectors.toList());
    }

    /**
     * Retrieves the users whose first name or last name start with the term argument ignoring case.
     *
     * @param term The search term with which the firstName or lastName of returned users start
     * @return A list of users
     */
    public List<User> getUsersByName(String term) {
        return userRepository.getUsersByNameStartingWith(term);
    }

    /**
     * Retrieves all users that belong in one of the roles of the collection.
     *
     * @param userRoles the list of the user roles.
     * @return a list of users.
     */
    public List<User> findUsersByRoleName(List<String> userRoles) {
        return userRepository.findUsersByRoleName(userRoles);
    }

    /**
     * Retrieves all users that belong in one of the roles of the collection and in one of the statuses provided.
     *
     * @param userRoles the list of the user roles.
     * @param statuses the list of the user statuses.
     * @return a list of users.
     */
    public List<User> findUsersByRoleNameAndState(List<String> userRoles, List<UserStatus> statuses) {
        return userRepository.findUsersByRoleNameAndState(userRoles, statuses);
    };

    /**
     * Retrieves the user by its ID.
     *
     * @param id The user ID.
     * @return the user
     */
    public User getUserById(Long id) {
        return userRepository.getOne(id);
    }

    /**
     * Retrieves a user file by the file id.
     *
     * @param fileId The id of the file
     * @return {@link UploadedFile}
     */
    public UploadedFile getFileById(Long fileId) {
        return uploadedFilesRepository.findById(fileId)
            .orElseThrow(() -> new FileUploadException("Error while fetching the file"));
    }
    
    /**
     * Deletes a user file.
     * @param urid 
     *
     * @param id The id of the file
     */
    @Transactional
    public void deleteUserFile(String urid, Long id) {
        User user = getUserByUrid(urid);
        // verify that this file belongs to the specified user
        UserFile userFile = userFileRepository.findByIdAndUser(id, user);
        if (userFile == null) {
            throw new AuthorizationServiceException("File not found");
        }
        userFileRepository.delete(userFile);
        uploadedFilesRepository.delete(userFile.getUploadedFile());
        
        // publish user event
        User currentUser = getCurrentUser();
        eventService.createAndPublishEvent(urid, currentUser.getUrid(), userFile.getUploadedFile().getFileName(),
                EventType.USER_DELETE_SUBMITTED_DOCUMENT, "User documentation deleted");
    }

    /**
     * Enrols a user based on the provided enrolment key.
     *
     * @param enrolmentKey the user enrolment key
     */
    @Transactional
    public void enrolUser(String enrolmentKey) {
        User currentUser = getCurrentUser();
        // order here matters, we first want to check if the code entered is valid and then check for expiration
        if (!enrolmentKey.equalsIgnoreCase(currentUser.getEnrolmentKey())) {
            throw UserActionException.create(UserActionError.ENROLMENT_KEY_INVALID);
        }

        EnrolmentKeyDTO enrolmentKeyDetails = this.generateEnrolmentKeyDetails(currentUser.getUrid());
        LocalDate expirationDate =
            enrolmentKeyDetails.getEnrolmentKeyDateExpired().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (!isRegistrationCodeStillValid(expirationDate)) {
            throw UserActionException.create(UserActionError.ENROLMENT_KEY_EXPIRED);
        }

        currentUser.setState(UserStatus.ENROLLED);

        userAdministrationService.updateUserState(currentUser.getIamIdentifier(), UserStatus.ENROLLED);
        String action = "User enrolled";
        eventService.createAndPublishEvent(currentUser.getUrid(), currentUser.getUrid(),
            "", EventType.USER_ENROLLED, action);
    }

    /**
     * Returns the expiration date of the encolment key according to the business rule
     * "The registry activation code expires in 2 months from issuance".
     *
     * @param creationDate The creation date of the enrolment key
     * @return The expiration date
     */
    public Date getEnrolmentKeyExpirationDate(@NotNull Date creationDate) {
        Calendar expired = Calendar.getInstance();
        expired.setTime(creationDate);
        expired.add(Calendar.DAY_OF_MONTH, enrolmentKeyExpirationDays);
        return expired.getTime();
    }

    /**
     * Retrieves the enrolment key information from users table for the specific user.
     *
     * @param urid The user identifier
     * @return The enrolment key information
     */
    @Transactional
    public EnrolmentKeyDTO getEnrolmentKeyDetails(String urid) {
        return generateEnrolmentKeyDetails(urid);
    }

    private EnrolmentKeyDTO generateEnrolmentKeyDetails(String urid) {
        EnrolmentKeyDTO enrolmentKeyDetails = userRepository.getEnrolmentKeyDetails(urid);
        if (enrolmentKeyDetails.getEnrolmentKeyDateCreated() != null) {
            Date expirationDate = getEnrolmentKeyExpirationDate(enrolmentKeyDetails.getEnrolmentKeyDateCreated());
            enrolmentKeyDetails.setEnrolmentKeyDateExpired(expirationDate);
        }
        return enrolmentKeyDetails;
    }

    /**
     * Retrieves the {@link UserWorkContact} entities stored in keycloak for the users of urids list.
     */
    public List<UserWorkContact> getUserWorkContacts(Set<String> urids) {
        if (CollectionUtils.isEmpty(urids)) {
            throw new IllegalArgumentException("The list of urid should not be empty");
        }
        return userWorkContactRepository.fetch(urids, false);
    }

    /**
     * Retrieves the {@link UserDTO} entities stored in registry for the users of urids list.
     */
    public List<UserDTO> getUserDetails(List<String> urids) {
        return userRepository.findByUrids(urids);
    }


    /**
     * Updates the state of the provided user to VALIDATED & generates an enrollment key.
     *
     * @param user the AR user input
     * @return the user
     */
    @Transactional
    public User validateUser(User user) {
        try {
            user.setState(UserStatus.VALIDATED);
            user.setEnrolmentKey(userGeneratorService.generateEnrolmentKey());
            user.setEnrolmentKeyDate(new Date());
        } catch (NoSuchAlgorithmException e) {
            log.error("Error validating user", e);
        }
        return user;
    }

    /**
     * This method performs the following:
     * <ol>
     * <li>Updates the state of the provided user to VALIDATED & generates an enrolment key.</li>
     * <li>Modifies the attribute named State and adds the client role AUTHORISED_REPRESENTATIVE in keycloak.</li>
     * </ol>
     *
     * @param registeredUser The user to be validated
     * @param currentUser    The user who triggers the validation
     * @return
     */
    @Transactional
    @EmitsGroupNotifications({GroupNotificationType.EMAIL_CHANGE_STATUS})
    public UserStatusChangeResultDTO validateUserAndGenerateEvents(User registeredUser, User currentUser, Task task) {
        if (!hasAnyAdminRole(registeredUser.getIamIdentifier())) {
            userAdministrationService.addUserRole(registeredUser.getIamIdentifier(),
                                                  UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral());

            // request to add reports-user role in reports-api
            reportRequestAddRemoveRoleService.requestReportsApiAddRole(registeredUser.getIamIdentifier());
        }
        if (UserStatus.REGISTERED.equals(registeredUser.getState())) {
            this.validateUser(registeredUser);
            KeycloakUser keycloakUser = userAdministrationService.updateUserState(registeredUser.getIamIdentifier(), UserStatus.VALIDATED);
            String action = "User validated";
            printEnrolmentLetterTaskService.create(registeredUser, currentUser, task);
            eventService.createAndPublishEvent(registeredUser.getUrid(), currentUser.getUrid(),
                "", EventType.USER_VALIDATED, action);
            action = "Registry activation code issued";
            eventService.createAndPublishEvent(registeredUser.getUrid(), currentUser.getUrid(),
                "", EventType.REGISTRY_ACTIVATION_CODE, action);

            return UserStatusChangeResultDTO.
                builder().
                userStatus(registeredUser.getState()).
                iamIdentifier(registeredUser.getIamIdentifier()).
                requestId(Optional.ofNullable(task != null ? task.getRequestId() : null)).
                user(keycloakUser).
                build();
        }
        return null;
    }

    public boolean hasAnyAdminRole(String iamIdentifier) {
        return authorizationService.getClientLevelRoles(iamIdentifier)
                                   .stream()
                                   .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                                   .anyMatch(UserRole::isRegistryAdministrator);
    }

    /**
     * Checks if a user has Senior or Junior Admin role.
     * This check depends on registry DB.
     *
     * @param user the user
     * @return true if the user has Senior or Junior role, otherwise false
     */
    public boolean isSeniorOrJuniorAdminUser(User user) {
        return hasRoles(user, UserRole::isSeniorOrJuniorRegistryAdministrator);
    }

    /**
     * Checks if a user has any Admin role.
     * This check depends on registry DB.
     *
     * @param user the user
     * @return true if the user has any admin role, otherwise false
     */
    public boolean isAdminUser(User user) {
        return hasRoles(user, UserRole::isRegistryAdministrator);
    }

    private boolean hasRoles(User user, Predicate<UserRole> roleCheck) {
        return user.getUserRoles()
            .stream()
            .map(UserRoleMapping::getRole)
            .map(IamUserRole::getRoleName)
            .filter(UserRole::belongsToRegistryRoles)
            .map(UserRole::fromKeycloakLiteral)
            .anyMatch(roleCheck);
    }

    /**
     * Retrieves the user by its urid.
     *
     * @param urid The user urid.
     * @return the user
     */
    public User getUserByUrid(String urid) {
        return userRepository.findByUrid(urid);
    }

    /**
     * Requests new registry activation code for user.
     */
    public Long requestNewRegistryActivationCode() {
        User currentUser = this.getCurrentUser();
        // the order here matters, first check if a task already exists and then check the date.
        // this is because we update the enrolment info at task creation, so the check for the date doesn't make sense
        // for a task that is not yet approved
        List<Task> tasks = taskRepository
            .findPendingTasksByTypeAndUser(RequestType.PRINT_ENROLMENT_LETTER_REQUEST, currentUser.getUrid());
        if (!tasks.isEmpty()) {
            throw UserActionException.create(UserActionError.ENROLMENT_KEY_TASK_PENDING);
        }
        //generate a new RAC for this user (also expires the existing code)
        updateEnrolmentInfo(currentUser);
        //create a new print letter task for the new RAC
        Task printEnrolmentLetterTask = printEnrolmentLetterTaskService.create(currentUser, currentUser, null);
        return printEnrolmentLetterTask.getRequestId();
    }

    /**
     * Updates the user's disclosed name based on role addition or removal.
     * @param iamIdentifier the IAM identifier.
     * @param isRoleAdded the isRoleAdded boolean value.
     */
    @Transactional
    public void updateUserDisclosedName(String iamIdentifier, boolean isRoleAdded) {
        User user = userRepository.findByIamIdentifier(iamIdentifier);
        if (user != null) {
            if (isRoleAdded) {
                user.setDisclosedName("Registry Administrator");
            } else {
                if (!StringUtils.isEmpty(user.getKnownAs())) {
                    user.setDisclosedName(user.getKnownAs());
                } else {
                    user.setDisclosedName(
                            Utils.concat(" ", user.getFirstName(), user.getLastName()));
                }
            }
        }
    }

    /**
     * Retrieves user if his status is not in the list given of statuses.
     */
    public Optional<User> findByEmailNotInStatuses(String email, UserStatus... statuses) {
        return userAdministrationService
            .findByEmail(email)
            .map(r -> userRepository.findByIamIdentifier(r.getId()))
            .filter(u -> Arrays.stream(statuses).noneMatch(s -> s == u.getState()));
    }

    private boolean isRegistrationCodeStillValid(LocalDate expirationDate) {
        LocalDate today = LocalDate.now();
        return expirationDate.isAfter(today) || expirationDate.isEqual(today);
    }

    /**
     * Enrolment info is set when creating the task (and not when completing it).
     * see {@link gov.uk.ets.registry.api.user.admin.service.UserStatusService#changeUserStatus}
     */
    private void updateEnrolmentInfo(User currentUser) {
        try {
            currentUser.setEnrolmentKey(userGeneratorService.generateEnrolmentKey());
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating enrolment key", e);
        }
        currentUser.setEnrolmentKeyDate(new Date());
    }
    
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.USER_DETAILS_UPDATE_REQUEST)
    public Long submitMajorUserDetailsUpdateRequest(String urid,  UserDetailsUpdateDTO dto) {
    	User user = userRepository.findByUrid(urid);
        if (user == null) {
            throw new UkEtsException(String
                .format("Requested update user details for user with id:%s which does not exist", urid));
        }
        
        if (!taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DETAILS_UPDATE_REQUEST, urid).isEmpty()) {
            throw new BusinessRuleErrorException(ErrorBody.from(
                    "Another request to update the user details is pending approval."));
        }

        validateUserPhoneNumbers(dto);

    	Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        User currentUser = getCurrentUser();
        task.setUser(user);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setType(RequestType.USER_DETAILS_UPDATE_REQUEST);
        task.setDifference(mapper.convertToJson(dto.getDiff()));
        task.setBefore(mapper.convertToJson(dto.getCurrent()));

        persistenceService.save(task);
        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());
        
        return task.getRequestId();
    }
    
	@Transactional
	public Long submitMinorUserDetailsUpdateRequest(String urid, UserDetailsUpdateDTO dto) {
		User user = userRepository.findByUrid(urid);
		if (user == null) {
			throw new UkEtsException(
					String.format("Requested update user details for user with id:%s which does not exist", urid));
		}

		if (!taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DETAILS_UPDATE_REQUEST, urid).isEmpty()) {
			throw new BusinessRuleErrorException(
					ErrorBody.from("Another request to update the user details is pending approval."));
		}

		this.updateUserDetails(dto);

		eventService.createAndPublishEvent(user.getUrid(), user.getUrid(), UserDetailsUtil.generateUserDetailsUpdateComment(dto.getCurrent(), dto.getDiff()),
				EventType.USER_MINOR_DETAILS_UPDATED, "Update User details");

		return null;
	}

    /**
     * {@inheritDoc}
     */
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.USER_DEACTIVATION_REQUEST)
    public Long submitUserDeactivationRequest(String urid, UserDeactivationDTO deactivationDTO) {
        User user = userRepository.findByUrid(urid);
        if (user == null) {
            throw new UkEtsException(String
                                         .format("Requested user with urid:%s which does not exist", urid));
        }
        
        this.checkForBlockingTasksForUserDeactivationRequest(urid);

        Task task = new Task();
        task.setRequestId(persistenceService.getNextBusinessIdentifier(Task.class));
        User currentUser = getCurrentUser();
        task.setUser(user);
        user.setPreviousState(user.getState());
        user.setState(UserStatus.DEACTIVATION_PENDING);
        task.setInitiatedBy(currentUser);
        task.setInitiatedDate(new Date());
        task.setStatus(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        task.setType(RequestType.USER_DEACTIVATION_REQUEST);

        EnrolmentKeyDTO enrolmentKeyDTO = getEnrolmentKeyDetails(urid);
        UserRepresentation userRepresentation = userAdministrationService.findByIamId(user.getIamIdentifier());
        if (userRepresentation == null) {
            throw new UkEtsException(String
                                         .format("User with urid:%s which does not exist in Keycloak DB", urid));
        }
        KeycloakUser keycloakUser = userAdministrationService.updateUserState(
        		user.getIamIdentifier(), UserStatus.DEACTIVATION_PENDING);
        deactivationDTO.setUserDetails(keycloakUser);
        deactivationDTO.setEnrolmentKeyDetails(enrolmentKeyDTO);

        task.setDifference(mapper.convertToJson(deactivationDTO));

        persistenceService.save(task);
        eventService.createAndPublishEvent(user.getUrid(), currentUser.getUrid(),
                                           UserStatus.DEACTIVATION_PENDING.name(),
                                           EventType.USER_STATUS_CHANGED, CHANGE_USER_STATUS);

        taskEventService.createAndPublishTaskAndAccountRequestEvent(task, currentUser.getUrid());
        serviceAccountAuthorizationService.invalidateUserSessions(user.getIamIdentifier());

        return task.getRequestId();
    }
    
    /**
     * {@inheritDoc}
     */
    public void updateUserDetails(UserDetailsUpdateDTO dto) {
    	this.updateUserDetails(dto.getCurrent(), dto.getDiff());
    }

    /**
     * {@inheritDoc}
     */
    public void updateUserDetails(UserDetailsUpdateTaskDetailsDTO dto) {
    	this.updateUserDetails(dto.getCurrent(), dto.getChanged());
    }

    public void updateUserRecoveryMethods(RecoveryMethodUpdateRequest recoveryMethodUpdateRequest) {
        UserRepresentation storedUserRepresentation = Optional.ofNullable(userAdministrationService.findByIamId(getCurrentUser().getIamIdentifier()))
            .orElseThrow(() -> new IllegalArgumentException("Illegal user representation"));

        Optional.ofNullable(recoveryMethodUpdateRequest.getEmail()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "recoveryEmailAddress", Collections.singletonList(value)));
        Optional.ofNullable(recoveryMethodUpdateRequest.getCountryCode()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "recoveryCountryCode", Collections.singletonList(value)));
        Optional.ofNullable(recoveryMethodUpdateRequest.getPhoneNumber()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "recoveryPhoneNumber", Collections.singletonList(value)));

        userAdministrationService.updateUserDetails(storedUserRepresentation);
    }

    public void setHideRecoveryMethodsNotification() {
        UserRepresentation storedUserRepresentation = Optional.ofNullable(userAdministrationService.findByIamId(getCurrentUser().getIamIdentifier()))
            .orElseThrow(() -> new IllegalArgumentException("Illegal user representation"));

        storedUserRepresentation.getAttributes().put("hideRecoveryMethodsNotification", Collections.singletonList("true"));

        userAdministrationService.updateUserDetails(storedUserRepresentation);
    }
    
    private void updateUserDetails(UserDetailsDTO currentDto, UserDetailsDTO changedDto) {
        User storedUser = Optional.ofNullable(userRepository.findByUrid(currentDto.getUrid()))
            .orElseThrow(() -> new UkEtsException(String.format("The user %s does not exist in registry DB", currentDto.getUrid())));

        UserRepresentation storedUserRepresentation = Optional.ofNullable(userAdministrationService.findByIamId(storedUser.getIamIdentifier()))
            .orElseThrow(() -> new IllegalArgumentException("Illegal user representation"));
        
        //update both registry and keycloak DB with first/last name
        Optional.ofNullable(changedDto.getFirstName()).ifPresent(value -> {
            storedUserRepresentation.setFirstName(value);
            storedUser.setFirstName(value); 
        });
        Optional.ofNullable(changedDto.getLastName()).ifPresent(value -> {
            storedUserRepresentation.setLastName(value);
            storedUser.setLastName(value); 
        });
        Optional.ofNullable(changedDto.getAlsoKnownAs()).ifPresent(value -> {
            storedUserRepresentation.getAttributes().put("alsoKnownAs", Collections.singletonList(value));
            storedUser.setKnownAs(value);
        });
        if (changedDto.getAlsoKnownAs() != null || changedDto.getFirstName() != null 
                || changedDto.getLastName() != null) {
            if (!hasAnyAdminRole(storedUser.getIamIdentifier())) {
                if (!StringUtils.isEmpty(changedDto.getAlsoKnownAs())) {
                    storedUser.setDisclosedName(storedUser.getKnownAs());
                } else if (StringUtils.isEmpty(storedUser.getKnownAs())) {
                    storedUser.setDisclosedName(Utils.concat(" ", storedUser.getFirstName(), storedUser.getLastName()));
                }
            }
            userRepository.save(storedUser);
        }
        //replace the changed attributes in the existing user
        replaceUpdatedAttributes(changedDto, storedUserRepresentation);

        userAdministrationService.updateUserDetails(storedUserRepresentation);
    }

    private void replaceUpdatedAttributes(UserDetailsDTO changed,
        UserRepresentation storedUserRepresentation) {
        Optional.ofNullable(changed.getCountryOfBirth()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "countryOfBirth", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkBuildingAndStreet()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workBuildingAndStreet", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkBuildingAndStreetOptional()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workBuildingAndStreetOptional", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkBuildingAndStreetOptional2()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workBuildingAndStreetOptional2", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkTownOrCity()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workTownOrCity", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkStateOrProvince()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workStateOrProvince", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkPostCode()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workPostCode", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkCountry()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workCountry", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkMobileCountryCode()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workMobileCountryCode", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkMobilePhoneNumber()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workMobilePhoneNumber", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkAlternativeCountryCode()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workAlternativeCountryCode", Collections.singletonList(value)));
        Optional.ofNullable(changed.getWorkAlternativePhoneNumber()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "workAlternativePhoneNumber", Collections.singletonList(value)));
        Optional.ofNullable(changed.getNoMobilePhoneNumberReason()).ifPresent(value -> storedUserRepresentation.getAttributes().put(
            "noMobilePhoneNumberReason", Collections.singletonList(value)));
        if (changed.getBirthDate() != null) {
            String formattedDate = changed.getBirthDate().getDay() + "/" + changed.getBirthDate().getMonth() +
                    "/" + changed.getBirthDate().getYear();
            storedUserRepresentation.getAttributes().put("birthDate", Collections.singletonList(formattedDate));
        }
        Optional.ofNullable(changed.getMemorablePhrase())
            .ifPresent(value -> storedUserRepresentation.getAttributes().put(
                "memorablePhrase", Collections.singletonList(value)));
    }

    public void revertDeactivationPending(String urid, String claimantUrid) {
        User user = userRepository.findByUrid(urid);
        if (user == null) {
            throw new UkEtsException(String
                .format("Requested revert state for user with id:%s which does not exist", urid));
        }
        userAdministrationService.updateUserState(user.getIamIdentifier(), user.getPreviousState());		
        user.setState(user.getPreviousState());
        userRepository.save(user);
        eventService.createAndPublishEvent(urid, claimantUrid,
                                           user.getPreviousState().name(),
                                           EventType.USER_STATUS_CHANGED, CHANGE_USER_STATUS);
    }

    public void deactivateUser(String urid, String claimantUrid) {
        if (taskRepository.findPendingTasksForUser(urid).size() > 1 || 
                !taskRepository.findPendingTasksClaimedByUser(urid).isEmpty()) {
            throw new BusinessRuleErrorException(ErrorBody.from(
                    "You cannot approve deactivation as pending tasks exist for this user"));
        }
        User user = userRepository.findByUrid(urid);
        if (user == null) {
            throw new UkEtsException(String
                .format("Requested deactivation for user with id:%s which does not exist", urid));
        }
        UserRepresentation userRepresentation = userAdministrationService
                    .findByIamId(user.getIamIdentifier());
        if (userRepresentation == null) {
            throw new UkEtsException(
                String.format("The user %s does not exist in keycloak DB", user.getId()));
        }

        userRepresentation.getAttributes().put("deactivatedEmail", Arrays.asList(userRepresentation.getEmail()));
        // replace colons since they are not accepted inside an email address in keycloak
        String timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).toString().replace(":", ".");
        String email = userRepresentation.getEmail();
        String name   = email.substring(0, email.lastIndexOf('@'));
        String domain = email.substring(email.lastIndexOf('@'));
        userRepresentation.setEmail(name + "_DEACTIVATED_" + timestamp + domain);
        userAdministrationService.updateUserDetails(userRepresentation);
        userAdministrationService.updateUserState(user.getIamIdentifier(), UserStatus.DEACTIVATED);
        user.setState(UserStatus.DEACTIVATED);
        userRepository.save(user);
        eventService.createAndPublishEvent(urid, claimantUrid,
                                           UserStatus.DEACTIVATED.name(),
                                           EventType.USER_STATUS_CHANGED, CHANGE_USER_STATUS);
    }
    
	public void validateUserUpdateRequest(String urid, UserDetailsUpdateType updateType) {
		switch (updateType) {
		case UPDATE_USER_DETAILS -> checkForBlockingTasksForUserDetailsUpdateRequest(urid);
		case DEACTIVATE_USER -> checkForBlockingTasksForUserDeactivationRequest(urid);
		default -> throw new RuntimeException("Failed to resolve the User Details Update Type.");
		}
	}

    private void validateUserPhoneNumbers(UserDetailsUpdateDTO userDetailsUpdateDTO) {
        UserDetailsDTO changedUserDetailsDTO = userDetailsUpdateDTO.getDiff();
        UserDetailsDTO currentUserDetailsDto = userDetailsUpdateDTO.getCurrent();

        // work mobile phone number
        validateMobilePhoneNumber(currentUserDetailsDto.getWorkMobileCountryCode(), changedUserDetailsDTO.getWorkMobileCountryCode(),
            currentUserDetailsDto.getWorkMobilePhoneNumber(), changedUserDetailsDTO.getWorkMobilePhoneNumber());

        // work alternative phone number
        validatePhoneNumber(currentUserDetailsDto.getWorkAlternativeCountryCode(), changedUserDetailsDTO.getWorkAlternativeCountryCode(),
            currentUserDetailsDto.getWorkAlternativePhoneNumber(), changedUserDetailsDTO.getWorkAlternativePhoneNumber());
    }

    private void validatePhoneNumber(String currentCode, String changedCode, String currentPhone, String changedPhone) {
        getNewPhoneNumber(currentCode, changedCode, currentPhone, changedPhone);
    }

    private void validateMobilePhoneNumber(String currentCode, String changedCode, String currentPhone, String changedPhone) {
        Phonenumber.PhoneNumber number = getNewPhoneNumber(currentCode, changedCode, currentPhone, changedPhone);
        if (number != null) {
            fixedLineNotAllowedCheck(number);
        }
    }

    private void fixedLineNotAllowedCheck(Phonenumber.PhoneNumber number) {
        if (PhoneNumberUtil.getInstance().getNumberType(number) == PhoneNumberUtil.PhoneNumberType.FIXED_LINE) {
            throw new BusinessRuleErrorException(ErrorBody.from("Enter a valid mobile number"));
        }
    }

    private Phonenumber.PhoneNumber getNewPhoneNumber(String currentCode,
                                                      String changedCode,
                                                      String currentPhone,
                                                      String changedPhone) {

        if (changedCode == null && changedPhone == null) {
            return null;
        }

        // allow phone removal
        if (isBlank(changedCode) && isBlank(changedPhone)) {
            return null;
        }

        String code = Stream.of(changedCode, currentCode)
            .filter(Objects::nonNull)
            .map(PhoneNumberUtil::normalizeDigitsOnly)
            .findFirst()
            .orElse("");

        String phone = Stream.of(changedPhone, currentPhone)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("");

        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneUtil.parse("+" + code + phone, "");
            if (!phoneUtil.isValidNumber(number)) {
                throw new BusinessRuleErrorException(ErrorBody.from("Invalid phone number given"));
            }
            return number;
        } catch (NumberParseException e) {
            throw new BusinessRuleErrorException(ErrorBody.from("Invalid phone number format"));
        }
    }

    private boolean isBlank(String str) {
        return str != null && str.isBlank();
    }
		
	private void checkForBlockingTasksForUserDetailsUpdateRequest(String urid) {
		if (CollectionUtils.isNotEmpty(taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DETAILS_UPDATE_REQUEST, urid))) {
			throw new BusinessRuleErrorException(
					ErrorBody.from("Another request to update user details is pending approval"));
		}
	}
		
	private void checkForBlockingTasksForUserDeactivationRequest(String urid) {
		final String REQUESTS_PENDING_APPROVAL_ERROR_MSG = "There are requests pending approval";
		
		if(CollectionUtils.isNotEmpty(taskRepository.findPendingTasksByTypeAndUser(RequestType.USER_DEACTIVATION_REQUEST, urid))) {
			throw new BusinessRuleErrorException(
					ErrorBody.from("Another request to deactivate user is pending approval"));
		}

		final List<RequestType> blockingTaskTypes = List.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
															RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
															RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, 
															RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);

		if(CollectionUtils.isNotEmpty(taskRepository.findPendingTasksByTypesAndUser(blockingTaskTypes, urid))) {
			throw new BusinessRuleErrorException(ErrorBody.from(REQUESTS_PENDING_APPROVAL_ERROR_MSG));
		}
		
		boolean arPendingToBeReplaced = arUpdateActionRepository.fetchPendingArUpdateActionsByType(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST)
											                    .stream()
											                    .anyMatch(action -> action.getToBeReplacedUrid().equals(urid));
		
		if(arPendingToBeReplaced) {
			throw new BusinessRuleErrorException(ErrorBody.from(REQUESTS_PENDING_APPROVAL_ERROR_MSG));
		}

		List<Task> accountOpeningTasks = taskRepository.findPendingTasksByType(RequestType.ACCOUNT_OPENING_REQUEST);
		for (Task task : accountOpeningTasks) {
			Optional<String> diff = Optional.ofNullable(task.getDifference());
			if (diff.isPresent() && !diff.get().isBlank()) {
				AccountDTO openingAccountDTO = mapper.convertToPojo(diff.get(), AccountDTO.class);
				boolean blockingTaskExists = openingAccountDTO.getAuthorisedRepresentatives()
								                              .stream()
								                              .map(AuthorisedRepresentativeDTO::getUrid)
								                              .anyMatch(userId -> userId.equals(urid));
				if (blockingTaskExists) {
					throw new BusinessRuleErrorException(ErrorBody.from(REQUESTS_PENDING_APPROVAL_ERROR_MSG));
				}
			}
		}
		 
		if(CollectionUtils.isNotEmpty(taskRepository.findPendingTasksClaimedByUser(urid))) {
			throw new BusinessRuleErrorException(
					ErrorBody.from("The selected user is claimant of one or more pending tasks"));
		}
	}
}
