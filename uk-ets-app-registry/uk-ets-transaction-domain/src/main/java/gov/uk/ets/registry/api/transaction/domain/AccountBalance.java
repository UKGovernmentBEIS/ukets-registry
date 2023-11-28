package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"identifier","balance","unitType"})
public class AccountBalance {

    /**
     * Account: The unique account business identifier, e.g. 10455.
     */
    private Long identifier;
    
    /**
     * The account balance.
     * The total quantity an account holds, regardless the unit type.
     */
    private Long balance;

    /**
     * The account balance unit type.
     * An indication of what unit types an account holds.
     * For example:
     * <ul>
     * <li>MULTIPLE means that the account holds units of multiple types,</li>
     * <li>AAU means that the account holds only AAUs etc.</li>
     * </ul>
     */
    private UnitType unitType;
}
