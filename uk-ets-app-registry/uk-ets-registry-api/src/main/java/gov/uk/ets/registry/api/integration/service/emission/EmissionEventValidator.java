package gov.uk.ets.registry.api.integration.service.emission;

import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.compliance.repository.ExcludeEmissionsRepository;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.SubmitEmissionsValidityInfo;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.emission.AccountEmissionsUpdateEvent;
import uk.gov.netz.integration.model.error.IntegrationEventError;

@Component
@RequiredArgsConstructor
public class EmissionEventValidator {

    @Value("${emissions-maritime-starting-year}")
    private Integer maritimeStartingYear;

    private final CompliantEntityRepository compliantEntityRepository;
    private final ExcludeEmissionsRepository excludeEmissionsRepository;

    public List<IntegrationEventError> validate(AccountEmissionsUpdateEvent event) {

        Map<Long, SubmitEmissionsValidityInfo> existingCompliantEntities =
            compliantEntityRepository.findAllIdentifiersFetchAccountStatusAndYears().stream()
                .collect(Collectors.toMap(SubmitEmissionsValidityInfo::getIdentifier, t -> t));

        List<IntegrationEventError> errors = new ArrayList<>();

        Long operatorId = event.getRegistryId();
        if (operatorId == null) {
            errors.add(IntegrationEventError.ERROR_0801);
        }

        Year reportingYear = event.getReportingYear();
        if (reportingYear == null) {
            errors.add(IntegrationEventError.ERROR_0802);
        }

        Long reportableEmissions = event.getReportableEmissions();
        if (reportableEmissions != null && reportableEmissions < 0) {
            errors.add(IntegrationEventError.ERROR_0809);
        }

        if (operatorId != null) {
            if (!existingCompliantEntities.containsKey(operatorId)) {
                errors.add(IntegrationEventError.ERROR_0803);
                return errors; // No reason to continue
            }

            validateAccountStatus(existingCompliantEntities.get(operatorId), errors);

            if (reportingYear != null) {
                validateYearRange(reportingYear, errors, operatorId);
                validateEmissionYear(existingCompliantEntities.get(operatorId), reportingYear, errors);
            }
        }

        return errors;
    }

    private void validateYearRange(Year reportingYear, List<IntegrationEventError> errors, Long operatorId) {

        boolean isMaritimeOperator = compliantEntityRepository.findByIdentifier(operatorId)
            .map(Hibernate::unproxy)
            .filter(MaritimeOperator.class::isInstance)
            .isPresent();

        if (isMaritimeOperator && reportingYear.getValue() < maritimeStartingYear) {
            errors.add(IntegrationEventError.ERROR_0816);
        } else if (reportingYear.getValue() < 2021) {
            errors.add(IntegrationEventError.ERROR_0811);
        }

        int currentYear = LocalDate.now().getYear();
        if (reportingYear.getValue() > currentYear) {
            errors.add(IntegrationEventError.ERROR_0815);
        }
    }

    private void validateAccountStatus(SubmitEmissionsValidityInfo submitEmissionsValidityInfo,
                                       List<IntegrationEventError> errors) {

        AccountStatus accountStatus = submitEmissionsValidityInfo.getAccountStatus();

        if (accountStatus == null) {
            errors.add(IntegrationEventError.ERROR_0800);
        } else if (AccountStatus.CLOSED == accountStatus) {
            errors.add(IntegrationEventError.ERROR_0805);
        } else if (AccountStatus.TRANSFER_PENDING == accountStatus) {
            errors.add(IntegrationEventError.ERROR_0806);
        } else if (AccountStatus.CLOSURE_PENDING == accountStatus) {
            errors.add(IntegrationEventError.ERROR_0807);
        }
    }

    private void validateEmissionYear(SubmitEmissionsValidityInfo submitEmissionsValidityInfo,
                                      Year reportingYear,
                                      List<IntegrationEventError> errors) {

        int emissionsYear = reportingYear.getValue();
        int currentYear = LocalDate.now().getYear();

        Optional<Integer> lastYearVerifiedEmissionsOptional = Optional.ofNullable(submitEmissionsValidityInfo.getEndYear());
        if (emissionsYear == currentYear && !isLastEmissionYearEqualCurrentYear(lastYearVerifiedEmissionsOptional, currentYear)) {
            errors.add(IntegrationEventError.ERROR_0812);
            return;
        }

        int firstYearVerifiedEmissions = submitEmissionsValidityInfo.getStartYear();
        if (emissionsYear < firstYearVerifiedEmissions) {
            errors.add(IntegrationEventError.ERROR_0813);
        }

        if (lastYearVerifiedEmissionsOptional.isPresent() && emissionsYear > lastYearVerifiedEmissionsOptional.get()) {
            errors.add(IntegrationEventError.ERROR_0814);
        }

        excludeEmissionsRepository.findAll().stream()
            .filter(e -> submitEmissionsValidityInfo.getIdentifier().equals(e.getCompliantEntityId())
                && emissionsYear == e.getYear().intValue()
                && e.isExcluded())
            .findFirst()
            .ifPresent(e -> errors.add(IntegrationEventError.ERROR_0808));
    }

    private boolean isLastEmissionYearEqualCurrentYear(Optional<Integer> lastYearVerifiedEmissionsOptional,
                                                       int currentYear) {
        return lastYearVerifiedEmissionsOptional.filter(lyve -> lyve == currentYear).isPresent();
    }

}
