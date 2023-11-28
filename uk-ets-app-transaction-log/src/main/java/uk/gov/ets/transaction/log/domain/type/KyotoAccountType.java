package uk.gov.ets.transaction.log.domain.type;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Enumerates the various kyoto account types.
 */
public enum KyotoAccountType {

    /**
     * The party holding account.
     */
    PARTY_HOLDING_ACCOUNT(100),

    /**
     * The former operator holding account.
     */
    FORMER_OPERATOR_HOLDING_ACCOUNT(120, false),

    /**
     * The person holding account.
     */
    PERSON_HOLDING_ACCOUNT(121, false),

    /**
     * The previous period surplus reserve account.
     */
    PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT(130),

    /**
     * The net source cancellation account.
     */
    NET_SOURCE_CANCELLATION_ACCOUNT(210),

    /**
     * The non compliance cancellation account.
     */
    NON_COMPLIANCE_CANCELLATION_ACCOUNT(220),

    /**
     * The voluntary cancellation account.
     */
    VOLUNTARY_CANCELLATION_ACCOUNT(230),

    /**
     * The mandatory cancellation account.
     */
    MANDATORY_CANCELLATION_ACCOUNT(250),

    /**
     * The Art37Ter cancellation account.
     */
    ARTICLE_3_7_TER_CANCELLATION_ACCOUNT(270),

    /**
     * The ambition increase cancellation account.
     */
    AMBITION_INCREASE_CANCELLATION_ACCOUNT(280),

    /**
     * The retirement account.
     */
    RETIREMENT_ACCOUNT(300),

    /**
     * The TCER replacement account for expiry.
     */
    TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY(411),

    /**
     * The LCER replacement account for expiry.
     */
    LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY(421),

    /**
     * The LCER replacement account for reversal of storage.
     */
    LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE(422),

    /**
     * The LCER replacement account for non submission of certification report.
     */
    LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT(423);

    /**
     * The code.
     */
    private Integer code;

    /**
     * Whether this type refers to government accounts.
     */
    private Boolean government = true;

    /**
     * Constructor.
     * @param code The Kyoto code.
     */
    KyotoAccountType(Integer code) {
        this.code = code;
    }

    /**
     * Constructos.
     * @param code The Kyoto code.
     * @param government Whether this type refers to a government account.
     */
    KyotoAccountType(Integer code, Boolean government) {
        this.code = code;
        this.government = government;
    }

    public Boolean isGovernment() {
        return government;
    }

    /**
     * Parses the input string to an enumeration value.
     * @param input The input string
     * @return an enumeration value or null if the input does not correspond to a value
     */
    public static KyotoAccountType parse(String input) {
        KyotoAccountType result;
        try {
            result = KyotoAccountType.valueOf(input);
        } catch (Exception exc) {
            // nothing to log here
            result = null;
        }
        return result;
    }

    /**
     * Parses the input code to an enumeration value.
     * @param code The code.
     * @return an enumeration value or null if the input does not correspond to a value
     */
    public static KyotoAccountType parse(Integer code) {
        KyotoAccountType result = null;
        Optional<KyotoAccountType> optional = Stream.of(KyotoAccountType.values())
                .filter(kyotoAccountType -> kyotoAccountType.code.equals(code))
                .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }

    /**
     * Returns the Kyoto code.
     * @return the Kyoto code.
     */
    public Integer getCode() {
        return code;
    }
}
