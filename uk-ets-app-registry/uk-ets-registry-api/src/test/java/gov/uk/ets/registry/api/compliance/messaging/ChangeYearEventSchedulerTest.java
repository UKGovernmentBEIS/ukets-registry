package gov.uk.ets.registry.api.compliance.messaging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.event.service.EventService;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.repository.EmissionsEntryRepository;
import gov.uk.ets.registry.api.task.domain.types.EventType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import net.javacrumbs.shedlock.core.LockAssert;

/**
 * Note that this does not test the scheduler but rather the logic inside the
 * execute method.
 * 
 * @author P35036
 *
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Change Year Event for Compliance Status tests")
@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
class ChangeYearEventSchedulerTest {

    private ChangeYearEventScheduler changeYearEventScheduler;

    @Mock
    private ComplianceEventService service;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private EmissionsEntryRepository emissionsEntryRepository;
    @Autowired
    private ExcludeEmissionsRepository excludeEmissionsRepository;
    @Mock
    private EventService eventService;

    private Account account;
    private CompliantEntity compliantEntity;
    private Account accountWithEmissions;
    private CompliantEntity compliantEntityWithEmissions;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // We need this due to LockAssert.assertLocked() inside the execute
        LockAssert.TestHelper.makeAllAssertsPass(true);
        changeYearEventScheduler = new ChangeYearEventScheduler(service,
                accountRepository, emissionsEntryRepository,
                excludeEmissionsRepository, eventService);

        account = new Account();
        account.setIdentifier(10001L);
        account.setFullIdentifier("UK-100-10001-325");
        account.setAccountName("Test-Account-1");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setComplianceStatus(ComplianceStatus.A);

        compliantEntity = new Installation();
        compliantEntity.setAccount(account);
        compliantEntity.setIdentifier(123L);
        compliantEntity.setRegulator(RegulatorType.EA);

        account.setCompliantEntity(compliantEntity);

        accountWithEmissions = new Account();
        accountWithEmissions.setIdentifier(10002L);
        accountWithEmissions.setFullIdentifier("UK-100-10002-785");
        accountWithEmissions.setAccountName("Test-Account-2");
        accountWithEmissions.setAccountStatus(AccountStatus.OPEN);
        accountWithEmissions.setComplianceStatus(ComplianceStatus.B);

        compliantEntityWithEmissions = new AircraftOperator();
        compliantEntityWithEmissions.setAccount(accountWithEmissions);
        compliantEntityWithEmissions.setIdentifier(222L);
        compliantEntityWithEmissions.setRegulator(RegulatorType.OPRED);

        accountWithEmissions.setCompliantEntity(compliantEntityWithEmissions);
    }

    @Order(10)
    @Test
    @DisplayName("Roll over with no entry for year of exclusion , start year of verified emissions = lastYear and empty last year of verified emissions")
    void rollOver1() {
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear);
        // compliantEntity.setEndYear(null);
        ExcludeEmissionsEntry lastYearEntry = new ExcludeEmissionsEntry();
        lastYearEntry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
        lastYearEntry.setExcluded(true);
        lastYearEntry.setLastUpdated(new Date());
        lastYearEntry.setYear(Integer.toUnsignedLong(lastYear));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(lastYear))).thenReturn(List.of());
        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account));
        excludeEmissionsRepository.saveAll(List.of(lastYearEntry));
        excludeEmissionsRepository.flush();
        
        //Run the Scheduler
        changeYearEventScheduler.execute();
        
        assertTrue(excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(currentYear)).isExcluded());
    }

    @Order(20)
    @Test
    @DisplayName("Roll over with entry for year of exclusion , start year of verified emissions = lastYear and empty last year of verified emissions")
    void rollOver2() {
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear);
        // compliantEntity.setEndYear(null);
        ExcludeEmissionsEntry lastYearEntry = new ExcludeEmissionsEntry();
        lastYearEntry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
        lastYearEntry.setExcluded(true);
        lastYearEntry.setLastUpdated(new Date());
        lastYearEntry.setYear(Integer.toUnsignedLong(lastYear));
        ExcludeEmissionsEntry currentYearEntry = new ExcludeEmissionsEntry();
        currentYearEntry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
        currentYearEntry.setExcluded(false);
        currentYearEntry.setLastUpdated(new Date());
        currentYearEntry.setYear(Integer.toUnsignedLong(currentYear));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(lastYear))).thenReturn(List.of());
        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account));
        excludeEmissionsRepository
                .saveAll(List.of(lastYearEntry, currentYearEntry));
        excludeEmissionsRepository.flush();
        changeYearEventScheduler.execute();
        assertTrue(excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(currentYear)).isExcluded());
    }

    @Order(30)
    @Test
    @DisplayName("Roll over should not happen when start year of verified emissions > lastYear")
    void rollOver3() {
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(currentYear);
        // compliantEntity.setEndYear(null);
        ExcludeEmissionsEntry lastYearEntry = new ExcludeEmissionsEntry();
        lastYearEntry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
        lastYearEntry.setExcluded(false);
        lastYearEntry.setLastUpdated(new Date());
        lastYearEntry.setYear(Integer.toUnsignedLong(lastYear));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(lastYear))).thenReturn(List.of());
        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account));
        excludeEmissionsRepository.saveAll(List.of(lastYearEntry));
        excludeEmissionsRepository.flush();
        changeYearEventScheduler.execute();
        assertNull(excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(currentYear)));
    }

    @Order(40)
    @Test
    @DisplayName("Roll over should not insert an entry when no emissions uploaded")
    void rollOver4() {
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear);
        // compliantEntity.setEndYear(null);
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(lastYear))).thenReturn(List.of());
        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account));
        changeYearEventScheduler.execute();
        assertFalse(Optional.ofNullable(
                excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                        account.getCompliantEntity().getIdentifier(),
                        Integer.toUnsignedLong(lastYear)))
                .isPresent());
    }
    
    @Order(60)
    @Test
    @DisplayName("Roll over should not happen when end year of verified emissions = lastYear")
    void rollOver6() {
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear - 4);
        compliantEntity.setEndYear(lastYear);
        ExcludeEmissionsEntry lastYearEntry = new ExcludeEmissionsEntry();
        lastYearEntry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
        lastYearEntry.setExcluded(true);
        lastYearEntry.setLastUpdated(new Date());
        lastYearEntry.setYear(Integer.toUnsignedLong(lastYear));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(lastYear))).thenReturn(List.of());
        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account));
        excludeEmissionsRepository.saveAll(List.of(lastYearEntry));
        excludeEmissionsRepository.flush();
        changeYearEventScheduler.execute();
        assertFalse(Optional.ofNullable(
                excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                        account.getCompliantEntity().getIdentifier(),
                        Integer.toUnsignedLong(currentYear)))
                .isPresent());
    }

    @Order(70)
    @Test
    @DisplayName("Roll over should succeed for account with no emissions and fail for account with emissions")
    void rollOver7() {
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear);
        // compliantEntity.setEndYear(null);
        ExcludeEmissionsEntry lastYearEntry = new ExcludeEmissionsEntry();
        lastYearEntry.setCompliantEntityId(
                account.getCompliantEntity().getIdentifier());
        lastYearEntry.setExcluded(true);
        lastYearEntry.setLastUpdated(new Date());
        lastYearEntry.setYear(Integer.toUnsignedLong(lastYear));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(lastYear))).thenReturn(List.of());
        
        // Account with emissions
        ExcludeEmissionsEntry accountWithEmissionsExcludeEntry = new ExcludeEmissionsEntry();
        accountWithEmissionsExcludeEntry.setCompliantEntityId(
                accountWithEmissions.getCompliantEntity().getIdentifier());
        accountWithEmissionsExcludeEntry.setExcluded(true);
        accountWithEmissionsExcludeEntry.setLastUpdated(new Date());
        accountWithEmissionsExcludeEntry
                .setYear(Integer.toUnsignedLong(lastYear));
        compliantEntityWithEmissions.setStartYear(lastYear);
        // compliantEntityWithEmissions.setEndYear(null);
        EmissionsEntry emissions = new EmissionsEntry();
        emissions.setEmissions(150L);
        emissions.setCompliantEntityId(
                accountWithEmissions.getCompliantEntity().getIdentifier());
        emissions.setYear(Integer.toUnsignedLong(currentYear));
        Mockito.when(emissionsEntryRepository.findAllByCompliantEntityIdAndYear(
                accountWithEmissions.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(currentYear)))
                .thenReturn(List.of(emissions));

        Mockito.when(
                accountRepository.findActiveAccountsWithCompliantEntities())
                .thenReturn(List.of(account, accountWithEmissions));
        excludeEmissionsRepository.saveAll(
                List.of(lastYearEntry, accountWithEmissionsExcludeEntry));
        excludeEmissionsRepository.flush();
        
        // Run the scheduler
        changeYearEventScheduler.execute();

        // Verify account with no emissions
        assertTrue(excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                account.getCompliantEntity().getIdentifier(),
                Integer.toUnsignedLong(currentYear)).isExcluded());

        // Verify account with emissions
        assertFalse(Optional.ofNullable(
                excludeEmissionsRepository.findByCompliantEntityIdAndYear(
                        accountWithEmissions.getCompliantEntity()
                                .getIdentifier(),
                        Integer.toUnsignedLong(currentYear)))
                .isPresent());

        ArgumentCaptor<String> identifier = ArgumentCaptor
                .forClass(String.class);
        ArgumentCaptor<String> urid = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> description = ArgumentCaptor
                .forClass(String.class);
        ArgumentCaptor<EventType> eventType = ArgumentCaptor
                .forClass(EventType.class);
        ArgumentCaptor<String> action = ArgumentCaptor.forClass(String.class);

        verify(eventService, Mockito.times(1)).createAndPublishEvent(
                identifier.capture(), urid.capture(), description.capture(),
                eventType.capture(), action.capture());
        assertEquals(Long.toString(
                accountWithEmissions.getCompliantEntity().getIdentifier()),
                identifier.getValue());
        assertEquals(EventType.COMPLIANT_ENTITY_ROLL_OVER_FAILURE,
                eventType.getValue());        
        assertEquals(String.format("Year: %d, Emissions uploaded: %d",currentYear,150),
                description.getValue());
        assertEquals("Exclusion status roll-over failed due to already uploaded emissions",
                action.getValue());
    }
}
