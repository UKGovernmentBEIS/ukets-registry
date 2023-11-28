package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLUnitTypeEnum;

/**
 * Extends NotificationEntry to support cp2 report format
 *
 * @author kattoulp
 *
 */
public class Cp2NotificationEntry extends NotificationEntry {

    private ITLUnitTypeEnum unitTypeCode;

    /**
     * @return the unitTypeCode
     */
    public ITLUnitTypeEnum getUnitTypeCode() {
        return unitTypeCode;
    }

    /**
     * @param unitTypeCode the unitTypeCode to set
     */
    public void setUnitTypeCode(ITLUnitTypeEnum unitTypeCode) {
        this.unitTypeCode = unitTypeCode;
    }

}
