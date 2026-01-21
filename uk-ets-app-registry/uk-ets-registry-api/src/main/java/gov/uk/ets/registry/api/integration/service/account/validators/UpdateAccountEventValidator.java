package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.MaritimeOperator;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.repository.CompliantEntityRepository;
import gov.uk.ets.registry.api.account.service.AccountOperatorUpdateService;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountHolderTypeMissmatchException;
import gov.uk.ets.registry.api.account.validation.AccountNotFoundValidationException;
import gov.uk.ets.registry.api.account.validation.AccountStatusValidationException;
import gov.uk.ets.registry.api.account.validation.FYVEValidationException;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

import java.util.*;
import java.util.function.BiFunction;

import static org.hibernate.validator.internal.util.StringHelper.isNullOrEmptyString;

@Component
@RequiredArgsConstructor
public class UpdateAccountEventValidator {
    private final AccountService accountService;
    private final AccountOperatorUpdateService accountOperatorUpdateService;
    private final UpdateAccountFormatValidator formatValidator;
    private final CompliantEntityRepository compliantEntityRepository;

    @Value("${emissions-maritime-starting-year}")
    private Integer maritimeStartingYear;

    private static final Set<AccountType> UPDATABLE_ACCOUNT_TYPES =
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
                AccountType.OPERATOR_HOLDING_ACCOUNT,
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT,
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT
        )));

    @Qualifier("updateAccountValidator")
    private final CommonAccountValidator updateAccountValidator;

    public List<IntegrationEventErrorDetails> validate(AccountUpdatingEvent event, OperatorType operatorType) {

        // 1. FORMAT-ONLY validation (0301, 0303)
        List<IntegrationEventErrorDetails> errors = formatValidator.validate(event);
        UpdateAccountDetailsMessage details = event.getAccountDetails();
        if(details != null) {
            // Registry ID mandatory + numeric (0303 + 0317)
            updateAccountValidator.validateMandatoryField(
                    details.getRegistryId(),
                    "Registry ID",
                    s -> !s.matches("^\\d+$"),
                    IntegrationEventError.ERROR_0317,
                    errors
            );
            Long registryId = null;
            if (details.getRegistryId() != null && details.getRegistryId().matches("^\\d+$")) {
                registryId = Long.valueOf(details.getRegistryId());
            }

            if (registryId == null) {
                return errors; // cannot continue without valid registry ID
            } else {
                boolean existsAccount = validateOperatorTypeAndCheckIfAccountExists(
                        errors,
                        event.getAccountHolder().getAccountHolderType(),
                        operatorType,
                        registryId
                );
                if(!existsAccount) {
                    return errors; // cannot continue without existing account
                }
            }

            // INSTALLATION business rules
            if (operatorType == OperatorType.INSTALLATION) {

                updateAccountValidator.validateMandatoryField(
                        details.getPermitId(), "Permit ID", 256, errors
                );

                validateAvailability(
                        details.getPermitId(),
                        "Permit ID",
                        accountService::validateInstallationPermitUniqueness,
                        registryId,
                        IntegrationEventError.ERROR_0318,
                        errors
                );

                updateAccountValidator.validateMandatoryField(
                        details.getInstallationName(), "Installation Name", 256, errors
                );
            }

            // AIRCRAFT business rules
            if (operatorType == OperatorType.AIRCRAFT_OPERATOR) {
                String monitoringPlanId = details.getMonitoringPlanId();
                if (monitoringPlanId == null) {
                    monitoringPlanId = getEmitterIdIfEqualsMonitoringPlanId(registryId).orElse(null);
                }

                updateAccountValidator.validateMandatoryField(
                        monitoringPlanId, "Monitoring Plan ID", 256, errors
                );

                validateAvailability(
                        monitoringPlanId,
                        "Monitoring Plan ID",
                        accountService::validateAircraftUniquenessMonitoringPlanId,
                        registryId,
                        IntegrationEventError.ERROR_0319,
                        errors
                );
            }

            // MARITIME business rules
            if (operatorType == OperatorType.MARITIME_OPERATOR) {
                String monitoringPlanId = details.getMonitoringPlanId();
                if (monitoringPlanId == null) {
                    monitoringPlanId = getEmitterIdIfEqualsMonitoringPlanId(registryId).orElse(null);
                }

                updateAccountValidator.validateMandatoryField(
                        monitoringPlanId, "Monitoring Plan ID", 256, errors
                );

                validateAvailability(
                        monitoringPlanId,
                        "Monitoring Plan ID",
                        accountService::validateMaritimeUniquenessMonitoringPlanId,
                        registryId,
                        IntegrationEventError.ERROR_0319,
                        errors
                );

                updateAccountValidator.validateMandatoryField(
                        details.getCompanyImoNumber(), "Company IMO number", 256, errors
                );

                validateAvailability(
                        details.getCompanyImoNumber(),
                        "Company IMO number",
                        accountService::validateMaritimeUniquenessImo,
                        registryId,
                        IntegrationEventError.ERROR_0310,
                        errors
                );
            }
            validateRegulator(details.getRegulator(), errors);

            // Allowed Account Type (0316)
            validateAccountType(details.getAccountType(), errors);

            // Regulated Activities enum check (0312)
            validateRegulatedActivities(details, operatorType, errors);
            // FYVE business logic
            validateFYVE(
                    details.getFirstYearOfVerifiedEmissions(),
                    operatorType,
                    registryId,
                    maritimeStartingYear,
                    errors
            );
        }

        // Country & Postal code NOT validated for UPDATE anymore
        updateAccountValidator.validateAccountHolder(event.getAccountHolder(), errors);
        updateAccountValidator.validateCountry(event.getAccountHolder().getCountry(), errors);
        updateAccountValidator.validatePostalCode(event.getAccountHolder(), errors);
        return errors;
    }

    private void validateRegulator(String regulator, List<IntegrationEventErrorDetails> errors) {
        updateAccountValidator.validateMandatoryField(regulator, "Regulator",
                v -> Arrays.stream(RegulatorType.values()).map(Enum::name).noneMatch(r -> r.equals(v)), IntegrationEventError.ERROR_0308, errors);
    }

    public void validateAvailability(
            String value,
            String fieldName,
            BiFunction<String, Long, Boolean> availabilityCheck,
            Long registryId,
            IntegrationEventError error,
            List<IntegrationEventErrorDetails> errorDetails) {

        if (isNullOrEmptyString(value)) {
            return; // skip, handled by mandatory check
        }

        boolean available = availabilityCheck.apply(value, registryId);
        if (!available) {
            errorDetails.add(new IntegrationEventErrorDetails(error, fieldName));
        }
    }

    private boolean validateOperatorTypeAndCheckIfAccountExists(List<IntegrationEventErrorDetails> errorDetails,
                                                                String accountHolderType,
                                                                OperatorType newType,
                                                                Long registryId) {

        boolean operatorTypeUnchanged;

        try {
            operatorTypeUnchanged = accountService.validateAccountUpdateRules(accountHolderType, newType, registryId);
        } catch (AccountStatusValidationException e) {
            errorDetails.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0313,
                    "Account status"
            ));
            return true;
        } catch (AccountNotFoundValidationException ex) {
            errorDetails.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0317,  // “Account not found”
                    "Registry ID"
            ));
            return false;
        } catch (AccountHolderTypeMissmatchException mx) {
            errorDetails.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0314,
                    "Account Holder Type"
            ));
            return true;
        }

        if (!operatorTypeUnchanged) {
            errorDetails.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0314,
                    "Operator type"
            ));
        }
        return true;
    }

    private void validateAccountType(String type, List<IntegrationEventErrorDetails> errors) {

        String ref = "Account Type";

        // 1. Mandatory
        if (type == null || type.isBlank()) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0303, ref
            ));
            return;
        }

        // 2. Format rule (Expected ERROR_0301)
        if (!type.matches("^[A-Z_]+$")) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0301, ref
            ));
            return;
        }

        validateAccountTypes(type, errors, ref);
    }

    private void validateAccountTypes(String type, List<IntegrationEventErrorDetails> errors, String ref) {
        Set<String> allowed = Set.of(
                AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name(),
                AccountType.OPERATOR_HOLDING_ACCOUNT.name(),
                AccountType.MARITIME_OPERATOR_HOLDING_ACCOUNT.name()
        );

        if (!allowed.contains(type)) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0316, ref
            ));
        }
    }

    private void validateFYVE(Integer firstYearOfVerifiedEmissions,
                              OperatorType operatorType,
                              Long registryId,
                              Integer maritimeStartingYear,
                              List<IntegrationEventErrorDetails> errors) {

        String field = "First Year Of Verified Emission";

        if (firstYearOfVerifiedEmissions == null) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0303,
                    field
            ));
            return;
        }

        if (operatorType == OperatorType.MARITIME_OPERATOR) {
            if (firstYearOfVerifiedEmissions < maritimeStartingYear) {
                errors.add(new IntegrationEventErrorDetails(
                        IntegrationEventError.ERROR_0306,
                        field
                ));
                return;
            }
        } else {
            if (firstYearOfVerifiedEmissions < 2021) {
                errors.add(new IntegrationEventErrorDetails(
                        IntegrationEventError.ERROR_0311,
                        field
                ));
                return;
            }
        }

        try {
            accountOperatorUpdateService.validateIfFYVECanBeUpdated(registryId, firstYearOfVerifiedEmissions);
        } catch (AccountNotFoundValidationException ex) {
            // Already handled by operatorType validation logic do NOT add error
            return;
        } catch (FYVEValidationException ex) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0315,
                    field
            ));
        }
    }

    private void validateRegulatedActivities(
            UpdateAccountDetailsMessage updateAccountDetailsMessage,
            OperatorType operatorType,
            List<IntegrationEventErrorDetails> errorDetails) {
        Set<String> regulatedActivities = updateAccountDetailsMessage.getInstallationActivityTypes();

        if (operatorType == OperatorType.INSTALLATION) {

            // Mandatory
            if (regulatedActivities == null || regulatedActivities.isEmpty()) {
                errorDetails.add(new IntegrationEventErrorDetails(
                        IntegrationEventError.ERROR_0303,
                        "Regulated activities"
                ));
                return;
            }

            // Validate enum values
            boolean anyInvalid = regulatedActivities.stream()
                    .anyMatch(value -> {
                        try {
                            InstallationActivityType.valueOf(value);
                            return false;
                        } catch (Exception ex) {
                            return true;
                        }
                    });

            if (anyInvalid) {
                errorDetails.add(new IntegrationEventErrorDetails(
                        IntegrationEventError.ERROR_0312,
                        "Regulated activities"
                ));
            }
        }
    }

    private Optional<String> getEmitterIdIfEqualsMonitoringPlanId(Long registryId) {
        return compliantEntityRepository.findByIdentifier(registryId)
                .flatMap(entity -> {
                    if (entity instanceof AircraftOperator ao) {
                        return Objects.equals(ao.getEmitterId(), ao.getMonitoringPlanIdentifier())
                                ? Optional.ofNullable(ao.getEmitterId())
                                : Optional.empty();
                    }
                    if (entity instanceof MaritimeOperator mo) {
                        return Objects.equals(mo.getEmitterId(), mo.getMaritimeMonitoringPlanIdentifier())
                                ? Optional.ofNullable(mo.getEmitterId())
                                : Optional.empty();
                    }
                    return Optional.empty();
                });
    }
}
