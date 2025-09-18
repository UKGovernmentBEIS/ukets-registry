package uk.gov.ets.registration.user;

import static gov.uk.ets.commons.logging.RequestParamType.USER_ID;
import static uk.gov.ets.registration.user.UserAttributes.REGISTERED_ON_DATE;
import static uk.gov.ets.registration.user.UserAttributes.REGISTRATION_IN_PROGRESS;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.commons.logging.MDCWrapper;
import gov.uk.ets.commons.logging.SecurityLog;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.gov.ets.registration.user.configuration.service.BusinessConfigurationService;
import uk.gov.ets.registration.user.exception.EmailAlreadyRegistered;
import uk.gov.ets.registration.user.exception.UserRegistrationException;
import uk.gov.ets.registration.user.service.UserRegistrationService;
import uk.gov.ets.registration.user.validation.UserRegistrationValidator;

@RestController
@RequestMapping(path = "/api-registration", produces = "application/json")
@CrossOrigin(origins = {"${user.registration.keycloak.verifyEmailRedirectUrl}"})
@Validated
@Log4j2
public class UserRegistrationController {

    private static final String REGISTRATION_START = "registration_start";
    private static final String REGISTRATION_END = "registration_end";
    private static final String CANNOT_FIND_USER_AFTER_REGISTRATION =
        "User was registered in Keycloak but after that we could not find user in Keycloak!";
    private static final String COULD_NOT_SEND_VERIFICATION_EMAIL_DELETING_USER =
        "Could not send verification email. Deleting user.";
    private static final String COULD_NOT_CREATE_USER_REGISTRY_DELETING_USER =
            "Could not create user in registry database. Deleting user.";
    @Autowired
    private UserRegistrationService service = null;

    /**
     * Validator of user registration.
     */
    @Autowired
    private UserRegistrationValidator userRegistrationValidator;

    @Autowired
    private BusinessConfigurationService businessConfigurationService;

    @Value("${user.registration.delete.user.time.threshold.in.seconds}")
    private String deleteUserThresholdTime;

    public UserRegistrationController(BusinessConfigurationService businessConfigurationService) {
        this.businessConfigurationService = businessConfigurationService;
    }

