package gov.uk.ets.registry.api.task.searchmetadata.handler;

import gov.uk.ets.registry.api.task.domain.Task;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.query.NativeQuery;

import java.util.Map;

public interface SearchMetadataHandler {

    boolean canHandle(Task task);
    void handle(Task task, AbstractEvent event);
    default void executeUpdate(AbstractEvent abstractEvent, String query, Map<String, Object> params) {
        NativeQuery<?> nativeQuery = abstractEvent.getSession().createNativeQuery(query).setFlushMode(FlushMode.MANUAL);
        params.forEach(nativeQuery::setParameter);
        nativeQuery.executeUpdate();
    }
}