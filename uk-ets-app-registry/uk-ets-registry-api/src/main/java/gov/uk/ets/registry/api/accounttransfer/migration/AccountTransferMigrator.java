package gov.uk.ets.registry.api.accounttransfer.migration;

import static java.util.Comparator.comparing;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.accounttransfer.web.model.AccountTransferAction;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.RequestDocumentsTaskDifference;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.web.model.RequestDocumentUploadTaskDetailsDTO;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class AccountTransferMigrator implements Migrator {

    private final TaskRepository taskRepository;
    private final Mapper mapper;
    private final MigratorHistoryRepository migratorHistoryRepository;
    private final AccountHolderService accountHolderService;

    @Transactional
    @Override
    public void migrate() {
        log.info("Starting Account Transfer Task Migrator");
        List<MigratorHistory> migratorHistoryList =
            migratorHistoryRepository.findByMigratorName(MigratorName.ACCOUNT_TRANSFER_TASK_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("Migration of account transfer tasks already performed previously, skipping.");
            return;
        }

        QTask task = QTask.task;
        Iterable<Task> transferTasksWithoutBefore = taskRepository.findAll(
            task.type.eq(RequestType.ACCOUNT_TRANSFER).and(task.before.isNull().or(task.before.isEmpty())),
            Sort.by(Sort.Direction.ASC, "initiatedDate"));

        for (Task transferTask : transferTasksWithoutBefore) {

            Optional<AccountHolderDTO> accountHolderDto;
            if (transferTask.getStatus() == RequestStateEnum.SUBMITTED_NOT_YET_APPROVED) {
                Long ahIdentifier = transferTask.getAccount().getAccountHolder().getIdentifier();
                accountHolderDto = Optional.of(accountHolderService.getAccountHolder(ahIdentifier));
            } else {
                accountHolderDto = getAccountHolderFromLatestUpdateDetailsTask(transferTask);
            }

            accountHolderDto.ifPresent(accountHolderDTO -> {
                transferTask.setBefore(mapper.convertToJson(accountHolderDto));
                taskRepository.save(transferTask);
            });
        }

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ACCOUNT_TRANSFER_TASK_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of account transfer tasks completed");
    }

    private Optional<AccountHolderDTO> getAccountHolderFromLatestUpdateDetailsTask(Task transferTask) {
        Account account = transferTask.getAccount();
        Date beforeDate = transferTask.getInitiatedDate();

        return retrieveLatestTaskWithBeforeObject(account.getId(), beforeDate)
            .map(this::extractDto);
    }

    private AccountHolderDTO extractDto(Task task) {
        AccountHolderDTO accountHolderDTO = null;

        if (task.getType() == RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS) {
            accountHolderDTO = mapper.convertToPojo(task.getBefore(), AccountHolderDTO.class);
        }

        if (task.getType() == RequestType.ACCOUNT_TRANSFER ) {
            accountHolderDTO = mapper.convertToPojo(task.getDifference(), AccountTransferAction.class).getAccountHolderDTO();
        }

        if (task.getType() == RequestType.ACCOUNT_OPENING_REQUEST) {
            accountHolderDTO = mapper.convertToPojo(task.getDifference(), AccountDTO.class).getAccountHolder();
        }

        if (task.getType() == RequestType.AH_REQUESTED_DOCUMENT_UPLOAD) {
            RequestDocumentsTaskDifference requestDocumentsTaskDifference =
                mapper.convertToPojo(task.getDifference(), RequestDocumentsTaskDifference.class);
            accountHolderDTO = new AccountHolderDTO();
            accountHolderDTO.setId(requestDocumentsTaskDifference.getAccountHolderIdentifier());
            DetailsDTO detailsDTO = new DetailsDTO();
            detailsDTO.setName(requestDocumentsTaskDifference.getAccountHolderName());
            accountHolderDTO.setDetails(detailsDTO);
        }

        return Optional.ofNullable(accountHolderDTO)
            .map(AccountHolderDTO::getId)
            .map(accountHolderService::getAccountHolder)
            .orElse(accountHolderDTO); // Applicable on Account Opening and Account Transfer, if no AH exists, return what we have in json at that point of time.
    }

    private Optional<Task> retrieveLatestTaskWithBeforeObject(Long accountId, Date beforeDate) {
        QTask task = QTask.task;
        Iterable<Task> tasks = taskRepository.findAll(
            task.account.id.eq(accountId)
                .and(task.initiatedDate.before(beforeDate))
                .and(task.type.eq(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS)
                    .or(task.type.eq(RequestType.ACCOUNT_OPENING_REQUEST))
                    .or(task.type.eq(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD))
                    .or(task.type.eq(RequestType.ACCOUNT_TRANSFER)
                        .and(task.status.eq(RequestStateEnum.APPROVED)))));

        return StreamSupport.stream(tasks.spliterator(), false)
            .max(comparing(Task::getCompletedDate));
    }
}
