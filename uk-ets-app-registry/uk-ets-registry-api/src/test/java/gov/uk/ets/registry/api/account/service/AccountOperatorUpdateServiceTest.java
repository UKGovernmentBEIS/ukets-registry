package gov.uk.ets.registry.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.account.web.model.AccountOperatorDetailsUpdateDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.common.Mapper;
import gov.uk.ets.registry.api.common.model.services.PersistenceService;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.service.ComplianceService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.services.EmissionsTableService;
import gov.uk.ets.registry.api.task.domain.Task;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.service.TaskEventService;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountOperatorUpdateServiceTest {

    private static final Long TEST_COMPLIANT_ENTITY_ID = 12345L;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private PersistenceService persistenceService;
    @Mock
    private UserService userService;
    @Mock
    private TaskEventService taskEventService;
    @Mock
    private EmissionsTableService emissionsTableService;
    @Mock
    private User currentUser;
    @Mock
    private ComplianceService complianceService;
    @Mock
    private Mapper mapper;

    @InjectMocks
    AccountOperatorUpdateService cut;

    Account account;

    @BeforeEach
    public void setup() {
        when(userService.getCurrentUser()).thenReturn(currentUser);

        account = new Account();
        account.setIdentifier(1234L);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setRegistryAccountType(RegistryAccountType.OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        Installation compliantEntity = new Installation();
        compliantEntity.setIdentifier(TEST_COMPLIANT_ENTITY_ID);
        compliantEntity.setStartYear(2021);
        account.setCompliantEntity(compliantEntity);


        when(accountRepository.findByIdentifier(account.getIdentifier())).thenReturn(Optional.of(account));

        when(persistenceService.getNextBusinessIdentifier(Task.class)).thenReturn(10001L);
    }

    @Test
    @DisplayName("Submit Account Operator Update Request.")
    void test_submitAccountOperatorUpdateRequest() {


        AccountOperatorDetailsUpdateDTO dto = populateAccountOperatorDetailsUpdateDTO();

        Long taskIdentifier = cut.submitAccountOperatorUpdateRequest(account.getIdentifier(), dto);
        assertEquals(10001L, taskIdentifier);

        verify(userService, times(1)).getCurrentUser();

        ArgumentCaptor<Object> argument = ArgumentCaptor.forClass(Object.class);
        verify(persistenceService, times(1)).save(argument.capture());

        ArgumentCaptor<Task> argument2 = ArgumentCaptor.forClass(Task.class);
        ArgumentCaptor<String> argument3 = ArgumentCaptor.forClass(String.class);
        verify(taskEventService, times(1))
            .createAndPublishTaskAndAccountRequestEvent(argument2.capture(), argument3.capture());

    }

    @Test
    public void shouldThrowForInvalidFirstYearIfEmissionsExist() {

        AccountOperatorDetailsUpdateDTO dto = populateAccountOperatorDetailsUpdateDTO();

        EmissionsEntry emissionsEntry = new EmissionsEntry();
        emissionsEntry.setId(1L);
        emissionsEntry.setCompliantEntityId(20L);
        emissionsEntry.setEmissions(30000L);
        emissionsEntry.setFilename("emissions.xls");
        emissionsEntry.setYear(2023L);
        
        when(emissionsTableService.findByCompliantEntityIdAndYearBefore(TEST_COMPLIANT_ENTITY_ID,
            2022L)).thenReturn(List.of(emissionsEntry));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> cut.submitAccountOperatorUpdateRequest(account.getIdentifier(), dto));

        assertThat(exception.getMessage()).contains(
            "First year of Verified Emissions Submission cannot be set to a later date");
    }

    @Test
    public void shouldThrowForInvalidFirstYearIfExcludedEmissionsExist() {

        AccountOperatorDetailsUpdateDTO dto = populateAccountOperatorDetailsUpdateDTO();

        when(complianceService.findExcludedEntriesForCompliantEntityForYearBefore(TEST_COMPLIANT_ENTITY_ID,
            2022L)).thenReturn(List.of(new ExcludeEmissionsEntry(12L,1092L,2021L,true,new Date())));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> cut.submitAccountOperatorUpdateRequest(account.getIdentifier(), dto));

        assertThat(exception.getMessage()).contains(
            "First year of Verified Emissions Submission cannot be set to a later date");
    }

    @Test
    public void shouldThrowForInvalidLastYearIfEmissionsExist() {

        AccountOperatorDetailsUpdateDTO dto = populateAccountOperatorDetailsUpdateDTO();

        when(emissionsTableService.findNonEmptyEntriesForCompliantEntityForYearAfter(TEST_COMPLIANT_ENTITY_ID,
            2024L)).thenReturn(List.of(new EmissionsEntry()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> cut.submitAccountOperatorUpdateRequest(account.getIdentifier(), dto));

        assertThat(exception.getMessage()).contains(
            "Last Year of Verified Emission Submission cannot be set to an earlier date ");
    }

    @Test
    public void shouldThrowForInvalidLastYearIfExcludedEmissionsExist() {

        AccountOperatorDetailsUpdateDTO dto = populateAccountOperatorDetailsUpdateDTO();

        when(complianceService.findExcludedEntriesForCompliantEntityForYearAfter(TEST_COMPLIANT_ENTITY_ID,
            2024L)).thenReturn(List.of(new ExcludeEmissionsEntry()));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> cut.submitAccountOperatorUpdateRequest(account.getIdentifier(), dto));

        assertThat(exception.getMessage()).contains(
            "Last Year of Verified Emission Submission cannot be set to an earlier date ");
    }

    @Test
    void testUpdateOperator() {
        Account account = new Account();
        account.setIdentifier(10001L);
        Installation installation = new Installation();
        installation.setIdentifier(1000036L);
        installation.setInstallationName("Installation Name");
        installation.setActivityType("COMBUSTION_OF_FUELS");
        installation.setPermitIdentifier("1234589");
        installation.setPermitExpiryDate(new Date());
        installation.setStartYear(2021);
        installation.setEndYear(2022);
        account.setCompliantEntity(installation);
        when(accountRepository.findByIdentifier(10001L)).thenReturn(Optional.of(account));

        InstallationOrAircraftOperatorDTO dto = new InstallationOrAircraftOperatorDTO();
        dto.setRegulator(RegulatorType.EA);
        dto.setType("INSTALLATION");
        dto.setFirstYear(2022);
        dto.setLastYear(2024);
        dto.setLastYearChanged(true);

        cut.updateOperator(dto, 10001L, RequestType.INSTALLATION_OPERATOR_UPDATE_REQUEST, account);

        ArgumentCaptor<Installation> captor = ArgumentCaptor.forClass(Installation.class);
        then(persistenceService).should(times(1)).save(captor.capture());
    }

    private AccountOperatorDetailsUpdateDTO populateAccountOperatorDetailsUpdateDTO() {
        AccountOperatorDetailsUpdateDTO dto = new AccountOperatorDetailsUpdateDTO();
        InstallationOrAircraftOperatorDTO diff = new InstallationOrAircraftOperatorDTO();
        diff.setFirstYear(2022);
        diff.setLastYear(2024);
        diff.setType("INSTALLATION");
        dto.setDiff(diff);

        InstallationOrAircraftOperatorDTO current = new InstallationOrAircraftOperatorDTO();
        current.setFirstYear(2021);
        current.setType("INSTALLATION");
        dto.setCurrent(current);
        return dto;
    }
}
