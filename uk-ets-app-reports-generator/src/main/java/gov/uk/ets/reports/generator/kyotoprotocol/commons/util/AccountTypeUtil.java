package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.AccountType;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;

/**
 * Utility class for retrieving unit quantity by unit type
 */
public final class AccountTypeUtil {

    private AccountTypeUtil(){}

    /**
     * Will search and return the unit quantity
     * @param unitType
     * @return
     */
    public static UnitQty getUnitQtyByUnitType(AccountType accountType, UnitTypeEnum unitType) {
        for (UnitQty q : accountType.getUnitQty()) {
            if (unitType == q.getType()) {
                return q;
            }
        }
        return null;
    }

}
