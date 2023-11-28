package gov.uk.ets.publication.api.sort;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;

@Getter
@Setter
public class SortParameters implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * The field to sort on.
     */
    private String sortField;

    /**
     * The sort direction (e.g. ASC).
     */
    private Sort.Direction sortDirection;
}
