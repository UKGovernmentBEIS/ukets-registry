package gov.uk.ets.registry.api.regulatornotice.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.shared.AccountActionError;
import gov.uk.ets.registry.api.account.shared.AccountActionException;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.assign.OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.claim.OnlySeniorOrJuniorCanClaimTaskRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.regulatornotice.web.model.RegulatorNoticeTaskDetailsDTO;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskActionError;
import gov.uk.ets.registry.api.task.service.TaskServiceException;
import gov.uk.ets.registry.api.task.service.TaskTypeService;
import gov.uk.ets.registry.api.task.web.model.TaskCompleteResponse;
import gov.uk.ets.registry.api.task.web.model.TaskDetailsDTO;
import gov.uk.ets.registry.api.task.web.model.TaskFileDownloadInfoDTO;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegulatorNoticeTaskService implements TaskTypeService<RegulatorNoticeTaskDetailsDTO> {

    private final AccountRepository accountRepository;
    private final AccountHolderRepository accountHolderRepository;
    private final UploadedFilesRepository uploadedFilesRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public Set<RequestType> appliesFor() {
        return Set.of(RequestType.REGULATOR_NOTICE);
    }

    @Override
    @Transactional(readOnly = true)
    public RegulatorNoticeTaskDetailsDTO getDetails(TaskDetailsDTO taskDetailsDTO) {
        RegulatorNoticeTaskDetailsDTO response = new RegulatorNoticeTaskDetailsDTO(taskDetailsDTO);

        final Account account = accountRepository.findByAccountIdentifierWithCompliantEntity(Long.parseLong(taskDetailsDTO.getAccountNumber()))
                .orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("Account does not exist.")));
        final AccountHolder accountHolder =
                Optional.ofNullable(accountHolderRepository.getAccountHolderOfAccount(Long.parseLong(taskDetailsDTO.getAccountNumber()))
                ).orElseThrow(() -> AccountActionException.create(
                        AccountActionError.build("Account holder does not exist.")));
        Optional.ofNullable(uploadedFilesRepository.findFirstByTaskRequestId(taskDetailsDTO.getRequestId()))
                .ifPresent(file -> {
                    response.setFileName(file.getFileName());
                    response.setFileSize(file.getFileSize());
                    response.setFile(createFileHeader(file));
                });
        response.setProcessType(taskDetailsDTO.getDifference());
        response.setAccountHolderName(accountHolder.actualName());
        response.setAccountHolderIdentifier(accountHolder.getIdentifier());

        CompliantEntity compliantEntity = account.getCompliantEntity();

        response.setOperatorId(compliantEntity.getIdentifier());
        if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {
            Installation installation = (Installation) Hibernate.unproxy(account.getCompliantEntity());
            response.setPermitOrMonitoringPlanIdentifier(installation.getPermitIdentifier());
        } else if (RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {
            AircraftOperator aircraftOperator = (AircraftOperator) Hibernate.unproxy(account.getCompliantEntity());
            response.setPermitOrMonitoringPlanIdentifier(aircraftOperator.getMonitoringPlanIdentifier());
        } else if (RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.equals(account.getRegistryAccountType())) {
            MaritimeOperator maritimeOperator = (MaritimeOperator) Hibernate.unproxy(account.getCompliantEntity());
            response.setPermitOrMonitoringPlanIdentifier(maritimeOperator.getMaritimeMonitoringPlanIdentifier());
        }
        return response;
    }

    private FileHeaderDto createFileHeader(UploadedFile file) {
        return new FileHeaderDto(file.getId(),
                file.getFileName(),
                null,
                file.getCreationDate().atZone(ZoneId.of("UTC")));
    }

    @Override
    @Transactional
    @Protected({
            OnlyRegistryAdminCanCompleteTaskRule.class,
    })
    public TaskCompleteResponse complete(RegulatorNoticeTaskDetailsDTO taskDTO, TaskOutcome taskOutcome, String comment) {
        String eventComment = String.format("Regulator notice type ‘%s’ for account ‘%s’", taskDTO.getProcessType(),
                taskDTO.getAccountName());
        User currentUser = userService.getCurrentUser();
        eventService.createAndPublishEvent(taskDTO.getAccountNumber(), currentUser.getUrid(), eventComment,
                EventType.ACCOUNT_TASK_COMPLETED, "Regulator notice completed");
        return defaultResponseBuilder(taskDTO).build();
    }

    @Protected({
            OnlySeniorOrJuniorCanClaimTaskRule.class
    })
    @Override
    public void checkForInvalidClaimantPermissions() {
    }

    @Protected({
            OnlySeniorOrJuniorRegistryAdminCanBeAssignedTaskRule.class,
    })
    @Override
    public void checkForInvalidAssignPermissions() {
    }

    @Override
    @Transactional
    public UploadedFile getRequestedTaskFile(TaskFileDownloadInfoDTO infoDTO) {
        return uploadedFilesRepository.findById(infoDTO.getFileId())
                .orElseThrow(() -> TaskServiceException.create(TaskActionError.builder().message("File Not found").build()));
    }
}
