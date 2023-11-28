package gov.uk.ets.registry.api.transaction.checks.allocation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import gov.uk.ets.registry.api.allocation.data.AllocationOverview;
import gov.uk.ets.registry.api.allocation.data.AllocationOverviewRow;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.transaction.checks.BusinessCheckContext;
import gov.uk.ets.registry.api.transaction.domain.data.AccountSummary;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.service.AccountHoldingService;
import gov.uk.ets.registry.api.transaction.service.TransactionPersistenceService;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CheckAllocationAccountHasEnoughUnitsTest {

    @InjectMocks
    CheckAllocationAccountHasEnoughUnits check;

    @Mock
    AccountHoldingService holdingService;

    @Mock
    TransactionPersistenceService persistenceService;

    AllocationOverview installationOverview;
    AllocationOverview[] allocationOverviews;

    BusinessCheckContext context;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        AccountSummary natAccountSummary = new AccountSummary();
        natAccountSummary.setIdentifier(12345L);
        when(persistenceService.getAccount(AccountType.UK_ALLOCATION_ACCOUNT, AccountStatus.OPEN)).thenReturn(natAccountSummary);

        AccountSummary nerAccountSummary = new AccountSummary();
        nerAccountSummary.setIdentifier(23456L);
        when(persistenceService.getAccount(AccountType.UK_NEW_ENTRANTS_RESERVE_ACCOUNT, AccountStatus.OPEN)).thenReturn(nerAccountSummary);

        Map<AllocationType, AllocationOverviewRow> installationRows = new HashMap<>();
        Map<AllocationType, AllocationOverviewRow> aviationRows = new HashMap<>();

        AllocationOverviewRow natRow = new AllocationOverviewRow();
        natRow.setQuantity(100_000L);
        installationRows.put(AllocationType.NAT, natRow);

        AllocationOverviewRow nerRow = new AllocationOverviewRow();
        nerRow.setQuantity(100_000L);
        installationRows.put(AllocationType.NER, nerRow);

        AllocationOverviewRow navatRow = new AllocationOverviewRow();
        navatRow.setQuantity(100_000L);
        aviationRows.put(AllocationType.NAVAT, navatRow);

        AllocationOverview aviationOverview = new AllocationOverview();
        aviationOverview.setRows(aviationRows);

        installationOverview = new AllocationOverview();
        installationOverview.setRows(installationRows);
        allocationOverviews = new AllocationOverview[2];
        allocationOverviews[0] = installationOverview;
        allocationOverviews[1] = aviationOverview;
        context = new BusinessCheckContext();
    }

    @Test
    void testUnitsNotEnough() {
        context.store(AllocationOverview[].class.getName(), allocationOverviews);
        when(holdingService.getQuantity(eq(12345L), any())).thenReturn(50_000L);
        when(holdingService.getQuantity(eq(23456L), any())).thenReturn(50_000L);
        check.execute(context);
        assertTrue(context.hasError());
    }

    @Test
    void testUnitsEnough() {
        context.store(AllocationOverview.class.getName(), installationOverview);
        when(holdingService.getQuantity(eq(12345L), any())).thenReturn(1_000_000L);
        when(holdingService.getQuantity(eq(23456L), any())).thenReturn(1_000_000L);
        check.execute(context);
        assertFalse(context.hasError());
    }

    @Test
    void testUnitsExactly() {
        context.store(AllocationOverview[].class.getName(), allocationOverviews);
        when(holdingService.getQuantity(eq(12345L), any())).thenReturn(2_00_000L);
        when(holdingService.getQuantity(eq(23456L), any())).thenReturn(1_00_000L);
        check.execute(context);
        assertFalse(context.hasError());
    }
}