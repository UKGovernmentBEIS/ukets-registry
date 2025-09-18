package gov.uk.ets.registry.api.common.search;

import static gov.uk.ets.registry.api.helper.persistence.TokenizeHelper.extractSelectClause;
import static gov.uk.ets.registry.api.helper.persistence.TokenizeHelper.tokenizeClause;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQuery;
import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.repository.AccountSearchRepositoryImpl;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.web.mappers.AccountFilterMapper;
import gov.uk.ets.registry.api.account.web.model.search.AccountSearchCriteria;
import gov.uk.ets.registry.api.allocation.type.AllocationClassification;
import gov.uk.ets.registry.api.common.test.PostgresJpaTest;
import gov.uk.ets.registry.api.task.domain.types.RequestStateEnum;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.task.domain.types.TaskStatus;
import gov.uk.ets.registry.api.task.repository.TaskRepository;
import gov.uk.ets.registry.api.task.shared.EndUserSearch;
import gov.uk.ets.registry.api.task.shared.TaskProjection;
import gov.uk.ets.registry.api.task.shared.TaskSearchCriteria;
import gov.uk.ets.registry.api.transaction.domain.TransactionFilter;
import gov.uk.ets.registry.api.transaction.domain.TransactionProjection;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.repository.SearchableTransactionRepository;
import gov.uk.ets.reports.model.ReportQueryInfo;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PostgresJpaTest
class JpaQueryExtractorTest {

    private static final CharSequence[] TASK_SEARCH_ADMINS_SELECT_CLAUSE_TOKENS =
        {"request_identifier", "type", "known_as)<>0)", "known_as", "first_name", "known_as)<>0)", "last_name",
        	"id", "urid", "known_as)<>0)", "known_as","first_name", "known_as)<>0)", "last_name", "identifier",
        	"full_identifier", "kyoto_account_type", "registry_account_type", "metadata_name='ACCOUNT_TYPE')",
        	"metadata_value", "account_status", "metadata_name='AH_NAME')", "metadata_value", "name", "type",
        	"disclosed_name", "disclosed_name='Registry", "urid","transaction_identifier", "initiated_date", "status",
            "recipient_account_number", "difference", "urid", "completed_date", "identifier", "type_label",
            "metadata_name='ALLOCATION_CATEGORY')", "metadata_value", "metadata_name='ALLOCATION_YEAR')", "metadata_value"};
    
    private static final CharSequence[] TASK_SEARCH_USERS_SELECT_CLAUSE_TOKENS =
        {"request_identifier", "type", "disclosed_name", "id", "urid",
            "disclosed_name", "identifier", "full_identifier", "kyoto_account_type",
            "registry_account_type", "metadata_name='ACCOUNT_TYPE')", "metadata_value", "account_status",
            "metadata_name='AH_NAME')", "metadata_value", "name", "type", "disclosed_name",
            "disclosed_name='Registry", "urid","transaction_identifier", "initiated_date", "status",
            "id", "type", "account_name"};

    private static final CharSequence[] ACCOUNT_SEARCH_SELECT_CLAUSE_TOKENS =
        {"id", "id", "account_name", "account_status", "type_label", "approval_second_ar_required",
            "balance", "billing_address_same_as_account_holder_address", "check_digits", "commitment_period_code",
            "compliance_status", "id", "contact_id", "full_identifier", "identifier",
            "kyoto_account_type", "opening_date", "registry_account_type", "registry_code", "request_status",
            "transfers_outside_tal", "unit_type", "birth_country", "birth_date", "contact_id",
            "first_name", "identifier", "last_name", "name", "no_reg_justification",
            "registration_number", "type", "regulator"
        };

