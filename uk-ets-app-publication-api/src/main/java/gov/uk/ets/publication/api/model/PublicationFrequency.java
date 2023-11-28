package gov.uk.ets.publication.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PublicationFrequency {
    DAILY,
    YEARLY,
    EVERY_X_DAYS,
    DISABLED;
}
