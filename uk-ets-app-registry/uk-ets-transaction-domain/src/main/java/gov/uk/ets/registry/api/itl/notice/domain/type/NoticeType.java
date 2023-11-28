package gov.uk.ets.registry.api.itl.notice.domain.type;

import gov.uk.ets.registry.api.itl.notice.domain.ITLNotification;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.ExternalPredefinedAccount;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * The type of the {@link ITLNotification notification}
 */
@Getter
@AllArgsConstructor
public enum NoticeType {

    /**
     * Net source cancellation (1).
     */
    NET_SOURCE_CANCELLATION(NoticeConfiguration.builder()
        .code(1)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.SPECIFIC_INTERNAL)
        .transaction(TransactionType.MandatoryCancellation)
        .account(AccountType.NET_SOURCE_CANCELLATION_ACCOUNT)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .build()),

    /**
     * Non-compliance cancellation (2).
     */
    NON_COMPLIANCE_CANCELLATION(NoticeConfiguration.builder()
        .code(2)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.SPECIFIC_INTERNAL)
        .transaction(TransactionType.MandatoryCancellation)
        .account(AccountType.NON_COMPLIANCE_CANCELLATION_ACCOUNT)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .build()),

    /**
     * Impending tCER/lCER expiry (3).
     */
    IMPENDING_EXPIRY_OF_TCER_AND_LCER(NoticeConfiguration.builder()
        .code(3)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.VARIABLE_BY_UNIT_TYPE)
        .transaction(TransactionType.Replacement)
        .account(AccountType.TCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .account(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_EXPIRY)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .build()),

    /**
     * Reversal of storage for CDM project (4).
     */
    REVERSAL_OF_STORAGE_FOR_CDM_PROJECT(NoticeConfiguration.builder()
        .code(4)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.VARIABLE_BY_TRANSACTION)
        .transaction(TransactionType.MandatoryCancellation)
        .transaction(TransactionType.Replacement)
        .account(AccountType.MANDATORY_CANCELLATION_ACCOUNT)
        .account(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_REVERSAL_OF_STORAGE)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .build()),

    /**
     * Non-submission of certification report for CDM project (5).
     */
    NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT(NoticeConfiguration.builder()
        .code(5)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.VARIABLE_BY_TRANSACTION)
        .transaction(TransactionType.MandatoryCancellation)
        .transaction(TransactionType.Replacement)
        .account(AccountType.MANDATORY_CANCELLATION_ACCOUNT)
        .account(AccountType.LCER_REPLACEMENT_ACCOUNT_FOR_NON_SUBMISSION_OF_CERTIFICATION_REPORT)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .build()),

    /**
     * Excess issuance for CDM project (6).
     */
    EXCESS_ISSUANCE_FOR_CDM_PROJECT(NoticeConfiguration.builder()
        .code(6)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.CDM)
        .transaction(TransactionType.ExternalTransfer)
        .transaction(TransactionType.InternalTransfer)
        .externalPredefinedAccount(ExternalPredefinedAccount.CDM_EXCESS_ISSUANCE_ACCOUNT_FOR_CP2)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .build()),

    /**
     * Commitment Period reserve (7).
     */
    COMMITMENT_PERIOD_RESERVE(NoticeConfiguration.builder()
        .code(7)
        .transactionNeededForFulfilment(false)
        .mode(NoticeAcquiringAccountMode.NONE)
        .build()),

    /**
     * Unit carry-over (8).
     */
    UNIT_CARRY_OVER(NoticeConfiguration.builder()
        .code(8)
        .transactionNeededForFulfilment(false)
        .mode(NoticeAcquiringAccountMode.NONE)
        .outOfScope(true)
        .build()),

    /**
     * Expiry date change (9).
     */
    EXPIRY_DATE_CHANGE(NoticeConfiguration.builder()
        .code(9)
        .transactionNeededForFulfilment(true)
        .transaction(TransactionType.ExpiryDateChange)
        .mode(NoticeAcquiringAccountMode.NONE)
        .unit(UnitType.TCER)
        .unit(UnitType.LCER)
        .build()),

    /**
     * Notification update (10).
     */
    NOTIFICATION_UPDATE(NoticeConfiguration.builder()
        .code(10)
        .transactionNeededForFulfilment(false)
        .mode(NoticeAcquiringAccountMode.NONE)
        .build()),

    /**
     * EU15 Commitment Period reserve (11).
     */
    EU15_COMMITMENT_PERIOD_RESERVE(NoticeConfiguration.builder()
        .code(11)
        .transactionNeededForFulfilment(false)
        .mode(NoticeAcquiringAccountMode.NONE)
        .outOfScope(true)
        .build()),

    /**
     * Net reversal of storage of a CDM CCS project (12).
     */
    NET_REVERSAL_OF_STORAGE_OF_A_CDM_CCS_PROJECT(NoticeConfiguration.builder()
        .code(12)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.CDM)
        .transaction(TransactionType.ExternalTransfer)
        .transaction(TransactionType.InternalTransfer)
        .externalPredefinedAccount(ExternalPredefinedAccount.CCS_NET_REVERSAL_CANCELLATION_ACCOUNT)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .build()),

    /**
     * Non-submission of verification report for a CDM CCS project (13).
     */
    NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT(NoticeConfiguration.builder()
        .code(13)
        .transactionNeededForFulfilment(true)
        .mode(NoticeAcquiringAccountMode.CDM)
        .transaction(TransactionType.ExternalTransfer)
        .transaction(TransactionType.InternalTransfer)
        .externalPredefinedAccount(ExternalPredefinedAccount.CCS_NON_SUBMISSION_OF_VERIFICATION_REPORT_CANCELLATION_ACCOUNT)
        .unit(UnitType.AAU)
        .unit(UnitType.ERU_FROM_AAU)
        .unit(UnitType.ERU_FROM_RMU)
        .unit(UnitType.RMU)
        .unit(UnitType.CER)
        .build());

    /**
     * The configuration information.
     */
    private NoticeConfiguration configuration;

    /**
     * Returns the code, based on ITL DES technical specifications.
     * @return the code.
     */
    public Integer getCode() {
        return configuration.getCode();
    }

    /**
     * Checks whether this ITL notification type has the provided acquiring account mode.
     * @param mode The acquiring account mode.
     * @return false/true
     */
    public boolean hasMode(NoticeAcquiringAccountMode mode) {
        return configuration.getMode().equals(mode);
    }

    public ExternalPredefinedAccount getCdmAccount() {
        return configuration.getExternalPredefinedAccount();
    }

    public AccountType getAcquiringAccountType(UnitType unitType, TransactionType transactionType) {
        AccountType result = null;
        if (hasMode(NoticeAcquiringAccountMode.SPECIFIC_INTERNAL)) {
            result = configuration.getAcquiringAccount().get(0);

        } else if (hasMode(NoticeAcquiringAccountMode.VARIABLE_BY_UNIT_TYPE)) {
            result = configuration.getAcquiringAccount().stream().filter(
                accountType -> accountType.getUnitTypes().contains(unitType)).findFirst().orElse(null);

        } else if (hasMode(NoticeAcquiringAccountMode.VARIABLE_BY_TRANSACTION)) {
            int index = configuration.getTransactions().indexOf(transactionType);
            result = configuration.getAcquiringAccount().get(index);
        }
        return result;
    }

    /**
     * Identifies the notice type based on the notice type codes.
     * @param noticeType The notice type code.
     * @return a notice type.
     */
    public static NoticeType of(int noticeType) {
        NoticeType result = null;
        Optional<NoticeType> optional = Stream.of(values())
                .filter(type ->
                        type.getCode().equals(noticeType))
                .findFirst();
        if (optional.isPresent()) {
            result = optional.get();
        }
        return result;
    }

    /**
     * Returns the unit types allowed during this ITL Notification type.
     *
     * @return some unit types.
     */
    public List<UnitType> getUnits() {
        return configuration.getUnitTypes();
    }

    /**
     * Returns the transaction types allowed during this ITL Notification type.
     *
     * @return some transaction types.
     */
    public List<TransactionType> getTransactionTypes() {
        return configuration.getTransactions();
    }
}
