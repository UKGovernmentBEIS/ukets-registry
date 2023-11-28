package gov.uk.ets.registry.api.reconciliation.transfer;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.io.Serializable;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a reconciliation entry transfer object.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = {"accountIdentifier", "unitType"})
public class ReconciliationEntrySummary implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = 8453484153620294725L;

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

    @QueryProjection
    public ReconciliationEntrySummary(Long accountIdentifier,
        UnitType unitType, Long total) {
        this.accountIdentifier = accountIdentifier;
        this.unitType = unitType;
        this.total = total;
    }
}
