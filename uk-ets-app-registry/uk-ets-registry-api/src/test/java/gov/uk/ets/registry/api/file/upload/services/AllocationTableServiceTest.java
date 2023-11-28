package gov.uk.ets.registry.api.file.upload.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AircraftOperatorRepository;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.repository.InstallationRepository;
import gov.uk.ets.registry.api.allocation.configuration.AllocationConfigurationService;
import gov.uk.ets.registry.api.allocation.data.AllocationClassificationSummary;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.service.AllocationCalculationService;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.file.upload.allocationtable.AllocationTableSummary;
import gov.uk.ets.registry.api.file.upload.allocationtable.services.AllocationTableService;
import gov.uk.ets.registry.api.file.upload.domain.UploadedFile;
import gov.uk.ets.registry.api.file.upload.repository.UploadedFilesRepository;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

class AllocationTableServiceTest {

    @InjectMocks
    private AllocationTableService allocationTableService;

    @Mock
    private UploadedFilesRepository uploadedFilesRepository;

    @Mock
    private AllocationEntryRepository allocationEntryRepository;

    @Mock
    private AllocationYearRepository allocationYearRepository;

    @Mock
    private InstallationRepository installationRepository;

    @Mock
    private AircraftOperatorRepository aircraftOperatorRepository;

    @Mock
    private AllocationStatusRepository allocationStatusRepository;
    
    @Mock
    private CompliantEntityRepository compliantEntityRepository;

    @Mock
    private AllocationConfigurationService allocationConfigurationService;
    
    @Mock
    private AllocationCalculationService allocationCalculationService;

    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void loadAllocationTable() throws IOException {
        Resource resource = new ClassPathResource("UK_NAT_2021_2030_298b78743ceb213f06df2542c191579c_v0.10.xlsx");
        byte[] file = resource.getInputStream().readAllBytes();
        AllocationTableSummary table = allocationTableService.loadAllocationTable(resource.getFilename(), file);
        assertNotNull(table.getAllocations());
        assertEquals(90, table.getAllocations().size());
        assertEquals(AllocationType.NAT, table.getType());

        table.getAllocations().forEach(allocationSummary -> {
            assertNotNull(allocationSummary.getYear());
            assertNotNull(allocationSummary.getIdentifier());
            assertNotNull(allocationSummary.getEntitlement());
        });

        assertNotNull(table.getType());
    }
    
    @Test
    void submitAllocationEntries_verifyCompliantEntityChanges() throws IOException {
        Resource resource = new ClassPathResource("UK_NAT_2021_2030_298b78743ceb213f06df2542c191579c_v0.10.xlsx");
        byte[] file = resource.getInputStream().readAllBytes();
        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileName(resource.getFilename());
        uploadedFile.setFileData(file);
        Mockito.when(uploadedFilesRepository.findFirstByTaskRequestId(1234L)).thenReturn(uploadedFile);
        Mockito.when(installationRepository.getCompliantEntityId(any())).thenReturn(4321L);
        Mockito.when(allocationConfigurationService.getAllocationYear()).thenReturn(2030);
        AllocationClassificationSummary summmary = new AllocationClassificationSummary(4321L, "FULLY_ALLOCATED");
        Mockito.when(allocationCalculationService.calculateAllocationClassification(2030, 4321L)).thenReturn(summmary);
        CompliantEntity entity = new Installation();
        entity.setId(4321L);
        Mockito.when(compliantEntityRepository.findById(4321L)).thenReturn(Optional.of(entity));
        allocationTableService.submitAllocationEntries(1234L);
           
        ArgumentCaptor<CompliantEntity> argument = ArgumentCaptor.forClass(CompliantEntity.class);
        verify(compliantEntityRepository, Mockito.atLeast(1)).save(argument.capture());
        assertTrue(argument.getValue().getAllocationWithholdStatus().equals(AllocationStatusType.ALLOWED));
        assertTrue(argument.getValue().getAllocationClassification().equals(AllocationClassification.FULLY_ALLOCATED));
    }
}
