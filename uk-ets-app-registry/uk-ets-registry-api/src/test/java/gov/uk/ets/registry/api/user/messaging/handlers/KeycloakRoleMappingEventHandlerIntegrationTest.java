package gov.uk.ets.registry.api.user.messaging.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import gov.uk.ets.registry.api.user.messaging.AdminEventRepresentation;
import gov.uk.ets.registry.api.user.messaging.KeycloakEvent;
import gov.uk.ets.registry.api.user.messaging.KeycloakEventHelper;
import gov.uk.ets.registry.api.user.messaging.OperationType;
import gov.uk.ets.registry.api.user.messaging.ResourceType;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;


@Import({KeycloakRoleMappingEventHandler.class, KeycloakEventHelper.class, Mapper.class, ObjectMapper.class, EventService.class,
    DisabledKeycloakUserAdministrationService.class})
class KeycloakRoleMappingEventHandlerIntegrationTest extends KeycloakEventHandlerTestBase {

    @Autowired
    KeycloakRoleMappingEventHandler cut;

    @Autowired
    UserRepository userRepository;

    @Test
    public void shouldCreateRoleMapping() throws JsonProcessingException {
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

        Set<UserRoleMapping> userRoles = userRepository.findByIamIdentifier(TEST_USER_IAM_ID).getUserRoles();
        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).extracting(UserRoleMapping::getRole).extracting(IamUserRole::getRoleName)
            .containsOnly(TEST_ROLE_NAME_1, TEST_ROLE_NAME_2);
    }

    @Test
    public void shouldDeleteRoleMapping() throws JsonProcessingException {
        AdminEventRepresentation representation = AdminEventRepresentation.builder()
            .id(TEST_ROLE_ID_2)
            .build();

        KeycloakEvent event = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.DELETE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(event);

        persistState();

        Set<UserRoleMapping> userRoles = userRepository.findByIamIdentifier(TEST_USER_IAM_ID).getUserRoles();
        assertThat(userRoles).hasSize(0);
    }

    @Test
    public void shouldCreateRoleBeforeMappingWhenRoleNotFound() throws JsonProcessingException {

        AdminEventRepresentation representation = AdminEventRepresentation.builder()
            .id(TEST_ROLE_ID_3)
            .name(TEST_ROLE_NAME_3)
            .build();

        KeycloakEvent event = KeycloakEvent.builder()
            .resourceType(ResourceType.CLIENT_ROLE_MAPPING)
            .representation(objectMapper.writeValueAsString(List.of(representation)))
            .operationType(OperationType.CREATE)
            .resourcePath(TESTS_RESOURCE_PATH)
            .build();

        cut.handle(event);

        List<IamUserRole> allRoles = iamUserRoleRepository.findAll();

        assertThat(allRoles).hasSize(3);
        assertThat(allRoles).extracting(IamUserRole::getIamIdentifier)
            .containsOnly(TEST_ROLE_ID, TEST_ROLE_ID_2, TEST_ROLE_ID_3);

        Set<UserRoleMapping> userRoles = userRepository.findByIamIdentifier(TEST_USER_IAM_ID).getUserRoles();
        persistState();

        assertThat(userRoles).hasSize(2);
        assertThat(userRoles).extracting(UserRoleMapping::getRole).extracting(IamUserRole::getRoleName)
            .containsOnly(TEST_ROLE_NAME_2, TEST_ROLE_NAME_3);

    }
}