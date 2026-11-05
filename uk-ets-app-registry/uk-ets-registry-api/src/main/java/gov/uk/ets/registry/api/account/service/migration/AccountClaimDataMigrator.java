package gov.uk.ets.registry.api.account.service.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountClaim;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.MetsAccountContact;
import gov.uk.ets.registry.api.account.repository.AccountClaimRepository;
import gov.uk.ets.registry.api.account.repository.AccountContactRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepresentativeInvitationRepository;
import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AccountClaimCompletionEvent;
import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AccountClaimInitiatedEvent;
import gov.uk.ets.registry.api.common.reporting.metrics.messaging.events.AccountClaimInvitationSentEvent;
import gov.uk.ets.registry.api.common.reporting.metrics.service.ReportingMetricsEventService;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountClaimDataMigrator implements LockingTaskExecutor.Task {

    private final ReportingMetricsEventService reportingMetricsEventService;
    private final TaskRepository taskRepository;
    private final AccountClaimRepository accountClaimRepository;
    private final AccountRepresentativeInvitationRepository accountRepresentativeInvitationRepository;
    private final AccountContactRepository accountContactRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;


    @Override
    @Transactional
    public void call() throws Throwable {

        Instant start = Instant.now();
        log.info("AccountClaimDataMigrator started");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[AccountClaimDataMigrator] has already been performed, skipping.");
            return;
        }

        Date date = Date.from(
                LocalDate.of(2026, 2, 18)
                        .atStartOfDay(ZoneId.of("UTC"))
                        .toInstant());

        List<Task> allAddARTasks =
                taskRepository.findSelfInitiatedTasksByTypeSince(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, date);

        final List<Task> tasks = allAddARTasks
                .stream()
                .filter(t -> t.getAccount() != null)
                .collect(Collectors.toList());

        int foundTasks = tasks.size();
        int processedAccountClaimsCount = 0;

        if (!tasks.isEmpty()) {
            final List<AccountClaim> existingAccountClaims = accountClaimRepository.findAll();

            existingAccountClaims.forEach(ac -> tasks.removeIf(t -> t.getAccount().getIdentifier().equals(ac.getAccountIdentifier()) && t.getRequestId().equals(ac.getRequestId())));
            
            final Set<AccountClaim> processedAccountClaims = tasks.stream()
                    .map(t -> AccountClaim.builder()
                            .accountIdentifier(t.getAccount().getIdentifier())
                            .requestId(t.getRequestId())
                            .build())
                    .collect(Collectors.toSet());
            
            accountClaimRepository.saveAll(processedAccountClaims);
            processedAccountClaimsCount = processedAccountClaims.size();
            tasks.forEach(this::sendReportingMetricsEvents);
        } else {
            log.info("[AccountClaimDataMigrator] no Add AR tasks found to migrate.");
        }

        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);

        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();

        log.info("[AccountClaimDataMigrator] Completed. Found tasks: {}, Inserted account claims: {}",
                foundTasks, processedAccountClaimsCount);
        log.info("[AccountClaimDataMigrator] elapsed time: {} minutes {} seconds", minutes, seconds);

        updateMigrationHistory();
    }

    private void sendReportingMetricsEvents(Task task) {

        final Account account = task.getAccount();

        if (account.getCompliantEntity() != null &&
                account.getCompliantEntity().getFirstInvitationDate() != null) {
            reportingMetricsEventService.processEvent(AccountClaimInvitationSentEvent
                    .builder()
                    .accountIdentifier(account.getIdentifier())
                    .accountClaimCode(account.getAccountClaimCode())
                    .manual(true)
                    .accountClaimInvitationSendDateTime(account.getCompliantEntity().getFirstInvitationDate())
                    .build());
        }

        final List<Long> accountHolderRepresentativeIds = account.getAccountHolder()
                .getAccountHolderRepresentatives().stream()
                .map(AccountHolderRepresentative::getId)
                .toList();

        final List<AccountRepresentativeInvitation> invitations = accountRepresentativeInvitationRepository
                .findByRepresentativeIdInAndAccountId(accountHolderRepresentativeIds, account.getId());

        final List<MetsAccountContact> metsContacts = accountContactRepository.findByAccountIdentifier(account.getIdentifier());

        final LocalDateTime latestRegistryInvitedDate = invitations.stream()
                .map(AccountRepresentativeInvitation::getInvitedDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        final LocalDateTime latestMetsInvitedDate = metsContacts.stream()
                .map(MetsAccountContact::getInvitedOn)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Stream.of(latestRegistryInvitedDate, latestMetsInvitedDate)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .ifPresent(latestInvitation ->
                        reportingMetricsEventService.processEvent(AccountClaimInvitationSentEvent
                                .builder()
                                .accountIdentifier(account.getIdentifier())
                                .accountClaimCode(account.getAccountClaimCode())
                                .manual(true)
                                .accountClaimInvitationSendDateTime(latestInvitation)
                                .build()
                        )
                );

        if (task.getInitiatedDate() != null) {
            reportingMetricsEventService.processEvent(AccountClaimInitiatedEvent
                    .builder()
                    .accountIdentifier(account.getIdentifier())
                    .accountClaimInitiatedDateTime(task.getInitiatedDate().toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime())
                    .accountClaimCode(account.getAccountClaimCode())
                    .requestIdentifier(task.getRequestId())
                    .build());
        }

        if (task.getCompletedDate() != null) {
            reportingMetricsEventService.processEvent(AccountClaimCompletionEvent
                    .builder()
                    .accountIdentifier(account.getIdentifier())
                    .accountClaimCode(account.getAccountClaimCode())
                    .accountClaimCompletionDateTime(task.getCompletedDate().toInstant()
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDateTime())
                    .requestIdentifier(task.getRequestId())
                    .taskOutcome(task.getStatus().equals(RequestStateEnum.APPROVED) ?
                            TaskOutcome.APPROVED : TaskOutcome.REJECTED)
                    .build());
        }
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }
}
