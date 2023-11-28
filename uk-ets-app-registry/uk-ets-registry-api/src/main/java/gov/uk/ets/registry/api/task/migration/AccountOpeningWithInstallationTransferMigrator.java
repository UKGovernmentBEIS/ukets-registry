package gov.uk.ets.registry.api.task.migration;

import gov.uk.ets.registry.api.account.domain.*;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.shared.ExtendedTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class AccountOpeningWithInstallationTransferMigrator implements Migrator {

    private final TaskRepository taskRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;
    private final Mapper mapper;
    private final AccountRepository accountRepository;
    private final InstallationOwnershipRepository installationOwnershipRepository;
    private final PersistenceService persistenceService;
    private final AccountService accountService;

    @Transactional
    public void migrate() {
        log.info("AccountOpeningWithInstallationTransferMigrator");

        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.ACCOUNT_OPENING_INST_TRANSFER_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[Account opening with installation transfer migrator]," +
                    " has already been performed, skipping.");
            return;
        }

        List<Task> taskList = taskRepository.
                findTasksByType(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST);
        List<Task> tasksToPersist = new ArrayList<>();

        List<ExtendedTask> tasks = new ArrayList<>();
        for (Task task : taskList) {
            try {
                tasks.add(new ExtendedTask(task, mapper.convertToPojo(task.getDifference(), AccountDTO.class)));
            } catch (Exception ex) {
                log.error("{}", ex.getMessage());
                log.error("ERROR while parsing task difference for account opening" +
                        " with installation transfer task Id : {}", task.getId());
            }
        }

        Map<Long, List<ExtendedTask>> groupedTasks = tasks.stream()
                .collect(Collectors.groupingBy(ExtendedTask::getInstallationId));

        sortTasks(groupedTasks);

        for (Long key : groupedTasks.keySet()) {
            int moveOneRowDownward = 0;

            for (ExtendedTask task : groupedTasks.get(key)) {
                try {
                    AccountDTO accountDTO = mapper.convertToPojo(task.getDifference(), AccountDTO.class);
                    Optional<Account> account =
                            accountRepository.findByCompliantEntityIdentifier(accountDTO.
                                    getInstallationToBeTransferred().getIdentifier());

                    if (account.isPresent()) {
                        Account currentAccount = (Account) Hibernate.unproxy(account.get());
                        Installation installation = (Installation) currentAccount.getCompliantEntity();
                        List<InstallationOwnership> installationOwnershipList =
                                installationOwnershipRepository.findByInstallation(installation);
                        Account transferringAccount
                                = (Account) Hibernate.unproxy(
                                installationOwnershipList.get(moveOneRowDownward).getAccount());
                        accountService.setTransferringAccountDtoAndContactInfoDto(transferringAccount, accountDTO);
                        task.setAccount(transferringAccount);
                        task.setDifference(mapper.convertToJson(accountDTO));
                        tasksToPersist.add(task.convertToNewTask());
                        moveOneRowDownward++;
                    }
                } catch (Exception ex) {
                    log.error("{}",ex.getMessage());
                }
            }
        }
        updateTasks(tasksToPersist);
        updateMigrationHistory();

        log.info("Account opening with installation transfer" +
                "migrator completed for {}/{} tasks", tasksToPersist.size(), tasks.size());
    }

    private void sortTasks(Map<Long, List<ExtendedTask>> tasks) {
        for (Long key : tasks.keySet()) {
            List<ExtendedTask> list = tasks.get(key);
            list = list.stream().sorted(Comparator.comparing(Task::getInitiatedDate))
                    .collect(Collectors.toList());
            tasks.put(key, list);
        }
    }

    private void updateTasks(List<Task> tasksToPersist) {
        for (Task task : tasksToPersist) {
            persistenceService.save(task);
        }
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ACCOUNT_OPENING_INST_TRANSFER_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of account opening tasks with installation transfers completed");
    }
}
