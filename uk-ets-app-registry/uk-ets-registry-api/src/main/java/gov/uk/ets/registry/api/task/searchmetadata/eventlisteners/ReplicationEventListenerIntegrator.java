package gov.uk.ets.registry.api.task.searchmetadata.eventlisteners;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReplicationEventListenerIntegrator {

    private final EntityManagerFactory entityManagerFactory;

    private final ReplicationInsertEventListener replicationInsertEventListener;

    private final ReplicationUpdateEventListener replicationUpdateEventListener;

    private final ReplicationDeleteEventListener replicationDeleteEventListener;

    @PostConstruct
    public void integrate() {
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
        registry.getEventListenerGroup(EventType.POST_INSERT).appendListener(replicationInsertEventListener);
        registry.getEventListenerGroup(EventType.POST_UPDATE).appendListener(replicationUpdateEventListener);
        registry.getEventListenerGroup(EventType.PRE_DELETE).appendListener(replicationDeleteEventListener);
    }
}
