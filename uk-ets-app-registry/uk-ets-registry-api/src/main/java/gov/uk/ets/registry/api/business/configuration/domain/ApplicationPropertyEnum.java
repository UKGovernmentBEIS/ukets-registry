package gov.uk.ets.registry.api.business.configuration.domain;

import lombok.Getter;

/**
 * In this enumeration we declare which application properties are available to the user through the application.
 * All the properties are declared below and are available.
 * <p>
 * Please remove the properties that must not be displayed.
 */
@Getter
public enum ApplicationPropertyEnum {
    /**
     * Cookies configuration.
     */
    COOKIES_EXPIRATION_TIME("business.property.cookies.expiration.time"),

    /**
     * The minimum number of authorised representatives permitted in account.
     */
    MIN_NUMBER_OF_ARS("business.property.account.min.number.of.authorised.representatives"),
    
    /**
     * The minimum number of authorised representatives permitted in account creation per account type.
     */
    MIN_NUMBER_OF_ARS_FOR_ACCOUNT_OPENING_PER_TYPE("business.property.account.opening.min.number.of.authorised.representatives.per.account.type.map"),

    /**
     * The maximum number of authorised representatives permitted in account.
     */
    MAX_NUMBER_OF_ARS("business.property.account.max.number.of.authorised.representatives"),
    
    /**
     * The maximum number of authorised representatives permitted in account creation per account type.
     */
    MAX_NUMBER_OF_ARS_FOR_ACCOUNT_OPENING_PER_TYPE("business.property.account.opening.max.number.of.authorised.representatives.per.account.type.map"),
    
    /**
     * The date of warn for counting ar additions.
     */
    COUNT_NUMBER_OF_ARS_ADDITIONS_WARNING_DATE("business.property.account.count.number.of.authorised.representatives.additionsWarningDate"),

    /**
     * The current allocation (issuance) year.
     */
    CURRENT_ALLOCATION_YEAR("business.property.transaction.allocation-year"),

    /**
     * The service desk mail address.
     */
    MAIL_ETR_ADDRESS("mail.etrAddress"),

    /**
     * Reset password expiration in minutes.
     */
    RESET_PASSWORD_URL_EXPIRATION("business.property.reset.password.url.expiration"),

    CHANGE_OTP_EMERGENCY_VERIFICATION_URL_EXPIRATION("change.otp.emergency.verification.url.expiration"),

    CHANGE_PASSWORD_OTP_EMERGENCY_VERIFICATION_URL_EXPIRATION(
        "change.password-otp.emergency.verification.url.expiration"),

    /**
     * Toggles signing integration
     */
    SIGNING_SERVICE_ENABLED("signing.enabled"),

    /**
     * Used for the reset database functionality.
     */
    SYSTEM_ADMINISTRATION_ENABLED("system.administration.enabled"),

    /**
     * Offset time in seconds for timeout warning in the UI
     */
    SESSION_EXPIRATION_NOTIFICATION_OFFSET("session.expiration.notification.offset"),

    /**
     * Checks whether we enable the "account request" functionality for RAs only
     */
    REGISTRY_ACCOUNT_OPENING_ONLY_FOR_REGISTRY_ADMINISTRATION("account.opening.only.for.registry.administration"),

    /**
     * The max file of the file upload
     */
    REGISTRY_FILE_MAX_SIZE("registry.file.max.size"),

    /**
     * The allowed allocation table file type
     */
    REGISTRY_FILE_ALLOCATION_TABLE_TYPE("registry.file.allocation.table.type"),
    
    /**
     * The allowed emissions table file type
     */
    REGISTRY_FILE_EMISSIONS_TABLE_TYPE("registry.file.emissions.table.type"),

    /**
     * The allowed bulk AR file type
     */
    REGISTRY_FILE_BULK_AR_TYPE("registry.file.bulk.ar.type"),

    /**
     * The allowed bulk AR file type
     */
    REGISTRY_FILE_PUBLICATION_REPORT_TYPE("registry.file.publication.report.type"),

    /**
     * The allowed submit document types
     */
    REGISTRY_FILE_SUBMIT_DOCUMENT_TYPE("registry.file.submit.document.type"),

    /**
     * The allowed document area types
     */
    REGISTRY_FILE_DOCUMENT_AREA_TYPE("registry.file.document.area.type"),

    /**
     * The Google tracking id for google analytics
     */
    GOOGLE_TRACKING_ID("google.tracking.id"),

    /**
     * The password policy's minimum characters value
     */
    PASSWORD_POLICY_MIN_CHARS("password.policy.minimum-chars"),

    /**
     * Show the running balances for Kyoto Protocol accounts.
     */
    REGISTRY_TRANSACTION_LIST_SHOW_RUNNING_BALANCES_KYOTO("registry.transaction.list.showRunningBalancesKp"),
    
    /**
     * Show the running balances for ETS Protocol accounts.
     */
    REGISTRY_TRANSACTION_LIST_SHOW_RUNNING_BALANCES_ETS("registry.transaction.list.showRunningBalancesEts"),

    TRANSACTIONS_PUBLIC_REPORT_STARING_DAY("transactions.public.report.startingDay"),

    MARITIME_EMISSIONS_START_YEAR("emissions-maritime-starting-year");
    
    private String key;

    ApplicationPropertyEnum(String key) {
        this.key = key;
    }
}
