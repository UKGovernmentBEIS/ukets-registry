package gov.uk.ets.reports.generator.kyotoprotocol.commons.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerQty;
import gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0.CerTypeEnum;

import java.util.List;

/**
 *
 * @author gkountak
 */
public class CerQtyUtil {

    /**
     * Retrieves the {@link CerQty} per type.
     * @param unitQties
     * @param unitType
     * @return
     */
    public static CerQty getCerQtyByCerType(List<CerQty> unitQties, CerTypeEnum unitType) {
        for (CerQty q : unitQties) {
            if (unitType == q.getType()) {
                return q;
            }
        }
        return null;
    }
}
