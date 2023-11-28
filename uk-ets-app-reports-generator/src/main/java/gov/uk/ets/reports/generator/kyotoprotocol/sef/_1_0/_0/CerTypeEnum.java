package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for CerTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="CerTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="lCER"/>
 *     &lt;enumeration value="tCER"/>
 *     &lt;enumeration value="CER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "CerTypeEnum")
@XmlEnum
public enum CerTypeEnum {

    @XmlEnumValue("lCER")
    L_CER("lCER"),
    @XmlEnumValue("tCER")
    T_CER("tCER"),
    CER("CER");
    private final String value;

    CerTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static CerTypeEnum fromValue(String v) {
        for (CerTypeEnum c: CerTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
