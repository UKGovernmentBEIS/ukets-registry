package gov.uk.ets.registry.api.tal.service;

import gov.uk.ets.registry.api.tal.domain.TrustedAccount;
import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import gov.uk.ets.registry.api.tal.repository.TrustedAccountRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * Scheduler for trusted account lists (TAL).
 */
@Log4j2
@Service
@AllArgsConstructor
public class TrustedAccountListScheduler {

    /**
     * Repository for trusted accounts.
     */
    private TrustedAccountRepository trustedAccountRepository;

    /**
     * Launches delayed transactions which are eligible to start.
     */
    @Transactional
    @SchedulerLock(name = "trustedAccountListSchedulerLock", lockAtLeastFor = "500ms")
    @Scheduled(cron = "${scheduler.tal.start}")
    public void processDelayedTrustedAccounts() {
        LockAssert.assertLocked();
        List<TrustedAccount> trustedAccounts = trustedAccountRepository.findByStatusEqualsAndActivationDateBefore(
            TrustedAccountStatus.PENDING_ACTIVATION, LocalDateTime.now());

        if (CollectionUtils.isEmpty(trustedAccounts)) {
            log.info("No eligible delayed TALs found");
        }
        trustedAccounts.forEach(trustedAccount -> trustedAccount.setStatus(TrustedAccountStatus.ACTIVE));
    }

}