    private static final CharSequence[] ACCOUNT_SEARCH_SELECT_CLAUSE_TOKENS_FULL_FILTER =
        {"id", "id", "account_name", "account_status", "type_label", "approval_second_ar_required",
            "balance", "billing_address_same_as_account_holder_address", "check_digits", "commitment_period_code",
            "compliance_status", "compliant_entity_id", "contact_id", "full_identifier", "identifier",
            "kyoto_account_type", "opening_date", "registry_account_type", "registry_code", "request_status",
            "transfers_outside_tal", "unit_type", "birth_country", "birth_date", "contact_id",
            "first_name", "identifier", "last_name", "name",
            "no_reg_justification", "registration_number", "type", "regulator"
        };
    private static final CharSequence[] TRANSACTION_SEARCH_SELECT_CLAUSE =
        {"identifier", "type", "status", "last_updated", "quantity", "unit_type",
            "transferring_account_full_identifier", "account_name", "transferring_account_type",
            "registry_account_type", "name", "identifier", "type_label", "account_status", "acquiring_account_full_identifier",
            "account_name", "acquiring_account_type", "registry_account_type", "name", "identifier",
            "type_label", "account_status", "started","identifier", "identifier"};


    @Autowired
    TaskRepository taskProjectionRepository;

    @Autowired
    AccountSearchRepositoryImpl accountSearchRepository;

    @Autowired
    SearchableTransactionRepository searchableTransactionRepository;

    @Test
    void shouldExtractQueryForTasks() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setTaskStatus(TaskStatus.OPEN);
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setIamIdentifier("test-iam-id");
        criteria.setEndUserSearch(endUserSearch);
        JPAQuery<TaskProjection> adminQuery = taskProjectionRepository.getAdminQuery(criteria);

        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(adminQuery.createQuery());

        assertThat(reportQueryInfo).isNotNull();
        String query = reportQueryInfo.getQuery();
        assertThat(query).contains("status<>'APPROVED'", "status<>'REJECTED'");

