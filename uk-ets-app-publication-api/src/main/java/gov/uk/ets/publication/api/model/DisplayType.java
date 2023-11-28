package gov.uk.ets.publication.api.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DisplayType {
    ONE_FILE,
    ONE_FILE_PER_YEAR,
    MANY_FILES;
}
