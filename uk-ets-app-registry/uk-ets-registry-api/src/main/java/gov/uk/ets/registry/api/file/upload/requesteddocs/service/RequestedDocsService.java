package gov.uk.ets.registry.api.file.upload.requesteddocs.service;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.error.FileUploadException;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.RequestedDocsParams;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.AccountHolderFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.UserFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.UserFileRepository;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.file.upload.types.FileTypes;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Supports the requested documents upload wizard.
 */
@Service
@RequiredArgsConstructor
public class RequestedDocsService {

    private final FileUploadService fileUploadService;

    private final UploadedFilesRepository uploadedFilesRepository;

    private final AccountHolderRepository accountHolderRepository;

    private final AccountHolderFileRepository accountHolderFileRepository;

    private final UserRepository userRepository;

    private final UserFileRepository userFileRepository;

    private final TaskRepository taskRepository;

    private final Mapper mapper;

    /**
     * Loads a user/account holder requested doc to the database with {@link FileStatus#NOT_SUBMITTED} status.
     *
     * @param params the params of the file.
     */
    @Transactional
    public Long loadAndVerifyFileIntegrity(RequestedDocsParams params) {
        if (params.getFile() == null) {
            deleteTaskFilesByFileId(params.getFileId());
            return null;
        }

        fileUploadService.scan(params.getFile());
        try (InputStream multiPartInputStream = new ByteArrayInputStream(params.getFile().getBytes())) {
            fileUploadService.validateFileType(multiPartInputStream, FileTypes.REQUESTED_DOCUMENT);
            UploadedFile uploadedFile;
            if (params.getFileId() != null) {
                uploadedFile = fileUploadService
                    .updateFileInDatabase(params.getFile(), params.getFileId(), params.getTaskRequestId());
            } else {
                uploadedFile = fileUploadService.saveFileInDatabase(params.getFile());
            }
            Task byRequestId = taskRepository.findByRequestId(params.getTaskRequestId());
            if (byRequestId == null) {
                throw new FileUploadException("Provided task request identifier does not exist");
            }
            // this holds a reference to the Requested Documents task.
            uploadedFile.setTask(byRequestId);
            //TODO: evaluate if this one should be removed after  after UKETS-4732 is merged.
            // the account holder file entity should be happenign after the parent task completion.
            if (params.getAccountHolderIdentifier() != null) {
                if (params.getFileId() != null) {
                    updateAccountHolderFileEntity(uploadedFile, params);
                } else {
                    createAccountHolderFileEntity(uploadedFile, params);
                }
            }
            if (params.getUserUrid() != null) {
                if (params.getFileId() != null) {
                    updateUserFile(uploadedFile, params);
                } else {
                    createUserFile(uploadedFile, params);
                }
            }
            setTaskDifferenceWithFileIds(params, uploadedFile.getId());
            return uploadedFile.getId();
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new FileUploadException("Error while processing the file");
        }

    }

    @Transactional
    public void deleteFileFromTask(Long fileId, Long taskRequestId, String totalFileUploads, String accountHolderId, String userId) {

        try {
            Task byRequestId = taskRepository.findByRequestId(taskRequestId);
            if (byRequestId == null) {
                throw new FileUploadException("Provided task request identifier does not exist");
            }


            if (accountHolderId != null) {
                deleteAccountHolderFileEntity(fileId);
            }
            if (userId != null) {
                deleteUserFileEntity(fileId);
            }

            Set<Long> totalFileUploadsSet = new HashSet<>();
            if(totalFileUploads.length() > 0){
                totalFileUploadsSet = Arrays.stream(totalFileUploads.split(","))
                        .map(f -> Long.parseLong(f))
                        .collect(Collectors.toSet());
            }
            removeFileFromDifference(taskRequestId, fileId, totalFileUploadsSet);
        } catch (Exception exception) {
            throw new FileUploadException("Error while processing the file");
        }

    }

