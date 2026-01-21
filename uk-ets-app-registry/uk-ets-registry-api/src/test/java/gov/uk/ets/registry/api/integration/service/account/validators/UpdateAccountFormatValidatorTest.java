package gov.uk.ets.registry.api.integration.service.account.validators;

import gov.uk.ets.registry.api.common.CountryMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.netz.integration.model.account.*;
import uk.gov.netz.integration.model.error.*;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateAccountFormatValidatorTest {

    private UpdateAccountFormatValidator validator;

    @BeforeEach
    void setup() {
        validator = new UpdateAccountFormatValidator();
    }

    private AccountUpdatingEvent baseValidEvent() {
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId("123456");
        details.setAccountType("OPERATOR_HOLDING_ACCOUNT");
        details.setPermitId("PERMIT-123");
        details.setInstallationName("Valid Name");
        details.setMonitoringPlanId("MPID");
        details.setCompanyImoNumber("IMO123");
        details.setFirstYearOfVerifiedEmissions(2025);
        details.setInstallationActivityTypes(Set.of("COMBUSTION_OF_FUELS"));

        AccountHolderMessage holder = new AccountHolderMessage();
        holder.setAccountHolderType("ORGANISATION");
        holder.setName("Holder Name");
        holder.setAddressLine1("Address 1");
        holder.setTownOrCity("Athens");
        holder.setCountry("GR");

        AccountUpdatingEvent ev = new AccountUpdatingEvent();
        ev.setAccountDetails(details);
        ev.setAccountHolder(holder);
        return ev;
    }

    // ---------------------------------------------------------------------
    // ERROR_0301 – SERVICE FORMAT
    // ---------------------------------------------------------------------
    @Test
    void shouldReturn0301WhenServiceHasInvalidFormat() {
        AccountUpdatingEvent ev = baseValidEvent();
        ev.getAccountDetails().setAccountType("invalid lowerCase");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev);

        assertThat(errors)
                .anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0301 &&
                        e.getErrorMessage().equals("Account Type"));
    }

    // ---------------------------------------------------------------------
    // ERROR_0301 – REGISTRY ID FORMAT
    // ---------------------------------------------------------------------
    @Test
    void shouldReturn0301WhenRegistryIdNotNumeric() {
        AccountUpdatingEvent ev = baseValidEvent();
        ev.getAccountDetails().setRegistryId("ABC123");

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev);

        assertThat(errors)
                .anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0301 &&
                        e.getErrorMessage().equals("Registry ID"));
    }

    // ---------------------------------------------------------------------
    // ERROR_0301 – INSTALLATION NAME FORMAT
    // ---------------------------------------------------------------------
    @Test
    void shouldReturn0301WhenInstallationNameTooLong() {
        AccountUpdatingEvent ev = baseValidEvent();
        ev.getAccountDetails().setInstallationName("X".repeat(300));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev);

        assertThat(errors)
                .anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0301 &&
                        e.getErrorMessage().equals("Installation Name"));
    }

    // ---------------------------------------------------------------------
    // ERROR_0301 – MONITORING PLAN ID FORMAT
    // ---------------------------------------------------------------------
    @Test
    void shouldReturn0301WhenMonitoringPlanIdTooLong() {
        AccountUpdatingEvent ev = baseValidEvent();
        ev.getAccountDetails().setMonitoringPlanId("X".repeat(300));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev);

        assertThat(errors)
                .anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0301 &&
                        e.getErrorMessage().equals("Monitoring Plan ID"));
    }

    // ---------------------------------------------------------------------
    // ERROR_0301 – COMPANY IMO NUMBER FORMAT
    // ---------------------------------------------------------------------
    @Test
    void shouldReturn0301WhenIMOTooLong() {
        AccountUpdatingEvent ev = baseValidEvent();
        ev.getAccountDetails().setCompanyImoNumber("X".repeat(300));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev);

        assertThat(errors)
                .anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0301 &&
                        e.getErrorMessage().equals("Company IMO number"));
    }

    // ---------------------------------------------------------------------
    // ERROR_0301 – ACCOUNT HOLDER NAME INVALID FORMAT
    // ---------------------------------------------------------------------
    @Test
    void shouldReturn0301WhenAccountHolderNameTooLong() {
        AccountUpdatingEvent ev = baseValidEvent();
        ev.getAccountHolder().setName("X".repeat(300));

        List<IntegrationEventErrorDetails> errors =
                validator.validate(ev);

        assertThat(errors)
                .anyMatch(e -> e.getError() == IntegrationEventError.ERROR_0301 &&
                        e.getErrorMessage().equals("Account Holder Name"));
    }
}
