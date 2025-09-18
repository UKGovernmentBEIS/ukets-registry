package gov.uk.ets.reports.generator.domain;

import java.util.Arrays;
import lombok.Getter;

/**
 * Account types and their labels.
 * Minimal copy of gov.uk.ets.registry.api.transaction.domain.type.AccountType
 */
@Getter
public enum AccountType {

    /**
     * The Operator holding account (ETS) type.
     */
    OPERATOR_HOLDING_ACCOUNT("ETS - Operator holding account"),

    /**
     * The Aircraft operator holding account (ETS) type.
     */
    AIRCRAFT_OPERATOR_HOLDING_ACCOUNT("ETS - Aircraft operator holding account"),

    /**
     * The Maritime operator holding account (ETS) type.
     */
    MARITIME_OPERATOR_HOLDING_ACCOUNT("ETS - Maritime operator holding account"),

    /**
     * The Trading account (ETS) type.
     */
    TRADING_ACCOUNT("ETS - Trading account"),

    /**
     * The UK Auction delivery account type.
     */
    UK_AUCTION_DELIVERY_ACCOUNT("ETS - UK Auction delivery account"),

    /**
     * The Person holding account (KP) type.
     */
    PERSON_HOLDING_ACCOUNT("KP - Person holding account"),

    /**
     * Party holding account (KP).
     */
    PARTY_HOLDING_ACCOUNT("KP - Party holding account"),

    /**
     * The Former operator holding account type.
     */
    FORMER_OPERATOR_HOLDING_ACCOUNT("KP - Former operator holding account"),

    /**
     * Previous Period Surplus Reserve account (PPSR account).
     */
    PREVIOUS_PERIOD_SURPLUS_RESERVE_ACCOUNT("KP - Previous Period Surplus Reserve account (PPSR)"),

    /**
     * Net source cancellation account.
     */
    NET_SOURCE_CANCELLATION_ACCOUNT("KP - Net source cancellation account"),

    /**
     * Non-compliance cancellation account.
     */
    NON_COMPLIANCE_CANCELLATION_ACCOUNT("KP - Non-compliance cancellation account"),

    /**
     * UK Surrender Account.
     */
    UK_SURRENDER_ACCOUNT("ETS - UK Surrender Account"),

    /**
     * UK Total Quantity Account.
     */
    UK_TOTAL_QUANTITY_ACCOUNT("ETS - UK Total Quantity Account"),

    /**
     * UK Aviation Total Quantity Account.
     */
    UK_AVIATION_TOTAL_QUANTITY_ACCOUNT("ETS - UK Aviation Total Quantity Account"),

    /**
     * UK Auction Account.
     */
    UK_AUCTION_ACCOUNT("ETS - UK Auction Account"),

    /**
     * UK Allocation Account.
     */
    UK_ALLOCATION_ACCOUNT("ETS - UK Allocation Account"),

    /**
     * UK New Entrants Reserve Account.
     */
    UK_NEW_ENTRANTS_RESERVE_ACCOUNT("ETS - UK New Entrants Reserve Account"),

    /**
     * UK Market Stability Mechanism Account.
     */
    UK_MARKET_STABILITY_MECHANISM_ACCOUNT("ETS - UK Market Stability Mechanism Account"),

    /**
     * UK General Holding Account.
     */
    UK_GENERAL_HOLDING_ACCOUNT("ETS - UK General Holding Account"),

    /**
     * UK Deletion Account.
     */
    UK_DELETION_ACCOUNT("ETS - UK Deletion Account"),

    /**
     * National holding account.
     */
    NATIONAL_HOLDING_ACCOUNT("ETS - National holding account"),

    /**
     * The retirement account.
     */
    RETIREMENT_ACCOUNT("KP - Retirement account"),

    /**
     * The voluntary cancellation account.
     */
    VOLUNTARY_CANCELLATION_ACCOUNT("KP - Voluntary cancellation account"),

    /**
     * The mandatory cancellation account.
     */
    MANDATORY_CANCELLATION_ACCOUNT("KP - Mandatory cancellation account"),

    /**
     * The Art37Ter cancellation account.
     */
    ARTICLE_3_7_TER_CANCELLATION_ACCOUNT("KP - Art37ter cancellation account"),

    /**
     * The ambition increase cancellation account.
     */
    AMBITION_INCREASE_CANCELLATION_ACCOUNT("KP - Ambition increase cancellation account"),

    TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY("KP - tCER Replacement account for expiry"),

    LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY("KP - lCER Replacement account for expiry"),

    LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE("KP - lCER Replacement account for reversal of storage"),

    LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT("KP - lCER Replacement account for non-submission of certification report"),

    /**
     * Excess issuance cancellation account.
     */
    EXCESS_ISSUANCE_CANCELLATION_ACCOUNT("KP - Excess issuance cancellation account"),

    /**
     * CCS net reversal cancellation account.
     */
    CCS_NET_REVERSAL_CANCELLATION_ACCOUNT("KP - CCS net reversal cancellation account"),

    /**
     * CCS non-submission of verification report cancellation account.
     */
    CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT("KP - CCS non-submission of verification report cancellation account"),

    /**
     * Pending account.
     */
    PENDING_ACCOUNT("KP - Pending Account"),

    /**
     * AAU deposit account.
     */
    AAU_DEPOSIT("ETS - AAU Deposit Account");

    /**
     * The label.
     */
    private String label;

    /**
     * Constructor.
     *
     * @param label The label.
     */
    AccountType(String label) {
        this.label = label;
    }

    /**
     * Parses the provided string to an account type and returns its label.
     *
     * @param input The input string.
     * @return the account type label, or null if no matching input is found.
     */
    public static String getLabel(String input) {
        return Arrays.stream(values())
            .filter(accountType -> accountType.name().equals(input))
            .findFirst()
            .map(AccountType::getLabel)
            .orElse(null);
    }

}
