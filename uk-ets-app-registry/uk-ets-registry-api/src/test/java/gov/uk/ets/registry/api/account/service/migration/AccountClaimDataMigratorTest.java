package gov.uk.ets.registry.api.account.service.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountClaim;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AccountRepresentativeInvitation;
import gov.uk.ets.registry.api.account.domain.Installation;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountClaimDataMigratorTest {

    @Mock
    private ReportingMetricsEventService reportingMetricsEventService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AccountClaimRepository accountClaimRepository;

    @Mock
    private AccountRepresentativeInvitationRepository accountRepresentativeInvitationRepository;

    @Mock
    private AccountContactRepository accountContactRepository;

    @Mock
    private MigratorHistoryRepository migratorHistoryRepository;

    @InjectMocks
    private AccountClaimDataMigrator migrator;

    @Test
    void shouldSkipMigrationWhenHistoryAlreadyExists() throws Throwable {
        when(migratorHistoryRepository.findByMigratorName(
                MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR))
                .thenReturn(List.of(mock(MigratorHistory.class)));

        migrator.call();

        verify(taskRepository, never())
                .findSelfInitiatedTasksByTypeSince(any(), any(Date.class));
        verify(accountClaimRepository, never()).findAll();
        verify(accountClaimRepository, never()).saveAll(anyList());
        verify(migratorHistoryRepository, never()).save(any(MigratorHistory.class));
        verifyNoInteractions(reportingMetricsEventService, accountRepresentativeInvitationRepository,
                accountContactRepository);
    }

    @Test
    void shouldNotInsertClaimsWhenNoTasksAreFound() throws Throwable {
        when(migratorHistoryRepository.findByMigratorName(
                MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR))
                .thenReturn(List.of());

        when(taskRepository.findSelfInitiatedTasksByTypeSince(
                any(RequestType.class), any(Date.class)))
                .thenReturn(List.of());

        migrator.call();

        verify(taskRepository).findSelfInitiatedTasksByTypeSince(any(RequestType.class), any(Date.class));
        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
        verify(accountClaimRepository, never()).saveAll(anyList());
        verifyNoInteractions(reportingMetricsEventService, accountRepresentativeInvitationRepository,
                accountContactRepository);
    }

    @Test
    void shouldInsertAccountClaimsForTasksNotAlreadyMigrated() throws Throwable {
        when(migratorHistoryRepository.findByMigratorName(
                MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR))
                .thenReturn(List.of());

        LocalDateTime now = LocalDateTime.now();

        Task task1 = createTask(100L, 1000L, now);
        Task task2 = createTask(200L, 2000L, now);

        when(taskRepository.findSelfInitiatedTasksByTypeSince(
                any(RequestType.class), any(Date.class)))
                .thenReturn(List.of(task1, task2));

        when(accountClaimRepository.findAll()).thenReturn(List.of());
        when(accountRepresentativeInvitationRepository
                .findByRepresentativeIdInAndAccountId(List.of(1L, 2L), 10L))
                .thenReturn(List.of(createRepresentativeInvitation(now.minusDays(7)),
                                createRepresentativeInvitation(now.minusDays(6))
                        )
                );
        when(accountContactRepository.findByAccountIdentifier(100L))
                .thenReturn(List.of(createMetsAccountContact(now.minusDays(5))));
        when(accountContactRepository.findByAccountIdentifier(200L))
                .thenReturn(List.of(createMetsAccountContact(now.minusDays(5))));

        migrator.call();

        ArgumentCaptor<Collection<AccountClaim>> accountClaimsCaptor =
                ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<AccountClaimInvitationSentEvent> sentEventCaptor =
                ArgumentCaptor.forClass(AccountClaimInvitationSentEvent.class);
        ArgumentCaptor<AccountClaimInitiatedEvent> initiatedEventCaptor =
                ArgumentCaptor.forClass(AccountClaimInitiatedEvent.class);
        ArgumentCaptor<AccountClaimCompletionEvent> completionEventCaptor =
                ArgumentCaptor.forClass(AccountClaimCompletionEvent.class);

        verify(accountClaimRepository).saveAll(accountClaimsCaptor.capture());

        Collection<AccountClaim> savedClaims = accountClaimsCaptor.getValue();

        assertEquals(2, savedClaims.size());
        assertThat(savedClaims).extracting(AccountClaim::getAccountIdentifier)
                .containsExactlyInAnyOrder(100L, 200L);
        assertThat(savedClaims).extracting(AccountClaim::getRequestId)
                .containsExactlyInAnyOrder(1000L, 2000L);
        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
        verify(reportingMetricsEventService, times(4)).processEvent(sentEventCaptor.capture());
        verify(reportingMetricsEventService, times(2)).processEvent(initiatedEventCaptor.capture());
        verify(reportingMetricsEventService, times(2)).processEvent(completionEventCaptor.capture());

        assertThat(sentEventCaptor.getAllValues()).extracting(AccountClaimInvitationSentEvent::getAccountIdentifier)
                .containsExactlyInAnyOrder(100L, 100L, 200L, 200L);
        assertThat(sentEventCaptor.getAllValues()).extracting(AccountClaimInvitationSentEvent::getAccountClaimCode)
                .containsExactlyInAnyOrder("ACC123", "ACC123", "ACC123", "ACC123");
        assertThat(sentEventCaptor.getAllValues()).extracting(AccountClaimInvitationSentEvent::getAccountClaimInvitationSendDateTime)
                .containsExactlyInAnyOrder(now.minusDays(10), now.minusDays(10), now.minusDays(5), now.minusDays(5));

        assertThat(initiatedEventCaptor.getAllValues())
                .extracting(AccountClaimInitiatedEvent::getAccountIdentifier)
                .containsExactlyInAnyOrder(100L, 200L);
        assertThat(initiatedEventCaptor.getAllValues())
                .extracting(AccountClaimInitiatedEvent::getAccountClaimCode)
                .containsExactlyInAnyOrder("ACC123", "ACC123");
        assertThat(initiatedEventCaptor.getAllValues())
                .extracting(AccountClaimInitiatedEvent::getAccountClaimInitiatedDateTime)
                .extracting(dateTime -> dateTime.truncatedTo(ChronoUnit.SECONDS))
                .containsExactlyInAnyOrder(now.minusDays(2).truncatedTo(ChronoUnit.SECONDS), now.minusDays(2).truncatedTo(ChronoUnit.SECONDS));

        assertThat(completionEventCaptor.getAllValues())
                .extracting(AccountClaimCompletionEvent::getAccountIdentifier)
                .containsExactlyInAnyOrder(100L, 200L);
        assertThat(completionEventCaptor.getAllValues())
                .extracting(AccountClaimCompletionEvent::getAccountClaimCode)
                .containsExactlyInAnyOrder("ACC123", "ACC123");
        assertThat(completionEventCaptor.getAllValues())
                .extracting(AccountClaimCompletionEvent::getAccountClaimCompletionDateTime)
                .extracting(dateTime -> dateTime.truncatedTo(ChronoUnit.SECONDS))
                .containsExactlyInAnyOrder(now.minusDays(1).truncatedTo(ChronoUnit.SECONDS), now.minusDays(1).truncatedTo(ChronoUnit.SECONDS));

    }

    @Test
    void shouldNotInsertAccountClaimWhenKeyAlreadyExists() throws Throwable {
        when(migratorHistoryRepository.findByMigratorName(
                MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR))
                .thenReturn(List.of());

        LocalDateTime now = LocalDateTime.now();
        Task existingTask = createTask(100L, 1000L, now);
        Task newTask = createTask(200L, 2000L, now);

        when(taskRepository.findSelfInitiatedTasksByTypeSince(
                any(RequestType.class), any(Date.class)))
                .thenReturn(List.of(existingTask, newTask));

        AccountClaim existingClaim = new AccountClaim();
        existingClaim.setAccountIdentifier(100L);
        existingClaim.setRequestId(1000L);

        when(accountClaimRepository.findAll())
                .thenReturn(List.of(existingClaim));
        when(accountRepresentativeInvitationRepository
                .findByRepresentativeIdInAndAccountId(List.of(1L, 2L), 10L))
                .thenReturn(List.of(createRepresentativeInvitation(now.minusDays(7)),
                                createRepresentativeInvitation(now.minusDays(6))
                        )
                );
        when(accountContactRepository.findByAccountIdentifier(200L))
                .thenReturn(List.of(createMetsAccountContact(now.minusDays(5))));

        migrator.call();

        ArgumentCaptor<Collection<AccountClaim>> accountClaimsCaptor =
                ArgumentCaptor.forClass(Collection.class);
        ArgumentCaptor<AccountClaimInvitationSentEvent> sentEventCaptor =
                ArgumentCaptor.forClass(AccountClaimInvitationSentEvent.class);
        ArgumentCaptor<AccountClaimInitiatedEvent> initiatedEventCaptor =
                ArgumentCaptor.forClass(AccountClaimInitiatedEvent.class);
        ArgumentCaptor<AccountClaimCompletionEvent> completionEventCaptor =
                ArgumentCaptor.forClass(AccountClaimCompletionEvent.class);

        verify(accountClaimRepository).saveAll(accountClaimsCaptor.capture());

        Collection<AccountClaim> savedClaims = accountClaimsCaptor.getValue();

        verify(reportingMetricsEventService, times(2)).processEvent(sentEventCaptor.capture());
        verify(reportingMetricsEventService, times(1)).processEvent(initiatedEventCaptor.capture());
        verify(reportingMetricsEventService, times(1)).processEvent(completionEventCaptor.capture());

        assertEquals(1, savedClaims.size());
        assertThat(savedClaims).extracting(AccountClaim::getAccountIdentifier)
                .containsExactly(200L);
        assertThat(savedClaims).extracting(AccountClaim::getRequestId)
                .containsExactly(2000L);

        assertThat(sentEventCaptor.getAllValues()).extracting(AccountClaimInvitationSentEvent::getAccountIdentifier)
                .containsExactlyInAnyOrder(200L, 200L);
        assertThat(sentEventCaptor.getAllValues()).extracting(AccountClaimInvitationSentEvent::getAccountClaimCode)
                .containsExactlyInAnyOrder("ACC123", "ACC123");
        assertThat(sentEventCaptor.getAllValues()).extracting(AccountClaimInvitationSentEvent::getAccountClaimInvitationSendDateTime)
                .containsExactlyInAnyOrder(now.minusDays(10), now.minusDays(5));

        assertThat(initiatedEventCaptor.getAllValues())
                .extracting(AccountClaimInitiatedEvent::getAccountIdentifier)
                .containsExactlyInAnyOrder( 200L);
        assertThat(initiatedEventCaptor.getAllValues())
                .extracting(AccountClaimInitiatedEvent::getAccountClaimCode)
                .containsExactlyInAnyOrder("ACC123");
        assertThat(initiatedEventCaptor.getAllValues())
                .extracting(AccountClaimInitiatedEvent::getAccountClaimInitiatedDateTime)
                .extracting(dateTime -> dateTime.truncatedTo(ChronoUnit.SECONDS))
                .containsExactlyInAnyOrder(now.minusDays(2).truncatedTo(ChronoUnit.SECONDS));

        assertThat(completionEventCaptor.getAllValues())
                .extracting(AccountClaimCompletionEvent::getAccountIdentifier)
                .containsExactlyInAnyOrder(200L);
        assertThat(completionEventCaptor.getAllValues())
                .extracting(AccountClaimCompletionEvent::getAccountClaimCode)
                .containsExactlyInAnyOrder("ACC123");
        assertThat(completionEventCaptor.getAllValues())
                .extracting(AccountClaimCompletionEvent::getAccountClaimCompletionDateTime)
                .extracting(dateTime -> dateTime.truncatedTo(ChronoUnit.SECONDS))
                .containsExactlyInAnyOrder(now.minusDays(1).truncatedTo(ChronoUnit.SECONDS));

        verify(migratorHistoryRepository).save(any(MigratorHistory.class));
    }


    @Test
    void shouldQueryTasksFromFebruary18IncludingStartOfDay() throws Throwable {
        when(migratorHistoryRepository.findByMigratorName(
                MigratorName.ACCOUNT_CLAIM_DATA_MIGRATOR))
                .thenReturn(List.of());

        when(taskRepository.findSelfInitiatedTasksByTypeSince(
                any(RequestType.class), any(Date.class)))
                .thenReturn(List.of());

        migrator.call();

        ArgumentCaptor<RequestType> typeCaptor =
                ArgumentCaptor.forClass(RequestType.class);
        ArgumentCaptor<Date> dateCaptor =
                ArgumentCaptor.forClass(Date.class);

        verify(taskRepository).findSelfInitiatedTasksByTypeSince(
                typeCaptor.capture(), dateCaptor.capture());

        assertEquals(
                RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST,
                typeCaptor.getValue());

        Date expectedDate = Date.from(
                LocalDate.of(2026, 2, 18)
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant());

        assertEquals(expectedDate, dateCaptor.getValue());
    }

    private Task createTask(Long accountIdentifier, Long requestId, LocalDateTime now) {

        AccountHolderRepresentative representative1 = new AccountHolderRepresentative();
        representative1.setId(1L);
        AccountHolderRepresentative representative2 = new AccountHolderRepresentative();
        representative2.setId(2L);

        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setAccountHolderRepresentatives(List.of(representative1, representative2));

        Installation installation = new Installation();
        installation.setId(1L);
        installation.setFirstInvitationDate(now.minusDays(10));

        Account account = new Account();
        account.setIdentifier(accountIdentifier);
        account.setAccountHolder(accountHolder);
        account.setCompliantEntity(installation);
        account.setId(10L);
        account.setAccountClaimCode("ACC123");

        Task task = new Task();
        task.setAccount(account);
        task.setRequestId(requestId);
        task.setStatus(RequestStateEnum.APPROVED);
        task.setInitiatedDate(Date.from(now.minusDays(2).atZone(ZoneId.systemDefault())
                .toInstant()));
        task.setCompletedDate(Date.from(now.minusDays(1).atZone(ZoneId.systemDefault())
                .toInstant()));

        return task;
    }

    private AccountRepresentativeInvitation createRepresentativeInvitation(LocalDateTime invitedDate) {
        return AccountRepresentativeInvitation.builder()
                .invitedDate(invitedDate)
                .build();
    }

    private MetsAccountContact createMetsAccountContact(LocalDateTime invitedDate) {
        return MetsAccountContact.builder()
                .invitedOn(invitedDate)
                .build();
    }
}