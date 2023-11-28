package gov.uk.ets.registry.api.transaction.domain.data;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a summary of an issuance transaction block.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"year"})
public class IssuanceBlockSummary extends TransactionBlockSummary {

    /**
     * The year.
     */
    private Integer year;

    /**
     * The cap.
     */
    private Long cap;

    /**
     * The issued quantity.
     */
    private Long issued;

    /**
     * The remaining quantity.
     */
    private Long remaining;

}

