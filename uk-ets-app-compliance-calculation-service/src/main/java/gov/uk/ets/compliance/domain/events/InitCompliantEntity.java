package gov.uk.ets.compliance.domain.events;

import java.time.LocalDateTime;

public interface InitCompliantEntity {

    Long getCompliantEntityId();
    int getCurrentYear();
    int getFirstYearOfVerifiedEmissions();
    Integer getLastYearOfVerifiedEmissions();
    LocalDateTime getDateRequested();
}
