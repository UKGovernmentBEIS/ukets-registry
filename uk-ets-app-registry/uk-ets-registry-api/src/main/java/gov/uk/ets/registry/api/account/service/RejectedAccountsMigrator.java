package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.migration.Migrator;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.List;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Log4j2
public class RejectedAccountsMigrator implements Migrator {

    private final AccountRepository accountRepository;

    private final TaskRepository taskRepository;

    private final Mapper mapper;

    private final AccountDTOFactory accountDTOFactory;

    private final CompliantEntityRepository compliantEntityRepository;

    private final EntityManager entityManager;

    private final MigratorHistoryRepository migratorHistoryRepository;

    @Transactional
    public void migrate() {
        log.info("Starting Rejected Accounts Migrator");
        List<MigratorHistory> migratorHistoryList =
                migratorHistoryRepository.findByMigratorName(MigratorName.REJECTED_ACCOUNTS_MIGRATOR);
        if (CollectionUtils.isNotEmpty(migratorHistoryList)) {
            log.info("[Migration of Rejected Accounts attribute]," +
                    " has already been performed, hence, it is skipping it.");
            return;
        }
        var proposedAccounts =
                accountRepository.findAccountByAccountStatus(AccountStatus.REJECTED).stream().toList();
        log.info("Found {} accounts in rejected status", proposedAccounts.size());
        for (Account account : proposedAccounts) {
            Long accountIdentifier = account.getIdentifier();
            var accountDTO = this.getAccount(accountIdentifier);
            var accountOpeningTaskForMigration = taskRepository
                .findByAccount_IdentifierAndTypeAndStatus(accountIdentifier, RequestType.ACCOUNT_OPENING_REQUEST,
                    RequestStateEnum.REJECTED);
            if (accountOpeningTaskForMigration.isPresent()) {
                var task = accountOpeningTaskForMigration.get();
                task.setAccount(null);
                var subTasks = taskRepository.findSubTasks(task.getId());
                for (Task child : subTasks) {
                    child.setAccount(null);
                    log.info("Parent Task:{}, Child task identifier:{} type {} diff:{}", task.getRequestId(),
                        child.getRequestId(), child.getType(),
                        child.getDifference());
                }
                if (account.getCompliantEntity() != null) {
                    // simplify migration - we are not interested in previous changes. The latest regulator is enough
                    // the old implementation did not support reset, and after two subsequent regulator changes the initial
                    // one is lost anyway.
                    accountDTO.getOperator().setChangedRegulator(null);
                }
                String accountAsJson = mapper.convertToJson(accountDTO);
                log.info("Setting json:{} on task request id:{}", accountAsJson, task.getRequestId());
                task.setDifference(accountAsJson);
                deleteRejectedAccount(account);
            }
        }
        log.info("Rejected accounts migration completed successfully");
        deleteRejectedAccountHoldersWithNoAccountReference();
    }

    private void deleteRejectedAccount(Account account) {
        entityManager.createQuery("DELETE FROM InstallationOwnership WHERE account = :account")
            .setParameter("account", account)
            .executeUpdate();
        entityManager.createQuery("DELETE FROM AccountOwnership WHERE account = :account")
            .setParameter("account", account)
            .executeUpdate();
        entityManager.createQuery("DELETE FROM AccountAccess WHERE account = :account")
            .setParameter("account", account)
            .executeUpdate();
        entityManager.createQuery("DELETE FROM Task WHERE account = :account")
            .setParameter("account", account)
            .executeUpdate();
        accountRepository.delete(account);
        if (account.getCompliantEntity() != null) {
            compliantEntityRepository.delete(account.getCompliantEntity());
        }
    }

    private AccountDTO getAccount(Long identifier) {
        Account account = accountRepository.findByIdentifierWithAccountHolder(identifier).orElse(null);
        if (account == null) {
            return new AccountDTO();
        }
        return accountDTOFactory.create(account, true);
    }

    private void deleteRejectedAccountHoldersWithNoAccountReference() {
        List<Long> rejectedAccountHoldersWithExistingReferences = entityManager.createNativeQuery(
                " select distinct ah.id from account_holder ah, account a where ah.status='REJECTED' and ah.id=a.account_holder_id")
            .getResultList();
        if (rejectedAccountHoldersWithExistingReferences.size() > 0) {

            log.warn(
                "There are still references of rejected account holders to non rejected accounts for the account holder ids:{}",
                rejectedAccountHoldersWithExistingReferences.toString());
            // just to be on the save side keep rejected holders with references to existing accounts if exist

            entityManager.createNativeQuery(
                    "DELETE FROM account_holder ah where ah.status='REJECTED' and ah.id not in (?1)")
                .setParameter(1, rejectedAccountHoldersWithExistingReferences)
                .executeUpdate();
        } else {
            //no references to existing accounts - so delete all the account holders with rejected status
            entityManager.createNativeQuery(
                    "DELETE FROM account_holder ah where ah.status='REJECTED'")
                .executeUpdate();
        }

    }

}
