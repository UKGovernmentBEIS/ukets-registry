package gov.uk.ets.registry.api.user.profile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.authz.ruleengine.Protected;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.AnyAdminRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.task.rules.complete.FourEyesPrincipleRule;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.security.GenerateTokenCommand;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.common.security.UsedTokenService;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.api.user.admin.service.UserStatusService;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusChangeDTO;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

class TokenTaskServiceTest {

    TokenTaskService service;

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserService userService;
    
    @Mock
    UserStatusService userStatusService;

    @Mock
    TaskEventService taskEventService;

    @Mock
    private Mapper mapper;

    TokenChangeService tokenChangeService;

    TokenVerifier tokenVerifier;

    @Mock
    UsedTokenService usedTokenService;

    @Mock
    ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenVerifier = new TokenVerifier("http://localhost:8080/auth", "uk-ets-web-app", "uk-ets");
        tokenChangeService = mock(TokenChangeService.class);
        service = new TokenTaskService(taskRepository, userService, userStatusService, tokenChangeService, tokenVerifier,
            usedTokenService, serviceAccountAuthorizationService, taskEventService, mapper);
    }

    @Test
    void appliesFor() {
        assertTrue(service.appliesFor().contains(RequestType.CHANGE_TOKEN));
    }

    @Test
    void proposeRequest() {
        when(userService.getCurrentUser()).thenReturn(mock(User.class));
        when(serviceAccountAuthorizationService.getUser(any())).thenReturn(mock(UserRepresentation.class));
        when(serviceAccountAuthorizationService.obtainAccessToken()).thenReturn(mock(AccessTokenResponse.class));
        when(taskRepository.getNextRequestId()).thenReturn(1000L);
        String result = service.proposeRequest("Some reason here");
        assertEquals("1000", result);
    }

    @Test
    void proceed() throws JsonProcessingException {
        String token = tokenVerifier.generateToken(GenerateTokenCommand.builder().payload(new ObjectMapper().writeValueAsString("GR2134234213545"))
                .expiration(10000L).build());
        when(usedTokenService.isTokenAlreadyUsed(token)).thenReturn(false);
        when(userService.getUserByUrid(any())).thenReturn(mock(User.class));
        when(serviceAccountAuthorizationService.obtainAccessToken()).thenReturn(mock(AccessTokenResponse.class));
        service.proceed(token);
        assertThrows(JWTDecodeException.class, () -> service.proceed("invalid token"));
    }

    @Test
    void proceedWithUsedToken() throws JsonProcessingException {
        String token = tokenVerifier.generateToken(GenerateTokenCommand.builder().payload(new ObjectMapper().writeValueAsString("GR2134234213545"))
            .expiration(10000L).build());
        when(usedTokenService.isTokenAlreadyUsed(token)).thenReturn(true);
        Boolean result = service.proceed(token);
        assertTrue(result);
    }

    @Test
    void testProtectedRulesForCompleteMethod() throws Exception {
        Method method = service.getClass().getMethod("complete", TokenTaskDetailsDTO.class, TaskOutcome.class, String.class);

        assertTrue(method.isAnnotationPresent(Protected.class));

        Protected protectedAnnotation = method.getAnnotation(Protected.class);
        Optional<Class<? extends AbstractBusinessRule>> fourEyesPrincipleRule = Arrays.stream(protectedAnnotation.value()).filter(annotation ->
                annotation.getName().contains(FourEyesPrincipleRule.class.getName())).findAny();
        assertTrue(fourEyesPrincipleRule.isPresent());

        Optional<Class<? extends AbstractBusinessRule>> anyAdminRule = Arrays.stream(protectedAnnotation.value()).filter(annotation ->
                annotation.getName().contains(AnyAdminRule.class.getName())).findAny();
        assertTrue(anyAdminRule.isPresent());
    }

    @Test
    void testComplete() {
        // given
        TokenTaskDetailsDTO taskDTO = new TokenTaskDetailsDTO();
        taskDTO.setReferredUserURID("urid");
        taskDTO.setInitiatorUrid("urid");
        taskDTO.setEmail("email");

        User user = new User();
        user.setUrid("urid");
        user.setPreviousState(UserStatus.ENROLLED);

        AccessTokenResponse response = new AccessTokenResponse();
        response.setToken("token");

        when(userService.getUserByUrid("urid")).thenReturn(user);
        when(serviceAccountAuthorizationService.obtainAccessToken()).thenReturn(response);

        // when
        service.complete(taskDTO, TaskOutcome.APPROVED, "comment");

        // then
        verify(userStatusService, times(1)).changeUserStatus(any(UserStatusChangeDTO.class), eq("token"));
    }

}
