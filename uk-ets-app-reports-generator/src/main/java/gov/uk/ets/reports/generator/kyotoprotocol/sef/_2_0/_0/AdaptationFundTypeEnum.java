package gov.uk.ets.reports.generator.kyotoprotocol.sef._2_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for AdaptationFundTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AdaptationFundTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="FirstInternationalTransfersOfAAU"/>
 *     &lt;enumeration value="IssuanceERUFromPartyVerifiedProjects"/>
 *     &lt;enumeration value="IssuanceIndependentlyVerifiedERU"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "AdaptationFundTypeEnum")
@XmlEnum
public enum AdaptationFundTypeEnum {

    @XmlEnumValue("FirstInternationalTransfersOfAAU")
    FIRST_INTERNATIONAL_TRANSFERS_OF_AAU("FirstInternationalTransfersOfAAU"),
    @XmlEnumValue("IssuanceERUFromPartyVerifiedProjects")
    ISSUANCE_ERU_FROM_PARTY_VERIFIED_PROJECTS("IssuanceERUFromPartyVerifiedProjects"),
    @XmlEnumValue("IssuanceIndependentlyVerifiedERU")
    ISSUANCE_INDEPENDENTLY_VERIFIED_ERU("IssuanceIndependentlyVerifiedERU");
    private final String value;

    AdaptationFundTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AdaptationFundTypeEnum fromValue(String v) {
        for (AdaptationFundTypeEnum c: AdaptationFundTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
