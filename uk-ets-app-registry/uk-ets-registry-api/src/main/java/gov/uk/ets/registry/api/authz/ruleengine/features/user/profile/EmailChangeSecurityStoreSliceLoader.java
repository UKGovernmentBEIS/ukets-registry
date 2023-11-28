package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeChecker;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeDTO;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Component which is responsible for loading the {@link EmailChangeSecurityStoreSlice} data.
 */
@Component
@RequiredArgsConstructor
public class EmailChangeSecurityStoreSliceLoader {
    private final EmailChangeChecker emailChangeChecker;
    private BusinessSecurityStore businessSecurityStore;
    private RuleInputStore ruleInputStore;

    /**
     * Loads the {@link EmailChangeSecurityStoreSlice}
     */
    public void load() {
        if (businessSecurityStore.getEmailChangeSecuritySlice() != null || !ruleInputStore.containsKey(RuleInputType.NEW_EMAIL)) {
            return;
        }
        EmailChangeSecurityStoreSlice emailChangeSecurityStoreSlice = new EmailChangeSecurityStoreSlice();
        EmailChangeDTO emailChangeDTO = (EmailChangeDTO)ruleInputStore.get(RuleInputType.NEW_EMAIL);
        emailChangeSecurityStoreSlice
            .setOtherPendingEmailChangeByCurrentUserExists(emailChangeChecker.otherCurrentUserPendingRequests());
        emailChangeSecurityStoreSlice.setOtherPendingEmailChangeWithSameNewEmailExists(
            emailChangeChecker.pendingRequestsWithSameNewEmail(emailChangeDTO.getNewEmail()));
        emailChangeSecurityStoreSlice.setOtherUsersEmail(emailChangeChecker.otherUserHasNewEmailAsWorkingEmail(emailChangeDTO.getNewEmail()));
        businessSecurityStore.setEmailChangeSecuritySlice(emailChangeSecurityStoreSlice);
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
