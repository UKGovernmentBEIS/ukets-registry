package gov.uk.ets.compliance.domain.events;

public interface CanBeMarkedAsProcessed {

    void markAsProcessed();

    boolean isMarked();
}