    private void removeFileFromDifference(Long taskRequestId, Long fileId, Set<Long> totalFileUploads) {
        if (taskRequestId != null) {
            Task task = taskRepository.findByRequestId(taskRequestId);
            RequestDocumentsTaskDifference difference = mapper.convertToPojo(task.getDifference(),
                    RequestDocumentsTaskDifference.class);
            if (difference != null) {
                // null check because legacy account opening tasks - did not have this fields.
                // at the time the task was created.
                if (difference.getUploadedFileNameIdMap() != null) {
                    List<String> documentNames = difference.getUploadedFileNameIdMap().entrySet()
                        .stream()
                        .filter(entry -> fileId.equals(entry.getValue()))
                        .map(entry -> entry.getKey())
                        .collect(Collectors.toList());

                    if(documentNames.size() > 0) {
                        difference.getUploadedFileNameIdMap().remove(documentNames.get(0));
                    }
                    Set<Long> currentFileIds = difference.getUploadedFileNameIdMap().values().stream().collect(Collectors.toSet());
                    difference.setDocumentIds(currentFileIds);
                    deleteOldTaskFiles(task.getRequestId(), currentFileIds);
                }
                task.setDifference(mapper.convertToJson(difference));
            }
        }
    }

    private void setTaskDifferenceWithFileIds(RequestedDocsParams params, Long fileId) {
        if (params.getTaskRequestId() != null) {
            Task task = taskRepository.findByRequestId(params.getTaskRequestId());
            RequestDocumentsTaskDifference difference = mapper.convertToPojo(task.getDifference(),
                RequestDocumentsTaskDifference.class);
            if (difference != null) {
                if (CollectionUtils.isEmpty(params.getTotalFileUploads())) {
                    params.setTotalFileUploads(new HashSet<>());
                }
                params.getTotalFileUploads().add(fileId);
                // null check because legacy account opening tasks - did not have this fields.
                // at the time the task was created.
                if (difference.getUploadedFileNameIdMap() != null) {
                    difference.getUploadedFileNameIdMap().put(params.getDocumentName(), fileId);
                    Set<Long> currentFileIds = difference.getUploadedFileNameIdMap().values().stream().collect(Collectors.toSet());
                    difference.setDocumentIds(currentFileIds);
                    deleteOldTaskFiles(task.getRequestId(), currentFileIds);
                }
                task.setDifference(mapper.convertToJson(difference));
            }
        }
    }

    private void createAccountHolderFileEntity(UploadedFile uploadedFile, RequestedDocsParams requestedDocsParams) {
        AccountHolder accountHolder =
            accountHolderRepository.getAccountHolder(requestedDocsParams.getAccountHolderIdentifier());
        if (accountHolder == null) {
            throw new FileUploadException("Provided account holder does not exist.");
        }
        AccountHolderFile accountHolderFile = new AccountHolderFile();
        accountHolderFile.setAccountHolder(accountHolder);
        accountHolderFile.setUploadedFile(uploadedFile);
        accountHolderFile.setDocumentName(requestedDocsParams.getDocumentName());
        accountHolderFileRepository.save(accountHolderFile);
    }

    private void updateAccountHolderFileEntity(UploadedFile uploadedFile, RequestedDocsParams requestedDocsParams) {
        Optional<AccountHolderFile> currentAHFile =
            accountHolderFileRepository.findById(requestedDocsParams.getFileId());
        if (currentAHFile.isPresent()) {
            currentAHFile.get().setUploadedFile(uploadedFile);
            currentAHFile.get().setDocumentName(requestedDocsParams.getDocumentName());
            accountHolderFileRepository.save(currentAHFile.get());
        } else {
            throw new FileUploadException("Error while fetching the account holder file");
        }
    }

    private void createUserFile(UploadedFile uploadedFile, RequestedDocsParams requestedDocsParams) {
        User user = userRepository.findByUrid(requestedDocsParams.getUserUrid());
        if (user == null) {
            throw new FileUploadException("Provided user does not exist.");
        }
        UserFile userFile = new UserFile();
        userFile.setUser(user);
        userFile.setUploadedFile(uploadedFile);
        userFile.setDocumentName(requestedDocsParams.getDocumentName());
        userFileRepository.save(userFile);
    }

