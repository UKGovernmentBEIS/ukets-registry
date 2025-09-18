package gov.uk.ets.registry.api.account.web.mappers;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import gov.uk.ets.registry.api.account.domain.AccountFilter;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.web.model.search.AccountSearchCriteria;
import gov.uk.ets.registry.api.common.search.SearchFiltersUtils;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import java.util.List;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AccountFilterMapperTest {

    private AccountFilterMapper mapper;
    private AccountSearchCriteria criteria;

    @Before
    public void setup() {
        mapper = new AccountFilterMapper();
        criteria = Mockito.mock(AccountSearchCriteria.class);
    }

    @Test
    public void null_criteria_should_be_mapped_to_null_filter_properties() {
        Mockito.when(criteria.getAccountType()).thenReturn(null);
        Mockito.when(criteria.getComplianceStatus()).thenReturn(null);
        Mockito.when(criteria.getRegulatorType()).thenReturn(null);
        Mockito.when(criteria.getAccountHolderName()).thenReturn(null);
        Mockito.when(criteria.getAccountIdOrName()).thenReturn(null);
        Mockito.when(criteria.getAccountStatus()).thenReturn(null);
        Mockito.when(criteria.getAuthorizedRepresentativeUrid()).thenReturn(null);
        Mockito.when(criteria.getPermitOrMonitoringPlanIdentifier()).thenReturn(null);
        Mockito.when(criteria.getOperatorId()).thenReturn(null);

        AccountFilter filter = mapper.map(criteria);

        assertThat(filter.getAccountTypes(), nullValue());
        assertThat(filter.getAccountFullIdentifierOrName(), nullValue());
        assertThat(filter.getAccountHolderName(), nullValue());
        assertThat(filter.getAccountStatuses(), nullValue());
        assertThat(filter.getAuthorizedRepresentativeUrid(), nullValue());
        assertThat(filter.getComplianceStatus(), nullValue());
        assertThat(filter.getPermitOrMonitoringPlanIdentifier(), nullValue());
        assertThat(filter.getRegulatorType(), nullValue());
        assertThat(filter.getOperatorId(), nullValue());
    }

    @Test
    public void test_account_status_mapping() {
        Mockito.when(criteria.getAccountStatus()).thenReturn(AccountFilterMapper.ALL_EXCEPT_CLOSED);
        final AccountFilter filter = mapper.map(criteria);
        assertThat(filter.getAccountStatuses(), notNullValue());
        assertThat(filter.getAccountStatuses().size(), greaterThan(0));
        assertThat(filter.getAccountStatuses().contains(AccountStatus.CLOSED), is(false));
        Stream.of(AccountStatus.values()).filter(
            status -> !List.of(AccountStatus.PROPOSED, AccountStatus.REJECTED, AccountStatus.CLOSED).contains(status))
            .forEach(
                status -> assertThat(filter.getAccountStatuses().contains(status), is(true))
            );

        Mockito.when(criteria.getAccountStatus()).thenReturn(AccountStatus.ALL_TRANSACTIONS_RESTRICTED.name());
        AccountFilter otherFilter = mapper.map(criteria);
        assertThat(otherFilter.getAccountStatuses(), is(List.of(AccountStatus.ALL_TRANSACTIONS_RESTRICTED)));
    }

    @Test
    public void test_account_type_mapping() {
        Mockito.when(criteria.getAccountType()).thenReturn(SearchFiltersUtils.ALL_ETS_GOVERNMENT_ACCOUNTS);
        AccountFilter filter = mapper.map(criteria);
        assertThat(filter.getAccountTypes(), is(AccountType.getAllRegistryGovernmentTypes()));

        Mockito.when(criteria.getAccountType()).thenReturn(SearchFiltersUtils.ALL_KP_GOVERNMENT_ACCOUNTS);
        filter = mapper.map(criteria);
        assertThat(filter.getAccountTypes(), is(AccountType.getAllKyotoGovernmentTypes()));
    }

    @Test
    public void test_compliance_status_and_regulator_type_mapping() {
        Mockito.when(criteria.getComplianceStatus()).thenReturn(ComplianceStatus.EXCLUDED.name());
        AccountFilter filter = mapper.map(criteria);
        assertThat(filter.getComplianceStatus(), is(ComplianceStatus.valueOf(criteria.getComplianceStatus())));

        Mockito.when(criteria.getRegulatorType()).thenReturn(RegulatorType.EA.name());
        filter = mapper.map(criteria);
        assertThat(filter.getRegulatorType(), is(RegulatorType.valueOf(criteria.getRegulatorType())));
    }

    @Test
    public void test_that_all_valid_criteria_are_set_to_filter_properties() {
        Mockito.when(criteria.getAccountType()).thenReturn(SearchFiltersUtils.ALL_KP_GOVERNMENT_ACCOUNTS);
        Mockito.when(criteria.getComplianceStatus()).thenReturn(ComplianceStatus.A.name());
        Mockito.when(criteria.getRegulatorType()).thenReturn(RegulatorType.EA.name());
        Mockito.when(criteria.getAccountHolderName()).thenReturn("account-holder-name");
        Mockito.when(criteria.getAccountIdOrName()).thenReturn("UK-100-123");
        Mockito.when(criteria.getAccountStatus()).thenReturn(AccountStatus.OPEN.name());
        Mockito.when(criteria.getAuthorizedRepresentativeUrid()).thenReturn("test-urid");
        Mockito.when(criteria.getPermitOrMonitoringPlanIdentifier()).thenReturn("permit-or-monitoring");
        Mockito.when(criteria.getOperatorId()).thenReturn("installation-or-aircraft-op-maritime");
        AccountFilter filter = mapper.map(criteria);
        assertThat(filter.getAccountTypes(), notNullValue());
        assertThat(filter.getComplianceStatus(), notNullValue());
        assertThat(filter.getRegulatorType(), notNullValue());
        assertThat(filter.getAccountHolderName(), notNullValue());
        assertThat(filter.getAccountFullIdentifierOrName(), notNullValue());
        assertThat(filter.getAccountStatuses(), notNullValue());
        assertThat(filter.getAuthorizedRepresentativeUrid(), notNullValue());
        assertThat(filter.getPermitOrMonitoringPlanIdentifier(), notNullValue());
        assertThat(filter.getOperatorId(), notNullValue());
    }
}
