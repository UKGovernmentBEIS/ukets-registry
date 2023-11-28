package gov.uk.ets.registry.api.file.upload.allocationtable.services;

import gov.uk.ets.registry.api.allocation.service.AllocationUtils;
import gov.uk.ets.registry.api.allocation.type.AllocationCategory;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AuthorityUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlyAuthorityUserCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableUploadDetails;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableError;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation service for allocation table upload tasks.
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AllocationTableUploadTaskService implements TaskTypeService<TaskDetailsDTO> {
    private final UploadedFilesRepository uploadedFilesRepository;
    private final AllocationTableService allocationTableService;
    private final AllocationUtils allocationUtils;
    private final EventService eventService;
    private final UserService userService;
    private final Mapper mapper;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.ALLOCATION_TABLE_UPLOAD_REQUEST);
    }

    @Override
    public TaskDetailsDTO getDetails(TaskDetailsDTO taskDetails) {
        UploadedFile file = uploadedFilesRepository.findFirstByTaskRequestId(taskDetails.getRequestId());
        taskDetails.setFileName(file.getFileName());
        taskDetails.setFileSize(file.getFileSize());
        return taskDetails;
    }

    @Override
    @Transactional
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        return uploadedFilesRepository.findFirstByTaskRequestId(infoDTO.getTaskRequestId());
    }

    @Protected({
        FourEyesPrincipleRule.class,
        AuthorityUserRule.class
    })
    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.UPLOAD_ALLOCATION_TABLE_COMPLETED)
    public TaskCompleteResponse complete(TaskDetailsDTO task, TaskOutcome outcome, String comment) {

        if (TaskOutcome.REJECTED.equals(outcome)) {
            eventService.createAndPublishEvent(task.getRequestId().toString(), userService.getCurrentUser().getUrid(), comment,
                EventType.REJECT_ALLOCATION_TABLE, "Reject allocation table");
            return defaultResponseBuilder(task).build();
        }

        AllocationCategory allocationCategory = Optional.ofNullable(task.getDifference())
            .map(s -> mapper.convertToPojo(s, AllocationTableUploadDetails.class))
            .map(AllocationTableUploadDetails::getAllocationCategory)
            .orElseThrow();
        if (allocationUtils.hasPendingAllocationOrTransactions(allocationCategory)) {
            throw AllocationTableBusinessRulesException.create(AllocationTableError.PENDING_ALLOCATION_REQUEST_TASK_OR_ALLOCATION_JOB);
        }

        try {
            allocationTableService.submitAllocationEntries(task.getRequestId());            
            eventService.createAndPublishEvent(task.getRequestId().toString(), userService.getCurrentUser().getUrid(), comment,
                    EventType.APPROVE_ALLOCATION_TABLE, "Approve allocation table");
        } catch (IOException e) {
            throw new UkEtsException(e.getMessage());
        }
        return defaultResponseBuilder(task).build();
    }

    @Protected({
        OnlyAuthorityUserCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }
}
