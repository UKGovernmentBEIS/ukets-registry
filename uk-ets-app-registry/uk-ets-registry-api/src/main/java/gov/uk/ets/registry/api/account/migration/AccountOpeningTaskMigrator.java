package gov.uk.ets.registry.api.account.migration;

import static java.util.Comparator.comparing;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.service.AccountDTOFactory;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.ContactDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.accountholder.service.AccountHolderService;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.QTask;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.repository.TaskSearchMetadataRepository;
import gov.uk.ets.registry.api.task.searchmetadata.domain.TaskSearchMetadata;
import gov.uk.ets.registry.api.task.searchmetadata.domain.types.MetadataName;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import gov.uk.ets.registry.api.user.UserConversionService;
import gov.uk.ets.registry.api.user.UserDTO;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class AccountOpeningTaskMigrator implements Migrator {

    private final AccountDTOFactory accountDTOFactory;
    private final AccountAccessRepository accountAccessRepository;
    private final UserConversionService userConversionService;
    private final UserAdministrationService userAdministrationService;
    private final TaskRepository taskRepository;
    private final Mapper mapper;
    private final MigratorHistoryRepository migratorHistoryRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final AccountHolderService accountHolderService;
    private final TaskSearchMetadataRepository taskSearchMetadataRepository;

    @Transactional
    @Override
    public void migrate() {
        log.info("Starting Account Opening Task Migrator");
        List<MigratorHistory> migratorHistoryList =
            migratorHistoryRepository.findByMigratorName(MigratorName.ACCOUNT_OPENING_TASK_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("Migration of account opening tasks already performed previously, skipping.");
            return;
        }

        QTask task = QTask.task;
        Iterable<Task> openingTasksWithoutDiff = taskRepository.findAll(
            task.type.eq(RequestType.ACCOUNT_OPENING_REQUEST)
                .and(task.difference.isNull().or(task.difference.isEmpty()))
                .and(task.status.eq(RequestStateEnum.APPROVED)));

        for (Task openingTask : openingTasksWithoutDiff) {
            Account account = openingTask.getAccount();
            AccountDTO accountDTO = accountDTOFactory.create(account, true);
            accountDTO.setIdentifier(null);
            accountDTO.setBalance(null);
            accountDTO.setAddedARs(null);
            accountDTO.setRemovedARs(null);

            List<AuthorisedRepresentativeDTO> originalARs =
                authorizedRepresentativeService.calculateOriginalARs(account.getIdentifier()).stream()
                    .map(user -> convertToAuthorizedRepresentativeDTO(account.getIdentifier(), user))
                    .toList();
            accountDTO.setAuthorisedRepresentatives(originalARs);

            updateAccountHolder(account.getId(), accountDTO);

            retrieveOperator(account.getId()).ifPresent(accountDTO::setOperator);
            retrieveTransactionRules(account.getId()).ifPresent(accountDTO::setTrustedAccountListRules);

            openingTask.setDifference(mapper.convertToJson(accountDTO));

            taskRepository.save(openingTask);

            // Insert taskSearchMetadata values
            Optional.of(accountDTO)
                .map(AccountDTO::getAccountHolder)
                .map(AccountHolderDTO::getDetails)
                .map(DetailsDTO::getName)
                .ifPresent(accountHolderName ->
                    taskSearchMetadataRepository.save(buildMetadata(openingTask, MetadataName.AH_NAME, accountHolderName)));

            Optional.of(accountDTO)
                .map(AccountDTO::getAccountType)
                .ifPresent(accountType ->
                    taskSearchMetadataRepository.save(buildMetadata(openingTask, MetadataName.ACCOUNT_TYPE, accountType)));
        }

        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ACCOUNT_OPENING_TASK_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of account opening tasks completed");
    }

    private void updateAccountHolder(Long accountId, AccountDTO accountDTO) {

        Optional<Task> transferTask = retrieveFirstTask(accountId, List.of(RequestType.ACCOUNT_TRANSFER));
        Date accountTransferDate = transferTask.map(Task::getCompletedDate).orElse(new Date());

        Optional<Task> accountHolderTask = retrieveAccountHolderTask(accountId);

        // If the account transfer has happened before the account holder update then we cannot recover the original data.
        if (transferTask.isPresent()) {
            boolean accountHolderUpdateAfterTransfer = accountHolderTask.map(Task::getCompletedDate)
                .filter(accountHolderUpdateDate -> accountHolderUpdateDate.compareTo(accountTransferDate) > 0)
                .isPresent();
            if (accountHolderUpdateAfterTransfer) {
                return;
            }
        }

        accountHolderTask.map(task -> mapper.convertToPojo(task.getBefore(), AccountHolderDTO.class))
            .ifPresent(accountHolderDTO -> {
                accountDTO.setAccountHolder(accountHolderDTO);
                accountDTO.getAccountDetails().setAccountHolderName(accountHolderDTO.getDetails().getName());
                accountDTO.getAccountDetails().setAccountHolderId(String.valueOf(accountHolderDTO.getId()));
                accountDTO.setAccountHolderContactInfo(accountHolderService.getAccountHolderContactInfo(accountHolderDTO.getId()));
            });

        AccountHolderContactInfoDTO contactInfo = accountDTO.getAccountHolderContactInfo();

        retrievePrimaryContactTask(accountId)
            .filter(task -> task.getCompletedDate().compareTo(accountTransferDate) < 0)
            .map(task -> mapper.convertToPojo(task.getBefore(), AccountHolderRepresentativeDTO.class))
            .ifPresent(contactInfo::setPrimaryContact);

        retrieveAlternativeContactTask(accountId)
            .filter(task -> task.getCompletedDate().compareTo(accountTransferDate) < 0)
            .ifPresent(t -> {
                if (t.getType() == RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD) {
                    contactInfo.setAlternativeContact(null);
                } else {
                    contactInfo.setAlternativeContact(mapper.convertToPojo(t.getBefore(), AccountHolderRepresentativeDTO.class));
                }
            });
    }

    private AuthorisedRepresentativeDTO convertToAuthorizedRepresentativeDTO(Long accountIdentifier, User user) {

        AccountAccess accountAccess =
            accountAccessRepository.findArsByAccount_IdentifierAndUser_Urid(accountIdentifier, user.getUrid());

        AuthorisedRepresentativeDTO dto = new AuthorisedRepresentativeDTO();

        dto.setRight(accountAccess.getRight());
        dto.setUrid(user.getUrid());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        UserDTO userDTO = userConversionService.convert(user);
        dto.setUser(userDTO);
        dto.setUrid(userDTO.getUrid());
        ContactDTO workContact = userAdministrationService.findWorkContactDetailsByIamId(userDTO.getKeycloakId(), true);
        dto.setContact(workContact);

        return dto;
    }

    private Optional<Task> retrieveAccountHolderTask(Long accountId) {
        List<RequestType> accountHolderUpdateRequest = List.of(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS);
        return retrieveFirstTaskWithBeforeObject(accountId, accountHolderUpdateRequest);
    }

    private Optional<Task> retrievePrimaryContactTask(Long accountId) {
        List<RequestType> primaryContactRequest = List.of(RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS);
        return retrieveFirstTaskWithBeforeObject(accountId, primaryContactRequest);
    }

    private Optional<OperatorDTO> retrieveOperator(Long accountId) {
        List<RequestType> operatorUpdateRequest =
            List.of(RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST, RequestType.AIRCRAFT_OPERATOR_UPDATE_REQUEST);
        return retrieveFirstTaskWithBeforeObject(accountId, operatorUpdateRequest)
            .map(task -> mapper.convertToPojo(task.getBefore(), OperatorDTO.class));
    }

    private Optional<Task> retrieveAlternativeContactTask(Long accountId) {
        List<RequestType> alternativeContactAddUpdateDeleteRequest =
            List.of(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD,
                RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE,
                RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE);
        return retrieveFirstTaskWithBeforeObject(accountId, alternativeContactAddUpdateDeleteRequest);
    }

    private Optional<TrustedAccountListRulesDTO> retrieveTransactionRules(Long accountId) {

        Optional<TrustedAccountListRulesDTO> trustedAccountListRulesDTO =
            retrieveFirstTask(accountId, List.of(RequestType.TRANSACTION_RULES_UPDATE_REQUEST))
                .filter(t -> Objects.nonNull(t.getDifference()))
                .map(t -> mapper.convertToPojo(t.getDifference(), TrustedAccountListRulesDTO.class));

        // Unfortunately, the previous state lives inside difference field along with the new value instead of before field.
        trustedAccountListRulesDTO.ifPresent(dto -> {
            Optional.ofNullable(dto.getCurrentRule1()).ifPresent(dto::setRule1);
            dto.setCurrentRule1(null);
            Optional.ofNullable(dto.getCurrentRule2()).ifPresent(dto::setRule2);
            dto.setCurrentRule2(null);
            Optional.ofNullable(dto.getCurrentRule3()).ifPresent(dto::setRule3);
            dto.setCurrentRule3(null);
        });

        return trustedAccountListRulesDTO;
    }

    private Optional<Task> retrieveFirstTaskWithBeforeObject(Long accountId, List<RequestType> requestTypes) {
        QTask task = QTask.task;
        Iterable<Task> tasks = taskRepository.findAll(
            task.type.in(requestTypes)
                .and(task.account.id.eq(accountId))
                .and(task.before.isNotNull().and(task.before.isNotEmpty()))
                .and(task.status.eq(RequestStateEnum.APPROVED)));

        return StreamSupport.stream(tasks.spliterator(), false)
            .min(comparing(Task::getCompletedDate));
    }

    private Optional<Task> retrieveFirstTask(Long accountId, List<RequestType> requestTypes) {
        QTask task = QTask.task;
        Iterable<Task> tasks = taskRepository.findAll(
            task.type.in(requestTypes)
                .and(task.account.id.eq(accountId))
                .and(task.status.eq(RequestStateEnum.APPROVED)));

        return StreamSupport.stream(tasks.spliterator(), false)
            .min(comparing(Task::getCompletedDate));
    }

    private static TaskSearchMetadata buildMetadata(Task task, MetadataName metadataName, String metadataValue) {
        TaskSearchMetadata taskSearchMetadata = new TaskSearchMetadata();
        taskSearchMetadata.setTask(task);
        taskSearchMetadata.setMetadataName(metadataName);
        taskSearchMetadata.setMetadataValue(metadataValue);
        return taskSearchMetadata;
    }
}
