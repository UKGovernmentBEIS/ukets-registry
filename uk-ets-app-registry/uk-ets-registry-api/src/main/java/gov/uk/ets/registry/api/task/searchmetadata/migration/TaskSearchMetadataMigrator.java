package gov.uk.ets.registry.api.task.searchmetadata.migration;

import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.repository.TaskSearchMetadataRepository;
import gov.uk.ets.registry.api.task.searchmetadata.domain.TaskSearchMetadata;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.user.admin.web.model.UserDetailsDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskSearchMetadataMigrator implements Migrator {
    private static final List<RequestType> REQUEST_TYPES =
            List.of(/*RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST,
                    RequestType.ACCOUNT_OPENING_REQUEST,
                    RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
                    RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                    RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
                    RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST,
                    RequestType.USER_DETAILS_UPDATE_REQUEST,
                    RequestType.CHANGE_TOKEN,
                    RequestType.LOST_PASSWORD_AND_TOKEN,
                    RequestType.LOST_TOKEN,
                    RequestType.PRINT_ENROLMENT_LETTER_REQUEST,
                    RequestType.REQUESTED_EMAIL_CHANGE,
                    RequestType.USER_DEACTIVATION_REQUEST,
                    RequestType.AR_REQUESTED_DOCUMENT_UPLOAD*/
                    RequestType.PRINT_ENROLMENT_LETTER_REQUEST);
    private final TaskRepository taskRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    private final TaskSearchMetadataRepository taskSearchMetadataRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final List<RequestType> userMappedTasks = List.of(RequestType.CHANGE_TOKEN,
            RequestType.LOST_PASSWORD_AND_TOKEN,
            RequestType.LOST_TOKEN,
            RequestType.PRINT_ENROLMENT_LETTER_REQUEST,
            RequestType.REQUESTED_EMAIL_CHANGE,
            RequestType.USER_DEACTIVATION_REQUEST,
            RequestType.AR_REQUESTED_DOCUMENT_UPLOAD,
            RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST,
            RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST);

    @Transactional
    public void migrate() {
        log.info("TaskSearchMetadataMigrator");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.TASK_SEARCH_METADATA_MIGRATOR);
        if (!migratorHistoryList.isEmpty()) {
            log.info("[Task search metadata migrator]," +
                    " has already been performed, skipping.");
            return;
        }

        List<Task> tasks = taskRepository.findByTypeIn(REQUEST_TYPES);
        for (Task task : tasks) {
            if (userMappedTasks.contains(task.getType()) && task.getUser() != null) {
                User user = userRepository.findByUrid(task.getUser().getUrid());
                taskSearchMetadataRepository.save(createTaskSearchMetadataEntry(task, user));
            }

            if (task.getType().equals(RequestType.USER_DETAILS_UPDATE_REQUEST)
                    && task.getBefore() != null) {
                UserDetailsDTO before = mapper.convertToPojo(task.getBefore(), UserDetailsDTO.class);
                User user = userRepository.findByUrid(before.getUrid());
                taskSearchMetadataRepository.save(createTaskSearchMetadataEntry(task, user));
            }

            if (task.getType().equals(RequestType.ACCOUNT_OPENING_INSTALLATION_TRANSFER_REQUEST) ||
                    task.getType().equals(RequestType.ACCOUNT_OPENING_REQUEST) &&
                            task.getDifference() != null) {
                AccountDTO diff = mapper.convertToPojoWithUnknownEnumValuesAndUnknownProperties(task.getDifference(), AccountDTO.class);
                if (diff.getAuthorisedRepresentatives() != null && !diff.getAuthorisedRepresentatives().isEmpty()) {
                    diff.getAuthorisedRepresentatives().forEach(ar -> {
                        User user = userRepository.findByUrid(ar.getUrid());
                        taskSearchMetadataRepository.save(createTaskSearchMetadataEntry(task, user));
                    });
                }
            }
        }

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.TASK_SEARCH_METADATA_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of task search metadata completed");
    }

    private TaskSearchMetadata createTaskSearchMetadataEntry(Task task, User user) {
        TaskSearchMetadata taskSearchMetadata = new TaskSearchMetadata();
        taskSearchMetadata.setTask(task);
        taskSearchMetadata.setMetadataName(MetadataName.USER_ID_NAME_KNOWN_AS);

        if (user.getUrid() == null) {
            return new TaskSearchMetadata();
        }

        String urid = user.getUrid();
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String knownAs = user.getKnownAs() != null ? user.getKnownAs() : "";

        taskSearchMetadata.setMetadataValue(
                urid
                        .concat(", ")
                        .concat(firstName)
                        .concat(", ")
                        .concat(lastName)
                        .concat(", ")
                        .concat(knownAs));

        return taskSearchMetadata;
    }


}
