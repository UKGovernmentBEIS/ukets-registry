package gov.uk.ets.registry.api.user.migration;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@PostgresJpaTest
@Import({UserKnownAsAttributeMigrator.class})
public class UserKnownAsAttributeMigratorTest {

    private static final String TEST_IAM_ID_1 = "12-12-12-233-123";
    private static final String TEST_IAM_ID_2 = "12-12-12-233-124";
    private static final String KNOWN_AS = "Test name";

    @Autowired
    UserRepository userRepository;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    
    @Autowired
    UserKnownAsAttributeMigrator migrator;

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
        user2.setState(UserStatus.REGISTERED);

        userRepository.saveAll(List.of(user1, user2));

        UserRepresentation userRepresentation1 = new UserRepresentation();
        userRepresentation1.setId(TEST_IAM_ID_1);
        userRepresentation1.setAttributes(new HashMap<>());
        userRepresentation1.getAttributes().put("alsoKnownAs", Arrays.asList(KNOWN_AS));

        UserRepresentation userRepresentation2 = new UserRepresentation();
        userRepresentation2.setId(TEST_IAM_ID_2);
        // attribute not set for this user
        userRepresentation2.setAttributes(new HashMap<>());

        when(serviceAccountAuthorizationService.getUser(TEST_IAM_ID_1)).thenReturn(userRepresentation1);
        when(serviceAccountAuthorizationService.getUser(TEST_IAM_ID_2)).thenReturn(userRepresentation2);
    }
    
    @Test
    @Disabled("TODO: fails only locally, and only when all tests are executed together")
    public void shouldMigrateKnownAsAttribute() {

        migrator.migrate();

        User user1 = userRepository.findByIamIdentifier(TEST_IAM_ID_1);
        User user2 = userRepository.findByIamIdentifier(TEST_IAM_ID_2);

        assertThat(user1.getKnownAs()).isEqualTo(KNOWN_AS);
        assertThat(user2.getKnownAs()).isNull();
    }
    
    @Test
    public void shouldSkipMigrationIfKnownAsAlreadyPresent() {
        user2.setKnownAs(KNOWN_AS);
        userRepository.save(user2);
        migrator.migrate();
        verify(serviceAccountAuthorizationService, times(0)).getUser(any());

    }
}
