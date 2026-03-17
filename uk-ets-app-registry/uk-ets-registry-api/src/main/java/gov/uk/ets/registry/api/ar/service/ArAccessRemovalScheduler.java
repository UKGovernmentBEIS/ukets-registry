package gov.uk.ets.registry.api.ar.service;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountAccessBackup;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.repository.AccountAccessBackupRepository;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ArAccessRemovalScheduler {

    private final AccountAccessRepository accountAccessRepository;
    private final AccountAccessBackupRepository accountAccessBackupRepository;
    private final AuthorizedRepresentativeService authorizedRepresentativeService;
    private final EventService eventService;
    public static final String ACCOUNT_AUTHORISED_REPRESENTATIVE_REMOVED =
            "Authorised representative removed due to Kyoto termination.";

    /**
     * Removes account access to all ARs appointed to Person Holding Accounts or
     * Former Operator Holding Accounts.
     */
    @Transactional
    @SchedulerLock(name = "arAccessRemovalSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.ar.access.removal.start}")
    public void processArAccessRemoval() {

        int currentYear = Year.now(ZoneId.of("Europe/London")).getValue();
        if (currentYear != 2026) {
            log.info("AR Access Removal Scheduler skipped. Current year: {}", currentYear);
            return;
        }

        log.info("AR Access Removal Scheduler started at {}", LocalDateTime.now());
        LockAssert.assertLocked();
        List<AccountAccess> accountAccesses = accountAccessRepository.findByKyotoAccountTypeAndState(List.of(KyotoAccountType.PERSON_HOLDING_ACCOUNT,
                KyotoAccountType.FORMER_OPERATOR_HOLDING_ACCOUNT), AccountAccessState.ACTIVE);

        if (CollectionUtils.isEmpty(accountAccesses)) {
            log.info("No eligible KP Account Accesses found to remove.");
            return;
        }

        if (accountAccessBackupRepository.count() > 0) {
            throw new IllegalStateException("Account Access Backup table is not empty.");
        }

        List<AccountAccessBackup> accountAccessBackups = accountAccesses.stream()
                .map(this::createAccountAccessBackup)
                .toList();

        long inserted = accountAccessBackupRepository.saveAll(accountAccessBackups).size();


        if (inserted != accountAccesses.size()) {
            throw new IllegalStateException("AR Access Removal Scheduler Backup size mismatch. Aborting removal.");
        }

        accountAccesses.forEach(accountAccess -> {
            accountAccess.setState(AccountAccessState.REMOVED);

            eventService.createAndPublishEvent(accountAccess.getAccount().getIdentifier().toString(), 
                     accountAccess.getUser().getUrid(),
                     "Removed due to Kyoto termination.",
                EventType.ACCOUNT_AR_REMOVED, ACCOUNT_AUTHORISED_REPRESENTATIVE_REMOVED);
            
            boolean removedRole = authorizedRepresentativeService.removeKeycloakRoleIfNoOtherAccountAccess(
                    accountAccess.getUser().getUrid(), accountAccess.getUser().getIamIdentifier());
            if (removedRole) {
                eventService.createAndPublishEvent(accountAccess.getUser().getUrid(), null, UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral(),
                        EventType.USER_ROLE_REMOVED, "Remove User Role");            	
            }

        });

        log.info("AR Access Removal Scheduler finished at {}", LocalDateTime.now());
    }

    private AccountAccessBackup createAccountAccessBackup(AccountAccess accountAccess) {
        return AccountAccessBackup.builder()
                .originalAccessId(accountAccess.getId())
                .userId(accountAccess.getUser().getId())
                .accountId(accountAccess.getAccount().getId())
                .state(accountAccess.getState())
                .right(accountAccess.getRight())
                .backupDate(LocalDateTime.now())
                .build();
    }
}
