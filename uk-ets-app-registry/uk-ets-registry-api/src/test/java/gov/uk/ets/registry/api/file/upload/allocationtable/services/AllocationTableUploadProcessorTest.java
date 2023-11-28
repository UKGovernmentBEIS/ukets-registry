package gov.uk.ets.registry.api.file.upload.allocationtable.services;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.repository.AircraftOperatorRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationUtils;
import gov.uk.ets.registry.api.allocation.service.AllocationYearCapService;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.allocationtable.error.AllocationTableUploadActionException;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.file.upload.services.ClamavService;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.data.IssuanceBlockSummary;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class AllocationTableUploadProcessorTest {

    @MockBean
    private UploadedFilesRepository uploadedFilesRepository;
    @MockBean
    private ConversionService conversionService;
    @MockBean
    private ClamavService clamavService;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private InstallationRepository installationRepository;
    @MockBean
    private AircraftOperatorRepository aircraftOperatorRepository;
    @MockBean
    private AllocationYearCapService allocationYearCapService;
    @MockBean
    private AllocationEntryRepository allocationEntryRepository;
    @MockBean
    private PersistenceService persistenceService;
    @MockBean
    private TransactionPersistenceService transactionPersistenceService;
    @MockBean
    private UserService userService;
    @MockBean
    private EventService eventService;
    @MockBean
    private CompliantEntityRepository compliantEntityRepository;
    @MockBean
    private TaskEventService taskEventService;
    @MockBean
    private AllocationUtils allocationUtils;
    @MockBean
    private Mapper mapper;

    private FileUploadService fileUploadService;
    private AllocationTableUploadProcessor processor;
    private MockMultipartFile file;
    private FileHeaderDto dto;
    private UploadedFile uploadedFile;
    private User user;
    private Account account;
    private Set<String> compliantEntityIdentifiers;
    private List<IssuanceBlockSummary> issuanceBlockSummaries;
    private AccountSummary accountSummary;
    private CompliantEntity compliantEntity;

    private static final String FILE_NAME_VALUE = "Error_UK_NAT_2021_2030_298b78743ceb213f06df2542c191579c_v0.10";
    private static final String FILE_CONTENT_VALUE = """
        Row number,Operator ID,BR code,Error message
        0,null,4010,Invalid year - all years must be within the current phase (2021-2030)
        2,1000000,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        3,1000001,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        4,1000002,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        5,1000003,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        6,1000004,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        7,1000005,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        8,1000006,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        9,1000007,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry
        10,1000008,4006,Invalid Installation or Operator ID - the ID does not exist in the Registry""";

    @BeforeEach
    void setUp() throws IOException {
        Resource resource = new ClassPathResource("UK_NAT_2021_2030_298b78743ceb213f06df2542c191579c_v0.10.xlsx");
        AllocationTableExcelFileValidationService allocationTableExcelFileValidationService =
            new AllocationTableExcelFileValidationService(installationRepository,
                aircraftOperatorRepository, allocationYearCapService, allocationEntryRepository,
                compliantEntityRepository, allocationUtils);
        fileUploadService =
            new FileUploadService(uploadedFilesRepository, conversionService, clamavService, eventService, mapper);
        processor = new AllocationTableUploadProcessor(fileUploadService, allocationTableExcelFileValidationService,
            userService, persistenceService, uploadedFilesRepository, eventService,
            transactionPersistenceService, accountRepository, taskEventService, mapper);
        file = new MockMultipartFile(
            "file",
            requireNonNull(resource.getFilename()).substring(0, resource.getFilename().lastIndexOf(".")),
            MediaType.MULTIPART_FORM_DATA_VALUE,
            resource.getInputStream().readAllBytes()
        );
        dto = new FileHeaderDto(1L, file.getOriginalFilename(), BaseType.ALLOCATION_TABLE, ZonedDateTime.now());
        uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setFileData(file.getBytes());
        uploadedFile.setId(dto.getId());
        uploadedFile.setCreationDate(LocalDateTime.now());
        user = new User();
        user.setUrid("UK465857017651");
        account = new Account();
        account.setFullIdentifier("UK-100-10000082-0-39");
        issuanceBlockSummaries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            IssuanceBlockSummary summary = new IssuanceBlockSummary();
            summary.setYear(2021 + i);
            issuanceBlockSummaries.add(summary);
        }
        compliantEntityIdentifiers = new HashSet<>(Arrays.asList("1000000", "1000001", "1000002", "1000003", "1000004",
            "1000005", "1000006", "1000007", "1000008"));
        accountSummary = new AccountSummary();
        accountSummary.setFullIdentifier(account.getFullIdentifier());

        compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2030);
    }

    @Test
    void verifyFileIntegrity_successful() {
        given(fileUploadService.saveFileInDatabase(file)).willReturn(uploadedFile);
        given(installationRepository.findAllInstallations()).willReturn(compliantEntityIdentifiers);
        given(aircraftOperatorRepository.findAllAircraftOperators()).willReturn(compliantEntityIdentifiers);
        given(allocationYearCapService.getCapsForCurrentPhase()).willReturn(issuanceBlockSummaries);
        given(uploadedFilesRepository.findFirstByFileNameContainsIgnoreCaseAndTaskStatus(
            anyString(),
            any())).willReturn(null);

        given(compliantEntityRepository.findByIdentifier(any())).willReturn(Optional.of(compliantEntity));
        assertEquals(dto.getFileName(), processor.loadAndVerifyFileIntegrity(file).getFileName());
    }

    @Test
    void verifyFileIntegrity_throwsAllocationTableBusinessRulesException() {
        given(fileUploadService.saveFileInDatabaseStartNewTransaction(FILE_NAME_VALUE ,FILE_CONTENT_VALUE.getBytes()))
            .willReturn(uploadedFile);
        assertThrows(AllocationTableUploadActionException.class, () -> processor.loadAndVerifyFileIntegrity(file));
    }

    @Test
    void submitUploadedFile_successful() {
        given(uploadedFilesRepository.findById(dto.getId())).willReturn(java.util.Optional.ofNullable(uploadedFile));
        given(userService.getCurrentUser()).willReturn(user);
        given(uploadedFilesRepository.findFirstByFileNameEquals(dto.getFileName())).willReturn(uploadedFile);
        given(transactionPersistenceService.getAccount(any(), any(), any(), any())).willReturn(accountSummary);
        given(accountRepository.findByIdentifier(account.getIdentifier()))
            .willReturn(java.util.Optional.ofNullable(account));
        given(persistenceService.getNextBusinessIdentifier(Task.class)).willReturn(1L);
        assertEquals(1L, processor.submitUploadedFile(dto));
    }
}
