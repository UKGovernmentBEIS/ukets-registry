package gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessRuleInfo;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.TaskBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class CommentMandatoryWhenApprovedTest {

    private BusinessSecurityStore securityStore;
    private List<TaskBusinessRuleInfo> taskBusinessRuleInfoList;
    private TaskBusinessSecurityStoreSlice slice;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        taskBusinessRuleInfoList = new ArrayList<>();
        TaskBusinessRuleInfo ruleInfo = new TaskBusinessRuleInfo();
        taskBusinessRuleInfoList.add(ruleInfo);
        slice = new TaskBusinessSecurityStoreSlice();
    }

    @Test
    void error() {
        ErrorBody errorBody = new CommentMandatoryWhenApproved(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void notApplicableOutcome() {
        slice.setTaskOutcome(TaskOutcome.REJECTED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setCompleteComment("task approve test comment");
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        CommentMandatoryWhenApproved rule = new CommentMandatoryWhenApproved(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

    }

    @Test
    void forbiddenOutcome() {
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        CommentMandatoryWhenApproved rule = new CommentMandatoryWhenApproved(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }

    @Test
    void permittedOutcome() {
        slice.setTaskOutcome(TaskOutcome.APPROVED);
        slice.setTaskBusinessRuleInfoList(taskBusinessRuleInfoList);
        slice.setCompleteComment("task approve test comment");
        securityStore.setTaskBusinessSecurityStoreSlice(slice);
        CommentMandatoryWhenApproved rule = new CommentMandatoryWhenApproved(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());
    }
}

