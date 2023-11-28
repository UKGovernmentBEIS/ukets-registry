package gov.uk.ets.registry.api.task.searchmetadata.eventlisteners;

import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import org.hibernate.FlushMode;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.springframework.stereotype.Component;

@Component
public class ReplicationDeleteEventListener implements PreDeleteEventListener {

    private static final long serialVersionUID = -9108967988371023846L;

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        final Object entity = event.getEntity();

        if (entity instanceof Task) {
            Task task = (Task) entity;
            if (RequestType.ACCOUNT_OPENING_REQUEST.equals(task.getType())) {
                event.getSession().createNativeQuery(
                            "DELETE FROM task_search_metadata " +
                            "WHERE task_id = :task_id")
                        .setParameter("task_id", task.getId())
                        .setFlushMode(FlushMode.MANUAL)
                        .executeUpdate();
            }
        }

        return false;
    }
}
