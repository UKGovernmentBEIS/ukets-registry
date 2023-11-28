package gov.uk.ets.registry.api.task;

import static gov.uk.ets.commons.logging.RequestParamType.DTO;
import static gov.uk.ets.commons.logging.RequestParamType.FILE_ID;
import static gov.uk.ets.commons.logging.RequestParamType.TASK_REQUEST_ID;
import static org.springframework.http.HttpHeaders.*;

import gov.uk.ets.commons.logging.MDCParam;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.accountaccess.service.AccountAccessService;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInput;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.AuthorityUserMandatoryCommentForTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.ARsCanAssignOnlyToOtherARsTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.ARsOfSameAccountTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.AdminCanAssignAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.AuthorityCanAssignAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.JuniorAdminCanOnlyAssignWhenAssignedToThem;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.SeniorRegistryAdminCanOnlyAssignToSeniorOrJuniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.AdminCanClaimAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.AuthorityCanClaimAccountBasedTasksThatHasAccessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.ReadOnlyAdminCannotClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CannotUpdateCompletedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ClaimantCanCompleteTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.ClaimantCanUpdateTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.CommentMandatoryWhenRejected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view.ARsCanViewSpecificTaskTypes;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view.ARsCanViewTasksForTheirAccounts;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view.AdminsOrInitiatorCanViewAccountOpeningFileRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.view.UsersCanOnlyViewTaskWithDirectReferenceToThem;
import gov.uk.ets.registry.api.common.search.PageParameters;
import gov.uk.ets.registry.api.common.search.SearchResponse;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.task.domain.types.TaskUpdateAction;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.service.TaskService;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.task.web.mappers.TaskSearchPageableMapper;
import gov.uk.ets.registry.api.task.web.mappers.TaskSearchResultDTOMapper;
import gov.uk.ets.registry.api.task.web.model.TaskBulkActionSuccess;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.task.web.model.TaskSearchResult;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.AssignUsersCriteriaDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.web.UserSearchByNameResultDTO;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST Controller for tasks.
 */
