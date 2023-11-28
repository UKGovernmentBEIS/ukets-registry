package gov.uk.ets.registry.api.transaction.domain.data;

import java.io.Serializable;
import java.util.Date;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = {"errorCode", "dateOccurred", "transactionBlockId"})
@Builder
public class TransactionResponseSummary implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -2645745308090025805L;

    private Long errorCode;

    private String details;

    private Date dateOccurred;

    private Long transactionBlockId;

}