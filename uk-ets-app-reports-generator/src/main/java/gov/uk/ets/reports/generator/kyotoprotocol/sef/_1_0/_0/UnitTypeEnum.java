package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for UnitTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UnitTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="AAU"/>
 *     &lt;enumeration value="CER"/>
 *     &lt;enumeration value="ERU"/>
 *     &lt;enumeration value="RMU"/>
 *     &lt;enumeration value="lCER"/>
 *     &lt;enumeration value="tCER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "UnitTypeEnum")
@XmlEnum
public enum UnitTypeEnum {

    AAU("AAU"),
    CER("CER"),
    ERU("ERU"),
    RMU("RMU"),
    @XmlEnumValue("lCER")
    L_CER("lCER"),
    @XmlEnumValue("tCER")
    T_CER("tCER");
    private final String value;

    UnitTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UnitTypeEnum fromValue(String v) {
        for (UnitTypeEnum c: UnitTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}