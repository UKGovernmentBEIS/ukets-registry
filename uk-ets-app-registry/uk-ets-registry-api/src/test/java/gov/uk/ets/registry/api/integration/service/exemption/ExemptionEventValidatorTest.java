package gov.uk.ets.registry.api.integration.service.exemption;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ExemptionEventValidatorTest {

    @Mock
    private AccountRepository accountRepository;

    private ExemptionEventValidator validator;

    @BeforeEach
    public void setup() {
        validator = new ExemptionEventValidator(accountRepository);
    }

    @Test
    void testValidateWithValidEvent() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(2024));

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2025);
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    void testValidateEventWithoutRegistryId() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(null);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(2024));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0402);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Registry Id");
    }

    @Test
    void testValidateEventWithUnknownRegistryId() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(2024));

        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.empty());

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0403);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("123");
    }

    @Test
    void testValidateEventWithoutReportingYear() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        event.setReportingYear(null);

        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0402);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Reporting Year");
    }

    @Test
    void testValidateEventWithoutExemptionFlag() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(null);
        event.setReportingYear(Year.of(2024));

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2025);
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0402);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Exemption Flag");
    }

    @Test
    void testValidateEventWithInvalidReportingYear() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(20202));

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0401);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Reporting Year");
    }

    @Test
    void testValidateEventWithReportingYearBeforeFYVE() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        event.setReportingYear(Year.of(2020));

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2025);
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0404);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("2020");
    }

    @Test
    void testValidateEventWithReportingYearAfterLYVE() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        Year currentYear = Year.now();
        event.setReportingYear(currentYear);

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(currentYear.minusYears(1).getValue());
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0405);
        assertThat(result.get(0).getErrorMessage()).isEqualTo(currentYear.toString());
    }

    @Test
    void testValidateEventWithFutureReportingYear() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(123L);
        event.setExemptionFlag(true);
        Year futureYear = Year.now().plusYears(1);
        event.setReportingYear(futureYear);

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(futureYear.getValue());
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0407);
        assertThat(result.get(0).getErrorMessage()).isEqualTo(Integer.toString(futureYear.getValue()));
    }

    @Test
    void testValidateEventWithAllValuesNull() {
        // given
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(null);
        event.setExemptionFlag(null);
        event.setReportingYear(null);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0402);
        assertThat(result.get(1).getError()).isEqualTo(IntegrationEventError.ERROR_0402);
        assertThat(result.get(2).getError()).isEqualTo(IntegrationEventError.ERROR_0402);
    }
}
