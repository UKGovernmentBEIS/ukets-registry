package gov.uk.ets.registry.api.integration.service.emission;

import static org.assertj.core.api.Assertions.assertThat;

import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.time.Year;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;

@ExtendWith(MockitoExtension.class)
class EmissionEventValidatorTest {

    @Mock
    private CompliantEntityRepository compliantEntityRepository;
    @Mock
    private ExcludeEmissionsRepository excludeEmissionsRepository;

    private EmissionEventValidator validator;


    @BeforeEach
    public void setup() {
        validator = new EmissionEventValidator(compliantEntityRepository, excludeEmissionsRepository);
        ReflectionTestUtils.setField(validator, "maritimeStartingYear", 2026);
    }

    @ParameterizedTest(name = "#{index} - Valid event for reporting year {0}")
    @MethodSource("reportingYears")
    void testValidateEvent(Integer year) {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(year));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).isEmpty();
    }

    private static Stream<Integer> reportingYears() {
        return IntStream.rangeClosed(2021, Year.now().getValue()).boxed();
    }

    @Test
    void testValidateEventWithoutRegistryId() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(null);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2024));

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(List.of());

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0801);
    }

    @Test
    void testValidateEventWithUnknownRegistryId() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2024));

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(List.of());

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0803);
    }

    @Test
    void testValidateEventWithoutReportingYear() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(null);

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0802);
    }

    @Test
    void testValidateEventWithNegativeEmissions() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(-123L);
        event.setReportingYear(Year.of(2023));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0809);
    }

    @Test
    void testValidateMaritimeEventWithReportingYearBeforeFYVE() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2020));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);
        Mockito.when(compliantEntityRepository.findByIdentifier(123456L)).thenReturn(Optional.of(new MaritimeOperator()));

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(2)
            .contains(IntegrationEventError.ERROR_0813)
            .contains(IntegrationEventError.ERROR_0816);
    }

    @Test
    void testValidateInstallationEventWithReportingYearBeforeFYVE() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2020));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);
        Mockito.when(compliantEntityRepository.findByIdentifier(123456L)).thenReturn(Optional.of(new Installation()));

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(2)
            .contains(IntegrationEventError.ERROR_0813)
            .contains(IntegrationEventError.ERROR_0811);
    }

    @Test
    void testValidateEventWithReportingYearInFuture() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.now().plusYears(1));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0815);
    }

    @ParameterizedTest(name = "#{index} - accountStatus {0} - should return {1}")
    @MethodSource("accountStatusPerError")
    void testValidateEventWithoutAppropriateStatus(AccountStatus accountStatus, IntegrationEventError error) {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2023));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, accountStatus, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(error);
    }

    private static Stream<Arguments> accountStatusPerError() {
        return Stream.of(
            Arguments.of(null, IntegrationEventError.ERROR_0800),
            Arguments.of(AccountStatus.CLOSED, IntegrationEventError.ERROR_0805),
            Arguments.of(AccountStatus.TRANSFER_PENDING, IntegrationEventError.ERROR_0806),
            Arguments.of(AccountStatus.CLOSURE_PENDING, IntegrationEventError.ERROR_0807)
        );
    }

    @Test
    void testValidateEventWithReportingYearAfterLYVE() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2023));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, 2022);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0814);
    }

    @Test
    void testValidateEventWithReportingYearAsExcluded() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();
        event.setRegistryId(123456L);
        event.setReportableEmissions(123L);
        event.setReportingYear(Year.of(2023));

        SubmitEmissionsValidityInfo info =
            new SubmitEmissionsValidityInfo(123456L, AccountStatus.OPEN, ComplianceStatus.C, 2021, null);
        List<SubmitEmissionsValidityInfo> existingCompliantEntities = List.of(info);

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(existingCompliantEntities);

        ExcludeEmissionsEntry excludeEmissionsEntry = new ExcludeEmissionsEntry(1L, 123456L, 2023L, true, new Date());

        Mockito.when(excludeEmissionsRepository.findAll()).thenReturn(List.of(excludeEmissionsEntry));

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(1).contains(IntegrationEventError.ERROR_0808);
    }

    @Test
    void testValidateEventWithAllValuesNull() {
        // given
        AccountEmissionsUpdateEvent event = new AccountEmissionsUpdateEvent();

        Mockito.when(compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears()).thenReturn(List.of());

        // when
        List<IntegrationEventError> result = validator.validate(event);

        // then
        assertThat(result).hasSize(2)
            .contains(IntegrationEventError.ERROR_0801)
            .contains(IntegrationEventError.ERROR_0802);
    }
}
