package gov.uk.ets.registry.api.user.migration;

import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.migration.domain.MigratorHistory;
import gov.uk.ets.registry.api.migration.domain.MigratorHistoryRepository;
import gov.uk.ets.registry.api.migration.domain.MigratorName;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@PostgresJpaTest
@Import({UserEmailMigrator.class})
public class UserEmailMigratorTest {


    private static final String TEST_IAM_ID_1 = "12-12-12-233-123";
    private static final String TEST_IAM_ID_2 = "12-12-12-233-124";
    private static final String EMAIL = "test@mail.com";


    @Autowired
    UserRepository userRepository;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @Autowired
    UserEmailMigrator userEmailMigrator;

    @Autowired
    MigratorHistoryRepository migratorHistoryRepository;

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
        userRepresentation1.setEmail(EMAIL);

        UserRepresentation userRepresentation2 = new UserRepresentation();
        userRepresentation2.setId(TEST_IAM_ID_2);

        when(serviceAccountAuthorizationService.getUser(TEST_IAM_ID_1)).thenReturn(userRepresentation1);
        when(serviceAccountAuthorizationService.getUser(TEST_IAM_ID_2)).thenReturn(userRepresentation2);
    }

    @Test
    @Disabled("TODO: fails only locally, and only when all tests are executed together")
    public void shouldMigrateEmailAttribute() {

        userEmailMigrator.migrate();

        User user1 = userRepository.findByIamIdentifier(TEST_IAM_ID_1);
        User user2 = userRepository.findByIamIdentifier(TEST_IAM_ID_2);

        assertThat(user1.getEmail()).isEqualTo(EMAIL);
        assertThat(user2.getEmail()).isNull();
    }

    @Test
    public void shouldSkipMigrationIfKnownAsAlreadyPresent() {
        MigratorHistory migratorHistory = new MigratorHistory();
        migratorHistory.setMigratorName(MigratorName.USER_EMAIL_MIGRATOR);
        migratorHistory.setCreatedOn(LocalDateTime.now());
        migratorHistoryRepository.save(migratorHistory);
        userEmailMigrator.migrate();

        verify(serviceAccountAuthorizationService, times(0)).getUser(any());

    }
}
