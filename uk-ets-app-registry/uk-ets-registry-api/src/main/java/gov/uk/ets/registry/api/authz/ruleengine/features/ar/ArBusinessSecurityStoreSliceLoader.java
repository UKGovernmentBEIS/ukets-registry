package gov.uk.ets.registry.api.authz.ruleengine.features.ar;

import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.web.model.ReplaceARRequest;
import gov.uk.ets.registry.api.ar.web.model.UpdateARRequest;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArBusinessSecurityStoreSliceLoader {
    private final ARAccountAccessRepository arAccountAccessRepository;
    private final ARUpdateActionRepository arUpdateActionRepository;
    private BusinessSecurityStore businessSecurityStore;
    private final AuthorizationService authorizationService;
    private final UserService userService;
    private RuleInputStore ruleInputStore;

    public void load() {
        if (businessSecurityStore.getArUpdateStoreSlice() != null
            || !ruleInputStore.containsKey(RuleInputType.ACCOUNT_ID)
            || !ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE)
            && !ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR)
            && !ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_URID)) {
            return;
        }
        ARBusinessSecurityStoreSlice arUpdateState = new ARBusinessSecurityStoreSlice();
        Long accountId = (Long)ruleInputStore.get(RuleInputType.ACCOUNT_ID);
        arUpdateState.setAccountARs(arAccountAccessRepository.fetchARs(accountId, null));
        arUpdateState.setPendingARUpdateRequests(arUpdateActionRepository.fetchByAccountId(accountId));
        arUpdateState.setPendingARAddRequests(arUpdateActionRepository.fetchPendingArAdditionsByAccountId(accountId));
        if (ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_URID)) {
            arUpdateState.setCandidateUrid((String)ruleInputStore.get(RuleInputType.AR_CANDIDATE_URID));
            User user = userService.getUserByUrid(arUpdateState.getCandidateUrid());
            setRequestedUserRoles(arUpdateState, user);
        }
        if (ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE)) {
            UpdateARRequest request = (UpdateARRequest)ruleInputStore.get(RuleInputType.AR_CANDIDATE);
            arUpdateState.setCandidateUrid(request.getCandidateUrid());
            User arCandidate =  userService.getUserByUrid(arUpdateState.getCandidateUrid());
            setRequestedUserRoles(arUpdateState, arCandidate);
            if (arCandidate != null) {
                businessSecurityStore.setRequestedUser(arCandidate);
            }
        }
        if (ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR)) {
            ReplaceARRequest request = (ReplaceARRequest)ruleInputStore.get(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR);
            arUpdateState.setCandidateUrid(request.getCandidateUrid());
            arUpdateState.setPredecessorUrid(request.getReplaceeUrid());
            User arCandidate =  userService.getUserByUrid(request.getCandidateUrid());
            if (arCandidate != null) {
                businessSecurityStore.setRequestedUser(arCandidate);
            }
        }
        businessSecurityStore.setArUpdateStoreSlice(arUpdateState);
    }

    private void setRequestedUserRoles(ARBusinessSecurityStoreSlice arUpdateState, User user) {
        List<UserRole> userRoleList = new ArrayList<>();
        if (user != null) {
            userRoleList = authorizationService
                .getClientLevelRoles(user.getIamIdentifier())
                .stream()
                .map(clientRole -> UserRole.fromKeycloakLiteral(clientRole.getName()))
                .collect(Collectors.toList());
        }
        arUpdateState.setCandidateUserRoles(userRoleList);
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
