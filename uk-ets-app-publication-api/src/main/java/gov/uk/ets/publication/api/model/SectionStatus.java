package gov.uk.ets.publication.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SectionStatus {
    PUBLISHING,
    PUBLISHED;

}
