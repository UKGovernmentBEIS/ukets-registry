package gov.uk.ets.registry.api.helper.integration;

import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.compliance.domain.ExcludeEmissionsEntry;
import gov.uk.ets.registry.api.file.upload.emissionstable.model.EmissionsEntry;
import org.springframework.stereotype.Component;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import uk.gov.netz.integration.model.account.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Component
public class AccountEventTestHelper {

    /**
     * Builds a standard AccountUpdatingEvent for an Installation account.
     */
    public AccountUpdatingEvent buildInstallationSuccessUpdateEvent(
            String registryId,
            String permitId,
            String installationName,
            String accountHolderName
    ) {
        // --- Account details ---
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setAccountName(installationName);
        details.setAccountType("OPERATOR_HOLDING_ACCOUNT");
        details.setPermitId(permitId);
        details.setInstallationName(installationName);
        details.setRegistryId(registryId);
        details.setFirstYearOfVerifiedEmissions(2023);
        details.setInstallationActivityTypes(Set.of(InstallationActivityType.COMBUSTION_OF_FUELS.toString()));
        details.setRegulator(RegulatorType.OPRED.name());

        // --- Account holder details ---
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");
        holder.setName(accountHolderName);
        holder.setAddressLine1("Updated Address");
        holder.setTownOrCity("Updated Town");
        holder.setCountry("GR");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("Integration test");

        // --- Full event ---
        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }

    public AccountUpdatingEvent buildInvalidInstallationUpdateEventWithValidationErrors() {
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        // Missing or invalid mandatory fields
        details.setRegistryId("10027778"); // should be numeric
        details.setAccountType("UNKNOWN_TYPE"); // invalid service type
        details.setPermitId(""); // empty
        details.setInstallationName(""); // empty
        details.setFirstYearOfVerifiedEmissions(2010); // too early (below 2021)
        details.setInstallationActivityTypes(Set.of("INVALID_ACTIVITY")); // invalid enum

        // Missing key account holder fields
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("INVALID_TYPE");
        holder.setName("");
        holder.setAddressLine1("");
        holder.setTownOrCity("");
        holder.setCountry("XX"); // invalid country code
        holder.setCrnNotExist(true);
        holder.setCrnJustification(""); // empty justification

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }

    /**
     * Builds a simple AccountUpdatingEvent for an Aircraft Operator account.
     * Minimal data, only what is required by the validator.
     */
    public AccountUpdatingEvent buildAircraftSuccessUpdateEvent(
            String registryId,
            String monitoringPlanId,
            String accountHolderName
    ) {

        // --- Account details ---
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId(registryId);
        details.setMonitoringPlanId(monitoringPlanId);
        details.setRegulator(RegulatorType.EA.name());

        // The service MUST be AIRCRAFT_OPERATOR_HOLDING_ACCOUNT
        details.setAccountType("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT");

        // Required for non-maritime
        details.setFirstYearOfVerifiedEmissions(2025);

        // Aircraft does NOT use regulated activities → leave null or empty
        details.setInstallationActivityTypes(null);

        // --- Account holder ---
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType(AccountHolderType.ORGANISATION.toString());
        holder.setName(accountHolderName);
        holder.setAddressLine1("Address");
        holder.setTownOrCity("City");
        holder.setCountry("GR");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("Integration test");

        // --- Full event ---
        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }

    public AccountUpdatingEvent buildInvalidAircraftUpdateEventWithValidationErrors() {

        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();

        // INVALID registry ID (not numeric)
        details.setRegistryId("ABC123");

        // Missing mandatory monitoring plan ID
        details.setMonitoringPlanId(null);

        // Invalid service (will trigger ERROR_0316)
        details.setAccountType("INVALID_SERVICE");

        // Invalid first year (will trigger ERROR_0311)
        details.setFirstYearOfVerifiedEmissions(2010);

        // Aviation does not use installationActivityTypes
        details.setInstallationActivityTypes(null);

        // --- INVALID Account Holder ---
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");

        // Missing mandatory name
        holder.setName("");

        // Missing mandatory details
        holder.setAddressLine1("");
        holder.setTownOrCity("");
        holder.setCountry(null);

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }

