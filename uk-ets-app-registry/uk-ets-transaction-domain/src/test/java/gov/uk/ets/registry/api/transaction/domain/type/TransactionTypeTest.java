package gov.uk.ets.registry.api.transaction.domain.type;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


class TransactionTypeTest {

    @DisplayName("Display Tasks of specific transaction types to AR.")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {1}")
    void test_tasksAccessibleOnlyToAdmin(TransactionType transactionType, boolean displayTaskOnlyToAdmin) {
        Assert.assertEquals(displayTaskOnlyToAdmin, TransactionType.tasksAccessibleOnlyToAdmin().contains(transactionType));
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(TransactionType.ExpiryDateChange, true),
                Arguments.of(TransactionType.CancellationKyotoUnits, false),
                Arguments.of(TransactionType.TransferToSOPforFirstExtTransferAAU, true),
                Arguments.of(TransactionType.Retirement, true),
                Arguments.of(TransactionType.MandatoryCancellation, true),
                Arguments.of(TransactionType.Art37Cancellation, true),
                Arguments.of(TransactionType.AmbitionIncreaseCancellation, true),
                Arguments.of(TransactionType.ConversionCP1, true),
                Arguments.of(TransactionType.ConversionA, true),
                Arguments.of(TransactionType.CarryOver_AAU, true),
                Arguments.of(TransactionType.CarryOver_CER_ERU_FROM_AAU, true),
                Arguments.of(TransactionType.AllocateAllowances, true),
                Arguments.of(TransactionType.IssueOfAAUsAndRMUs, true),
                Arguments.of(TransactionType.IssueAllowances, false),
                Arguments.of(TransactionType.InternalTransfer, false),
                Arguments.of(TransactionType.ExternalTransfer, false),
                Arguments.of(TransactionType.CentralTransferAllowances, false),
                Arguments.of(TransactionType.TransferAllowances, false),
                Arguments.of(TransactionType.AuctionDeliveryAllowances, false),
                Arguments.of(TransactionType.SurrenderAllowances, false)
        );
    }

    @DisplayName("Originating Country Code set in Transaction types.")
    @MethodSource("getArgumentsForSpecificCountryCode")
    @ParameterizedTest(name = "#{index} - {0} - {1}")
    void test_hasSpecificCountryCode(TransactionType transactionType, String originatingCountryCode) {
        Assert.assertEquals(originatingCountryCode, transactionType.getOriginatingCountryCode());
    }

    static Stream<Arguments> getArgumentsForSpecificCountryCode() {
        return Stream.of(
                Arguments.of(TransactionType.ExpiryDateChange, null),
                Arguments.of(TransactionType.CancellationKyotoUnits, null),
                Arguments.of(TransactionType.TransferToSOPforFirstExtTransferAAU, null),
                Arguments.of(TransactionType.Retirement, null),
                Arguments.of(TransactionType.MandatoryCancellation, null),
                Arguments.of(TransactionType.Art37Cancellation, null),
                Arguments.of(TransactionType.AmbitionIncreaseCancellation, null),
                Arguments.of(TransactionType.ConversionCP1, Constants.KYOTO_REGISTRY_CODE),
                Arguments.of(TransactionType.ConversionA, Constants.KYOTO_REGISTRY_CODE),
                Arguments.of(TransactionType.ConversionB, Constants.KYOTO_REGISTRY_CODE),
                Arguments.of(TransactionType.CarryOver_AAU, null),
                Arguments.of(TransactionType.CarryOver_CER_ERU_FROM_AAU, null),
                Arguments.of(TransactionType.AllocateAllowances, null),
                Arguments.of(TransactionType.IssueOfAAUsAndRMUs, null),
                Arguments.of(TransactionType.IssueAllowances, null),
                Arguments.of(TransactionType.InternalTransfer, null),
                Arguments.of(TransactionType.ExternalTransfer, null),
                Arguments.of(TransactionType.CentralTransferAllowances, null),
                Arguments.of(TransactionType.TransferAllowances, null),
                Arguments.of(TransactionType.AuctionDeliveryAllowances, null)
        );
    }

    @DisplayName("Is allowed from Blocked Account.")
    @MethodSource("getArgumentsIsAllowedFromBlockedAccount")
    @ParameterizedTest(name = "#{index} - {0} - {1}")
    void test_isAllowedFromBlockedAccount(TransactionType transactionType, boolean isAllowedFromBlockedAccount) {
        Assert.assertEquals(isAllowedFromBlockedAccount, transactionType.isAllowedFromPartiallyTransactionRestrictedAccount());
    }

    static Stream<Arguments> getArgumentsIsAllowedFromBlockedAccount() {
        return Stream.of(
                Arguments.of(TransactionType.ExpiryDateChange, false),
                Arguments.of(TransactionType.CancellationKyotoUnits, false),
                Arguments.of(TransactionType.TransferToSOPforFirstExtTransferAAU, false),
                Arguments.of(TransactionType.Retirement, false),
                Arguments.of(TransactionType.MandatoryCancellation, false),
                Arguments.of(TransactionType.Art37Cancellation, false),
                Arguments.of(TransactionType.AmbitionIncreaseCancellation, false),
                Arguments.of(TransactionType.ConversionCP1, false),
                Arguments.of(TransactionType.ConversionA, false),
                Arguments.of(TransactionType.ConversionB, false),
                Arguments.of(TransactionType.CarryOver_AAU, false),
                Arguments.of(TransactionType.CarryOver_CER_ERU_FROM_AAU, false),
                Arguments.of(TransactionType.AllocateAllowances, false),
                Arguments.of(TransactionType.IssueOfAAUsAndRMUs, false),
                Arguments.of(TransactionType.IssueAllowances, false),
                Arguments.of(TransactionType.InternalTransfer, false),
                Arguments.of(TransactionType.ExternalTransfer, false),
                Arguments.of(TransactionType.CentralTransferAllowances, false),
                Arguments.of(TransactionType.TransferAllowances, false),
                Arguments.of(TransactionType.SurrenderAllowances, true),
                Arguments.of(TransactionType.AuctionDeliveryAllowances, false)
        );
    }
}
