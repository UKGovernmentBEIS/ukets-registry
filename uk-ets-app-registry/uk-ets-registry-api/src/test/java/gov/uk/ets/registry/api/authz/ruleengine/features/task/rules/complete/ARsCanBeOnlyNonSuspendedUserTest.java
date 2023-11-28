package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ARsCanBeOnlyNonSuspendedUserTest {

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        securityStore = new BusinessSecurityStore();
    }

    @Test
    void error() {
        ErrorBody errorBody = new ARsCanBeOnlyNonSuspendedUser(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - User {1} - Request {0} - Outcome {2} - Result {3}")
    void permit(RequestType requestType, UserStatus userStatus, TaskOutcome outcome, BusinessRule.Result result) {
        // given

        User user = new User();
        user.setId(1L);
        user.setState(userStatus);

        Task task = new Task();
        task.setUser(user);
        task.setType(requestType);

        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        ruleInfo.setTask(task);

        List<TaskBusinessRuleInfo> taskBusinessRuleInfoList = new ArrayList<>();
        taskBusinessRuleInfoList.add(ruleInfo);

        TaskBusinessSecurityStoreSlice slice = new TaskBusinessSecurityStoreSlice();
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setTaskOutcome(outcome);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);

        // when
        BusinessRule.Outcome rule = new ARsCanBeOnlyNonSuspendedUser(securityStore).permit();

        // then
        assertEquals(result, rule.getResult());
    }

    static Stream<Arguments> getArguments() {

        return Stream.of(
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, UserStatus.ENROLLED, TaskOutcome.APPROVED, BusinessRule.Result.PERMITTED),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, UserStatus.ENROLLED, TaskOutcome.APPROVED, BusinessRule.Result.NOT_APPLICABLE),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, UserStatus.ENROLLED, TaskOutcome.APPROVED, BusinessRule.Result.PERMITTED),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, UserStatus.SUSPENDED, TaskOutcome.APPROVED, BusinessRule.Result.FORBIDDEN),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, UserStatus.SUSPENDED, TaskOutcome.APPROVED, BusinessRule.Result.NOT_APPLICABLE),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, UserStatus.SUSPENDED, TaskOutcome.APPROVED, BusinessRule.Result.FORBIDDEN),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, UserStatus.ENROLLED, TaskOutcome.REJECTED, BusinessRule.Result.PERMITTED),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, UserStatus.ENROLLED, TaskOutcome.REJECTED, BusinessRule.Result.NOT_APPLICABLE),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, UserStatus.ENROLLED, TaskOutcome.REJECTED, BusinessRule.Result.PERMITTED),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST, UserStatus.SUSPENDED, TaskOutcome.REJECTED, BusinessRule.Result.PERMITTED),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST, UserStatus.SUSPENDED, TaskOutcome.REJECTED, BusinessRule.Result.NOT_APPLICABLE),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST, UserStatus.SUSPENDED, TaskOutcome.REJECTED, BusinessRule.Result.PERMITTED)
        );
    }
}
