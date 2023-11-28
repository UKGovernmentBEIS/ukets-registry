package gov.uk.ets.registry.api.transaction;

import gov.uk.ets.registry.api.authz.AuthorizationService;
import gov.uk.ets.registry.api.authz.Scope;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilterFactory;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.shared.TransactionSearchCriteria;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.service.UserService;
import lombok.Builder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionFilterFactoryTest {
    @Mock
    TransactionSearchCriteria criteria;

    @Mock
    User user;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private UserService userService;

    TransactionFilterFactory transactionFilterFactory;

    ExpectedFilterProperties expectedFilterProperties;

    @BeforeEach
    public void setup() throws ParseException {
        transactionFilterFactory = new TransactionFilterFactory(authorizationService, userService);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        expectedFilterProperties = ExpectedFilterProperties
                .builder()
                .transactionId("transaction-id")
                .transactionType(TransactionType.IssueOfAAUsAndRMUs)
                .transactionStatus(TransactionStatus.ACCEPTED)
                .transactionLastUpdateDateFrom(dateFormat.parse("12/12/2019"))
                .transactionLastUpdateDateTo(dateFormat.parse("13/12/2019"))
                .transferringAccountNumber("transferringAccountNumber")
                .acquiringAccountNumber("accuaring-account-number")
                .acquiringAccountType(AccountType.FORMER_OPERATOR_HOLDING_ACCOUNT)
                .transferringAccountType(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT)
                .unitType(UnitType.AAU)
                .initiatorUserId("initiatorUserId")
                .approverUserId("approverUserId")
                .transactionalProposalDateFrom(dateFormat.parse("14/12/2019"))
                .transactionalProposalDateTo(dateFormat.parse("15/12/2019")).build();
    }

    @Test
    public void testCriteriaToFilterMapping() throws ParseException {
        mockCriteria();
        TransactionFilter filter = transactionFilterFactory.createTransactionFilter(criteria);
        assertEquals(expectedFilterProperties.transactionId, filter.getTransactionId());
        assertEquals(expectedFilterProperties.transactionType, filter.getTransactionType());
        assertEquals(expectedFilterProperties.transactionStatus, filter.getTransactionStatus());
        assertEquals(expectedFilterProperties.transactionLastUpdateDateFrom, filter.getTransactionLastUpdateDateFrom());
        assertEquals(expectedFilterProperties.transactionLastUpdateDateTo, filter.getTransactionLastUpdateDateTo());
        assertEquals(expectedFilterProperties.transferringAccountNumber, filter.getTransferringAccountNumber());
        assertEquals(expectedFilterProperties.acquiringAccountNumber, filter.getAcquiringAccountNumber());
        assertEquals(List.of(expectedFilterProperties.acquiringAccountType), filter.getAcquiringAccountTypes());
        assertEquals(expectedFilterProperties.unitType, filter.getUnitType());
        assertEquals(expectedFilterProperties.initiatorUserId, filter.getInitiatorUserId());
        assertEquals(expectedFilterProperties.approverUserId, filter.getApproverUserId());
        assertEquals(expectedFilterProperties.transactionalProposalDateFrom, filter.getTransactionalProposalDateFrom());
        assertEquals(expectedFilterProperties.transactionalProposalDateTo, filter.getTransactionalProposalDateTo());
        assertEquals(List.of(expectedFilterProperties.transferringAccountType), filter.getTransferringAccountTypes());
    }

    @Test
    public void createTransactionFilterForAdmins() {
        // given
        given(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)).willReturn(false);
        // when
        TransactionFilter filter = transactionFilterFactory.createTransactionFilter(criteria);
        // then
        assertNull(filter.getAuthorizedRepresentativeUrid());
        assertFalse(filter.isEnrolledNonAdmin());
    }

    @Test
    public void createTransactionFilterForNonAdmins() {
        // given
        given(user.getUrid()).willReturn("a-urid-for-test");
        given(authorizationService.hasScopePermission(Scope.SCOPE_ACTION_ENROLLED_NON_ADMIN)).willReturn(true);
        given(userService.getCurrentUser()).willReturn(user);

        // when
        TransactionFilter filter = transactionFilterFactory.createTransactionFilter(criteria);

        // then
        assertEquals(filter.getAuthorizedRepresentativeUrid(), user.getUrid());
        assertTrue(filter.isEnrolledNonAdmin());
    }

    private void mockCriteria() {
        when(criteria.getTransactionId()).thenReturn(expectedFilterProperties.transactionId);
        when(criteria.getTransactionType()).thenReturn(expectedFilterProperties.transactionType.name());
        when(criteria.getTransactionStatus()).thenReturn(expectedFilterProperties.transactionStatus.name());
        when(criteria.getTransactionLastUpdateDateFrom()).thenReturn(expectedFilterProperties.transactionLastUpdateDateFrom);
        when(criteria.getTransactionLastUpdateDateTo()).thenReturn(expectedFilterProperties.transactionLastUpdateDateTo);
        when(criteria.getTransferringAccountNumber()).thenReturn(expectedFilterProperties.transferringAccountNumber);
        when(criteria.getAcquiringAccountNumber()).thenReturn(expectedFilterProperties.acquiringAccountNumber);
        when(criteria.getAcquiringAccountType()).thenReturn(expectedFilterProperties.acquiringAccountType.name());
        when(criteria.getTransferringAccountType()).thenReturn(expectedFilterProperties.transferringAccountType.name());
        when(criteria.getUnitType()).thenReturn(expectedFilterProperties.unitType.name());
        when(criteria.getInitiatorUserId()).thenReturn(expectedFilterProperties.initiatorUserId);
        when(criteria.getApproverUserId()).thenReturn(expectedFilterProperties.approverUserId);
        when(criteria.getTransactionalProposalDateFrom()).thenReturn(expectedFilterProperties.transactionalProposalDateFrom);
        when(criteria.getTransactionalProposalDateTo()).thenReturn(expectedFilterProperties.transactionalProposalDateTo);
    }

    @Builder
    private static class ExpectedFilterProperties {
        private String transactionId;
        private TransactionType transactionType;
        private TransactionStatus transactionStatus;
        private Date transactionLastUpdateDateFrom;
        private Date transactionLastUpdateDateTo;
        private String transferringAccountNumber;
        private String acquiringAccountNumber;
        private AccountType acquiringAccountType;
        private AccountType transferringAccountType;
        private UnitType unitType;
        private String initiatorUserId;
        private String approverUserId;
        private Date transactionalProposalDateFrom;
        private Date transactionalProposalDateTo;
        private Boolean enrolledNonAdmin;
        private String authorizedRepresentativeUrid;
    }


}
