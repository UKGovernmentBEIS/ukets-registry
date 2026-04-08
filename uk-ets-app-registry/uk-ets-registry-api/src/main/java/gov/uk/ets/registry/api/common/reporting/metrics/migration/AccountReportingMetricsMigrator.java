package gov.uk.ets.registry.api.common.reporting.metrics.migration;

import static java.util.stream.Collectors.summingLong;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.InstallationOwnershipRepository;
import gov.uk.ets.registry.api.common.reporting.metrics.domain.AccountMetrics;
import gov.uk.ets.registry.api.common.reporting.metrics.outbox.repository.AccountMetricsRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockingTaskExecutor.Task;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountReportingMetricsMigrator implements Task {

    private final AccountRepository accountRepository;
    private final AccountMetricsRepository accountMetricsRepository;
    private final EmissionsEntryRepository emissionsEntryRepository;
    private final TransactionRepository transactionRepository;
    private final InstallationOwnershipRepository installationOwnershipRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;
    
    @Override
    @Transactional
    public void call() throws Throwable {
        log.info("AccountReportingMetricsMigrator");
        // CPD-OFF
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.ACCOUNT_REPORTING_METRICS_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[AccountReportingMetricsMigrator] has already been performed, skipping.");
            return;
        }

        List<Account> accounts = accountRepository
                .findByRegistryAccountTypeIn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT));

        List<AccountMetrics> accountMetrics = new ArrayList<AccountMetrics>();
        
        accounts
                .forEach(account -> {
                    AccountMetrics metrics = new AccountMetrics();
                    metrics.setIdentifier(account.getIdentifier());
                	metrics.setDynamicComplianceStatus(account.getComplianceStatus());

                    List<Long> totalVerifiedEmissions = emissionsEntryRepository
                            .findTotalVerifiedEmissionsByAccountIdentifier(account.getIdentifier())
                            .stream().filter(Objects::nonNull).collect(toList());

                    metrics.setTotalVerifiedEmissions(!totalVerifiedEmissions.isEmpty() ?
                            totalVerifiedEmissions.stream().collect(summingLong(t -> t)) :
                            null);

                    // Query the transactions to find the total amounts
                    Set<Long> accountIdentifiers = Stream.of(account.getIdentifier())
                        .collect(toSet());
                    if (RegistryAccountType.OPERATOR_HOLDING_ACCOUNT
                        .equals(account.getRegistryAccountType())) {
                        accountIdentifiers.addAll(installationOwnershipRepository
                            .findByInstallation((Installation) Hibernate
                                .unproxy(account.getCompliantEntity()))
                            .stream().map(t -> t.getAccount().getIdentifier())
                            .collect(toList()));
                    }

                    Long surrendered = accountIdentifiers.stream()
                        .map(t -> transactionRepository
                            .findByTransferringAccount_AccountIdentifierAndTypeAndStatus(
                                t, TransactionType.SurrenderAllowances,
                                TransactionStatus.COMPLETED))
                        .flatMap(List::stream)
                        .collect(summingLong(Transaction::getQuantity));
                    
                    metrics.setQuantitySurrendered(surrendered);

                    Long surrenderedReversed = accountIdentifiers.stream()
                        .map(t -> transactionRepository
                            .findByAcquiringAccount_AccountIdentifierAndTypeAndStatus(
                                t,
                                TransactionType.ReverseSurrenderAllowances,
                                TransactionStatus.COMPLETED))
                        .flatMap(List::stream)
                        .collect(summingLong(Transaction::getQuantity));

                    metrics.setQuantityReversedSurrendered(surrenderedReversed);
       
                    metrics.setSurrenderBalance(surrendered - surrenderedReversed - Optional.ofNullable(metrics.getTotalVerifiedEmissions()).orElse(0L));
                    accountMetrics.add(metrics);
                }
        );

        accountMetricsRepository.saveAll(accountMetrics);
        // CPD-ON
        updateMigrationHistory();
        log.info("[AccountReportingMetricsMigrator] completed for {} accounts.", accounts.size());

    }


    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.ACCOUNT_REPORTING_METRICS_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }
}
