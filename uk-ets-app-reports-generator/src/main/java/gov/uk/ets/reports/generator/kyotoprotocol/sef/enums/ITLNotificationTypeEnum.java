package gov.uk.ets.reports.generator.kyotoprotocol.sef.enums;

public enum ITLNotificationTypeEnum {

    NET_SOURCE_CANCEL(1), NON_COMPLIANCE_CANCELLATION(2), IMPENDING_TCER_LCER_EXPIRY(3), REVERSAL_STORAGE_CDM(4), NON_SUBM_CERT_REPORT_CDM(
            5), EXCESS_ISSUANCE_CDM(6), COMMITMENT_PERIOD_RESERVE(7), UNIT_CARRY_OVER(8), EXPIRY_DATE_RENEWAL(9), NOTIFICATION_UPDATE(
            10), OUTSTANDING_UNITS(51), EU15_COMMITMENT_PERIOD_RESERVE(11), NET_REVERSAL_STORAGE_CDM_CCS_PROJECT(12), NON_SUBM_CERT_REPORT_CDM_CCS_PROJECT(
            13), NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT(13);

    private int code;

    /**
     * @param code
     */
    private ITLNotificationTypeEnum(int code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    public static ITLNotificationTypeEnum getFromCode(String name) {
        for (ITLNotificationTypeEnum value : ITLNotificationTypeEnum.values()) {
            if (value.name().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
