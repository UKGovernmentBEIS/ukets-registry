package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for TransactionTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IssuanceConversionOfPartyVerifiedProjects"/>
 *     &lt;enumeration value="IndependentlyVerifiedProjects"/>
 *     &lt;enumeration value="Art33AfforestationReforestation"/>
 *     &lt;enumeration value="Art33Deforestation"/>
 *     &lt;enumeration value="Art34CropLandManagement"/>
 *     &lt;enumeration value="Art34ForestManagement"/>
 *     &lt;enumeration value="Art34GrazingLandManagement"/>
 *     &lt;enumeration value="Art34Revegetation"/>
 *     &lt;enumeration value="Art34WetLandDrainageRewetting"/>
 *     &lt;enumeration value="ReplacementExpiredlCERs"/>
 *     &lt;enumeration value="ReplacementExpiredtCERs"/>
 *     &lt;enumeration value="ReplacementForReversalOfStorage"/>
 *     &lt;enumeration value="CancellationForReversalOfStorage"/>
 *     &lt;enumeration value="ReplacementForNonSubmissionOfCertReport"/>
 *     &lt;enumeration value="ReplacementForReversalOfStorage"/>
 *     &lt;enumeration value="OtherCancellation"/>
 *     &lt;enumeration value="CancellationForNonSubmissionOfCertReport"/>
 *     &lt;enumeration value="VoluntaryCancellation"/>
 *     &lt;enumeration value="Art31TerQuaterAmbitionIncreaseCancellation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "TransactionTypeEnum")
@XmlEnum
public enum TransactionTypeEnum {

    @XmlEnumValue("IssuanceConversionOfPartyVerifiedProjects")
    ISSUANCE_CONVERSION_OF_PARTY_VERIFIED_PROJECTS("IssuanceConversionOfPartyVerifiedProjects"),
    @XmlEnumValue("IndependentlyVerifiedProjects")
    INDEPENDENTLY_VERIFIED_PROJECTS("IndependentlyVerifiedProjects"),
    @XmlEnumValue("Art33AfforestationReforestation")
    ART_33_AFFORESTATION_REFORESTATION("Art33AfforestationReforestation"),
    @XmlEnumValue("Art33Deforestation")
    ART_33_DEFORESTATION("Art33Deforestation"),
    @XmlEnumValue("Art34CropLandManagement")
    ART_34_CROP_LAND_MANAGEMENT("Art34CropLandManagement"),
    @XmlEnumValue("Art34ForestManagement")
    ART_34_FOREST_MANAGEMENT("Art34ForestManagement"),
    @XmlEnumValue("Art34GrazingLandManagement")
    ART_34_GRAZING_LAND_MANAGEMENT("Art34GrazingLandManagement"),
    @XmlEnumValue("Art34Revegetation")
    ART_34_REVEGETATION("Art34Revegetation"),
    @XmlEnumValue("Art34WetLandDrainageRewetting")
    ART_34_WET_LAND_DRAINAGE_REWETTING("Art34WetLandDrainageRewetting"),
    @XmlEnumValue("ReplacementExpiredlCERs")
    REPLACEMENT_EXPIREDL_CE_RS("ReplacementExpiredlCERs"),
    @XmlEnumValue("ReplacementExpiredtCERs")
    REPLACEMENT_EXPIREDT_CE_RS("ReplacementExpiredtCERs"),
    @XmlEnumValue("ReplacementForReversalOfStorage")
    REPLACEMENT_FOR_REVERSAL_OF_STORAGE("ReplacementForReversalOfStorage"),
    @XmlEnumValue("CancellationForReversalOfStorage")
    CANCELLATION_FOR_REVERSAL_OF_STORAGE("CancellationForReversalOfStorage"),
    @XmlEnumValue("ReplacementForNonSubmissionOfCertReport")
    REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT("ReplacementForNonSubmissionOfCertReport"),
    @XmlEnumValue("OtherCancellation")
    OTHER_CANCELLATION("OtherCancellation"),
    @XmlEnumValue("CancellationForNonSubmissionOfCertReport")
    CANCELLATION_FOR_NON_SUBMISSION_OF_CERT_REPORT("CancellationForNonSubmissionOfCertReport"),
    @XmlEnumValue("VoluntaryCancellation")
    VOLUNTARY_CANCELLATION("VoluntaryCancellation"),
    @XmlEnumValue("Art31TerQuaterAmbitionIncreaseCancellation")
    ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION("Art31TerQuaterAmbitionIncreaseCancellation");
    private final String value;

    TransactionTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionTypeEnum fromValue(String v) {
        for (TransactionTypeEnum c: TransactionTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
