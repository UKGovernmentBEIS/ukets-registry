package gov.uk.ets.registry.api.common.search;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

import java.io.Serializable;

/**
 * The page parameters.
 */
@Getter
@Setter
public class PageParameters implements Serializable {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 9087528676355176474L;

    /**
     * The page number (zero based).
     */
    private Integer page = 0;

    /**
     * The page size.
     */
    private Long pageSize;

    /**
     * The total results.
     */
    private Long totalResults;

    /**
     * The field to sort on.
     */
    private String sortField;

    /**
     * The sort direction (e.g. ASC).
     */
    private Sort.Direction sortDirection;

}
