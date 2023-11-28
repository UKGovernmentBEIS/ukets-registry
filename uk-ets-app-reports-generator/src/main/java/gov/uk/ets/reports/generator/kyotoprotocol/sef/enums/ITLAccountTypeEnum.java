package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

/**
 * @author gkountak
 *
 */
public enum ITLAccountTypeEnum {

    PARTY_HOLDING_ACCOUNT(100, "PartyHolding"), PENDING_ACCOUNT(110, "Pending"), FORMER_OPERATOR_HOLDING_ACCOUNT(120,"EntityHolding"),
             PERSON_HOLDING_ACCOUNT(121, "EntityHolding"), PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT(
            130, "PreviousPeriodSurplusReserveAccount"), CCS_PROJECT_RESERVE_ACCOUNT(131, "CcsProjectReserveAccount"), NET_SOURCE_CANCELLATION_ACCOUNT(
            210, "NetSourceCancellation"), NON_COMPLIANCE_CANCELLATION_ACCOUNT(220, "NonComplianceCancellation"), VOLUNTARY_CANCELLATION_ACCOUNT(
            230, "OtherCancellationAccounts", "VoluntaryCancellationAccount"), EXCESS_ISSUANCE_ACCOUNT(240, "OtherCancellationAccounts"), CCS_NET_REVERSAL_CANCELLATION_ACCOUNT(
            241, "CcsNetReversalCancellationAccount"), CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT(
            242, "CcsNonSubmissionOfVerificationReportCancellationAccount"), MANDATORY_CANCELLATION_ACCOUNT(250,
            "OtherCancellationAccounts"), ARTICLE_3_7_TER_CANCELLATION_ACCOUNT(270, "Art37TerCancellationAccount"), AMBITION_INCREASE_CANCELLATION_ACCOUNT(
            280, "Art31TerQuaterAmbitionIncreaseCancellationAccount"), RETIREMENT_ACCOUNT(300, "RetirementAccount"), TCER_REPLACEMENT_ACCOUNT_EXPIRY(
            411, "tCERReplacementForExpiry"), LCER_REPLACEMENT_ACCOUNT_EXPIRY(421, "lCERReplacementForExpiry"), LCER_REPLACEMENT_ACCOUNT_REVERSAL_OF_STORAGE(
            422, "lCERReplacementForReversalOfStorage"), LCER_REPLACEMENT_ACCOUNT_FAIL_SUBMISSION_CERT_REP(423,
            "lCERReplacementForNonSubmissionCertReport");
    // ARTICLE_31_AMBITION_INCREASE_CANCELLATION_ACCOUNT(424,
    // "article31AmbitionIncreaseCancellationAccount");

    private int code;
    private String description;
    private String cp2Description;

    /**
     * @param code
     * @param description
     */
    private ITLAccountTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
        this.cp2Description = description;
    }

    /**
     * @param code
     * @param description
     */
    private ITLAccountTypeEnum(int code, String description, String cp2Description) {
        this.code = code;
        this.description = description;
        this.cp2Description = cp2Description;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the cp2 format description
     */
    public String getCp2Description() {
        return cp2Description;
    }

    public static ITLAccountTypeEnum getFromCode(int code) {
        for (ITLAccountTypeEnum value : ITLAccountTypeEnum.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    public static ITLAccountTypeEnum getFromName(String name) {
        for (ITLAccountTypeEnum value : ITLAccountTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
