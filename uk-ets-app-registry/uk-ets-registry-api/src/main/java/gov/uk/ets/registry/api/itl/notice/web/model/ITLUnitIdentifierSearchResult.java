package gov.uk.ets.registry.api.itl.notice.web.model;

import gov.uk.ets.registry.api.common.search.SearchResult;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ITLUnitIdentifierSearchResult implements SearchResult {

    private static final long serialVersionUID = 1L;

    private long id;
    private long unitSerialBlockStart;
    private long unitSerialBlockEnd;
    private String originatingRegistryCode;

}
