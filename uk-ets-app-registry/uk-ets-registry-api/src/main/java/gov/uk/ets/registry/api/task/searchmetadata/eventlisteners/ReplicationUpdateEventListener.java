package gov.uk.ets.registry.api.task.searchmetadata.eventlisteners;

import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReplicationUpdateEventListener implements PostUpdateEventListener {
    
    private final ReplicationEventService replicationEventService;

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        replicationEventService.updateSearchMetadata(event);
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }
}
