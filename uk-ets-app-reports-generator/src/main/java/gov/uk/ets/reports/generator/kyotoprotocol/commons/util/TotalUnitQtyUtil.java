package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.TotalUnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;

public class TotalUnitQtyUtil {

    /**
     * Will search and return the unit quantity
     * @param unitType
     * @return
     */
    public static UnitQty getUnitQtyByUnitType(TotalUnitQty totalUnitQty, UnitTypeEnum unitType) {
        for (UnitQty q : totalUnitQty.getUnitQty()) {
            if (unitType == q.getType()) {
                return q;
            }
        }
        return null;
    }
}
