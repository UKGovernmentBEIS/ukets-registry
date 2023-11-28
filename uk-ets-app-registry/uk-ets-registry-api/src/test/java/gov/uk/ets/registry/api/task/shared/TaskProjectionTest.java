package gov.uk.ets.registry.api.task.shared;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TaskProjectionTest {

    private static Stream<Arguments> getAccountTypeValues() {
        return Stream.of(AccountType.values()).map(accountType ->
            Arguments.of(accountType.getKyotoType(), accountType.getRegistryType(), accountType));
    }

    @DisplayName("Create row task results and check the account type printed. Should succeed.")
    @ParameterizedTest(name = "#{index} - {0} - {1} should provide {2}")
    @MethodSource("getAccountTypeValues")
    void checkAccountTypeName(KyotoAccountType kyotoAccountType, RegistryAccountType registryAccountType,
                              AccountType accountType) {

        TaskProjection taskProjection = generateTaskProjection(kyotoAccountType, registryAccountType);
        assertEquals(taskProjection.getAccountType(), accountType.name());
        
        TaskProjection taskProjectionForAccountOpenTasks = generateTaskProjectionForAccountOpenTasks(
        	    kyotoAccountType, registryAccountType, accountType.name());
        assertEquals(taskProjectionForAccountOpenTasks.getAccountType(), accountType.name());

        taskProjection = generateTaskProjection(null, null);
        assertNull(taskProjection.getAccountType());
    }

    private TaskProjection generateTaskProjection(KyotoAccountType kyotoAccountType,
                                                  RegistryAccountType registryAccountType) {
        Calendar initiatedDate = Calendar.getInstance();
        initiatedDate.setTime(new Date());
        return new TaskProjection(1000098L, RequestType.ACCOUNT_TRANSFER,
            RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, null, "Registry Administrator", "Senior",
            -2L, "UK1234567890", initiatedDate.getTime(), null, null, null, null,
            null, null, 10000013L, "UK-100-10000013-2-45",
            kyotoAccountType, registryAccountType, null, "test person", null, null, null,
            null, null, null, null, null, null, null, null, null);
    }

    private TaskProjection generateTaskProjectionForAccountOpenTasks(KyotoAccountType kyotoAccountType,
            RegistryAccountType registryAccountType, String accountType) {
        Calendar initiatedDate = Calendar.getInstance();
        initiatedDate.setTime(new Date());
        return new TaskProjection(1000098L, RequestType.ACCOUNT_OPENING_REQUEST,
            RequestStateEnum.SUBMITTED_NOT_YET_APPROVED, null, "Registry Administrator", "Senior",
            -2L, "UK1234567890", initiatedDate.getTime(), null, null, null, null,
            null, null, 10000013L, "UK-100-10000013-2-45",
            kyotoAccountType, registryAccountType, accountType, "test person", null, null, null,
            null, null, null, null, null, null, null, null, null);
    }
}
