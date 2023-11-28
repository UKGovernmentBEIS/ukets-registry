package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.UnitTypeEnum;

import java.util.List;

/**
 *
 * @author gkountak
 */
public class UnitQtyUtil {

    public static UnitQty getUnitQtyByUnitType(List<UnitQty> unitQties, UnitTypeEnum unitType) {
        for (UnitQty q : unitQties) {
            if (unitType == q.getType()) {
                return q;
            }
        }
        return null;
    }
}