    /**
     * Creates a user with the provided email.
     *
     * @param params the registration params.
     * @return a UserRepresentation
     */
    @SuppressWarnings("java:S4449")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<UserRepresentation> registerUser(@RequestBody @Valid UserRegisterParams params) {
        MDCWrapper.getOne().put(MDCWrapper.Attr.USER_ID, params.getEmail());
        log.info(REGISTRATION_START);
        service.registerUser(params.getEmail());
        //Retrieve the keycloak generated id also...
        UserRepresentation userRepresentation = service.findByEmail(params.getEmail()).orElseThrow(
            () -> {
                log.error(CANNOT_FIND_USER_AFTER_REGISTRATION);
                throw new UserRegistrationException(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        );
        try {
            /* If the user is already registered, which means that REGISTRATION_IN_PROGRESS attribute is null,
             do not send the email UKETS-4049 */
            if (userRepresentation.getAttributes().get(UserAttributes.REGISTRATION_IN_PROGRESS.getName()) != null) {
                service.sendVerifyEmail(userRepresentation.getId(), params.getEmail());
                service.scheduleUnverifiedEmailAndExpiredUserDeletion(userRepresentation.getId());
            } else {
                SecurityLog.log(log, "Received a registration request for a user that is already registered.");
            }
        } catch (Exception e) {
            log.error(COULD_NOT_SEND_VERIFICATION_EMAIL_DELETING_USER);
            service.deleteUser(userRepresentation.getId());
            throw new UserRegistrationException(HttpStatus.SERVICE_UNAVAILABLE.value());
        }
        return new ResponseEntity<>(null, HttpStatus.CREATED);
    }

    /**
     * Verifies the user e-mail address in the keycloak db.
     *
     * @param token the incoming token
     * @return a UserRepresentation
     */
    @PatchMapping(path = "/{token}", consumes = "application/json")
    public UserRepresentation verifyUserEmail(@PathVariable("token") String token) {
        String userId = null;
        try {
            userId = service.validateToken(token);
        } catch (Exception exception) {
            SecurityLog.log(log, exception.getMessage(), exception.getCause());
            throw new UserRegistrationException(HttpStatus.BAD_REQUEST.value());
        }
        UserRepresentation userRepresentation = service.findById(userId);
        MDCWrapper.getOne().put(MDCWrapper.Attr.USER_ID, userRepresentation.getEmail());

        if (!userRepresentation.getAttributes().containsKey(REGISTRATION_IN_PROGRESS.getName())) {
            EmailAlreadyRegistered exception = new EmailAlreadyRegistered(userRepresentation.getEmail());
            SecurityLog.log(log, exception.getMessage(), exception.getCause());
            throw exception;
        }
        if (userRepresentation.isEmailVerified() != null && userRepresentation.isEmailVerified()) {
            return userRepresentation;
        }

        userRepresentation.setEmailVerified(Boolean.TRUE);
        List<String> requiredActions = new ArrayList<>();
        requiredActions.add(UserRegistrationService.CONFIGURE_OTP_REQUIRED_ACTION);
        userRepresentation.setRequiredActions(requiredActions);
        service.update(userRepresentation);

        return userRepresentation;
    }

    /**
     * Get user.
     *
     * @param userId the user id
     * @return the {@link UserRepresentation}
     */
    @GetMapping(path = "/{userId}")
    public UserRepresentation getUser(@PathVariable("userId") @MDCParam(USER_ID) String userId) {
        UserRepresentation userRepresentation = service.findById(userId);
        MDCWrapper.getOne().put(MDCWrapper.Attr.USER_ID, userRepresentation.getEmail());

        // UKETS-4015: Method should not return the user representation, if the registration process has finished.
        if (!userRepresentation.getAttributes().containsKey(REGISTRATION_IN_PROGRESS.getName())) {
            SecurityLog.log(log, "Someone tried to access user details after registration was finished.");
            userRepresentation = null;
        }

        return userRepresentation;
    }

    /**
     * Retrieves either the requested application property if the key is provided
     * or all the available application properties
     *
     * @param key is optional
     * @return the requested Map of application properties
     * @throws IOException
     */
    @GetMapping(path = "/configuration")
    @ResponseBody
    public ResponseEntity<Map<String, String>> getApplicationProperties(@RequestParam(required = false) String key)
        throws IOException {

        final Map<String, String> dto = (key == null) ? businessConfigurationService.getApplicationProperties() :
            businessConfigurationService.getApplicationPropertyByKey(key);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    /**
     * Actually completes the registration by applying the final user information.
     *
     * @param patch the patch
     * @return the {@link UserRepresentation}
     */
    @PutMapping(consumes = "application/json")
    public UserRepresentation putUser(@RequestBody UserRepresentation patch) {
        MDCWrapper.getOne().put(MDCWrapper.Attr.USER_ID, patch.getEmail());
        UserRepresentation userRepresentation = service.completeRegistration(patch);
        log.info(REGISTRATION_END);
        return userRepresentation;
    }

    /**
     * Deletes the user from keycloak database in case something goes wrong during the registration process
     * and the user is not stored in the Registry database.
     *
     * @param userId The keycloak user id
     * @return
     */
    @DeleteMapping()
    public ResponseEntity<Void> deleteUser(@RequestParam @MDCParam(USER_ID) String userId) {
        UserRepresentation userRepresentation = service.findById(userId);

        // Method should not delete the user, if the registration process has not finished.
        if (userRepresentation.getAttributes().containsKey(REGISTRATION_IN_PROGRESS.getName()) ||
                !userRepresentation.getAttributes().containsKey(REGISTERED_ON_DATE.getName())) {
            SecurityLog.log(log, "Someone tried to delete user before registration was finished.");
            throw new UserRegistrationException(HttpStatus.BAD_REQUEST.value());
        }

        LocalDateTime now = LocalDateTime.now();
        List<String> registeredOn = userRepresentation.getAttributes().get(REGISTERED_ON_DATE.getName());
        LocalDateTime registeredTime = LocalDateTime.parse(registeredOn.get(0), DateTimeFormatter.ISO_DATE_TIME);
        Duration timeDiff = Duration.between(registeredTime, now);
        // Method should not delete the user, if the registration process has finished no more than a specific time frame.
        if (timeDiff.getSeconds() > Long.parseLong(deleteUserThresholdTime)) {
            SecurityLog.log(log,
                    String.format("Someone tried to delete user after registration was finished. " +
                            "Tried at %s when registered time was at %s", now, registeredTime));
            throw new UserRegistrationException(HttpStatus.BAD_REQUEST.value());
        }
        service.deleteUser(userId);
        log.error(COULD_NOT_CREATE_USER_REGISTRY_DELETING_USER);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
