package gov.uk.ets.registry.api.allocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.domain.AllocationEntry;
import gov.uk.ets.registry.api.allocation.domain.AllocationStatus;
import gov.uk.ets.registry.api.allocation.domain.AllocationYear;
import gov.uk.ets.registry.api.allocation.repository.AllocationEntryRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationStatusRepository;
import gov.uk.ets.registry.api.allocation.repository.AllocationYearRepository;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.time.LocalDateTime;
import java.util.List;
import net.javacrumbs.shedlock.core.LockAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DisplayName("Allocation Withheld Status roll-over tests")
@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers=true",
    "spring.jpa.properties.hibernate.globally_quoted_identifiers_skip_column_definitions=true"})
class AllocationRollOverSchedulerTest {

    private AllocationRollOverScheduler scheduler;

    @Mock
    private AccountRepository accountRepository;
    @Autowired
    private AllocationEntryRepository allocationEntryRepository;
    @Autowired
    private AllocationStatusRepository allocationStatusRepository;
    @Autowired
    private AllocationYearRepository allocationYearRepository;

    private Account account;
    private CompliantEntity compliantEntity;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        // We need this due to LockAssert.assertLocked() inside the execute
        LockAssert.TestHelper.makeAllAssertsPass(true);

        AllocationStatusService allocationStatusService =
            new AllocationStatusService(allocationEntryRepository, allocationStatusRepository);

        scheduler = new AllocationRollOverScheduler(accountRepository, allocationStatusService);

        account = new Account();
        account.setIdentifier(10001L);
        account.setFullIdentifier("UK-100-10001-325");
        account.setAccountName("Test-Account-1");
        account.setAccountStatus(AccountStatus.OPEN);
        account.setComplianceStatus(ComplianceStatus.A);

        compliantEntity = new Installation();
        compliantEntity.setId(1L);
        compliantEntity.setAccount(account);
        compliantEntity.setIdentifier(123L);
        compliantEntity.setRegulator(RegulatorType.EA);

        account.setCompliantEntity(compliantEntity);
    }

    @Test
    @DisplayName("Roll over should not happen when start year > lastYear")
    void rollOverAllocationStatusSkip() {
        // given
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(currentYear);
        createAllocationStatus(lastYear, AllocationStatusType.WITHHELD);
        createAllocationStatus(currentYear, AllocationStatusType.ALLOWED);

        Mockito.when(accountRepository.findActiveAccountsWithCompliantEntities())
            .thenReturn(List.of(account));

        // when
        scheduler.execute();

        // then
        List<AllocationStatus> statuses = allocationStatusRepository.findAll();
        assertEquals(2, statuses.size());
        AllocationStatus first = statuses.get(0);
        assertEquals(AllocationStatusType.WITHHELD, first.getStatus());
        AllocationStatus second = statuses.get(1);
        assertEquals(AllocationStatusType.ALLOWED, second.getStatus());
    }

    @Test
    @DisplayName("Roll over allocation status should update AllocationStatus entity")
    void rollOverAllocationStatusUpdateCurrentStatus() {
        // given
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear);
        createAllocationStatus(lastYear, AllocationStatusType.WITHHELD);
        createAllocationStatus(currentYear, AllocationStatusType.ALLOWED);

        Mockito.when(accountRepository.findActiveAccountsWithCompliantEntities())
            .thenReturn(List.of(account));

        // when
        scheduler.execute();

        // then
        List<AllocationStatus> statuses = allocationStatusRepository.findAll();
        assertEquals(2, statuses.size());
        AllocationStatus first = statuses.get(0);
        assertEquals(AllocationStatusType.WITHHELD, first.getStatus());
        AllocationStatus second = statuses.get(1);
        assertEquals(AllocationStatusType.WITHHELD, second.getStatus());
    }

    @Test
    @DisplayName("Roll over allocation status should not happen if last status is ALLOWED")
    void rollOverAllocationStatusIgnoreNoWithholdStatus() {
        // given
        int currentYear = LocalDateTime.now().getYear();
        int lastYear = currentYear - 1;
        compliantEntity.setStartYear(lastYear);

        createAllocationStatus(lastYear, AllocationStatusType.ALLOWED);
        createAllocationStatus(currentYear, AllocationStatusType.ALLOWED);

        Mockito.when(accountRepository.findActiveAccountsWithCompliantEntities())
            .thenReturn(List.of(account));

        // when
        scheduler.execute();

        // then
        List<AllocationStatus> statuses = allocationStatusRepository.findAll();
        assertEquals(2, statuses.size());
        AllocationStatus first = statuses.get(0);
        assertEquals(AllocationStatusType.ALLOWED, first.getStatus());
        AllocationStatus second = statuses.get(1);
        assertEquals(AllocationStatusType.ALLOWED, second.getStatus());
    }

    private void createAllocationStatus(int year, AllocationStatusType status) {
        AllocationYear allocationYear = new AllocationYear();
        allocationYear.setYear(year);
        allocationYearRepository.save(allocationYear);
        allocationYearRepository.flush();

        AllocationEntry allocationEntry = new AllocationEntry();
        allocationEntry.setAllocationYear(allocationYearRepository.findByYear(year));
        allocationEntry.setCompliantEntityId(compliantEntity.getId());
        allocationEntryRepository.save(allocationEntry);
        allocationEntryRepository.flush();

        AllocationStatus allocationStatus = new AllocationStatus();
        allocationStatus.setCompliantEntityId(compliantEntity.getId());
        allocationStatus.setStatus(status);
        allocationStatus.setAllocationYear(allocationYear);
        allocationStatusRepository.save(allocationStatus);
        allocationStatusRepository.flush();
    }
}