@Tag(name = "Task Management")
@RestController
@RequestMapping(path = "/api-registry", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final TaskSearchResultDTOMapper dtoMapper;

    private final TaskSearchPageableMapper pageableMapper;

    private final TaskEventService taskEventService;

    private final UserAdministrationService userAdministrationService;

    private final AccountAccessService accountAccessService;

    /*
     * Retrieves a task based on its task ID.
     * @param requestId The unique business identifier.
     * @return a task.
     */
    @Protected(
        {
            ARsCanViewSpecificTaskTypes.class,
            ARsCanViewTasksForTheirAccounts.class,
            UsersCanOnlyViewTaskWithDirectReferenceToThem.class,
            AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule.class,
            AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule.class
        }
    )
    @GetMapping(path = "/tasks.get", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskDetailsDTO getTask(@RuleInput(RuleInputType.TASK_REQUEST_ID)
                                  @RequestParam @MDCParam(TASK_REQUEST_ID) Long requestId) {
        return taskService.getTaskDetails(requestId);
    }

    /**
     * Searches for tasks according to the passed criteria.
     *
     * @param criteria       The {@link TaskSearchCriteria} search criteria
     * @param pageParameters The {@link PageParameters} parameters
     * @return The {@link SearchResponse} for {@link TaskSearchResult} response.
     */
    @GetMapping(path = "/tasks.list", produces = MediaType.APPLICATION_JSON_VALUE)
    public SearchResponse<TaskSearchResult> search(TaskSearchCriteria criteria, PageParameters pageParameters,
                                                   @RequestHeader(AUTHORIZATION) String bearerToken,
                                                   @RequestHeader(name = "Is-Report", required = false)
                                                       boolean isReport) {
        Pageable pageable = pageableMapper.get(pageParameters);
        Page<TaskProjection> searchResults = taskService.search(criteria, pageable, isReport, bearerToken);
        SearchResponse<TaskSearchResult> response = new SearchResponse<>();
        pageParameters.setTotalResults(searchResults.getTotalElements());
        response.setPageParameters(pageParameters);
        List<AccountAccess> accesses = accountAccessService.getAllActiveAccessesForCurrentUser();
        response.setItems(
            searchResults.getContent().stream()
                .map(projection -> dtoMapper.get(projection, accesses))
                .collect(Collectors.toList())
        );
        return response;
    }

    /**
     * Performs updates to the task details.
     *
     * @param requestId        The unique business identifier.
     * @param updateInfo       The information to update the task.
     * @param taskUpdateAction The update action type.
     * @param taskDetails      The task details.
     * @return the updated task details dto.
     */
    @Protected({CannotUpdateCompletedTaskRule.class, ClaimantCanUpdateTaskRule.class})
    @PostMapping(path = "tasks.update")
    @ResponseBody
    public TaskDetailsDTO updateTask(@RuleInput(RuleInputType.TASK_REQUEST_ID) @RequestParam
                                     @MDCParam(TASK_REQUEST_ID) Long requestId,
                                     @RequestParam String updateInfo,
                                     @RequestParam TaskUpdateAction taskUpdateAction,
                                     @RequestBody TaskDetailsDTO taskDetails) {
        return taskService.updateTask(updateInfo, taskDetails, taskUpdateAction);
    }

    /**
     * Claims the corresponded tasks of the passed request ids to the current user.
     *
     * @param requestIds The request ids of tasks
     * @param comment    The comment provided by the user
     * @return {@link TaskBulkActionSuccess} response for success. For error response look at
     * {@link TaskControllerAdvice#applicationExceptionHandler}
     */
    @Protected({
        ReadOnlyAdminCannotClaimTaskRule.class,
        AuthorityUserMandatoryCommentForTaskRule.class,
        AdminCanClaimAccountBasedTasksThatHasAccessRule.class,
        AuthorityCanClaimAccountBasedTasksThatHasAccessRule.class
    })
    @PostMapping(value = "/tasks.claim", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public TaskBulkActionSuccess claim(@RuleInput(RuleInputType.TASK_REQUEST_IDS) @RequestParam
                                       @MDCParam(TASK_REQUEST_ID) List<Long> requestIds,
                                       @RuleInput(RuleInputType.TASK_COMPLETE_COMMENT)
                                       @RequestParam(required = false)
                                       @Size(max = 1024) String comment) {
        taskService.claim(requestIds, comment);
        return new TaskBulkActionSuccess(requestIds.size());
    }

    /**
     * Claims the corresponded tasks of the passed request ids to the user of the passed urid.
     *
     * @param urid       The urid of the user that the tasks are going to be claimed to.
     * @param requestIds The request ids of tasks
     * @param comment    The comment provided by the user
     * @return {@link TaskBulkActionSuccess} response for success. For error response look at
     * {@link TaskControllerAdvice#applicationExceptionHandler}
     */
    @Protected({
        SeniorRegistryAdminCanOnlyAssignToSeniorOrJuniorAdminRule.class,
        JuniorAdminCanOnlyAssignToAnotherJuniorAdminRule.class,
        JuniorAdminCanOnlyAssignWhenAssignedToThem.class,
        ARsCanAssignOnlyToOtherARsTaskRule.class,
        ARsOfSameAccountTaskRule.class,
        AdminCanAssignAccountBasedTasksThatHasAccessRule.class,
        AuthorityCanAssignAccountBasedTasksThatHasAccessRule.class
    })
    @PostMapping("/tasks.assign")
    public TaskBulkActionSuccess assign(@RuleInput(RuleInputType.TASK_ASSIGNEE_URID) @RequestParam String urid,
                                        @RuleInput(RuleInputType.TASK_REQUEST_IDS) @RequestParam
                                        @MDCParam(TASK_REQUEST_ID) List<Long> requestIds,
                                        @RequestParam(required = false) @Size(max = 1024) String comment) {
        taskService.assign(requestIds, urid, comment);
        return new TaskBulkActionSuccess(requestIds.size());
    }

    /**
     * Retrieves the users list in order to assign the task.
     *
     * @param input the search criteria include a string to filter firstname and lastnames and the account identifiers
     * @return a list of users
     */
    @GetMapping(path = "/tasks.get.candidate-assignees", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserSearchByNameResultDTO>> getCandidateAssignees(@MDCParam(DTO) AssignUsersCriteriaDTO input) {
        List<UserSearchByNameResultDTO> users = userAdministrationService.findAccountTaskAssignEntitledUsers(input);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Approves the provided task.
     *
     * @param requestId The task unique business identifier.
     * @param comment   The comment provided by the user.
     */
    @Protected(
        {
            ClaimantCanCompleteTaskRule.class,
            CommentMandatoryWhenRejected.class,
            AuthorityUserMandatoryCommentForTaskRule.class
        }
    )
    @PostMapping("/tasks.complete")
    public TaskCompleteResponse complete(@RuleInput(RuleInputType.TASK_REQUEST_ID) @RequestParam
                                         @MDCParam(TASK_REQUEST_ID) Long requestId,
                                         @RuleInput(RuleInputType.TASK_OUTCOME) TaskOutcome taskOutcome,
                                         @RuleInput(RuleInputType.TASK_COMPLETE_COMMENT) @RequestParam(required = false)
                                                 String comment) {
        return taskService.complete(requestId, taskOutcome, comment);
    }

    /**
     * Retrieves header information about a task (e.g. who initiated it, its current status etc.)
     * TODO: can we add the setCurrentUserClaimant to the  {@link #getTask} and remove this one?
     *
     * @param requestId The task unique business identifier.
     * @return The details of the task.
     */
    @GetMapping(path = "/tasks.get.header", produces = MediaType.APPLICATION_JSON_VALUE)
    public TaskDetailsDTO getTaskHeader(@RequestParam @MDCParam(TASK_REQUEST_ID) Long requestId) {
        return taskService.getTaskDetailsDTO(requestId);
    }

    /**
     * Retrieves the requested template file of the task.
     *
     * @param fileId The id of the file
     * @return The task template file
     */
    @GetMapping(path = "/tasks.get.template-file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getTaskTemplateFile(@RequestParam @MDCParam(FILE_ID) Long fileId) {
        HttpHeaders headers = new HttpHeaders();
        UploadedFile file = taskService.getTaskFileById(fileId);
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(file.getFileName())
                .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }

    /**
     * Retrieves the requested file of the task.
     *
     * @param input The File information of the requested document
     * @return The requested document
     */
    @Protected({
            AdminsOrInitiatorCanViewAccountOpeningFileRule.class,
            ARsCanViewTasksForTheirAccounts.class,
            UsersCanOnlyViewTaskWithDirectReferenceToThem.class,
            AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule.class,
            AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule.class
    })
    @GetMapping(path = "/tasks.get.task-file", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getTaskFile(@RuleInput(RuleInputType.TASK_FILE) @MDCParam(DTO)
                                                          TaskFileDownloadInfoDTO input) {
        HttpHeaders headers = new HttpHeaders();
        UploadedFile file = taskService.getRequestedTaskFile(input);
        headers.add(ACCESS_CONTROL_EXPOSE_HEADERS, CONTENT_DISPOSITION);
        headers.add(CONTENT_DISPOSITION,
            ContentDisposition.builder("attachment").filename(file.getFileName())
                .build().toString());
        return new ResponseEntity<>(file.getFileData(), headers, HttpStatus.OK);
    }

    /**
     * Retrieves the history (events & comments) of the provided task.
     *
     * @param requestId The task unique business identifier.
     * @return the task history
     */
    @Protected({
            ARsCanViewSpecificTaskTypes.class,
            ARsCanViewTasksForTheirAccounts.class,
            UsersCanOnlyViewTaskWithDirectReferenceToThem.class,
            AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule.class,
            AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule.class
    })
    @GetMapping(path = "/tasks.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<AuditEventDTO> getTaskHistory(@RuleInput(RuleInputType.TASK_REQUEST_ID)
                                                  @RequestParam @MDCParam(TASK_REQUEST_ID) Long requestId) {
        return taskEventService.getTaskHistory(requestId);
    }

    @Protected({
            ARsCanViewSpecificTaskTypes.class,
            ARsCanViewTasksForTheirAccounts.class,
            UsersCanOnlyViewTaskWithDirectReferenceToThem.class,
            AdminCanPerformActionsForAccountBasedTasksThatHasAccessRule.class,
            AuthorityCanPerformActionsForAccountBasedTasksThatHasAccessRule.class
    })
    @PostMapping(path = "/tasks.get.history", produces = MediaType.APPLICATION_JSON_VALUE)
    public AddCommentSuccess addCommentToHistory(@RuleInput(RuleInputType.TASK_REQUEST_ID)
                                                     @RequestParam @MDCParam(TASK_REQUEST_ID) Long requestId,
                                                 @RequestParam String comment) {
        taskService.comment(requestId, comment);
        return new AddCommentSuccess(requestId);
    }

    private static class AddCommentSuccess {
        private Long requestId;

        public AddCommentSuccess(Long requestId) {
            this.requestId = requestId;
        }

        public Long getRequestId() {
            return requestId;
        }
    }
}
