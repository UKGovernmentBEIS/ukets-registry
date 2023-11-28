package gov.uk.ets.registry.api.itl.notice.domain.type;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import org.junit.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class NoticeTypeTest {

    @DisplayName("Check Notice Type configuration.")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0} - {2}")
    void test_noticeTypeConfiguration(NoticeType noticeType, List<TransactionType> transactionTypes,
                                      Integer code, List<UnitType> unitTypes) {
        Assert.assertEquals(code, noticeType.getCode());
        Assert.assertEquals(transactionTypes, noticeType.getTransactionTypes());
        Assert.assertEquals(unitTypes, noticeType.getUnits());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of(NoticeType.NET_SOURCE_CANCELLATION, List.of(TransactionType.MandatoryCancellation), 1,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER)),
                Arguments.of(NoticeType.NON_COMPLIANCE_CANCELLATION, List.of(TransactionType.MandatoryCancellation), 2,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER)),
                Arguments.of(NoticeType.IMPENDING_EXPIRY_OF_TCER_AND_LCER, List.of(TransactionType.Replacement), 3,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER, UnitType.TCER, UnitType.LCER)),
                Arguments.of(NoticeType.REVERSAL_OF_STORAGE_FOR_CDM_PROJECT, List.of(TransactionType.MandatoryCancellation, TransactionType.Replacement), 4,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER, UnitType.TCER, UnitType.LCER)),
                Arguments.of(NoticeType.NON_SUBMISSION_OF_CERTIFICATION_REPORT_FOR_CDM_PROJECT, List.of(TransactionType.MandatoryCancellation, TransactionType.Replacement), 5,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER, UnitType.TCER, UnitType.LCER)),
                Arguments.of(NoticeType.EXCESS_ISSUANCE_FOR_CDM_PROJECT, List.of(TransactionType.ExternalTransfer, TransactionType.InternalTransfer), 6,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER, UnitType.TCER, UnitType.LCER)),
                Arguments.of(NoticeType.COMMITMENT_PERIOD_RESERVE, List.of(), 7, List.of()),
                Arguments.of(NoticeType.UNIT_CARRY_OVER, List.of(), 8, List.of()),
                Arguments.of(NoticeType.EXPIRY_DATE_CHANGE, List.of(TransactionType.ExpiryDateChange), 9, List.of(UnitType.TCER, UnitType.LCER)),
                Arguments.of(NoticeType.NOTIFICATION_UPDATE, List.of(), 10, List.of()),
                Arguments.of(NoticeType.EU15_COMMITMENT_PERIOD_RESERVE, List.of(), 11, List.of()),
                Arguments.of(NoticeType.NET_REVERSAL_OF_STORAGE_OF_A_CDM_CCS_PROJECT, List.of(TransactionType.ExternalTransfer, TransactionType.InternalTransfer), 12,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER)),
                Arguments.of(NoticeType.NON_SUBMISSION_OF_VERIFICATION_REPORT_FOR_A_CDM_CCS_PROJECT, List.of(TransactionType.ExternalTransfer, TransactionType.InternalTransfer), 13,
                        List.of(UnitType.AAU, UnitType.ERU_FROM_AAU, UnitType.ERU_FROM_RMU, UnitType.RMU, UnitType.CER))
        );
    }
}
