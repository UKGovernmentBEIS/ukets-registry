package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.UserFile;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

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
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getUrid(), is("UK12345"));

        users = userRepository.getUsersByNameStartingWith("Mas");
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getUrid(), is("UK12345"));

        User persistedUser = users.get(0);

        users = userRepository.getUsersByNameStartingWith("none");
        assertThat(users.size(), is(0));

        persistedUser.setState(UserStatus.REGISTERED);
        entityManager.persist(persistedUser);

        users = userRepository.getUsersByNameStartingWith("Tho");
        assertThat(users.size(), is(0));
    }

    @Test
    public void test_getUserFiles() {
        List<UserFileDTO> files = userRepository.getUserFiles("UK12345");
        assertEquals(1L, files.size());
        assertEquals("Sample_document.doc", files.get(0).getName());
    }
}
