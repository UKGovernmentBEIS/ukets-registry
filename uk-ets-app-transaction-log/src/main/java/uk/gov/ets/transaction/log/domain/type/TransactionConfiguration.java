package uk.gov.ets.transaction.log.domain.type;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

/**
 * Encapsulates the transaction configuration.
 */
@Builder
@Getter
public class TransactionConfiguration implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -792559698778017350L;

    /**
     * The primary code.
     */
    private final Integer primaryCode;

    /**
     * The supplementary code.
     */
    private final Integer supplementaryCode;

    /**
     * A description of the transaction type.
     */
    private String description;

}

