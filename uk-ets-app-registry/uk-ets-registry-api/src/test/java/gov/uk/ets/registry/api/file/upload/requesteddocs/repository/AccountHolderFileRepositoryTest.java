package gov.uk.ets.registry.api.file.upload.requesteddocs.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.AccountHolderFile;
import gov.uk.ets.registry.api.task.domain.Task;
import java.io.IOException;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create"})
class AccountHolderFileRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    AccountHolderFileRepository accountHolderFileRepository;

    private Long fileId;
    private UploadedFile uploadedFile;
    private AccountHolder holder;

    @BeforeEach
    void setUp() throws IOException {
        Task task = new Task();
        task.setRequestId(100001L);
        entityManager.persist(task);
        uploadedFile = new UploadedFile();
        uploadedFile.setTask(task);
        uploadedFile.setFileName("Test Document Name");
        entityManager.persist(uploadedFile);
        fileId = uploadedFile.getId();
        AccountHolder accountHolder = new AccountHolder();
        entityManager.persist(accountHolder);
        holder = accountHolder;
    }

    @Test
    void testInsertAccountHolderFile() {
        accountHolderFileRepository.insertAccountHolderFile(fileId, "test", holder.getId());
        Optional<AccountHolderFile> byId = accountHolderFileRepository.findById(fileId);
        assertEquals(fileId, byId.get().getId());

    }
}
