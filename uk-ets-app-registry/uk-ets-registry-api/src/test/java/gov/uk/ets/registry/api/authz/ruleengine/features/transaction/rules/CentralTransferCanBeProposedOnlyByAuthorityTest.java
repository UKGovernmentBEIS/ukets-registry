package gov.uk.ets.registry.api.authz.ruleengine.features.transaction.rules;

import static gov.uk.ets.registry.api.transaction.domain.type.TransactionType.CentralTransferAllowances;
import static gov.uk.ets.registry.api.transaction.domain.type.TransactionType.SurrenderAllowances;
import static gov.uk.ets.registry.api.transaction.domain.type.TransactionType.TransferAllowances;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessRuleRunner;
import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner;
import gov.uk.ets.registry.api.authz.ruleengine.features.BusinessRule;
import gov.uk.ets.registry.api.authz.ruleengine.features.transaction.TransactionBusinessSecurityStoreSlice;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.user.domain.UserRole;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CentralTransferCanBeProposedOnlyByAuthorityTest {

    private Account transferringAccount;
    private BusinessSecurityStore securityStore;
    private TransactionBusinessSecurityStoreSlice slice;
    private BusinessRuleRunner businessRuleRunner;
    private List<BusinessRule> rules;


    @BeforeEach
    void setUp() {
        transferringAccount = new Account();
        transferringAccount.setIdentifier(100002L);
        slice = new TransactionBusinessSecurityStoreSlice();
        slice.setTransferringAccount(transferringAccount);
        businessRuleRunner = new DontPermitIfOneRuleReturnsForbiddenBusinessRuleRunner();
        securityStore = new BusinessSecurityStore();
        securityStore.setAccount(transferringAccount);
        securityStore.setTransactionBusinessSecurityStoreSlice(slice);
        rules = new ArrayList<>();
        rules.add(new CentralTransferCanBeProposedOnlyByAuthority(securityStore));
    }

    @Test
    @DisplayName("Should forbid AR from proposing a Central Transfer transaction from UK Total Quantity account.")
    void shouldForbidArFromProposingACentralTransferFromUKAllocationAccount() {
        transferringAccount.setRegistryAccountType(RegistryAccountType.UK_TOTAL_QUANTITY_ACCOUNT);
        slice.setTransactionType(CentralTransferAllowances);
        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("Should forbid a Senior Admin from proposing a Central Transfer transaction from UK Allocation account.")
    void shouldForbidSeniorAdminFromProposingACentralTransferFromUKAllocationAccount() {
        transferringAccount.setRegistryAccountType(RegistryAccountType.UK_ALLOCATION_ACCOUNT);
        slice.setTransactionType(CentralTransferAllowances);
        securityStore.setUserRoles(List.of(UserRole.SENIOR_REGISTRY_ADMINISTRATOR));

        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertTrue(actualOutcome.isForbidden());
    }

    @Test
    @DisplayName("Should permit AR from proposing a Transfer Allowances transaction.")
    void shouldPermitArFromProposingTransferAllowances() {
        transferringAccount.setRegistryAccountType(RegistryAccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT);
        slice.setTransactionType(TransferAllowances);
        securityStore.setUserRoles(List.of(UserRole.AUTHORISED_REPRESENTATIVE));

        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, actualOutcome);
    }

    @Test
    @DisplayName("Should permit an Authority user from proposing a Central Transfer transaction from UK Allocation account.")
    void shouldPermitAuthorityProposeCentralTransferFromUKAllocationAccount() {
        transferringAccount.setRegistryAccountType(RegistryAccountType.UK_ALLOCATION_ACCOUNT);
        slice.setTransactionType(SurrenderAllowances);
        securityStore.setUserRoles(List.of(UserRole.AUTHORITY_USER));

        BusinessRule.Outcome actualOutcome = businessRuleRunner.run(rules);
        assertEquals(BusinessRule.Outcome.PERMITTED_OUTCOME, actualOutcome);
    }
}
