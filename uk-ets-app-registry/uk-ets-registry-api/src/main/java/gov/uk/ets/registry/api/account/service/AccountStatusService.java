package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * Service for changing the account status.
 */
@Service
@Log4j2
@AllArgsConstructor
public class AccountStatusService {

    /**
     * Repository for accounts.
     */
    private AccountRepository accountRepository;

    /**
     * Service for events.
     */
    private EventService eventService;

    /**
     * Changes the account status and produces a notification event.
     * @param accountIdentifier The account identifier.
     * @param newStatus The new status.
     * @param eventMessage The event message.
     */
    public void changeAccountStatus(Long accountIdentifier, AccountStatus newStatus, String eventMessage,
                                    String eventAction) {
        Optional<Account> optional = accountRepository.findByIdentifier(accountIdentifier);
        if (optional.isPresent()) {
            Account account = optional.get();
            account.setAccountStatus(newStatus);
            accountRepository.save(account);

            log.info("Change account status: {}", eventMessage);

            eventService.createAndPublishEvent(
                account.getIdentifier().toString(),
                null,
                eventMessage,
                EventType.ACCOUNT_STATUS_CHANGED,
                eventAction);
        }
    }
}
