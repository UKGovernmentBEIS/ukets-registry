package gov.uk.ets.registry.api.task.searchmetadata.handler;

import gov.uk.ets.registry.api.task.domain.Task;
import java.util.Map;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.AbstractEvent;
import org.hibernate.event.spi.EventSource;
import org.hibernate.query.NativeQuery;

public interface SearchMetadataHandler {

    boolean canHandle(Task task);
    
    void handle(Task task, AbstractEvent event);
    
    default void executeUpdate(AbstractEvent abstractEvent, String query, Map<String, Object> params) {
        EventSource session = abstractEvent.getSession();
        session.getJdbcCoordinator().executeBatch();
        NativeQuery<Object> nativeQuery = session.createNativeQuery(query, Object.class).setHibernateFlushMode(FlushMode.MANUAL);
        params.forEach(nativeQuery::setParameter);
        nativeQuery.executeUpdate();
    }
}