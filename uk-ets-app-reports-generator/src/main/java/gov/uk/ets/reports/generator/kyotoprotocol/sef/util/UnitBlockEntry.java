package gov.uk.ets.reports.generator.kyotoprotocol.sef.util;

import gov.uk.ets.reports.generator.kyotoprotocol.sef.enums.ITLUnitTypeEnum;

/**
 * Represents a unit block entry
 *
 * @author kattoulp
 *
 */
public class UnitBlockEntry {

    private ITLUnitTypeEnum unitTypeCode;

    private Long amount;

    private String registry;

    private Short expiredYear;

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

    /**
     * @return the amount
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getRegistry() {
        return registry;
    }

    public void setRegistry(String registryCode) {
        this.registry = registryCode;
    }

    /**
     * @return the expiredYear
     */
    public Short getExpiredYear() {
        return expiredYear;
    }

    /**
     * @param expiredYear the expiredYear to set
     */
    public void setExpiredYear(Short expiredYear) {
        this.expiredYear = expiredYear;
    }

}
