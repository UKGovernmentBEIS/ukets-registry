package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for AccountTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="AccountTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="PartyHolding"/>
 *     &lt;enumeration value="EntityHolding"/>
 *     &lt;enumeration value="RetirementAccount"/>
 *     &lt;enumeration value="PreviousPeriodSurplusReserveAccount"/>
 *     &lt;enumeration value="NetSourceCancellation"/>
 *     &lt;enumeration value="NonComplianceCancellation"/>
 *     &lt;enumeration value="VoluntaryCancellationAccount"/>
 *     &lt;enumeration value="CancellationAccountForRemainingUnitsAfterCarryover"/>
 *     &lt;enumeration value="Art31TerQuaterAmbitionIncreaseCancellationAccount"/>
 *     &lt;enumeration value="Art37TerCancellationAccount"/>
 *     &lt;enumeration value="tCERCancellationAccountForExpiry"/>
 *     &lt;enumeration value="lCERCancellationAccountForExpiry"/>
 *     &lt;enumeration value="lCERCancellationAccountForReversalOfStorage"/>
 *     &lt;enumeration value="lCERCancellationAccountForNonSubmissionCertReport"/>
 *     &lt;enumeration value="tCERReplacementForExpiry"/>
 *     &lt;enumeration value="lCERReplacementForExpiry"/>
 *     &lt;enumeration value="lCERReplacementForReversalOfStorage"/>
 *     &lt;enumeration value="lCERReplacementForNonSubmissionCertReport"/>
 *     &lt;enumeration value="OtherCancellationAccounts"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "AccountTypeEnum")
@XmlEnum
public enum AccountTypeEnum {

    @XmlEnumValue("PartyHolding")
    PARTY_HOLDING("PartyHolding"),
    @XmlEnumValue("EntityHolding")
    ENTITY_HOLDING("EntityHolding"),
    @XmlEnumValue("RetirementAccount")
    RETIREMENT_ACCOUNT("RetirementAccount"),
    @XmlEnumValue("PreviousPeriodSurplusReserveAccount")
    PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT("PreviousPeriodSurplusReserveAccount"),
    @XmlEnumValue("NetSourceCancellation")
    NET_SOURCE_CANCELLATION("NetSourceCancellation"),
    @XmlEnumValue("NonComplianceCancellation")
    NON_COMPLIANCE_CANCELLATION("NonComplianceCancellation"),
    @XmlEnumValue("VoluntaryCancellationAccount")
    VOLUNTARY_CANCELLATION_ACCOUNT("VoluntaryCancellationAccount"),
    @XmlEnumValue("CancellationAccountForRemainingUnitsAfterCarryover")
    CANCELLATION_ACCOUNT_FOR_REMAINING_UNITS_AFTER_CARRYOVER("CancellationAccountForRemainingUnitsAfterCarryover"),
    @XmlEnumValue("Art31TerQuaterAmbitionIncreaseCancellationAccount")
    ART_31_TER_QUATER_AMBITION_INCREASE_CANCELLATION_ACCOUNT("Art31TerQuaterAmbitionIncreaseCancellationAccount"),
    @XmlEnumValue("Art37TerCancellationAccount")
    ART_37_TER_CANCELLATION_ACCOUNT("Art37TerCancellationAccount"),
    @XmlEnumValue("tCERCancellationAccountForExpiry")
    T_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY("tCERCancellationAccountForExpiry"),
    @XmlEnumValue("lCERCancellationAccountForExpiry")
    L_CER_CANCELLATION_ACCOUNT_FOR_EXPIRY("lCERCancellationAccountForExpiry"),
    @XmlEnumValue("lCERCancellationAccountForReversalOfStorage")
    L_CER_CANCELLATION_ACCOUNT_FOR_REVERSAL_OF_STORAGE("lCERCancellationAccountForReversalOfStorage"),
    @XmlEnumValue("lCERCancellationAccountForNonSubmissionCertReport")
    L_CER_CANCELLATION_ACCOUNT_FOR_NON_SUBMISSION_CERT_REPORT("lCERCancellationAccountForNonSubmissionCertReport"),
    @XmlEnumValue("tCERReplacementForExpiry")
    T_CER_REPLACEMENT_FOR_EXPIRY("tCERReplacementForExpiry"),
    @XmlEnumValue("lCERReplacementForExpiry")
    L_CER_REPLACEMENT_FOR_EXPIRY("lCERReplacementForExpiry"),
    @XmlEnumValue("lCERReplacementForReversalOfStorage")
    L_CER_REPLACEMENT_FOR_REVERSAL_OF_STORAGE("lCERReplacementForReversalOfStorage"),
    @XmlEnumValue("lCERReplacementForNonSubmissionCertReport")
    L_CER_REPLACEMENT_FOR_NON_SUBMISSION_CERT_REPORT("lCERReplacementForNonSubmissionCertReport"),
    @XmlEnumValue("OtherCancellationAccounts")
    OTHER_CANCELLATION_ACCOUNTS("OtherCancellationAccounts");
    private final String value;

    AccountTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AccountTypeEnum fromValue(String v) {
        for (AccountTypeEnum c: AccountTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
