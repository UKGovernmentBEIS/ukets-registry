package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r2itl;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 *
 * @author gkountak
 */
@Getter
@Setter
public class RREG2TransactionItem {

    /**
     * The identifier
     */
    private String transactionIdentifier;

    /**
     * The data of the transaction
     */
    private Date transactionDate;

    /**
     * The type of the transaction
     */
    private String transactionType;

    /**
     * The status of the transaction
     */
    private String transactionStatus;

    /**
     * The transaction block units
     */
    private List<UnitInvolved> unitsInvolved;

}
