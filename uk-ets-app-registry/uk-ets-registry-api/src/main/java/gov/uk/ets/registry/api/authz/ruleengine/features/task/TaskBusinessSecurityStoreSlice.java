package gov.uk.ets.registry.api.authz.ruleengine.features.task;


import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The data that are used on task  business rules checks.
 */
@Getter
@Setter
public class TaskBusinessSecurityStoreSlice {

    private List<TaskBusinessRuleInfo> taskBusinessRuleInfoList;
    private User taskAssignee;
    private List<UserRole> taskAssigneeRoles;
    private List<UserRole> taskAssigneeAccountAccess;
    private TaskOutcome taskOutcome;
    private String completeComment;
    private List<UserRole> candidateUserRoles;
    private List<User> candidateAccountARs;
}
