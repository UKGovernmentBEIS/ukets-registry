package gov.uk.ets.reports.generator.kyotoprotocol.sef._1_0._0;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>Java class for TransactionOrEventTypeEnum.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="TransactionOrEventTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="tCERsExpiredInRetirementAndReplacementAccounts"/>
 *     &lt;enumeration value="tCERsExpiredInHoldingAccounts"/>
 *     &lt;enumeration value="lCERsExpiredInRetirementAndReplacementAccounts"/>
 *     &lt;enumeration value="lCERsExpiredInHoldingAccounts"/>
 *     &lt;enumeration value="SubjectToReplacementForReversalOfStorage"/>
 *     &lt;enumeration value="SubjectToReplacementForNonSubmissionOfCertReport"/>
 *     &lt;enumeration value="SubjectToReversalOfStorage"/>
 *     &lt;enumeration value="SubjectToNonSubmissionOfCertReport"/>
 *     &lt;enumeration value="CCSubjectToNetReversalOfStorage"/>
 *     &lt;enumeration value="CCSubjectToNonSubmissionOfCertReport"/>
 *     &lt;enumeration value="ReplacementOfExpiredtCERs"/>
 *     &lt;enumeration value="CancellationOftCERsExpiredInHoldingAccounts"/>
 *     &lt;enumeration value="ReplacementOfExpiredlCERs"/>
 *     &lt;enumeration value="CancellationOflCERsExpiredInHoldingAccounts"/>
 *     &lt;enumeration value="ReplacementForReversalOfStorage"/>
 *     &lt;enumeration value="ReplacementForNonSubmissionOfCertReport"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 *
 */
@XmlType(name = "TransactionOrEventTypeEnum")
@XmlEnum
public enum TransactionOrEventTypeEnum {

    @XmlEnumValue("tCERsExpiredInRetirementAndReplacementAccounts")
    T_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS("tCERsExpiredInRetirementAndReplacementAccounts"),
    @XmlEnumValue("tCERsExpiredInHoldingAccounts")
    T_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS("tCERsExpiredInHoldingAccounts"),
    @XmlEnumValue("lCERsExpiredInRetirementAndReplacementAccounts")
    L_CE_RS_EXPIRED_IN_RETIREMENT_AND_REPLACEMENT_ACCOUNTS("lCERsExpiredInRetirementAndReplacementAccounts"),
    @XmlEnumValue("lCERsExpiredInHoldingAccounts")
    L_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS("lCERsExpiredInHoldingAccounts"),
    @XmlEnumValue("SubjectToReplacementForReversalOfStorage")
    SUBJECT_TO_REPLACEMENT_FOR_REVERSAL_OF_STORAGE("SubjectToReplacementForReversalOfStorage"),
    @XmlEnumValue("SubjectToReplacementForNonSubmissionOfCertReport")
    SUBJECT_TO_REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT("SubjectToReplacementForNonSubmissionOfCertReport"),
    @XmlEnumValue("SubjectToReversalOfStorage")
    SUBJECT_TO_REVERSAL_OF_STORAGE("SubjectToReversalOfStorage"),
    @XmlEnumValue("SubjectToNonSubmissionOfCertReport")
    SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT("SubjectToNonSubmissionOfCertReport"),
    @XmlEnumValue("CCSubjectToNetReversalOfStorage")
    CC_SUBJECT_TO_NET_REVERSAL_OF_STORAGE("CCSubjectToNetReversalOfStorage"),
    @XmlEnumValue("CCSubjectToNonSubmissionOfCertReport")
    CC_SUBJECT_TO_NON_SUBMISSION_OF_CERT_REPORT("CCSubjectToNonSubmissionOfCertReport"),
    @XmlEnumValue("ReplacementOfExpiredtCERs")
    REPLACEMENT_OF_EXPIREDT_CE_RS("ReplacementOfExpiredtCERs"),
    @XmlEnumValue("CancellationOftCERsExpiredInHoldingAccounts")
    CANCELLATION_OFT_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS("CancellationOftCERsExpiredInHoldingAccounts"),
    @XmlEnumValue("ReplacementOfExpiredlCERs")
    REPLACEMENT_OF_EXPIREDL_CE_RS("ReplacementOfExpiredlCERs"),
    @XmlEnumValue("CancellationOflCERsExpiredInHoldingAccounts")
    CANCELLATION_OFL_CE_RS_EXPIRED_IN_HOLDING_ACCOUNTS("CancellationOflCERsExpiredInHoldingAccounts"),
    @XmlEnumValue("ReplacementForReversalOfStorage")
    REPLACEMENT_FOR_REVERSAL_OF_STORAGE("ReplacementForReversalOfStorage"),
    @XmlEnumValue("ReplacementForNonSubmissionOfCertReport")
    REPLACEMENT_FOR_NON_SUBMISSION_OF_CERT_REPORT("ReplacementForNonSubmissionOfCertReport");
    private final String value;

    TransactionOrEventTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TransactionOrEventTypeEnum fromValue(String v) {
        for (TransactionOrEventTypeEnum c: TransactionOrEventTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
