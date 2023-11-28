package gov.uk.ets.registry.api.account.service.migration;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Log4j2
public class KyotoGovAccountOpeningMigrator implements Migrator {

    private final AccountHolderRepository accountHolderRepository;
    private final AccountRepository accountRepository;
    private final AccountAccessRepository accountAccessRepository;
    private final UserRepository userRepository;
    private final GeneratorService generatorService;
    private final MigratorHistoryRepository migratorHistoryRepository;

    private final static String ACCOUNT_NAME = "ESD AAU Deposit Account";
    private static final String ACCOUNT_HOLDER_NAME = "BEIS International";
    private static final AccountType ACCOUNT_TYPE = AccountType.PARTY_HOLDING_ACCOUNT;

    @Override
    @Transactional
    public void migrate() {

        log.info("KyotoGovAccountOpeningMigrator");

        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(
                        MigratorName.KYOTO_GOV_ACCOUNT_OPENING_MIGRATOR);

        if (!migratorHistoryList.isEmpty()) {
            log.info("[KyotoGovAccount opening migrator]," + " has already been performed, skipping.");
            return;
        }

        List<AccountHolder> accountHolders = accountHolderRepository.findByNameAndType(ACCOUNT_HOLDER_NAME,AccountHolderType.INDIVIDUAL);
        if (accountHolders.isEmpty()) {
            log.info("No Account Holder was found with name {}", ACCOUNT_HOLDER_NAME);
            return;
        }

        AccountHolder accountHolder = accountHolders.stream().findFirst().orElseThrow();
        Account account = new Account();
        account.setAccountName(ACCOUNT_NAME);
        account.setRegistryAccountType(ACCOUNT_TYPE.getRegistryType());
        account.setKyotoAccountType(ACCOUNT_TYPE.getKyotoType());
        account.setIdentifier(accountRepository.getNextIdentifier());
        account.setStatus(Status.ACTIVE);
        account.setAccountStatus(AccountStatus.OPEN);
        Date insertDate = new Date();
        account.setOpeningDate(insertDate);
        account.setBalance(0L);
        account.setCommitmentPeriodCode(CommitmentPeriod.CP0.getCode());
        account.setCheckDigits(generatorService
                .calculateCheckDigits(ACCOUNT_TYPE.getKyotoCode(), account.getIdentifier(), account.getCommitmentPeriodCode()));
        account.setFullIdentifier(String
                .format("%s-%d-%d-%d-%d", ACCOUNT_TYPE.getRegistryCode(), ACCOUNT_TYPE.getKyotoCode(), account.getIdentifier(),
                        account.getCommitmentPeriodCode(), account.getCheckDigits()));
        account.setAccountType(ACCOUNT_TYPE.getLabel());
        account.setRegistryCode(ACCOUNT_TYPE.getRegistryCode());
        account.setAccountHolder(accountHolder);

        account.setTransfersToAccountsNotOnTheTrustedListAreAllowed(true);
        account.setApprovalOfSecondAuthorisedRepresentativeIsRequired(true);
        account.setBillingAddressSameAsAccountHolderAddress(false);
        account.setSinglePersonApprovalRequired(false);

        Account savedAccount = accountRepository.save(account);

        List<String> adminRoles = UserRole.getAdminRoles().stream().map(UserRole::getKeycloakLiteral).toList();
        List<User> adminUsers = userRepository.findUsersByRoleName(adminRoles);

        adminUsers.forEach(adminUser -> {
            AccountAccess accountAccess = new AccountAccess();
            accountAccess.setAccount(savedAccount);
            accountAccess.setUser(adminUser);
            accountAccess.setRight(AccountAccessRight.ROLE_BASED);
            accountAccess.setState(AccountAccessState.ACTIVE);
            accountAccessRepository.save(accountAccess);
        });

        updateMigrationHistory();
        log.info("Saved new KyotoGovAccount with id {}", savedAccount.getId().toString());
    }

    private void updateMigrationHistory() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.KYOTO_GOV_ACCOUNT_OPENING_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        log.info("Migration of KyotoGovAccount opening completed");
    }

}
