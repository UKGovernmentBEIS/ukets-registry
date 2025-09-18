package gov.uk.ets.registry.api.user;

import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.AdminsOrSameUserCanRequestUserDetailsRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AffectedUserCannotPerformActionRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.CanOnlyRequestUsersWithSameRoleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorOrJuniorAdministratorRule;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for users.
 */
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class UserController {

    /**
     * Service for users.
     */
    private final UserService userService;

    /**
     * Service for URIDs.
     */
    private final UserGeneratorService userGeneratorService;

    /**
     * Service for the task events.
     */
    private final TaskEventService taskEventService;
    /**
     * Service for the uploaded files.
     */
    private final FileUploadService fileUploadService;

    /**
     * Registers a user.
     *
     * @param user a user transfer object.
     */
    @PostMapping(path = "users.create", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDTO> registerUser(@RequestBody @Valid UserDTO user) {
        userService.registerUser(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Retrieves a user.
     *
     * @param urid The URID.
     * @return a user.
     */
    @GetMapping(path = "users.get", params = "urid", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDTO> get(@RequestParam String urid) {
        ResponseEntity<UserDTO> result = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (!userGeneratorService.validateURID(urid)) {
            result = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } else {
            UserDTO user = userService.getUser(urid);
            if (user != null) {
                result = new ResponseEntity<>(user, HttpStatus.OK);
            }
        }
        return result;
    }

    /**
     * Retrieves the user history (comments and events).
     *
     * @param urid The user identifier
     * @return A list of events
     */
    @Protected(AdminsOrSameUserCanRequestUserDetailsRule.class)
    @GetMapping(path = "users.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditEventDTO>> getTaskHistory(
        @RequestParam @RuleInput(RuleInputType.URID) String urid) {
        return new ResponseEntity<>(taskEventService.getTaskHistoryByUser(urid), HttpStatus.OK);
    }

    /**
     * Retrieves the user files.
     *
     * @param urid The user identifier
     * @return A list of files
     */
    @Protected(AdminsOrSameUserCanRequestUserDetailsRule.class)
    @GetMapping(path = "users.get.files", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserFileDTO>> getUserFiles(@RequestParam @RuleInput(RuleInputType.URID) String urid) {
        return new ResponseEntity<>(userService.getUserFiles(urid), HttpStatus.OK);
    }
    
    /**
     * Deletes both userFile and uploadedFile.
     *
     * @param id The user identifier
     * @param fileId The file identifier
     */
    @Protected({
    	SeniorOrJuniorAdministratorRule.class, 
    	AffectedUserCannotPerformActionRule.class
    	})
    @DeleteMapping(path = "users.delete.file")
    public ResponseEntity<Void> deleteUserFile(
    		@RequestParam @RuleInput(RuleInputType.URID) String id, @RequestParam Long fileId) {
    	userService.deleteUserFile(id, fileId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Download user file by id
     *
     * @param fileId The id of the file
     * @return The file
     */
    @GetMapping(path = "/users.get.file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getUserFile(@RequestParam Long fileId) {
        HttpHeaders headers = new HttpHeaders();
        UploadedFile file = userService.getFileById(fileId);
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(file.getFileName())
                .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }

    /**
     * Retrieves the enrolment key details for the specific user.
     *
     * @param urid The user identifier
     * @return The enrolment key information
     */
    @Protected(AdminsOrSameUserCanRequestUserDetailsRule.class)
    @GetMapping(path = "users.get.enrolment-key", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EnrolmentKeyDTO> getEnrollmentKey(@RequestParam @RuleInput(RuleInputType.URID) String urid) {
        return new ResponseEntity<>(userService.getEnrolmentKeyDetails(urid), HttpStatus.OK);
    }

    /**
     * Retrieves authorised representatives on the provided account holder adding the current user.
     *
     * @param accountHolderId The account holder id.
     * @return some users.
     */
    @GetMapping(path = "users.get.authorised-representatives", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserDTO>> getUsersOnAccountHolder(
        @RequestParam(required = false) Long accountHolderId,
        @RequestParam(required = false, defaultValue = "true") boolean includeCurrentUser) {
        List<UserDTO> users = userService.getAuthorisedRepresentatives(accountHolderId, includeCurrentUser);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Enrols a user.
     *
     * @param enrolmentKey the enrolment key provided by the user. For error response look at
     *                     {@link gov.uk.ets.registry.api.user.UserActionExceptionControllerAdvice#applicationExceptionHandler}
     */
    @PutMapping(path = "users.enrol")
    @ResponseStatus(HttpStatus.OK)
    public void enrolUser(@RequestParam String enrolmentKey) {
        userService.enrolUser(enrolmentKey);
    }

    /**
     * Request for a new Registry Activation Code, initiated by a user.
     */
    @PostMapping(path = "users.request.registry-code", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> requestNewRegistryActivationCode() {
        return new ResponseEntity<>(userService.requestNewRegistryActivationCode(), HttpStatus.OK);
    }
    
    @Protected(CanOnlyRequestUsersWithSameRoleRule.class)
    @PostMapping(path = "users.get.basic-info", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<UserDTO>> getUsers(@RequestBody @NotEmpty @RuleInput(RuleInputType.URIDS)
        List<String> urids) {
        return new ResponseEntity<>(userService.getUserDetails(urids), HttpStatus.OK);
    }
}