        // VERY IMPORTANT to check that the select clause has not been modified (the field names and their order).
        // Search Reports will break if there is a change here.
        List<String> expected = Arrays.stream(TASK_SEARCH_ADMINS_SELECT_CLAUSE_TOKENS).map(CharSequence::toString).collect(
            Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(query))).containsExactlyElementsOf((expected));
    }

    @Test
    void shouldExtractQueryForTasksWithAllFilters() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setAccountNumber("GB-100-10000022-1-45");
        criteria.setAccountHolder("test");
        criteria.setTaskStatus(TaskStatus.CLAIMED);
        criteria.setClaimantName("test");
        criteria.setTaskType(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST);
        criteria.setRequestId(111111L);
        criteria.setClaimedOnFrom(testDate());
        criteria.setClaimedOnTo(testDate());
        criteria.setCreatedOnFrom(testDate());
        criteria.setCreatedOnTo(testDate());
        criteria.setCompletedOnFrom(testDate());
        criteria.setCompletedOnTo(testDate());
        criteria.setTransactionId("UK100152");
        criteria.setTaskOutcome(RequestStateEnum.SUBMITTED_NOT_YET_APPROVED);
        criteria.setInitiatorName("test");
        criteria.setAccountType("AMBITION_INCREASE_CANCELLATION_ACCOUNT");
        criteria.setAllocationCategory("INSTALLATION");
        criteria.setAllocationYear("2022");
        criteria.setInitiatedBy("AUTHORISED_REPRESENTATIVE");
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setIamIdentifier("test-iam-id");
        criteria.setEndUserSearch(endUserSearch);

        JPAQuery<TaskProjection> adminQuery = taskProjectionRepository.getAdminQuery(criteria);

        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(adminQuery.createQuery());
        assertThat(reportQueryInfo).isNotNull();

        String query = reportQueryInfo.getQuery();

        assertThat(query).contains("status='SUBMITTED_NOT_YET_APPROVED'", "status<>'APPROVED'", "status<>'REJECTED'");

        List<String> expected = Arrays.stream(TASK_SEARCH_ADMINS_SELECT_CLAUSE_TOKENS).map(CharSequence::toString)
                .collect(Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(query))).containsExactlyElementsOf((expected));
    }
    
    @Test
    void shouldExtractUserQueryForTasks() {
        TaskSearchCriteria criteria = new TaskSearchCriteria();
        criteria.setTaskStatus(TaskStatus.OPEN);
        EndUserSearch endUserSearch = new EndUserSearch();
        endUserSearch.setIamIdentifier("test-iam-id");
        criteria.setEndUserSearch(endUserSearch);
        JPAQuery<TaskProjection> userQuery = taskProjectionRepository.getUserQuery(criteria);

        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(userQuery.createQuery());

        assertThat(reportQueryInfo).isNotNull();
        String query = reportQueryInfo.getQuery();
        assertThat(query).contains("status<>'APPROVED'", "status<>'REJECTED'");

        // VERY IMPORTANT to check that the select clause has not been modified (the field names and their order).
        // Search Reports will break if there is a change here.
        List<String> expected = Arrays.stream(TASK_SEARCH_USERS_SELECT_CLAUSE_TOKENS).map(CharSequence::toString)
                .collect(Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(query))).containsExactlyElementsOf((expected));
    }

    @Test
    void shouldExtractNativeSqlQueryForAccountSearchWithFiltersThatChangeTheWhereClause() {
        AccountSearchCriteria criteria = new AccountSearchCriteria();
        criteria.setAccountStatus("ALL_EXCEPT_CLOSED");
        criteria.setAccountIdOrName("UK-100-10000050-0-5");
        criteria.setAuthorizedRepresentativeUrid("UK405681794859");
        AccountFilter filter = new AccountFilterMapper().map(criteria);

        JPAQuery<AccountProjection> query = accountSearchRepository.getQuery(filter);
        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(query.createQuery());
        String nativeSqlQuery = reportQueryInfo.getQuery();

        List<String> expected = Arrays.stream(ACCOUNT_SEARCH_SELECT_CLAUSE_TOKENS).map(CharSequence::toString).collect(
            Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(nativeSqlQuery))).containsSubsequence(expected);
        assertThat(nativeSqlQuery).contains(
            "account_status in ('OPEN','ALL_TRANSACTIONS_RESTRICTED','SOME_TRANSACTIONS_RESTRICTED','SUSPENDED_PARTIALLY','SUSPENDED','TRANSFER_PENDING'");
    }

    @Test
    void shouldExtractSqlQueryForAccountSearchWithOneFilter() {
        AccountSearchCriteria criteria = new AccountSearchCriteria();
        criteria.setAccountIdOrName("UK-100-10000050-0-5");
        criteria.setAuthorizedRepresentativeUrid("UK405681794859");

        AccountFilter filter = new AccountFilterMapper().map(criteria);

        JPAQuery<AccountProjection> query = accountSearchRepository.getQuery(filter);
        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(query.createQuery());
        String nativeSqlQuery = reportQueryInfo.getQuery();

        List<String> expected = Arrays.stream(ACCOUNT_SEARCH_SELECT_CLAUSE_TOKENS).map(CharSequence::toString).collect(
            Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(nativeSqlQuery))).containsExactlyElementsOf(expected);
        assertThat(nativeSqlQuery).contains("id is not null");

    }

    @Test
    void shouldExtractSqlQueryForAccountSearchWithAllFilters() {
        AccountSearchCriteria criteria = new AccountSearchCriteria();
        criteria.setAccountIdOrName("UK-100-10000050-0-5");
        criteria.setAccountStatus("OPEN");
        criteria.setAccountType("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT");
        criteria.setAccountHolderName("test");
        criteria.setComplianceStatus("A");
        criteria.setPermitOrMonitoringPlanIdentifier("123");
        criteria.setAuthorizedRepresentativeUrid("UK405681794859");
        criteria.setRegulatorType("EA");
        criteria.setAllocationStatus(AllocationClassification.NOT_YET_ALLOCATED);
        criteria.setOperatorId("1000045");

        AccountFilter filter = new AccountFilterMapper().map(criteria);
        filter.addExcludedAccountStatus(AccountStatus.REJECTED);

        JPAQuery<AccountProjection> query = accountSearchRepository.getQuery(filter);
        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(query.createQuery());
        String nativeSqlQuery = reportQueryInfo.getQuery();

        List<String> expected =
            Arrays.stream(ACCOUNT_SEARCH_SELECT_CLAUSE_TOKENS_FULL_FILTER).map(CharSequence::toString).collect(
                Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(nativeSqlQuery))).containsExactlyElementsOf(expected);
        assertThat(nativeSqlQuery).contains("a1_0.account_status='OPEN' and a1_0.account_status<>'REJECTED'");
        assertThat(nativeSqlQuery).contains("like '%1000045%'");
    }

    @Test
    void shouldExtractQueryForTransactionsWIthNoFilters() {
        TransactionFilter filter = TransactionFilter.builder().build();

        JPAQuery<TransactionProjection> query = searchableTransactionRepository.getQuery(filter, PageRequest.ofSize(10));
        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(query.createQuery());
        String nativeSqlQuery = reportQueryInfo.getQuery();

        List<String> expected = Arrays.stream(TRANSACTION_SEARCH_SELECT_CLAUSE).map(CharSequence::toString).collect(
            Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(nativeSqlQuery))).containsExactlyElementsOf(expected);

    }

    @Test
    void shouldExtractQueryForTransactionsWIthOnFilter() {
        TransactionFilter filter = TransactionFilter.builder()
            .transactionId("UK1234567")
            .build();

        JPAQuery<TransactionProjection> query = searchableTransactionRepository.getQuery(filter, PageRequest.ofSize(10));
        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(query.createQuery());
        String nativeSqlQuery = reportQueryInfo.getQuery();

        List<String> expected = Arrays.stream(TRANSACTION_SEARCH_SELECT_CLAUSE).map(CharSequence::toString).collect(
            Collectors.toList());
        assertThat(tokenizeClause(extractSelectClause(nativeSqlQuery))).containsSubsequence(expected);

    }

    @Test
    void shouldExtractQueryForTransactionsWIthAllFilters() {
        TransactionFilter filter = TransactionFilter.builder()
            .transactionId("GB100000")
            .transactionType(TransactionType.IssueAllowances)
            .transactionStatus(TransactionStatus.FAILED)
            .transactionLastUpdateDateFrom(testDate())
            .transactionLastUpdateDateTo(testDate())
            .transferringAccountNumber("123456")
            .acquiringAccountNumber("123456")
            .acquiringAccountTypes(List.of(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT))
            .transferringAccountTypes(List.of(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT))
            .unitType(UnitType.AAU)
            .initiatorUserId("2345")
            .approverUserId("12345")
            .transactionalProposalDateFrom(testDate())
            .transactionalProposalDateTo(testDate())
            .authorizedRepresentativeUrid("UK123")
            .enrolledNonAdmin(true)
            .build();

        JPAQuery<TransactionProjection> query = searchableTransactionRepository.getQuery(filter, PageRequest.ofSize(10));
        ReportQueryInfo reportQueryInfo = JpaQueryExtractor.extractReportQueryInfo(query.createQuery());
        String nativeSqlQuery = reportQueryInfo.getQuery();

        List<String> expected = Arrays.stream(TRANSACTION_SEARCH_SELECT_CLAUSE).map(CharSequence::toString).collect(
            Collectors.toList());
        assertThat(extractSelectClause(nativeSqlQuery)).containsSubsequence(expected);

    }

    private Date testDate() {
        return Date.from(LocalDate.of(2021, 1, 1).atStartOfDay().toInstant(ZoneOffset.UTC));
    }

}
