package gov.uk.ets.registry.api.authz.ruleengine.features.task;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.List;
import lombok.Data;

/**
 * Task data object needed to verify business rules.
 */
@Data
public class TaskBusinessRuleInfo {
    private Task task;
    private User taskInitiator;
    private List<UserRole> taskInitiatorRoles;
    private List<AccountAccess> taskInitiatorAccountAccess;
    private List<Task> subTasks;

}
