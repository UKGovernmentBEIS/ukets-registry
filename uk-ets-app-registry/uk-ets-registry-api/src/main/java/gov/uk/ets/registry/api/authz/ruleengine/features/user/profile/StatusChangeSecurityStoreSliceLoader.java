package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import lombok.RequiredArgsConstructor;

/**
 * Component which is responsible for loading the {@link StatusChangeSecurityStoreSliceLoader} data.
 */
@Component
@RequiredArgsConstructor
public class StatusChangeSecurityStoreSliceLoader {

    private final TaskRepository taskRepository;
    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;

    /**
     * Loads the {@link StatusChangeSecurityStoreSliceLoader}
     */
    public void load() {
    	if (businessSecurityStore.getStatusChangeSecurityStoreSlice() != null || !ruleInputStore.containsKey(RuleInputType.NEW_STATUS)) {
            return;
        }
        StatusChangeSecurityStoreSlice slice = new StatusChangeSecurityStoreSlice();
        UserStatusChangeDTO userStatusChangeDTO = (UserStatusChangeDTO)ruleInputStore.get(RuleInputType.NEW_STATUS);
        if(CollectionUtils.isNotEmpty(taskRepository.findPendingTasksByTypesAndUser(RequestType.getTasksCausingUserSuspension(), userStatusChangeDTO.getUrid()))) {
			slice.setUserSuspendedByTheSystem(true);
		}
        businessSecurityStore.setStatusChangeSecurityStoreSlice(slice);
    }

    @Resource(name = "requestScopedBusinessSecurityStore")
    protected void setBusinessSecurityStore(BusinessSecurityStore businessSecurityStore) {
        this.businessSecurityStore = businessSecurityStore;
    }

    @Resource(name = "requestScopedRuleInputStore")
    protected void setRuleInputStore(RuleInputStore ruleInputStore) {
        this.ruleInputStore = ruleInputStore;
    }
}
