package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.OnlyRegistryAdminCanCompleteTaskRule;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LostPasswordAndTokenTaskServiceTest {

    @Mock
    ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Mock
    UserStatusService userStatusService;

    @Mock
    UserService userService;

    @Mock
    CommonEmergencyTaskService taskService;

    LostPasswordAndTokenTaskService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new LostPasswordAndTokenTaskService(serviceAccountAuthorizationService, userStatusService, userService, taskService);
    }

    @Test
    void appliesFor() {
        assertTrue(service.appliesFor().contains(RequestType.LOST_PASSWORD_AND_TOKEN));
    }

    @Test
    void testProtectedRulesForCompleteMethod() throws Exception {
        Method method = service.getClass().getMethod("complete", TokenTaskDetailsDTO.class, TaskOutcome.class, String.class);

        assertTrue(method.isAnnotationPresent(Protected.class));

        Protected protectedAnnotation = method.getAnnotation(Protected.class);
        Optional<Class<? extends AbstractBusinessRule>> fourEyesPrincipleRule = Arrays.stream(protectedAnnotation.value()).filter(annotation ->
                annotation.getName().contains(FourEyesPrincipleRule.class.getName())).findAny();
        assertTrue(fourEyesPrincipleRule.isPresent());

        Optional<Class<? extends AbstractBusinessRule>> onlyRegistryAdminCanCompleteTaskRule = Arrays.stream(protectedAnnotation.value()).filter(annotation ->
                annotation.getName().contains(OnlyRegistryAdminCanCompleteTaskRule.class.getName())).findAny();
        assertTrue(onlyRegistryAdminCanCompleteTaskRule.isPresent());
    }
}
