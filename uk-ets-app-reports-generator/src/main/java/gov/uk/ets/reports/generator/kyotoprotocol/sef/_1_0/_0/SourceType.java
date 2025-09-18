package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for SourceType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SourceType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ITL"/>
 *     &lt;enumeration value="REG"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "SourceType")
@XmlEnum
public enum SourceType {

    ITL,
    REG;

    public String value() {
        return name();
    }

    public static SourceType fromValue(String v) {
        return valueOf(v);
    }

}
