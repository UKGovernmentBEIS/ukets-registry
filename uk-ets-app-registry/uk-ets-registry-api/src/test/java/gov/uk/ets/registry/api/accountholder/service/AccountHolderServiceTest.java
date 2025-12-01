package gov.uk.ets.registry.api.accountholder.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepository;
import gov.uk.ets.registry.api.account.repository.AccountHolderRepresentativeRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.service.AccountConversionService;
import gov.uk.ets.registry.api.account.web.model.AccountHolderFileDTO;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.requesteddocs.domain.AccountHolderFile;
import gov.uk.ets.registry.api.file.upload.requesteddocs.repository.AccountHolderFileRepository;
import gov.uk.ets.registry.api.file.upload.types.FileStatus;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.user.domain.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AuthorizationServiceException;

class AccountHolderServiceTest {

    private AccountHolderService accountHolderService;

    @Mock
    private AccountHolderRepository holderRepository;
    @Mock
    private AccountHolderFileRepository holderFileRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private UploadedFilesRepository uploadedFilesRepository;
    @Mock
    private AccountHolderRepresentativeRepository accountHolderRepresentativeRepository;
    @Mock
    private AccountConversionService accountConversionService;
    @Mock
    private EventService eventService;
    @Mock
    private AuthorizationService authorizationService;

    public AccountHolderServiceTest() {
    }

    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);
        accountHolderService = new AccountHolderService(holderRepository, holderFileRepository, accountRepository, 
        		uploadedFilesRepository, accountHolderRepresentativeRepository, accountConversionService, eventService, authorizationService);
    }

    @Test
    @DisplayName("Check Account Holder file list")
    void test_getAccountHolderFiles() {
        List<AccountHolderFileDTO> files = new ArrayList<>();
        AccountHolderFileDTO file1 =
            new AccountHolderFileDTO(1L, "Filename1", "Proof of identity", LocalDateTime.now());
        files.add(file1);
        when(holderRepository.getAccountHolderFiles(1000036L)).thenReturn(files);

        Account account = new Account();
        account.setIdentifier(10001L);
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setIdentifier(1000036L);
        account.setAccountHolder(accountHolder);
        when(accountRepository.findByIdentifier(10001L)).thenReturn(Optional.of(account));

        List<AccountHolderFileDTO> ahFiles = accountHolderService.getAccountHolderFiles(10001L);
        assertEquals(1, ahFiles.size());
        assertEquals(files.get(0), ahFiles.get(0));
    }

    @Test
    @DisplayName("Check Account Holder file retrieval")
    void test_getFileById() {
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1L);
        uploadedFile.setFileStatus(FileStatus.SUBMITTED);
        uploadedFile.setFileName("Filename");
        when(uploadedFilesRepository.findById(1L)).thenReturn(Optional.of(uploadedFile));

        UploadedFile fileById = accountHolderService.getFileById(1L);
        assertEquals(1L, fileById.getId());
        assertEquals("Filename", fileById.getFileName());
    }
    
    @Test
    @DisplayName("Delete account holder file, file does not exist, expected to fail.")
    void test_deleteAccountHolderFileDoesNotExist() {
    	AccountHolderFile holderFile = new AccountHolderFile();
    	holderFile.setId(1234L);
    	AccountHolder holder = new AccountHolder();
    	holder.setIdentifier(1234L);
        User user = new User();
        user.setUrid("urid");
        when(holderRepository.getAccountHolder(holder.getIdentifier())).thenReturn(holder);
        when(holderFileRepository.findByIdAndAccountHolder(1234L, holder)).thenReturn(null);

        AuthorizationServiceException exception = assertThrows(
        		AuthorizationServiceException.class,
                () -> accountHolderService.deleteAccountHolderFile(holder.getIdentifier(), 1234L));

        assertTrue(exception.getMessage().contains("File not found"));
    }
    
    @Test
    @DisplayName("Delete account holder file, expected to pass.")
    void test_deleteAccountHolderFile() {
        AccountHolderFile holderFile = new AccountHolderFile();
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setId(1234L);
        uploadedFile.setFileName("Test file name");
        Account account = new Account();
        account.setIdentifier(4321L);
        Task task = new Task();
        task.setAccount(account);
        uploadedFile.setTask(task);
        holderFile.setUploadedFile(uploadedFile);
        AccountHolder holder = new AccountHolder();
        holder.setIdentifier(1234L);
        var urid = "urid";
        when(holderRepository.getAccountHolder(holder.getIdentifier())).thenReturn(holder);
        when(holderFileRepository.findByIdAndAccountHolder(1234L, holder)).thenReturn(holderFile);
        when(authorizationService.getUrid()).thenReturn(urid);
        accountHolderService.deleteAccountHolderFile(holder.getIdentifier(), 1234L);
        verify(eventService, Mockito.times(1)).createAndPublishEvent("4321", urid,
        		uploadedFile.getFileName(), EventType.ACCOUNT_HOLDER_DELETE_SUBMITTED_DOCUMENT, 
        		"Account Holder documentation deleted");
    }

    @Test
    void isOrphanedAccountHolder_true() {
        final Long accountHolderIdentifier = 123L;
        final Long accountIdentifier = 456L;
        List<Account> accounts = List.of(createAccount(accountIdentifier));
        when(holderRepository.getAccountHolder(accountHolderIdentifier))
                .thenReturn(createAccountHolder(accountHolderIdentifier, accounts));
        final Boolean actual = accountHolderService.isOrphanedAccountHolder(accountHolderIdentifier, accountIdentifier);
        assertTrue(actual);
    }

    @Test
    void isOrphanedAccountHolder_false() {
        final Long accountHolderIdentifier = 123L;
        final Long accountIdentifier = 456L;
        final Long accountIdentifier2 = 789L;
        List<Account> accounts = List.of(createAccount(accountIdentifier), createAccount(accountIdentifier2));
        when(holderRepository.getAccountHolder(accountHolderIdentifier))
                .thenReturn(createAccountHolder(accountHolderIdentifier, accounts));
        final Boolean actual = accountHolderService.isOrphanedAccountHolder(accountHolderIdentifier, accountIdentifier);
        assertFalse(actual);
    }

    @Test
    void isOrphanedAccountHolder_no_result_true() {
        final Long accountHolderIdentifier = 123L;
        final Long accountIdentifier = 456L;
        when(holderRepository.getAccountHolder(accountHolderIdentifier))
                .thenReturn(null);
        final Boolean actual = accountHolderService.isOrphanedAccountHolder(accountHolderIdentifier, accountIdentifier);
        assertTrue(actual);
    }

    private AccountHolder createAccountHolder(Long accountHolderIdentifier, List<Account> accounts) {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setId(1L);
        accountHolder.setIdentifier(accountHolderIdentifier);
        accountHolder.setAccounts(accounts);
        return accountHolder;
    }

    private Account createAccount(Long accountIdentifier) {
        Account account = new Account();
        account.setId(1L);
        account.setIdentifier(accountIdentifier);
        return account;
    }
}
