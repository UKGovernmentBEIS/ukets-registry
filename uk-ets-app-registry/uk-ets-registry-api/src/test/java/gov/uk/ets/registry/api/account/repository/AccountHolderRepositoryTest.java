package gov.uk.ets.registry.api.account.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.ImmutableMap;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountAccess;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountAccessState;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.accountholder.web.model.AccountHolderTypeAheadSearchResultDTO;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.AccountHolderFile;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
class AccountHolderRepositoryTest {
    public static final String TEST_URID = "UK12345678";
    public static final String TEST_URID_2 = "UK98745321";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    public void setUp() throws Exception {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(10000036L);
        accountHolder.setType(AccountHolderType.ORGANISATION);
        accountHolder.setName("Organisation 1");
        entityManager.persist(accountHolder);

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

        AccountHolderFile accountHolderFile = new AccountHolderFile();
        accountHolderFile.setDocumentName("Proof of identity");
        accountHolderFile.setAccountHolder(accountHolder);
        accountHolderFile.setUploadedFile(file);
        entityManager.persist(accountHolderFile);

        AccountHolderFile accountHolderFile2 = new AccountHolderFile();
        accountHolderFile2.setDocumentName("Proof of residence");
        accountHolderFile2.setAccountHolder(accountHolder);
        accountHolderFile2.setUploadedFile(file2);
        entityManager.persist(accountHolderFile2);

        Account account1 = new Account();
        account1.setAccountName("account1");
        account1.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account1.setAccountHolder(accountHolder);
        Account account2 = new Account();
        account2.setAccountName("account2");
        account2.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account2.setAccountHolder(accountHolder);
        Account account3 = new Account();
        account3.setAccountName("account2");
        account3.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account3.setAccountHolder(accountHolder);
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.persist(account3);

        User user = new User();
        user.setUrid(TEST_URID);
        entityManager.persist(user);

        AccountAccess access1 = new AccountAccess();
        access1.setUser(user);
        access1.setState(AccountAccessState.ACTIVE);
        access1.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access1.setAccount(account1);
        entityManager.persist(access1);

        AccountHolder accountHolder2 = new AccountHolder();
        accountHolder2.setType(AccountHolderType.INDIVIDUAL);
        accountHolder2.setFirstName("Firstname");
        accountHolder2.setLastName("Lastname");
        entityManager.persist(accountHolder2);

        Account account4 = new Account();
        account4.setAccountName("account4");
        account4.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account4.setAccountHolder(accountHolder2);
        entityManager.persist(account4);

        AccountAccess access2 = new AccountAccess();
        access2.setUser(user);
        access2.setState(AccountAccessState.ACTIVE);
        access2.setRight(AccountAccessRight.INITIATE_AND_APPROVE);
        access2.setAccount(account4);
        entityManager.persist(access2);

        // additions for checking the ROLE_BASED access right:
        AccountHolder accountHolder3 = new AccountHolder();
        accountHolder3.setType(AccountHolderType.ORGANISATION);
        accountHolder3.setIdentifier(10000038L);
        accountHolder3.setName("Org 3");
        entityManager.persist(accountHolder3);

        Account account5 = new Account();
        account5.setAccountName("account5");
        account5.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account5.setAccountHolder(accountHolder3);
        entityManager.persist(account5);

        AccountAccess access3 = new AccountAccess();
        access3.setUser(user);
        access3.setState(AccountAccessState.ACTIVE);
        access3.setRight(AccountAccessRight.ROLE_BASED);
        access3.setAccount(account5);
        entityManager.persist(access3);

        // account for checking query with no urid
        User user2 = new User();
        user2.setUrid(TEST_URID_2);
        entityManager.persist(user2);
        AccountHolder accountHolder4 = new AccountHolder();
        accountHolder4.setType(AccountHolderType.ORGANISATION);
        accountHolder4.setIdentifier(10000038L);
        accountHolder4.setName("Org 4");
        entityManager.persist(accountHolder4);

        Account account6 = new Account();
        account6.setAccountName("account6");
        account6.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account6.setAccountHolder(accountHolder3);
        entityManager.persist(account6);

        AccountAccess access4 = new AccountAccess();
        access3.setUser(user2);
        access3.setState(AccountAccessState.ACTIVE);
        access3.setRight(AccountAccessRight.READ_ONLY);
        access3.setAccount(account6);
        entityManager.persist(access4);
    }

    @Test
    void getAccountHoldersByIdentifier() {
        List<AccountHolderTypeAheadSearchResultDTO> holders =
            accountHolderRepository.getAccountHolders(Long.toString(10000036),
                EnumSet.of(AccountHolderType.ORGANISATION, AccountHolderType.INDIVIDUAL));
        assertEquals(1L, holders.size());
    }
    
    @Test
    void findAccountHoldersByNameAndType() {
        List<AccountHolder> holders =
            accountHolderRepository.findByNameAndType("Org 4",AccountHolderType.ORGANISATION);
        assertEquals(1L, holders.size());
    }

    @Test
    void getAccountHoldersByUrid_organization() {
        List<AccountHolderDTO> holders =
            accountHolderRepository.getAccountHolders(TEST_URID, AccountHolderType.ORGANISATION,
                AccountAccessState.ACTIVE);
        assertEquals(1L, holders.size());
        assertEquals("Organisation 1", holders.get(0).actualName());
    }

    @Test
    @DisplayName("Get ORGANISATION account holders without URID")
    void getAccountHoldersWithoutUridOrganization() {
        var holders2 = accountHolderRepository.getAccountHolders(
            null, AccountHolderType.ORGANISATION, AccountAccessState.ACTIVE
        );
        Assertions.assertEquals(2L, holders2.size());
    }

    @Test
    void getAccountHoldersByUrid_individual() {
        List<AccountHolderDTO> holders =
            accountHolderRepository.getAccountHolders(TEST_URID, AccountHolderType.INDIVIDUAL,
                AccountAccessState.ACTIVE);
        assertEquals(1L, holders.size());
        assertEquals("Firstname Lastname", holders.get(0).actualName());
    }

    @Test
    void test_getAccountHolderFiles() {
        List<AccountHolderFileDTO> files = accountHolderRepository.getAccountHolderFiles(10000036L);
        assertEquals(1L, files.size());
        assertEquals("Sample_document.doc", files.get(0).getName());
    }

    @Test
    void testIsAccountHolderOwnerOfMultipleAccount() {

        List<Account> accountHolderAccountsCount =
            accountRepository.getAccountsByAccountHolder_IdentifierEquals(10000036L);
        Map<RegistryAccountType, Long> actualAccountTypeIntegerMap = accountHolderAccountsCount
            .stream()
            .collect(Collectors.groupingBy(Account::getRegistryAccountType, Collectors.counting()));
        Map<RegistryAccountType, Long> expectedAccountTypeIntegerMap =
            ImmutableMap.of(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT, 1L,
                RegistryAccountType.OPERATOR_HOLDING_ACCOUNT, 2L);

        assertEquals(expectedAccountTypeIntegerMap, actualAccountTypeIntegerMap);
        assertTrue(isAccountHolderOfMultipleAccounts(accountHolderAccountsCount));
    }

    private boolean isAccountHolderOfMultipleAccounts(List<Account> accounts) {
        return accounts.size() > 1;
    }
}
