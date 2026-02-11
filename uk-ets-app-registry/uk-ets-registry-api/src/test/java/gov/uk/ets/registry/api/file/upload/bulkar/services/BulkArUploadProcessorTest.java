package gov.uk.ets.registry.api.file.upload.bulkar.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import gov.uk.ets.file.upload.services.ClamavService;
import gov.uk.ets.registry.api.account.authz.AccountAuthorizationService;
import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.repository.AccountAccessRepository;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.ar.domain.ARAccountAccessRepository;
import gov.uk.ets.registry.api.ar.domain.ARUpdateActionRepository;
import gov.uk.ets.registry.api.ar.service.AuthorizedRepresentativeService;
import gov.uk.ets.registry.api.ar.service.DTOFactory;
import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.publication.PublicationRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.common.reports.ReportRequestAddRemoveRoleService;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.bulkar.error.BulkArBusinessRulesException;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.dto.BaseType;
import gov.uk.ets.registry.api.file.upload.dto.FileHeaderDto;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import gov.uk.ets.registry.api.file.upload.services.FileUploadService;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArAccountDTO;
import gov.uk.ets.registry.api.file.upload.wrappers.BulkArUserDTO;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import gov.uk.ets.registry.api.user.service.UserService;

@ExtendWith(SpringExtension.class)
public class BulkArUploadProcessorTest {

    @MockitoBean
    private UploadedFilesRepository uploadedFilesRepository;
    @MockitoBean
    private ConversionService conversionService;
    @MockitoBean
    private ClamavService clamavService;
    @MockitoBean
    private AccountRepository accountRepository;
    @MockitoBean
    private ARAccountAccessRepository arAccountAccessRepository;
    @MockitoBean
    private ARUpdateActionRepository arUpdateActionRepository;
    @MockitoBean
    private TaskRepository taskRepository;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private DTOFactory dtoFactory;
    @MockitoBean
    private EventService eventService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private AccountAccessRepository accountAccessRepository;
    @MockitoBean
    private AuthorizationService authorizationService;
    @MockitoBean
    private AccountAuthorizationService accountAuthorizationService;
    @MockitoBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;
    @MockitoBean
    private TaskEventService taskEventService;
    @MockitoBean
    private ReportRequestAddRemoveRoleService reportRequestAddRemoveRoleService;
    @MockitoBean
    private PublicationRequestAddRemoveRoleService publicationRequestAddRemoveRoleService;
    @MockitoBean
    private Mapper mapper;

    private FileUploadService fileUploadService;
    private BulkArUploadProcessor processor;
    private MockMultipartFile file;
    private FileHeaderDto dto;
    private UploadedFile uploadedFile;
    private BulkArAccountDTO bulkArAccountDTO;
    private BulkArUserDTO bulkArUserDTO;
    private User user;
    private Account account;


    @BeforeEach
    void setUp() throws IOException {
        Resource resource = new ClassPathResource("BULK_AR_TEST.xlsx");
        AuthorizedRepresentativeService authorizedRepresentativeService =
            new AuthorizedRepresentativeService(arAccountAccessRepository, arUpdateActionRepository,
                accountRepository, taskRepository,
                userService, dtoFactory, eventService, authorizationService, accountAccessRepository,
                accountAuthorizationService, serviceAccountAuthorizationService, taskEventService,
                reportRequestAddRemoveRoleService, publicationRequestAddRemoveRoleService,
                mapper);
        BulkArExcelFileValidationService bulkArExcelFileValidationService =
            new BulkArExcelFileValidationService(accountRepository, userRepository,
                accountAccessRepository, taskRepository);
        fileUploadService =
            new FileUploadService(uploadedFilesRepository, conversionService, clamavService, eventService, mapper);
        processor = new BulkArUploadProcessor(fileUploadService, bulkArExcelFileValidationService,
            uploadedFilesRepository, authorizedRepresentativeService, taskEventService);
        file = new MockMultipartFile(
            "file",
            resource.getFilename(),
            MediaType.MULTIPART_FORM_DATA_VALUE,
            resource.getInputStream().readAllBytes()
        );
        dto = new FileHeaderDto(1L, file.getOriginalFilename(), BaseType.BULK_AR, ZonedDateTime.now());
        uploadedFile = new UploadedFile();
        uploadedFile.setFileName(file.getOriginalFilename());
        uploadedFile.setFileData(file.getBytes());
        uploadedFile.setId(dto.getId());
        uploadedFile.setCreationDate(LocalDateTime.now());

        bulkArAccountDTO = new BulkArAccountDTO("UK-100-10000082-0-39", "Feel Good Inc.",
            AccountStatus.OPEN, RegistryAccountType.OPERATOR_HOLDING_ACCOUNT,
            KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        bulkArUserDTO = new BulkArUserDTO("UK465857017651", UserStatus.ENROLLED);
        user = new User();
        user.setUrid(bulkArUserDTO.getUrid());
        account = new Account();
        account.setFullIdentifier(bulkArAccountDTO.getFullIdentifier());
    }

    @Test
    void verifyFileIntegrity_successful() {
        given(taskRepository.retrieveTasksByRequestType(any())).willReturn(new ArrayList<>());
        given(fileUploadService.saveFileInDatabase(file)).willReturn(uploadedFile);
        given(accountRepository.retrieveAccounts()).willReturn(Collections.singletonList(bulkArAccountDTO));
        given(userRepository.retrieveUsersByUrid()).willReturn(Collections.singletonList(bulkArUserDTO));
        assertEquals(dto.getFileName(), processor.loadAndVerifyFileIntegrity(file).getFileName());
    }

    @Test
    void verifyFileIntegrity_throwsBulkArBusinessRulesException() {
        given(fileUploadService.saveFileInDatabase(file)).willReturn(uploadedFile);
        given(accountRepository.retrieveAccounts()).willReturn(new ArrayList<>());
        assertThrows(BulkArBusinessRulesException.class, () -> processor.loadAndVerifyFileIntegrity(file));
    }

    @Test
    void submitUploadedFile_successful() {
        given(taskRepository.retrieveTasksByRequestType(any())).willReturn(new ArrayList<>());
        given(uploadedFilesRepository.findById(dto.getId())).willReturn(java.util.Optional.ofNullable(uploadedFile));
        given(userService.getCurrentUser()).willReturn(user);
        given(accountRepository.findByFullIdentifier(bulkArAccountDTO.getFullIdentifier()))
            .willReturn(java.util.Optional.ofNullable(account));
        given(taskRepository.getNextRequestId()).willReturn(1L);
        assertEquals(1L, processor.submitUploadedFile(dto));
    }
}
