package gov.uk.ets.registry.api.authz.ruleengine.features.account.holder;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.task.domain.Task;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountHolderSecurityStoreSlice {

    private AccountHolder accountHolder;
    private List<Task> tasksByAccountHolder;
}
