package gov.uk.ets.registry.api.account.service.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class SmallVolumeSalesMigrator implements Migrator {

    private final AccountRepository accountRepository;
    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    public void migrate() {

        log.info("SmallVolumeSalesMigrator");

        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.SMALL_VOLUME_SALES_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[SmallVolumeSalesMigrator] has already been performed, skipping.");
            return;
        }

        List<Account> accounts = accountRepository
                .findByRegistryAccountTypeIn(List.of(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT,
                        RegistryAccountType.TRADING_ACCOUNT));

        if (accounts.isEmpty()) {
            log.info("[SmallVolumeSalesMigrator] There are no Installation, Aviation, Maritime or Trading accounts to migrate.");
            return;
        }

        accounts
                .forEach(account -> {
                            if (Objects.isNull(account.getSalesContact()) || StringUtils.isBlank(account.getSalesContact().getEmailAddress()) &&
                                    StringUtils.isBlank(account.getSalesContact().getPhoneNumber())) {
                                account.setSellingAllowances(false);
                                account.setSalesContact(null);
                            } else if (account.getSalesContact() != null && (StringUtils.isNotBlank(account.getSalesContact().getEmailAddress()) ||
                                    StringUtils.isNotBlank(account.getSalesContact().getPhoneNumber()))) {
                                account.setSellingAllowances(true);
                                account.getSalesContact().setUka1To99(true);
                                account.getSalesContact().setUka100To999(true);
                                account.getSalesContact().setUka1000Plus(true);
                            }
                        }
                );

        accountRepository.saveAll(accounts);

        updateMigrationHistory();
        log.info("[SmallVolumeSalesMigrator] completed for {} accounts.", accounts.size());
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.SMALL_VOLUME_SALES_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
    }
}
