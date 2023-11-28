package gov.uk.ets.compliance.domain.events;

import java.util.Date;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
public abstract class AbstractEventThatCanBeMarked
    implements gov.uk.ets.compliance.domain.events.CanBeMarkedAsProcessed {

    private boolean markedAsProcessed = false;
    private Date consumedOn;

    @Override
    public void markAsProcessed() {
        markedAsProcessed = true;
    }

    @Override
    public boolean isMarked() {
        return markedAsProcessed;
    }

}
