package gov.uk.ets.registry.api.account.web.mappers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.shared.AccountProjection;
import gov.uk.ets.registry.api.account.web.model.search.AccountSearchResult;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import org.junit.Test;
import org.mockito.Mockito;

public class AccountSearchResultMapperTest {
    @Test
    public void test_mapping() {
        AccountProjection account = Mockito.mock(AccountProjection.class);
        Mockito.when(account.getIdentifier()).thenReturn(12345L);
        Mockito.when(account.getFullIdentifier()).thenReturn("UK-100-123213-222");
        Mockito.when(account.getAccountName()).thenReturn("account name");
        Mockito.when(account.getTypeLabel()).thenReturn("account type");
        Mockito.when(account.getAccountHolderName()).thenReturn("a name");
        Mockito.when(account.getAccountHolderName()).thenReturn("John Smith");
        Mockito.when(account.getAccountStatus()).thenReturn(AccountStatus.OPEN);
        Mockito.when(account.getComplianceStatus()).thenReturn(ComplianceStatus.A);
        Mockito.when(account.getBalance()).thenReturn(1234L);

        AccountSearchResult result = AccountSearchResult.of(account);
        assertThat(result.getAccountId(), is(account.getIdentifier()));
        assertThat(result.getFullAccountNo(), is(account.getFullIdentifier()));
        assertThat(result.getAccountHolderName(), is(account.getAccountHolderName()));
        assertThat(result.getAccountName(), is(account.getAccountName()));
        assertThat(result.getAccountStatus(), is(account.getAccountStatus().name()));
        assertThat(result.getAccountType(), is(account.getTypeLabel()));
        assertThat(result.getBalance(), is(account.getBalance()));
        assertThat(result.getComplianceStatus(), is(account.getComplianceStatus().name()));
    }

}
