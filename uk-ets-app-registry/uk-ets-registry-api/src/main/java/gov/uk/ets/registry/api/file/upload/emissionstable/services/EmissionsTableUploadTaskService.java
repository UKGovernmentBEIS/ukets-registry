package gov.uk.ets.registry.api.file.upload.emissionstable.services;

import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.SeniorAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorAdminCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation service for emissions table upload tasks.
 */
@Service
@RequiredArgsConstructor
public class EmissionsTableUploadTaskService implements TaskTypeService<TaskDetailsDTO> {

    private final UploadedFilesRepository uploadedFilesRepository;
    private final EmissionsTableService emissionsTableService;
    private final EmissionsTableEventService emissionsTableEventService;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.EMISSIONS_TABLE_UPLOAD_REQUEST);
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
        SeniorAdminRule.class
    })
    @Override
    @Transactional
    public TaskCompleteResponse complete(TaskDetailsDTO task, TaskOutcome outcome, String comment) {

        UploadedFile file = uploadedFilesRepository.findFirstByTaskRequestId(task.getRequestId());
        String what =
                String.format("Emissions table request %s", outcome == TaskOutcome.APPROVED ? "approved" : "rejected");
        Set<Long> unprocessedCompliantEntityIdentifers = new HashSet<>();
        if (TaskOutcome.APPROVED == outcome) {
            unprocessedCompliantEntityIdentifers = emissionsTableService.submitEmissionEntries(file, task.getCompletedDate());
            emissionsTableEventService.createAndPublishEmissionsCompliantEntityEventsWithCurrentAndUpdatedValues(file.getFileData(), what,unprocessedCompliantEntityIdentifers);
        }
        emissionsTableEventService.createAndPublishEvent(task.getRequestId().toString(), comment,
            EventType.APPROVE_EMISSIONS_TABLE, what);
        emissionsTableEventService.createAndPublishEmissionsUploadedAccountEvents(file.getFileData(), comment,
            EventType.APPROVE_ACCOUNT_EMISSIONS, what , unprocessedCompliantEntityIdentifers);
        emissionsTableEventService.createAndPublishEmissionsUploadedCompliantEntityEvents(file.getFileData(), comment,
            EventType.APPROVE_COMPLIANT_ENTITY_EMISSIONS, what , unprocessedCompliantEntityIdentifers);


        return defaultResponseBuilder(task).build();
    }

    @Protected({
        OnlySeniorRegistryAdminCanBeAssignedTaskRule.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        OnlySeniorAdminCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }
}
