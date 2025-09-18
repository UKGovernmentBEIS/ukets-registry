package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.allocation.data.AllocationSummary;
import gov.uk.ets.registry.api.allocation.type.AllocationType;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class FirstYearOfVerifiedEmissionsCheckAllocationRuleTest {

    @Mock
    private Account account;

    @Mock
    private OperatorDTO requestedOperatorUpdate;

    @Mock
    private List<AllocationSummary> allocationEntries;

    private BusinessSecurityStore securityStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(account);
        securityStore.setRequestedOperatorUpdate(requestedOperatorUpdate);
        securityStore.setAllocationEntries(allocationEntries);
    }

    @Test
    void error() {
        ErrorBody errorBody = new FirstYearOfVerifiedEmissionsCheckAllocationRule(securityStore).error();
        assertNotNull(errorBody);
        assertEquals(1, errorBody.getErrorDetails().size());
    }

    @Test
    void permit() {
        Account account = new Account();
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        securityStore.setAccount(account);

        FirstYearOfVerifiedEmissionsCheckAllocationRule rule = new FirstYearOfVerifiedEmissionsCheckAllocationRule(securityStore);
        assertEquals(BusinessRule.Result.NOT_APPLICABLE, rule.permit().getResult());

        account = new Account();
        account.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        account.setKyotoAccountType(KyotoAccountType.PARTY_HOLDING_ACCOUNT);
        Installation installation = new Installation();
        installation.setStartYear(2021);
        installation.setEndYear(2024);
        account.setCompliantEntity(installation);
        securityStore.setAccount(account);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setFirstYear(2022);
        securityStore.setRequestedOperatorUpdate(operatorDTO);

        List<AllocationSummary> allocationEntries = new ArrayList<>();
        securityStore.setAllocationEntries(allocationEntries);

        rule = new FirstYearOfVerifiedEmissionsCheckAllocationRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        allocationEntries = new ArrayList<>();
        AllocationSummary allocationSummary2021 = new AllocationSummary();
        allocationSummary2021.setAllocated(0L);
        allocationSummary2021.setEntitlement(100L);
        allocationSummary2021.setRemaining(0L);
        allocationSummary2021.setYear(2021);
        allocationSummary2021.setType(AllocationType.NAT);
        allocationEntries.add(allocationSummary2021);
        AllocationSummary allocationSummary2022 = new AllocationSummary();
        allocationSummary2022.setAllocated(100L);
        allocationSummary2022.setEntitlement(100L);
        allocationSummary2022.setRemaining(0L);
        allocationSummary2022.setYear(2022);
        allocationSummary2022.setType(AllocationType.NAT);
        allocationEntries.add(allocationSummary2022);
        securityStore.setAllocationEntries(allocationEntries);

        rule = new FirstYearOfVerifiedEmissionsCheckAllocationRule(securityStore);
        assertEquals(BusinessRule.Result.PERMITTED, rule.permit().getResult());

        allocationEntries = new ArrayList<>();
        allocationSummary2021 = new AllocationSummary();
        allocationSummary2021.setAllocated(100L);
        allocationSummary2021.setEntitlement(100L);
        allocationSummary2021.setRemaining(0L);
        allocationSummary2021.setYear(2021);
        allocationSummary2021.setType(AllocationType.NAT);
        allocationEntries.add(allocationSummary2021);
        allocationSummary2022 = new AllocationSummary();
        allocationSummary2022.setAllocated(100L);
        allocationSummary2022.setEntitlement(100L);
        allocationSummary2022.setRemaining(0L);
        allocationSummary2022.setYear(2022);
        allocationSummary2022.setType(AllocationType.NAT);
        allocationEntries.add(allocationSummary2022);
        securityStore.setAllocationEntries(allocationEntries);

        rule = new FirstYearOfVerifiedEmissionsCheckAllocationRule(securityStore);
        assertEquals(BusinessRule.Result.FORBIDDEN, rule.permit().getResult());
    }
}
