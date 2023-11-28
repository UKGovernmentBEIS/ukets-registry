package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for RetirementTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RetirementTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Retirement"/>
 *     &lt;enumeration value="RetirementFromPPSR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "RetirementTypeEnum")
@XmlEnum
public enum RetirementTypeEnum {

    @XmlEnumValue("Retirement")
    RETIREMENT("Retirement"),
    @XmlEnumValue("RetirementFromPPSR")
    RETIREMENT_FROM_PPSR("RetirementFromPPSR");
    private final String value;

    RetirementTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RetirementTypeEnum fromValue(String v) {
        for (RetirementTypeEnum c: RetirementTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