    /**
     * Builds a simple, fully-valid AccountUpdatingEvent
     * for a Maritime Operator account (happy path).
     */
    public AccountUpdatingEvent buildMaritimeSuccessUpdateEvent(
            String registryId,
            String monitoringPlanId,
            String companyImoNumber,
            String accountHolderName
    ) {

        // --- Account details ---
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId(registryId);
        details.setMonitoringPlanId(monitoringPlanId);
        details.setAccountType("MARITIME_OPERATOR_HOLDING_ACCOUNT");
        details.setFirstYearOfVerifiedEmissions(2026);
        details.setInstallationActivityTypes(null);
        // Maritime requires *company IMO number* (your validator checks this)
        details.setCompanyImoNumber(companyImoNumber);
        details.setRegulator(RegulatorType.EA.name());

        // --- Account holder ---
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");
        holder.setName(accountHolderName);
        holder.setAddressLine1("Address");
        holder.setTownOrCity("City");
        holder.setCountry("GR");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("Integration test");

        // --- Full event ---
        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }

    public AccountUpdatingEvent buildInvalidMaritimeUpdateEventWithValidationErrors() {

        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();

        // invalid registry ID (non-numeric)
        details.setRegistryId("654123");

        // missing monitoring plan ID (mandatory)
        details.setMonitoringPlanId(null);

        // missing IMO number (mandatory)
        details.setCompanyImoNumber(null);
        // invalid service
        details.setAccountType("WRONG_SERVICE");

        // emissions year too early
        details.setFirstYearOfVerifiedEmissions(1990);

        // Maritime does not use installation activities
        details.setInstallationActivityTypes(null);

        // Create invalid holder
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setName(null); // missing mandatory
        holder.setAccountHolderType("ORGANISATION");

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }

    /**
     * Scenario 1:
     * Tries to set FYVE to a year that is flagged as EXCLUDED (OHA/AOH/MOHA).
     * Should trigger ERROR_0315 during validation.
     */
    public AccountUpdatingEvent buildFYVEUpdateEvent_ExcludedYear(String registryId, int newFYVE) {

        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId(registryId);
        details.setAccountType("OPERATOR_HOLDING_ACCOUNT");

        // NEW FYVE THAT IS INVALID BECAUSE THERE ARE EXCLUDED ENTRIES BEFORE IT
        details.setFirstYearOfVerifiedEmissions(newFYVE);

        // minimal values for installation account
        details.setInstallationName("Test Installation");
        details.setPermitId("PERMIT-123");
        details.setInstallationActivityTypes(Set.of(InstallationActivityType.COMBUSTION_OF_FUELS.toString()));

        // Account holder
        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");
        holder.setName("Test Holder");
        holder.setAddressLine1("Address");
        holder.setTownOrCity("City");
        holder.setCountry("GR");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("Integration test");

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }


    /**
     * Scenario 2:
     * Tries to set FYVE to a year AFTER a year that has EMISSIONS.
     * Should trigger ERROR_0315.
     */
    public AccountUpdatingEvent buildFYVEUpdateEvent_AfterEmissions(String registryId, int newFYVE) {

        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId(registryId);
        details.setAccountType("OPERATOR_HOLDING_ACCOUNT");

        // Make FYVE invalid due to existing emissions before newFYVE
        details.setFirstYearOfVerifiedEmissions(newFYVE);

        details.setInstallationName("Test Installation");
        details.setPermitId("PERMIT-123");
        details.setInstallationActivityTypes(Set.of(InstallationActivityType.COMBUSTION_OF_FUELS.toString()));

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");
        holder.setName("Test Holder");
        holder.setAddressLine1("Address");
        holder.setTownOrCity("City");
        holder.setCountry("GR");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("Integration test");

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }


    /**
     * Scenario 3:
     * Tries to set FYVE to a year AFTER a year that has ALLOCATIONS.
     * Allocations are treated like emissions/excluded in FYVE logic → Should trigger ERROR_0315.
     */
    public AccountUpdatingEvent buildFYVEUpdateEvent_AfterAllocations(String registryId, int newFYVE) {

        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId(registryId);
        details.setAccountType("OPERATOR_HOLDING_ACCOUNT");

        // FYVE that conflicts with allocations (simulated by emissions/excluded)
        details.setFirstYearOfVerifiedEmissions(newFYVE);

        details.setInstallationName("Test Installation");
        details.setPermitId("PERMIT-123");
        details.setInstallationActivityTypes(Set.of(InstallationActivityType.COMBUSTION_OF_FUELS.toString()));

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");
        holder.setName("Test Holder");
        holder.setAddressLine1("Address");
        holder.setTownOrCity("City");
        holder.setCountry("GR");
        holder.setCrnNotExist(true);
        holder.setCrnJustification("Integration test");

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holder);

        return event;
    }
}
