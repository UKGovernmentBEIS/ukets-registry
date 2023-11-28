package gov.uk.ets.registry.api.authz.ruleengine.features.ar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateAction;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper;
import gov.uk.ets.registry.api.ar.rules.ARUpdateBRTestHelper.ExpectedARCommand;
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
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArBusinessSecurityStoreSliceLoaderTest {
    @Mock
    private ARAccountAccessRepository arAccountAccessRepository;

    @Mock
    private ARUpdateActionRepository arUpdateActionRepository;

    @Mock
    private RuleInputStore ruleInputStore;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    private BusinessSecurityStore businessSecurityStore;

    private ArBusinessSecurityStoreSliceLoader arBusinessSecurityStoreSliceLoader;

    private ARUpdateBRTestHelper helper = new ARUpdateBRTestHelper();

    @BeforeEach
    void setUp() {
        businessSecurityStore = new BusinessSecurityStore();
        arBusinessSecurityStoreSliceLoader = new ArBusinessSecurityStoreSliceLoader(arAccountAccessRepository,
                                                                                    arUpdateActionRepository,
                                                                                    authorizationService,
                                                                                    userService);
        arBusinessSecurityStoreSliceLoader.setBusinessSecurityStore(businessSecurityStore);
        arBusinessSecurityStoreSliceLoader.setRuleInputStore(ruleInputStore);
    }

    @Test
    @DisplayName("Given ACCOUNT_ID and AR_CANDIDATE rule input types when loads then it loads the expected business security store slice")
    void loadWithAccountIdAndCandidate() {
        // given
        UpdateARRequest updateARRequest = new UpdateARRequest();
        updateARRequest.setCandidateUrid("UK99988");
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE)).willReturn(true);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_URID)).willReturn(false);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR)).willReturn(false);
        given(ruleInputStore.get(RuleInputType.AR_CANDIDATE)).willReturn(updateARRequest);
        TestCommand command = TestCommand.builder()
            .accountId(123L)
            .accountARs(List.of(helper.buildAR(ExpectedARCommand
                .builder()
                .urid("UK21312")
                .build())))
            .build();
        givenExpected(command);
        // when
        arBusinessSecurityStoreSliceLoader.load();
        // then
        verifyCommonLoadBehavior(command);
        assertEquals(updateARRequest.getCandidateUrid(), businessSecurityStore.getArUpdateStoreSlice().getCandidateUrid());
    }

    @Test
    @DisplayName("Given ACCOUNT_ID and AR_CANDIDATE_URID rule input types when loads then it loads the expected business security store slice")
    void loadWithAccountIdAndCandidateUrid() {
        ARBusinessSecurityStoreSlice arUpdateState = new ARBusinessSecurityStoreSlice();
        User user = new User();
        // given
        String candidateUrid = "UK99988";
        arUpdateState.setCandidateUrid(candidateUrid);
        given(ruleInputStore.get(RuleInputType.AR_CANDIDATE_URID)).willReturn(candidateUrid);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_URID)).willReturn(true);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE)).willReturn(false);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR)).willReturn(false);
        given(userService.getUserByUrid(arUpdateState.getCandidateUrid())).willReturn(user);

        TestCommand command = TestCommand.builder()
            .accountId(123L)
            .accountARs(List.of(helper.buildAR(ExpectedARCommand
                .builder()
                .urid("UK21312")
                .build())))
            .candidateUserRoles(new ArrayList<>())
            .build();
        givenExpected(command);
        // when
        arBusinessSecurityStoreSliceLoader.load();
        // then
        verifyCommonLoadBehavior(command);
        assertEquals(candidateUrid, businessSecurityStore.getArUpdateStoreSlice().getCandidateUrid());
    }

    @Test
    @DisplayName("Given ACCOUNT_ID and AR_CANDIDATE_AND_PREDECESSOR rule input types when loads then it loads the expected business security store slice")
    void loadWithAccountIdAndCandidatePredecessorUrid() {
        // given
        ReplaceARRequest replaceARRequest = new ReplaceARRequest();
        replaceARRequest.setCandidateUrid("UK99988");
        replaceARRequest.setReplaceeUrid("UK232323");
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_URID)).willReturn(false);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE)).willReturn(false);
        given(ruleInputStore.containsKey(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR)).willReturn(true);
        given(ruleInputStore.get(RuleInputType.AR_CANDIDATE_AND_PREDECESSOR)).willReturn(replaceARRequest);
        TestCommand command = TestCommand.builder()
            .accountId(123L)
            .accountARs(List.of(helper.buildAR(ExpectedARCommand
                .builder()
                .urid("UK21312")
                .build())))
            .build();
        givenExpected(command);
        // when
        arBusinessSecurityStoreSliceLoader.load();
        // then
        verifyCommonLoadBehavior(command);
        assertNotNull(businessSecurityStore.getArUpdateStoreSlice().getPredecessorUrid());
        assertNotNull(businessSecurityStore.getArUpdateStoreSlice().getCandidateUrid());
        assertEquals(replaceARRequest.getReplaceeUrid(), businessSecurityStore.getArUpdateStoreSlice().getPredecessorUrid());
        assertEquals(replaceARRequest.getCandidateUrid(), businessSecurityStore.getArUpdateStoreSlice().getCandidateUrid());
    }

    private void verifyCommonLoadBehavior(TestCommand command) {
        then(arAccountAccessRepository).should(times(1)).fetchARs(command.accountId, null);
        then(arUpdateActionRepository).should(times(1)).fetchByAccountId(command.accountId);
        ARBusinessSecurityStoreSlice slice = businessSecurityStore.getArUpdateStoreSlice();
        assertEquals(command.accountARs, slice.getAccountARs());
        assertEquals(command.pendingARUpdateRequests, slice.getPendingARUpdateRequests());
    }

    private void givenExpected(TestCommand command) {
        given(ruleInputStore.get(RuleInputType.ACCOUNT_ID)).willReturn(command.accountId);
        given(ruleInputStore.containsKey(RuleInputType.ACCOUNT_ID)).willReturn(true);
        given(arAccountAccessRepository.fetchARs(command.accountId, null))
            .willReturn(command.accountARs);
        given(arUpdateActionRepository.fetchByAccountId(command.accountId)).willReturn(command.pendingARUpdateRequests);
    }

    @Builder
    private static class TestCommand {
        private Long accountId;
        private List<AccountAccess> accountARs;
        private List<ARUpdateAction> pendingARUpdateRequests;
        private List<UserRole> candidateUserRoles;
    }
}