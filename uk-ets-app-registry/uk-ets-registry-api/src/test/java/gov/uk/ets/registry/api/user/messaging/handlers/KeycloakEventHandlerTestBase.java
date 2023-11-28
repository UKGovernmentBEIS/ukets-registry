package gov.uk.ets.registry.api.user.messaging.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.IamUserRoleRepository;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Common parent class for keycloak handler tests.
 */
@PostgresJpaTest
public class KeycloakEventHandlerTestBase {

    static final String TEST_ROLE_ID = "test-id";
    static final String TEST_ROLE_ID_2 = "test-id-2";
    static final String TEST_ROLE_ID_3 = "test-id-3";
    static final String TEST_USER_IAM_ID = "3beaad08-0409-4c8e-a99a-526b638a6bad";
    static final String TESTS_RESOURCE_PATH =
        "users/" + TEST_USER_IAM_ID + "/role-mappings/clients/025d76a4-dc1b-4b71-92af-e23f0a864116";
    static final String TEST_ROLE_NAME_1 = UserRole.AUTHORITY_USER.getKeycloakLiteral();
    static final String TEST_ROLE_NAME_2 = UserRole.AUTHORISED_REPRESENTATIVE.getKeycloakLiteral();
    static final String TEST_ROLE_NAME_3 = UserRole.VERIFIER.getKeycloakLiteral();
    static final String TEST_ADMIN_ROLE_NAME = UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral();
    public static final String TEST_URID = "UK12345";

    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    IamUserRoleRepository iamUserRoleRepository;
    @Autowired
    AccountAccessRepository accountAccessRepository;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setUrid(TEST_URID);
        user.setFirstName("Thomas");
        user.setLastName("Mason");
        user.setState(UserStatus.ENROLLED);
        user.setIamIdentifier(TEST_USER_IAM_ID);
        IamUserRole existingRole = new IamUserRole();
        existingRole.setIamIdentifier(TEST_ROLE_ID_2);
        existingRole.setRoleName(TEST_ROLE_NAME_2);
        iamUserRoleRepository.save(existingRole);
        user.addUserRole(existingRole);
        userRepository.save(user);

        IamUserRole role = new IamUserRole();
        role.setIamIdentifier(TEST_ROLE_ID);
        role.setRoleName(TEST_ROLE_NAME_1);

        iamUserRoleRepository.save(role);

        persistState();
    }

    @AfterEach
    public void cleanUp() {
        iamUserRoleRepository.deleteAll();
        accountAccessRepository.deleteInBatch(accountAccessRepository.findByUser_Urid(TEST_URID));
        User user = userRepository.findByIamIdentifier(TEST_USER_IAM_ID);
        userRepository.delete(user);

        persistState();
    }


    /**
     * Only way to have a 'close to real' db interaction in @DataJpaTest is by persisting the state regularly.
     * Otherwise, queries are delayed, transactions rollback after a test, and generally you will see a different behavior
     * comparing with a normal run of the application.
     * See also this:
     * <p>
     * https://josefczech.cz/2020/02/02/datajpatest-testentitymanager-flush-clear/
     */
    void persistState() {
        entityManager.flush();
        entityManager.clear();
    }
}