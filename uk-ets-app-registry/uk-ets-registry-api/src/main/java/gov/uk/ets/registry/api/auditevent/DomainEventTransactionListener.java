package gov.uk.ets.registry.api.auditevent;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Log4j2
@RequiredArgsConstructor
@Component
public class DomainEventTransactionListener {

    private final DomainEventClient domainEventClient;

    @TransactionalEventListener
    public void processTransactionCommit(DomainEvent<DomainObject> event) {
        log.info("Handling event : {}", event);
        domainEventClient.emitDomainEvent(event);
    }
}
