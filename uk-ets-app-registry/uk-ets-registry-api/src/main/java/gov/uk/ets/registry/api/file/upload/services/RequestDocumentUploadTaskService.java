package gov.uk.ets.registry.api.file.upload.services;

import gov.uk.ets.registry.api.auditevent.domain.types.DomainAction;
import gov.uk.ets.registry.api.auditevent.repository.DomainEventEntityRepository;
import gov.uk.ets.registry.api.auditevent.web.AuditEventDTO;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.UserRequestedDocumentsCannotBeAssignedToDifferentUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.UserRequestedDocumentsCannotBeClaimedByDifferentUserRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.UserRequestedDocumentsCannotBeCompletedByDifferentUserRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.display.DisplayNameUtils;
import gov.uk.ets.registry.api.file.reference.domain.type.ReferenceFileType;
import gov.uk.ets.registry.api.file.reference.dto.ReferenceFileDTO;
import gov.uk.ets.registry.api.file.reference.service.ReferenceFileService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.service.RequestedDocsService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskServiceException;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.RequestDocumentUploadTaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RequestDocumentUploadTaskService implements TaskTypeService<RequestDocumentUploadTaskDetailsDTO> {

    private final UserService userService;
    private final RequestedDocsService requestedDocsService;
    private final ReferenceFileService referenceFileService;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final Mapper mapper;
    private final DomainEventEntityRepository domainEventEntityRepository;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD,
            RequestType.AR_REQUESTED_DOCUMENT_UPLOAD);
    }

    @Override
    public RequestDocumentUploadTaskDetailsDTO getDetails(TaskDetailsDTO taskDetails) {
        RequestDocumentUploadTaskDetailsDTO result = new RequestDocumentUploadTaskDetailsDTO(taskDetails);
        RequestDocumentsTaskDifference requestDocumentsTaskDifference =
            mapper.convertToPojo(taskDetails.getDifference(), RequestDocumentsTaskDifference.class);
        result.setAccountHolderIdentifier(requestDocumentsTaskDifference.getAccountHolderIdentifier());
        //TODO: Remove if not needed or set from map
        result.setDocumentNames(requestDocumentsTaskDifference.getDocumentNames());
        result.setComment(requestDocumentsTaskDifference.getComment());
        //TODO: Remove if not needed or set from map
        result.setDocumentIds(requestDocumentsTaskDifference.getDocumentIds());
        result.setUploadedFiles(requestedDocsService.getUploadedFiles(taskDetails.getRequestId()));
        result.setReasonForAssignment(fetchLastTaskAssignedComment(taskDetails.getRequestId()));
        result.setReferenceFiles(referenceFileService.getReferenceFileByType(ReferenceFileType.TASK_TEMPLATE_FILE)
            .stream()
            .filter(t -> t.getDocument().appliesForRequestTypes().contains(taskDetails.getTaskType()))
            .map(referenceFile -> ReferenceFileDTO.builder()
                .id(referenceFile.getId())
                .name(referenceFile.getDocument().toString())
                .type(referenceFile.getReferenceType())
                .build()
            ).collect(Collectors.toList()));
        if (RequestType.AR_REQUESTED_DOCUMENT_UPLOAD.equals(taskDetails.getTaskType())) {
            result.setUserUrid(requestDocumentsTaskDifference.getUserUrid());
            User user = userService.getUserByUrid(requestDocumentsTaskDifference.getUserUrid());
            result.setRecipient(DisplayNameUtils.getDisplayName(user));
        } else {
            result.setAccountHolderName(requestDocumentsTaskDifference.getAccountHolderName());
        }
        return result;
    }

    @Override
    @Transactional
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        Optional<UploadedFile> uploadedFileByFileIdOptional =
            uploadedFilesRepository.findById(infoDTO.getFileId());
        if (uploadedFileByFileIdOptional.isEmpty()) {
            throw TaskServiceException.create(TaskActionError.builder().message("File Not found").build());
        }
        return uploadedFileByFileIdOptional.get();
    }

    @Protected({
        UserRequestedDocumentsCannotBeCompletedByDifferentUserRule.class
    })
    @Override
    @Transactional
    @EmitsGroupNotifications(GroupNotificationType.DOCUMENT_REQUEST_FINALISATION)
    public TaskCompleteResponse complete(RequestDocumentUploadTaskDetailsDTO task, TaskOutcome outcome,
                                         String comment) {
        RequestDocumentsTaskDifference difference = mapper.convertToPojo(task.getDifference(),
            RequestDocumentsTaskDifference.class);
        difference.setComment(comment);
        requestedDocsService.submitUploadedFiles(task.getRequestId(), difference);
        return defaultResponseBuilder(task).build();
    }

    @Protected({
        UserRequestedDocumentsCannotBeAssignedToDifferentUserRule.class
    })
    @Override
    public void checkForInvalidAssignPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    @Protected({
        UserRequestedDocumentsCannotBeClaimedByDifferentUserRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
        // implemented for being able to apply permissions using annotations
    }

    private String fetchLastTaskAssignedComment(Long requestId) {
        Pageable topOne = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "creationDate"));
        List<AuditEventDTO> auditEventDTOS = domainEventEntityRepository.findByDomainIdAndDomainActionDesc(
                String.valueOf(requestId),
                List.of(DomainAction.SUBMIT_DOCS_FOR_AH_TASK_ASSIGNED_COMMENT.getAction(),
                        DomainAction.SUBMIT_DOCS_FOR_USER_TASK_ASSIGNED_COMMENT.getAction()),
                topOne
        );
        return auditEventDTOS.isEmpty()
                ? ""
                : auditEventDTOS.get(0).getDescription();
    }
}
