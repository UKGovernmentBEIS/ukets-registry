package gov.uk.ets.reports.generator.kyotoprotocol.nonsef.r3itl;

import lombok.Getter;
import lombok.Setter;

/**
 * The object that is received from the the query DETERMINE_THE_RELATED_TRANSFERRED_QUANTITIES
 *
 */
@Getter
@Setter
public class RREG3TransactionTypeItem {

    /**
     * The transaction type code
     */
    private String transactionTypeCode;

    /**
     * The qty
     */
    private Integer qty;

}
