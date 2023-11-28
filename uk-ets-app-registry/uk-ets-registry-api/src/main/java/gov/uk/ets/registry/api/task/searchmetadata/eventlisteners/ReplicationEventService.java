package gov.uk.ets.registry.api.task.searchmetadata.eventlisteners;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.searchmetadata.handler.SearchMetadataHandler;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostUpdateEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplicationEventService {

    private final List<SearchMetadataHandler> handlers;

    public void insertSearchMetadata(PostInsertEvent event) throws HibernateException {
        final Object entity = event.getEntity();
        execute(event, entity);
    }

    public void updateSearchMetadata(PostUpdateEvent event) throws HibernateException {
        final Object entity = event.getEntity();
        execute(event, entity);
    }

    private void execute(AbstractEvent event, Object entity) {
        if (entity instanceof Task task) {
            handlers.stream()
                    .filter(handler -> handler.canHandle(task))
                    .forEach(handler -> handler.handle(task, event));
        }
    }
}