    private void updateUserFile(UploadedFile uploadedFile, RequestedDocsParams requestedDocsParams) {
        Optional<UserFile> currentUserFile = userFileRepository.findById(requestedDocsParams.getFileId());
        if (currentUserFile.isPresent()) {
            currentUserFile.get().setUploadedFile(uploadedFile);
            currentUserFile.get().setDocumentName(requestedDocsParams.getDocumentName());
            userFileRepository.save(currentUserFile.get());
        } else {
            throw new FileUploadException("Error while fetching the user file");
        }
    }

    /**
     * Submit all files related to a {@link RequestType#AH_REQUESTED_DOCUMENT_UPLOAD} or
     * {@link RequestType#AR_REQUESTED_DOCUMENT_UPLOAD} Task.
     *
     * @param requestId the task request identifier
     */
    @Transactional
    public void submitUploadedFiles(Long requestId, RequestDocumentsTaskDifference difference) {
        List<UploadedFile> notSubmittedFiles = uploadedFilesRepository.findByTaskRequestId(requestId);
        if (!CollectionUtils.isEmpty(difference.getDocumentIds())) {
            difference.getDocumentIds()
                .retainAll(notSubmittedFiles.stream().map(UploadedFile::getId).collect(Collectors.toList()));
        }
        if ((difference.getComment() == null || difference.getComment().isEmpty())
            && (CollectionUtils.isEmpty(difference.getDocumentIds()) ||
            difference.getDocumentIds().size() != difference.getDocumentNames().size())) {
            throw new IllegalStateException("Upload the requested file OR Enter a reason for not uploading the file");
        }
        Task task = taskRepository.findByRequestId(requestId);
        task.setDifference(mapper.convertToJson(difference));
        notSubmittedFiles.forEach(f -> f.setFileStatus(FileStatus.SUBMITTED));
    }

    /**
     * Retrieves all files related with the specific task request id.
     *
     * @param requestId the task request identifier
     * @return a list of files
     */
    @Transactional
    @SneakyThrows
    public List<FileHeaderDto> getUploadedFiles(Long requestId) {
        List<FileHeaderDto> uploadedFiles = new ArrayList<>();
        uploadedFilesRepository.findByTaskRequestId(requestId)
            .forEach(file -> uploadedFiles.add(new FileHeaderDto(file.getId(),
                                                                 file.getFileName(),
                                                                 BaseType.DOCUMENT_REQUEST,
                                                                 file.getCreationDate().atZone(ZoneId.of("UTC")))));
        return uploadedFiles;
    }

    /**
     * Delete a file by the file id.
     *
     * @param fileId the file id.
     */
    @Transactional
    public void deleteTaskFilesByFileId(Long fileId) {
        if (fileId != null) {
            Optional<UploadedFile> file = uploadedFilesRepository.findById(fileId);
            file.ifPresent(uploadedFilesRepository::delete);
        }
    }

    /**
     * Delete old submitted files.
     *
     * @param requestId the task request id.
     * @param currentFileIds the current file ids.
     */
    @Transactional
    public void deleteOldTaskFiles(Long requestId, Set<Long> currentFileIds) {
        List<UploadedFile> notSubmittedFiles = uploadedFilesRepository.findByTaskRequestId(requestId);
        List<UploadedFile> unusedFiles = notSubmittedFiles.stream()
                .filter(file -> file.getFileStatus().equals(FileStatus.NOT_SUBMITTED))
                .filter(file -> !currentFileIds.contains(file.getId()))
                .collect(Collectors.toList());
        uploadedFilesRepository.deleteAll(unusedFiles);
    }

    private void deleteUserFileEntity(Long fileId) {
        userFileRepository.deleteById(fileId);
    }
    private void deleteAccountHolderFileEntity(Long fileId) {
        accountHolderFileRepository.deleteById(fileId);
    }
}
