package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.UserFile;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.user.domain.IamUserRole;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserRole;
import gov.uk.ets.registry.api.user.domain.UserRoleMapping;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create"
})
public class UserRepositoryTest {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
        User user = new User();
        user.setUrid("UK12345");
        user.setFirstName("Thomas");
        user.setLastName("Mason");
        user.setState(UserStatus.ENROLLED);
        entityManager.persist(user);

        UploadedFile file = new UploadedFile();
        file.setFileName("Sample_document.doc");
        file.setFileSize("1 kb");
        file.setCreationDate(LocalDateTime.now());
        file.setFileData(null);
        file.setFileStatus(FileStatus.SUBMITTED);
        entityManager.persist(file);

        UploadedFile file2 = new UploadedFile();
        file2.setFileName("Sample_document_2.doc");
        file2.setFileSize("1 kb");
        file2.setCreationDate(LocalDateTime.now());
        file2.setFileData(null);
        file2.setFileStatus(FileStatus.NOT_SUBMITTED);
        entityManager.persist(file2);

        UserFile userFile = new UserFile();
        userFile.setDocumentName("Proof of identity");
        userFile.setUser(user);
        userFile.setUploadedFile(file);
        entityManager.persist(userFile);

        UserFile userFile2 = new UserFile();
        userFile2.setDocumentName("Proof of residence");
        userFile2.setUser(user);
        userFile2.setUploadedFile(file2);
        entityManager.persist(userFile2);
    }

    @Test
    public void test_getUsersByNameStartingWith() {
        List<User> users = userRepository.getUsersByNameStartingWith("Tho");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUrid()).isEqualTo("UK12345");

        users = userRepository.getUsersByNameStartingWith("Mas");
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getUrid()).isEqualTo("UK12345");

        User persistedUser = users.get(0);

        users = userRepository.getUsersByNameStartingWith("none");
        assertThat(users).isEmpty();

        persistedUser.setState(UserStatus.REGISTERED);
        entityManager.persist(persistedUser);

        users = userRepository.getUsersByNameStartingWith("Tho");
        assertThat(users).isEmpty();
    }

    @Test
    public void test_getUserFiles() {
        List<UserFileDTO> files = userRepository.getUserFiles("UK12345");
        Assertions.assertEquals(1L, files.size());
        Assertions.assertEquals("Sample_document.doc", files.get(0).getName());
    }

    @Test
    public void test_findUsersByStatusAndRoleExcludingReportsUser() {

        final String REPORTS_ROLE_NAME = "reports-user";
        final String I_AM_IDENTIFIER = "IAM999";
        final String USER_URID = "UK99999";
        final String I_AM_IDENTIFIER2 = "IAM998";
        final String USER_URID2 = "UK99998";

        // Create roles
        IamUserRole adminRole = new IamUserRole("IAM111",
                UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral());
        IamUserRole juniorRole = new IamUserRole("IAM112",
                UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral());
        IamUserRole reportsRole = new IamUserRole("IAM222", REPORTS_ROLE_NAME);

        entityManager.persist(adminRole);
        entityManager.persist(reportsRole);
        entityManager.persist(juniorRole);

        User validUser1 = new User();
        validUser1.setUrid(USER_URID);
        validUser1.setIamIdentifier(I_AM_IDENTIFIER);
        validUser1.setState(UserStatus.ENROLLED);
        entityManager.persist(validUser1);

        UserRoleMapping validMapping = new UserRoleMapping(validUser1, adminRole);
        entityManager.persist(validMapping);

        User validUser2 = new User();
        validUser2.setUrid(USER_URID2);
        validUser2.setIamIdentifier(I_AM_IDENTIFIER2);
        validUser2.setState(UserStatus.ENROLLED);
        entityManager.persist(validUser2);

        UserRoleMapping validMapping2 = new UserRoleMapping(validUser2, juniorRole);
        entityManager.persist(validMapping2);

        User excludedUser = new User();
        excludedUser.setUrid("UK88888");
        excludedUser.setIamIdentifier("IAM888");
        excludedUser.setState(UserStatus.ENROLLED);
        entityManager.persist(excludedUser);

        entityManager.persist(new UserRoleMapping(excludedUser, adminRole));
        entityManager.persist(new UserRoleMapping(excludedUser, reportsRole));

        User wrongStatusUser = new User();
        wrongStatusUser.setUrid("UK77777");
        wrongStatusUser.setIamIdentifier("IAM777");
        wrongStatusUser.setState(UserStatus.REGISTERED);
        entityManager.persist(wrongStatusUser);

        entityManager.persist(new UserRoleMapping(wrongStatusUser, adminRole));

        entityManager.flush();
        entityManager.clear();

        List<UserRoleDetails> results =
                userRepository.findUsersByStatusAndRoleExcludingReportsUser(
                        UserStatus.ENROLLED,
                        List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                                UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral())
                );

        assertThat(results).extracting(UserRoleDetails::getIamIdentifier).containsExactlyInAnyOrder(I_AM_IDENTIFIER, I_AM_IDENTIFIER2);
        assertThat(results).extracting(UserRoleDetails::getUrid).containsExactlyInAnyOrder(USER_URID, USER_URID2);
        assertThat(results).extracting(UserRoleDetails::getRoleName).containsExactlyInAnyOrder(UserRole.SENIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral(),
                UserRole.JUNIOR_REGISTRY_ADMINISTRATOR.getKeycloakLiteral());
        assertThat(results).extracting(UserRoleDetails::getStatus).containsOnly(UserStatus.ENROLLED);

    }
}
