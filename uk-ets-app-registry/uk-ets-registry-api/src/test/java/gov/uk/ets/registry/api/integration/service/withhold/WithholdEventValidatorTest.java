package gov.uk.ets.registry.api.integration.service.withhold;

import gov.uk.ets.registry.api.account.domain.Account;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.repository.AccountRepository;
import gov.uk.ets.registry.api.allocation.service.AccountAllocationService;
import gov.uk.ets.registry.api.allocation.type.AllocationStatusType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class WithholdEventValidatorTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountAllocationService accountAllocationService;

    private WithholdEventValidator validator;

    @BeforeEach
    public void setup() {
        validator = new WithholdEventValidator(accountRepository, accountAllocationService);
    }

    @Test
    void testValidateWithValidEvent() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2024));

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2025);
        Account account = new Account();
        account.setIdentifier(1111L);
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        Mockito.when(accountAllocationService.getAccountAllocationStatus(account.getIdentifier()))
                .thenReturn(Map.of(2024, AllocationStatusType.ALLOWED));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    void testValidateWithInvalidStatus() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2024));

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(2025);
        Account account = new Account();
        account.setIdentifier(1111L);
        account.setAccountStatus(AccountStatus.TRANSFER_PENDING);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        Mockito.when(accountAllocationService.getAccountAllocationStatus(account.getIdentifier()))
                .thenReturn(Map.of(2024, AllocationStatusType.ALLOWED));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0506);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("TRANSFER_PENDING");
    }

    @Test
    void testValidateEventWithoutRegistryId() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(null);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2024));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0502);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Registry Id");
    }

    @Test
    void testValidateEventWithUnknownRegistryId() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
        event.setReportingYear(Year.of(2024));

        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.empty());

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0503);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("123");
    }

    @Test
    void testValidateEventWithoutReportingYear() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
        event.setReportingYear(null);

        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0502);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Reporting Year");
    }

    @Test
    void testValidateEventWithoutWithholdFlag() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(null);
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
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0502);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Withhold Flag");
    }

    @Test
    void testValidateEventWithInvalidReportingYear() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
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
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0501);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("Reporting Year");
    }

    @Test
    void testValidateEventWithReportingYearBeforeFYVE() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
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
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0504);
        assertThat(result.get(0).getErrorMessage()).isEqualTo("2020");
    }

    @Test
    void testValidateEventWithReportingYearAfterLYVE() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
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
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0505);
        assertThat(result.get(0).getErrorMessage()).isEqualTo(currentYear.toString());
    }

    @Test
    void testValidateEventWithoutEntitlementsForTheSpecifiedYear() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(123L);
        event.setWithholdFlag(true);
        Year lyve = Year.now().plusYears(1);
        event.setReportingYear(lyve);

        Installation compliantEntity = new Installation();
        compliantEntity.setStartYear(2021);
        compliantEntity.setEndYear(lyve.getValue());
        Account account = new Account();
        account.setAccountStatus(AccountStatus.OPEN);
        account.setCompliantEntity(compliantEntity);
        Mockito.when(accountRepository.findByCompliantEntityIdentifier(123L)).thenReturn(Optional.of(account));

        Mockito.when(accountAllocationService.getAccountAllocationStatus(account.getIdentifier())).thenReturn(Map.of());

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0508);
        assertThat(result.get(0).getErrorMessage()).isEqualTo(Integer.toString(lyve.getValue()));
    }

    @Test
    void testValidateEventWithAllValuesNull() {
        // given
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(null);
        event.setWithholdFlag(null);
        event.setReportingYear(null);

        // when
        List<IntegrationEventErrorDetails> result = validator.validate(event);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getError()).isEqualTo(IntegrationEventError.ERROR_0502);
        assertThat(result.get(1).getError()).isEqualTo(IntegrationEventError.ERROR_0502);
        assertThat(result.get(2).getError()).isEqualTo(IntegrationEventError.ERROR_0502);
    }
}
