package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for StartingValueEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="StartingValueEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IssuancePursuantToArt37-38"/>
 *     &lt;enumeration value="AssignedAmountUnitsIssued"/>
 *     &lt;enumeration value="Art3Par7TerCancellations"/>
 *     &lt;enumeration value="CancellationFollowingIncreaseAmbition"/>
 *     &lt;enumeration value="CancellationRemainingUnitsCarryOver"/>
 *     &lt;enumeration value="Non-compliance cancellation"/>
 *     &lt;enumeration value="CarryOver"/>
 *     &lt;enumeration value="CarryOverPPSR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "StartingValueEnum")
@XmlEnum
public enum StartingValueEnum {

    @XmlEnumValue("IssuancePursuantToArt37-38")
    ISSUANCE_PURSUANT_TO_ART_37_38("IssuancePursuantToArt37-38"),
    @XmlEnumValue("AssignedAmountUnitsIssued")
    ASSIGNED_AMOUNT_UNITS_ISSUED("AssignedAmountUnitsIssued"),
    @XmlEnumValue("Art3Par7TerCancellations")
    ART_3_PAR_7_TER_CANCELLATIONS("Art3Par7TerCancellations"),
    @XmlEnumValue("CancellationFollowingIncreaseAmbition")
    CANCELLATION_FOLLOWING_INCREASE_AMBITION("CancellationFollowingIncreaseAmbition"),
    @XmlEnumValue("CancellationRemainingUnitsCarryOver")
    CANCELLATION_REMAINING_UNITS_CARRY_OVER("CancellationRemainingUnitsCarryOver"),
    @XmlEnumValue("Non-compliance cancellation")
    NON_COMPLIANCE_CANCELLATION("Non-compliance cancellation"),
    @XmlEnumValue("CarryOver")
    CARRY_OVER("CarryOver"),
    @XmlEnumValue("CarryOverPPSR")
    CARRY_OVER_PPSR("CarryOverPPSR");
    private final String value;

    StartingValueEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static StartingValueEnum fromValue(String v) {
        for (StartingValueEnum c: StartingValueEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
