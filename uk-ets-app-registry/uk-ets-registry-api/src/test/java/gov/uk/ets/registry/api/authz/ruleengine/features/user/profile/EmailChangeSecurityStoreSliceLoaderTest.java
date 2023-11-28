package gov.uk.ets.registry.api.authz.ruleengine.features.user.profile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;

import com.querydsl.core.types.dsl.BooleanExpression;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputStore;
import gov.uk.ets.registry.api.authz.ruleengine.RuleInputType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.user.admin.repository.KeycloakUserRepository;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.admin.shared.KeycloakUserSearchPagedResults;
import gov.uk.ets.registry.api.user.profile.domain.EmailChangeBooleanExpressionFactory;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeChecker;
import gov.uk.ets.registry.api.user.profile.service.EmailChangeDTO;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmailChangeSecurityStoreSliceLoaderTest {
    @Mock
    UserAdministrationService userAdministrationService;
    @Mock
    private EmailChangeBooleanExpressionFactory emailChangeBooleanExpressionFactory;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private RuleInputStore ruleInputStore;

    private BusinessSecurityStore businessSecurityStore = new BusinessSecurityStore();

    private EmailChangeChecker emailChangeChecker;

    private EmailChangeSecurityStoreSliceLoader loader;

    @BeforeEach
    void setUp() {
        emailChangeChecker = new EmailChangeChecker(emailChangeBooleanExpressionFactory, taskRepository, userAdministrationService);
        loader = new EmailChangeSecurityStoreSliceLoader(emailChangeChecker);
        loader.setRuleInputStore(ruleInputStore);
        loader.setBusinessSecurityStore(businessSecurityStore);
    }


    @ParameterizedTest
    @MethodSource("getTestCases")
    void load(TestCase testCase) {
        //given
        String newEmail = "test_new@test.gr";
        EmailChangeDTO dto = EmailChangeDTO.builder()
            .newEmail(newEmail)
            .build();

        given(ruleInputStore.containsKey(RuleInputType.NEW_EMAIL)).willReturn(true);
        given(ruleInputStore.get(RuleInputType.NEW_EMAIL)).willReturn(dto);

        BooleanExpression currentUserPendingEmailChangesExpression = Mockito.mock(BooleanExpression.class);
        given(emailChangeBooleanExpressionFactory.getCurrentUserPendingEmailChangesExpression()).willReturn(currentUserPendingEmailChangesExpression);
        given(taskRepository.count(currentUserPendingEmailChangesExpression)).willReturn(testCase.otherPendingEmailChangeByCurrentUserExists ? 1L : 0);

        BooleanExpression sameNewEmailPendingEmailChangesExpression = Mockito.mock(BooleanExpression.class);
        given(emailChangeBooleanExpressionFactory.getOfSameNewEmailPendingEmailChangesExpression(eq(newEmail))).willReturn(sameNewEmailPendingEmailChangesExpression);
        given(taskRepository.count(sameNewEmailPendingEmailChangesExpression)).willReturn(testCase.otherPendingEmailChangeWithSameNewEmailExists ? 1L : 0);

        given(userAdministrationService.userExists(any())).willReturn(testCase.otherUsersEmail);

        // when
        loader.load();

        // then
        EmailChangeSecurityStoreSlice slice = businessSecurityStore.getEmailChangeSecuritySlice();
        assertEquals(testCase.otherPendingEmailChangeByCurrentUserExists, slice.isOtherPendingEmailChangeByCurrentUserExists());
        assertEquals(testCase.otherPendingEmailChangeWithSameNewEmailExists, slice.isOtherPendingEmailChangeWithSameNewEmailExists());
        assertEquals(testCase.otherUsersEmail, slice.isOtherUsersEmail());
    }

    static Stream<Arguments> getTestCases() throws ParseException {
        return Stream.of(new TestCase(false, false, false),
            new TestCase(false, false, true),
            new TestCase(false, true, false),
            new TestCase(false, true, true),
            new TestCase(true, false, false),
            new TestCase(true, false, true),
            new TestCase(true, true, false),
            new TestCase(true, true, true))
            .map(t -> Arguments.of(t));
    }

    @AllArgsConstructor
    static class TestCase {
        private boolean otherPendingEmailChangeByCurrentUserExists;
        private boolean otherPendingEmailChangeWithSameNewEmailExists;
        private boolean otherUsersEmail;
    }
}