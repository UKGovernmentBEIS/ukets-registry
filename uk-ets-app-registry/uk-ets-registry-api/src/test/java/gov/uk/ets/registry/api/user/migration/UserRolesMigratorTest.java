package gov.uk.ets.registry.api.user.migration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.user.admin.service.UserAdministrationService;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@PostgresJpaTest
@Import({UserRolesMigrator.class})
@Disabled("TODO: fails only on jenkins")
class UserRolesMigratorTest {

    private static final String TEST_IAM_ID_1 = "12-12-12-233-123";
    private static final String TEST_IAM_ID_2 = "12-12-12-233-124";
    private static final String TEST_ROLE_ID_1 = "test-role-id-1";
    private static final String TEST_ROLE_ID_2 = "test-role-id-2";
    private static final String TEST_ROLE_NAME_1 = "role-1";
    private static final String TEST_ROLE_NAME_2 = "role-2";

    @Autowired
    IamUserRoleRepository iamUserRoleRepository;
    @Autowired
    UserRepository userRepository;

    @MockBean
    private UserAdministrationService userAdministrationService;
    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Autowired
    UserRolesMigrator cut;

    User user1;
    User user2;

    @BeforeEach
    public void setUp() {

        user1 = new User();
        user1.setIamIdentifier(TEST_IAM_ID_1);
        user1.setUrid("urid-1");
        user1.setState(UserStatus.ENROLLED);
        user2 = new User();
        user2.setIamIdentifier(TEST_IAM_ID_2);
        user2.setUrid("urid-2");
        user2.setState(UserStatus.ENROLLED);

        userRepository.saveAll(List.of(user1, user2));

        RoleRepresentation roleRepresentation1 = new RoleRepresentation();
        roleRepresentation1.setId(TEST_ROLE_ID_1);
        roleRepresentation1.setName(TEST_ROLE_NAME_1);
        RoleRepresentation roleRepresentation2 = new RoleRepresentation();
        roleRepresentation2.setId(TEST_ROLE_ID_2);
        roleRepresentation2.setName(TEST_ROLE_NAME_2);


        when(userAdministrationService.getClientRoles())
            .thenReturn(List.of(roleRepresentation1, roleRepresentation2));
        UserRepresentation userRepresentation1 = new UserRepresentation();
        userRepresentation1.setId(TEST_IAM_ID_1);

        UserRepresentation userRepresentation2 = new UserRepresentation();
        userRepresentation2.setId(TEST_IAM_ID_2);

        when(serviceAccountAuthorizationService.getUser(TEST_IAM_ID_1)).thenReturn(userRepresentation1);
        when(serviceAccountAuthorizationService.getUser(TEST_IAM_ID_2)).thenReturn(userRepresentation2);

        RoleRepresentation user1Role1 = new RoleRepresentation();
        user1Role1.setId(TEST_ROLE_ID_1);
        RoleRepresentation user2Role1 = new RoleRepresentation();
        user2Role1.setId(TEST_ROLE_ID_1);
        RoleRepresentation user2Role2 = new RoleRepresentation();
        user2Role2.setId(TEST_ROLE_ID_2);
        when(userAdministrationService.getUserClientRoles(TEST_IAM_ID_1)).thenReturn(List.of(user1Role1));
        when(userAdministrationService.getUserClientRoles(TEST_IAM_ID_2)).thenReturn(List.of(user2Role1, user2Role2));
    }

    @Test
    public void shouldSaveKeycloakRolesAndUserRoleMappings() {

        cut.migrate();

        List<IamUserRole> iamUserRoles = iamUserRoleRepository.findAll();

        assertThat(iamUserRoles).hasSize(2);
        assertThat(iamUserRoles).extracting(IamUserRole::getIamIdentifier).containsOnly(TEST_ROLE_ID_1, TEST_ROLE_ID_2);
        assertThat(iamUserRoles).extracting(IamUserRole::getRoleName).containsOnly(TEST_ROLE_NAME_1, TEST_ROLE_NAME_2);

        User user1 = userRepository.findByIamIdentifier(TEST_IAM_ID_1);
        User user2 = userRepository.findByIamIdentifier(TEST_IAM_ID_2);

        assertThat(user1.getUserRoles()).hasSize(1);
        assertThat(user1.getUserRoles()).extracting(UserRoleMapping::getRole).extracting(IamUserRole::getIamIdentifier)
            .containsOnly(TEST_ROLE_ID_1);

        assertThat(user2.getUserRoles()).hasSize(2);
        assertThat(user2.getUserRoles()).extracting(UserRoleMapping::getRole).extracting(IamUserRole::getIamIdentifier)
            .containsOnly(TEST_ROLE_ID_1, TEST_ROLE_ID_2);
    }

    @Test
    public void shouldSkipSavingRolesAndMappingsIfAlreadyPresent() {

        // due to an open bug:
        // https://github.com/spring-projects/spring-boot/issues/7033
        // we cannot use @SpyBean in spring data repository so this is the workaround:
        IamUserRoleRepository mockIamUserRoleRepository =
            Mockito.mock(IamUserRoleRepository.class, AdditionalAnswers.delegatesTo(iamUserRoleRepository));

        IamUserRole role1 = new IamUserRole(TEST_ROLE_ID_1, TEST_ROLE_NAME_1);
        IamUserRole role2 = new IamUserRole(TEST_ROLE_ID_2, TEST_ROLE_NAME_2);

        iamUserRoleRepository.saveAll(List.of(role1, role2));

        user1.addUserRole(role1);

        user2.addUserRole(role1);
        user2.addUserRole(role2);

        cut.migrate();

        verify(userAdministrationService, times(0)).getClientRoles();
        verify(mockIamUserRoleRepository, times(0)).save(any());

    }


}
