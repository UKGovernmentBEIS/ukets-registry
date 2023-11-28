package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for AdditionalInfoEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AdditionalInfoEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FirstIndependentlyVerifiedERUs"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "AdditionalInfoEnum")
@XmlEnum
public enum AdditionalInfoEnum {

    @XmlEnumValue("FirstIndependentlyVerifiedERUs")
    FIRST_INDEPENDENTLY_VERIFIED_ER_US("FirstIndependentlyVerifiedERUs");
    private final String value;

    AdditionalInfoEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AdditionalInfoEnum fromValue(String v) {
        for (AdditionalInfoEnum c: AdditionalInfoEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
