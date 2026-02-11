package gov.uk.ets.registry.api.account.messaging;

import static java.time.ZoneOffset.UTC;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.compliance.messaging.ComplianceEventService;
import gov.uk.ets.registry.api.migration.Migrator;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.EntityManager;

import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class AccountsComplianceMigrator implements Migrator {

    private final EntityManager entityManager;

    private final ComplianceEventService complianceEventService;
    private final MigratorHistoryRepository migratorHistoryRepository;
    /**
     * Sends an account opening event to the compliance service for every existing account in
     * the registry that does not have a compliance status already.
     * Skips rejected accounts since there is a pending task to remove them from the database
     * as we did for the proposed accounts already
     */
    @Transactional
    public void migrate() {
        log.info("Starting migration of activeAccounts attribute");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.ACCOUNTS_COMPLIANCE_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[Accounts Compliance Migrator], has already been performed, hence, it is skipping it.");
            return;
        }
        List<Account> activeAccounts =
            entityManager.createQuery("select a from Account a " +
                        "join fetch a.compliantEntity where " +
                        "   a.complianceStatus is null and " +
                        "   a.accountStatus not in (gov.uk.ets.registry.api.transaction.domain.type.AccountStatus.REJECTED) " +
                        "   and a.compliantEntity is not null",
                    Account.class)
                .getResultList();
        if (activeAccounts == null) {
            log.debug("ActiveAccounts List<Account> Collection has not data! Migration canceled.");
            return;
        }
        log.info("Found {}  active accounts without compliance status", activeAccounts.size());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));
        for (Account account : activeAccounts) {
            CompliantEntity compliantEntity = account.getCompliantEntity();
            if (compliantEntity != null) {
                CompliantEntityInitializationEvent accountCreationEvent = CompliantEntityInitializationEvent.builder()
                    .firstYearOfVerifiedEmissions(compliantEntity.getStartYear())
                    .lastYearOfVerifiedEmissions(compliantEntity.getEndYear())
                    .actorId("system")
                    .eventId(UUID.randomUUID())
                    .compliantEntityId(compliantEntity.getIdentifier())
                    .dateTriggered(now)
                    // TODO: is this ok? using the opening date?
                    .dateRequested(LocalDateTime.ofInstant(account.getOpeningDate().toInstant(), UTC))
                    .currentYear(now.getYear())
                    .build();
                complianceEventService.processEvent(accountCreationEvent);
            }
        }
        if (!activeAccounts.isEmpty()) {
            log.info("Successfully sent account creation events to compliance service");
        }
    }
}
