package uk.gov.ets.transaction.log.messaging.types;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.type.UnitType;

/**
 * Represents a reconciliation entry transfer object.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"accountIdentifier", "unitType"})
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationEntrySummary {

    /**
     * The account unique business identifier.
     */
    private Long accountIdentifier;

    /**
     * The unit type.
     */
    private UnitType unitType;

    /**
     * The total units held by the account.
     */
    private Long total;
}
