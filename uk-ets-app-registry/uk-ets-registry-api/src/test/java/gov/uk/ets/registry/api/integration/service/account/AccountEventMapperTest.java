package gov.uk.ets.registry.api.integration.service.account;

import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.*;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.netz.integration.model.account.*;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AccountUpdatingEventConverterTest {

    private AccountEventMapper converter;

    @BeforeEach
    void setUp() {
        converter = new AccountEventMapper();
    }

    @Test
    void testConvertAccountUpdatingEvent_MapsOnlyRequiredFields() {
        UpdateAccountDetailsMessage details = new UpdateAccountDetailsMessage();
        details.setRegistryId("12345");
        details.setAccountName("Updated account");
        details.setAccountType("INSTALLATION_SERVICE");
        details.setFirstYearOfVerifiedEmissions(2025);
        details.setInstallationName("Test Installation");
        details.setInstallationActivityTypes(Set.of("UPSTREAM"));
        details.setPermitId("PERMIT-001");
        details.setRegulator(RegulatorType.OPRED.name());

        AccountHolderMessage holderMessage = new AccountHolderMessage();
        holderMessage.setName("ACME Corp");
        holderMessage.setCompanyRegistrationNumber("CRN123");
        holderMessage.setCrnJustification("No CRN");
        holderMessage.setAddressLine1("10 Downing St");
        holderMessage.setTownOrCity("London");
        holderMessage.setCountry("UK");
        holderMessage.setPostalCode("SW1A 2AA");

        AccountUpdatingEvent event = new AccountUpdatingEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holderMessage);

        AccountDTO dto = converter.convert(event, OperatorType.INSTALLATION);
         // Fields that should be set
        assertThat(dto.getIdentifier()).isEqualTo(12345L);
        assertThat(dto.getOperator()).isNotNull();
        assertThat(dto.getAccountHolder()).isNotNull();

        // Operator mapping
        OperatorDTO operator = dto.getOperator();
        assertThat(operator.getType()).isEqualTo("INSTALLATION");
        assertThat(operator.getFirstYear()).isEqualTo(2025);
        assertThat(operator.getName()).isEqualTo("Test Installation");
        assertThat(operator.getActivityTypes()).containsExactly(InstallationActivityType.UPSTREAM);
        assertThat(operator.getPermit().getId()).isEqualTo("PERMIT-001");
        assertThat(operator.getMonitoringPlan()).isNull();
        assertThat(operator.getImo()).isNull();
        assertThat(operator.getRegulator()).isEqualTo(RegulatorType.OPRED);

        // AccountHolder mapping
        AccountHolderDTO holder = dto.getAccountHolder();
        assertThat(holder.getAddress()).isNotNull();
        assertThat(holder.getAddress().getLine1()).isEqualTo("10 Downing St");
        assertThat(holder.getDetails()).isNotNull();
        assertThat(holder.getDetails().getName()).isEqualTo("ACME Corp");
        assertThat(holder.getDetails().getRegistrationNumber()).isEqualTo("CRN123");
        assertThat(holder.getDetails().getNoRegistrationNumJustification()).isEqualTo("No CRN");

        // Fields that should NOT be set on update
        assertThat(dto.getAccountType()).isNull();
        assertThat(dto.getTrustedAccountListRules()).isNull();
        assertThat(dto.getAccountHolderContactInfo()).isNull();

        // also verify that update-only mapper didn’t set defaults
        assertThat(holder.getPhoneNumber()).isNull();
        assertThat(holder.getEmailAddress()).isNull();
    }

    @Test
    void testConvertAccountOpeningEvent_SetsDefaultsAndMapsProperly() {
        // given
        AccountDetailsMessage details = new AccountDetailsMessage();
        details.setEmitterId("EMIT-001");
        details.setRegulator("OPRED");
        details.setFirstYearOfVerifiedEmissions(2025);
        details.setInstallationName("Test Installation");
        details.setInstallationActivityTypes(Set.of("UPSTREAM"));
        details.setPermitId("PERMIT-001");

        AccountHolderMessage holderMessage = new AccountHolderMessage();
        holderMessage.setAccountHolderType("INDIVIDUAL");
        holderMessage.setName("John Doe");
        holderMessage.setAddressLine1("123 Main St");
        holderMessage.setTownOrCity("London");
        holderMessage.setCountry("UK");
        holderMessage.setPostalCode("SW1A 2AA");

        AccountOpeningEvent event = new AccountOpeningEvent();
        event.setAccountDetails(details);
        event.setAccountHolder(holderMessage);

        AccountDTO dto = converter.convert(event, OperatorType.INSTALLATION);
        assertThat(dto).isNotNull();

        // Account details
        assertThat(dto.getAccountDetails()).isNotNull();
        assertThat(dto.getAccountDetails().getName()).isEqualTo("EMIT-001");
        assertThat(dto.getAccountType()).isEqualTo("OPERATOR_HOLDING_ACCOUNT");

        // Operator mapping
        OperatorDTO operator = dto.getOperator();
        assertThat(operator).isNotNull();
        assertThat(operator.getType()).isEqualTo("INSTALLATION");
        assertThat(operator.getName()).isEqualTo("Test Installation");
        assertThat(operator.getFirstYear()).isEqualTo(2025);
        assertThat(operator.getPermit().getId()).isEqualTo("PERMIT-001");
        assertThat(operator.getActivityTypes()).containsExactly(InstallationActivityType.UPSTREAM);

        // AccountHolder mapping
        AccountHolderDTO holder = dto.getAccountHolder();
        assertThat(holder).isNotNull();
        assertThat(holder.getType().name()).isEqualTo("INDIVIDUAL");
        assertThat(holder.getDetails()).isNotNull();
        assertThat(holder.getDetails().getName()).isEqualTo("John Doe");
        assertThat(holder.getDetails().getFirstName()).isEqualTo("John Doe");
        assertThat(holder.getDetails().getLastName()).isEqualTo("John Doe");
        assertThat(holder.getDetails().getBirthCountry()).isEqualTo("-");
        assertThat(holder.getAddress().getLine1()).isEqualTo("123 Main St");
        assertThat(holder.getAddress().getPostCode()).isEqualTo("SW1A 2AA");

        // Default email + phone
        assertThat(holder.getPhoneNumber()).isNotNull();
        assertThat(holder.getPhoneNumber().getCountryCode1()).isEqualTo("+44");
        assertThat(holder.getPhoneNumber().getPhoneNumber1()).isEqualTo("1234567890");
        assertThat(holder.getEmailAddress()).isNotNull();
        assertThat(holder.getEmailAddress().getEmailAddress())
                .isEqualTo("etregistryhelp@environment-agency.gov.uk");
        assertThat(holder.getEmailAddress().getEmailAddressConfirmation())
                .isEqualTo("etregistryhelp@environment-agency.gov.uk");

        // Contact Info defaults
        AccountHolderContactInfoDTO contactInfo = dto.getAccountHolderContactInfo();
        assertThat(contactInfo).isNotNull();
        AccountHolderRepresentativeDTO primary = contactInfo.getPrimaryContact();
        assertThat(primary).isNotNull();
        assertThat(primary.getPositionInCompany()).isEqualTo("Primary Contact");
        assertThat(primary.getAddress()).isNotNull();
        assertThat(primary.getPhoneNumber().getPhoneNumber1()).isEqualTo("1234567890");
        assertThat(primary.getEmailAddress().getEmailAddress())
                .isEqualTo("etregistryhelp@environment-agency.gov.uk");
        assertThat(primary.getDetails().getFirstName()).isEqualTo("Primary");
        assertThat(primary.getDetails().getLastName()).isEqualTo("Contact");

        // ✅ TAL rules defaults
        TrustedAccountListRulesDTO talRules = dto.getTrustedAccountListRules();
        assertThat(talRules).isNotNull();
        assertThat(talRules.getRule1()).isTrue();
        assertThat(talRules.getRule2()).isFalse();
        assertThat(talRules.getRule3()).isFalse();
    }
}
