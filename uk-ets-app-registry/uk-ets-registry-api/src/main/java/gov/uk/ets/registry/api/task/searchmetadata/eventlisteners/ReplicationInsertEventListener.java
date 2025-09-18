package gov.uk.ets.registry.api.task.searchmetadata.eventlisteners;

import lombok.RequiredArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplicationInsertEventListener implements PostInsertEventListener {

    private final ReplicationEventService replicationEventService;

    @Override
    public void onPostInsert(PostInsertEvent event) throws HibernateException {
        replicationEventService.insertSearchMetadata(event);
    }

	@Override
	public boolean requiresPostCommitHandling(EntityPersister persister) {
		return false;
	}
}
