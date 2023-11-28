package gov.uk.ets.registry.api.user.messaging.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.messaging.AdminEventRepresentation;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.messaging.OperationType;
import gov.uk.ets.registry.api.user.messaging.ResourceType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({KeycloakAdminRoleModifiedEventHandler.class, KeycloakRoleMappingEventHandler.class, KeycloakEventHelper.class,
        Mapper.class, ObjectMapper.class, EventService.class, DisabledKeycloakUserAdministrationService.class})
class KeycloakAdminRoleModifiedEventHandlerIntegrationTest extends KeycloakEventHandlerTestBase {

    @Autowired
    private KeycloakAdminRoleModifiedEventHandler cut;

    @Autowired
    private KeycloakRoleMappingEventHandler roleMappingEventHandler;

    @Autowired
    private IamUserRoleRepository iamUserRoleRepository;


    @Test
    public void shouldCreateAccountAccesses() throws JsonProcessingException {
        AdminEventRepresentation representation = AdminEventRepresentation.builder()
            .id(TEST_ROLE_ID)
            .build();
        KeycloakEvent event = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.CREATE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(event);

        persistState();

        List<AccountAccess> accountAccesses = accountAccessRepository.findByUser_Urid(TEST_URID);
        assertThat(accountAccesses).hasSizeGreaterThan(0);
        assertThat(accountAccesses).allSatisfy(aa -> {
            assertThat(aa.getRight()).isEqualTo(AccountAccessRight.ROLE_BASED);
            assertThat(aa.getState()).isEqualTo(AccountAccessState.ACTIVE);
        });
    }

    @Test
    public void shouldDeleteAccountAccesses() throws JsonProcessingException {
        // first create all AccessRights by dispatching a create role mapping event:
        AdminEventRepresentation representation = AdminEventRepresentation.builder()
            .id(TEST_ROLE_ID)
            .build();
        KeycloakEvent event = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.CREATE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(event);

        persistState();

        // then delete :

        KeycloakEvent deleteEvent = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.DELETE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(deleteEvent);

        persistState();

        List<AccountAccess> accountAccesses = accountAccessRepository.findByUser_Urid(TEST_URID);

        assertThat(accountAccesses).hasSize(0);
    }


    @Test
    public void shouldNotDeleteAccountAccessesWhenUserHasMultipleAdminRoles() throws JsonProcessingException {

        // first create all AccessRights by dispatching a create role mapping event:
        AdminEventRepresentation representation = AdminEventRepresentation.builder()
            .id(TEST_ROLE_ID)
            .build();
        KeycloakEvent event = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.CREATE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(event);

        persistState();

        // add second admin role for user:
        User user = userRepository.findByIamIdentifier(TEST_USER_IAM_ID);
        IamUserRole juniorAdmin = new IamUserRole();
        juniorAdmin.setIamIdentifier(TEST_ROLE_ID_2);
        juniorAdmin.setRoleName(TEST_ADMIN_ROLE_NAME);
        iamUserRoleRepository.save(juniorAdmin);
        user.addUserRole(juniorAdmin);
        userRepository.save(user);

        persistState();

        // then delete :

        KeycloakEvent deleteEvent = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.DELETE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(deleteEvent);

        persistState();

        List<AccountAccess> accountAccesses = accountAccessRepository.findByUser_Urid(TEST_URID);

        assertThat(accountAccesses).hasSizeGreaterThan(0);
    }

    @Test
    public void shouldNotCreateAccountAccessesWhenUserHasMultipleAdminRoles() throws JsonProcessingException {
        // add admin role for user:
        User user = userRepository.findByIamIdentifier(TEST_USER_IAM_ID);
        IamUserRole juniorAdmin = new IamUserRole();
        juniorAdmin.setIamIdentifier(TEST_ROLE_ID_2);
        juniorAdmin.setRoleName(TEST_ADMIN_ROLE_NAME);
        iamUserRoleRepository.save(juniorAdmin);
        user.addUserRole(juniorAdmin);
        userRepository.save(user);

        persistState();

        AdminEventRepresentation representation = AdminEventRepresentation.builder()
            .id(TEST_ROLE_ID)
            .build();
        KeycloakEvent event = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.CREATE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(event);

        persistState();

        List<AccountAccess> accountAccesses = accountAccessRepository.findByUser_Urid(TEST_URID);

        assertThat(accountAccesses).hasSize(0);
    }
}
