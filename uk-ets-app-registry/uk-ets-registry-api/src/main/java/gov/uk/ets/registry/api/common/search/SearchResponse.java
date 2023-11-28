package gov.uk.ets.registry.api.common.search;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Encapsulates a search response.
 * @param <T> The search result type
 */
@Getter
@Setter
public class SearchResponse<T extends SearchResult> {

    /**
     * The page parameters.
     */
    @JsonUnwrapped
    private PageParameters pageParameters;

    /**
     * The items returned by the search.
     */
    private List<T> items;

}
