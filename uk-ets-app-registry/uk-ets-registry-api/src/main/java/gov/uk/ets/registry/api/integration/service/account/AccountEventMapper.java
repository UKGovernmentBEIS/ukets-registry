package gov.uk.ets.registry.api.integration.service.account;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.MonitoringPlanDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.PermitDTO;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountHolderMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;

@Log4j2
@Component
public class AccountEventMapper {

    private static final String UK = "UK";

    public AccountDTO convert(AccountOpeningEvent event, OperatorType operatorType) {
        AccountDTO accountDTO = new AccountDTO();

        AccountDetailsMessage accountDetailsMessage = event.getAccountDetails();
        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();
        accountDetailsDTO.setName(accountDetailsMessage.getEmitterId());
        accountDTO.setAccountDetails(accountDetailsDTO);

        OperatorDTO operatorDTO = new OperatorDTO();
        operatorDTO.setType(operatorType.name());
        operatorDTO.setRegulator(RegulatorType.valueOf(accountDetailsMessage.getRegulator()));
        operatorDTO.setFirstYear(accountDetailsMessage.getFirstYearOfVerifiedEmissions());
        operatorDTO.setEmitterId(accountDetailsMessage.getEmitterId());
        if (operatorType == OperatorType.INSTALLATION) {
            accountDTO.setAccountType("OPERATOR_HOLDING_ACCOUNT");
            Set<InstallationActivityType> activityTypes = accountDetailsMessage.
                    getInstallationActivityTypes().
                    stream().
                    map(InstallationActivityType::valueOf).
                    collect(Collectors.toSet());
            operatorDTO.setActivityTypes(activityTypes);
            operatorDTO.setName(accountDetailsMessage.getInstallationName());
            PermitDTO permitDTO = new PermitDTO();
            permitDTO.setId(accountDetailsMessage.getPermitId());
            operatorDTO.setPermit(permitDTO);
        }
        if (operatorType == OperatorType.AIRCRAFT_OPERATOR) {
            accountDTO.setAccountType("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT");
            setupMonitoringPlan(accountDetailsMessage, operatorDTO);
        }
        if (operatorType == OperatorType.MARITIME_OPERATOR) {
            accountDTO.setAccountType("MARITIME_OPERATOR_HOLDING_ACCOUNT");
            setupMonitoringPlan(accountDetailsMessage, operatorDTO);
            operatorDTO.setImo(accountDetailsMessage.getCompanyImoNumber());
        }
        accountDTO.setOperator(operatorDTO);

        // Set up Account Holder
        AccountHolderMessage accountHolderMessage = event.getAccountHolder();
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        AccountHolderType accountHolderType = AccountHolderType.valueOf(accountHolderMessage.getAccountHolderType());
        accountHolderDTO.setType(accountHolderType);

        DetailsDTO detailsDTO = new DetailsDTO();
        detailsDTO.setName(accountHolderMessage.getName());
        if (accountHolderType == AccountHolderType.INDIVIDUAL) {
            accountHolderDTO.setPhoneNumber(getDefaultPhoneNumber());
            accountHolderDTO.setEmailAddress(getDefaultEmailAddress());
            detailsDTO.setFirstName(accountHolderMessage.getName());
            detailsDTO.setLastName(accountHolderMessage.getName());
            detailsDTO.setBirthCountry("-");
        } else if (Boolean.TRUE.equals(accountHolderMessage.getCrnNotExist())) {
            detailsDTO.setNoRegistrationNumJustification(accountHolderMessage.getCrnJustification());
        } else {
            detailsDTO.setRegistrationNumber(accountHolderMessage.getCompanyRegistrationNumber());
        }

        accountHolderDTO.setDetails(detailsDTO);

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setLine1(accountHolderMessage.getAddressLine1());
        addressDTO.setLine2(accountHolderMessage.getAddressLine2());
        addressDTO.setCity(accountHolderMessage.getTownOrCity());
        addressDTO.setStateOrProvince(accountHolderMessage.getStateOrProvince());
        addressDTO.setCountry(accountHolderMessage.getCountry());
        if (UK.equals(accountHolderMessage.getCountry())
            && (accountHolderMessage.getPostalCode() == null || accountHolderMessage.getPostalCode().isBlank())) {
            addressDTO.setPostCode("Not Provided by METS"); // Set Default value
        } else {
            addressDTO.setPostCode(accountHolderMessage.getPostalCode());
        }

        accountHolderDTO.setAddress(addressDTO);

        accountDTO.setAccountHolder(accountHolderDTO);

        // Setting Default values for primary contact as it is not provided
        AccountHolderContactInfoDTO accountHolderContactInfoDTO = defaultPrimaryContact(addressDTO);
        accountDTO.setAccountHolderContactInfo(accountHolderContactInfoDTO);

        // Setting Default values for TAL rules
        accountDTO.setTrustedAccountListRules(defaultTrustedAccountListRules());

        return accountDTO;
    }

    private void setupMonitoringPlan(AccountDetailsMessage accountDetailsMessage, OperatorDTO operatorDTO) {
        MonitoringPlanDTO monitoringPlanDTO = new MonitoringPlanDTO();
        if (accountDetailsMessage.getMonitoringPlanId() == null) {
            log.info("Monitoring Plan Id was not provided in the message. Setting emitterId in it's place.");
            monitoringPlanDTO.setId(accountDetailsMessage.getEmitterId());
        } else {
            monitoringPlanDTO.setId(accountDetailsMessage.getMonitoringPlanId());
        }
        operatorDTO.setMonitoringPlan(monitoringPlanDTO);
    }

    private AccountHolderContactInfoDTO defaultPrimaryContact(AddressDTO addressDTO) {
        AccountHolderContactInfoDTO accountHolderContactInfoDTO = new AccountHolderContactInfoDTO();
        AccountHolderRepresentativeDTO primaryContact = new AccountHolderRepresentativeDTO();
        primaryContact.setPositionInCompany("Primary Contact"); // Default value
        primaryContact.setAddress(addressDTO); // Set the same as AH

        primaryContact.setPhoneNumber(getDefaultPhoneNumber());
        primaryContact.setEmailAddress(getDefaultEmailAddress());

        LegalRepresentativeDetailsDTO representativeDetails = new LegalRepresentativeDetailsDTO();
        representativeDetails.setFirstName("Primary"); // Default value
        representativeDetails.setLastName("Contact"); // Default value
        representativeDetails.setAka(""); // Default value

        primaryContact.setDetails(representativeDetails);
        accountHolderContactInfoDTO.setPrimaryContact(primaryContact);
        return accountHolderContactInfoDTO;
    }

    private EmailAddressDTO getDefaultEmailAddress() {
        EmailAddressDTO emailAddressDTO = new EmailAddressDTO();
        emailAddressDTO.setEmailAddress("etregistryhelp@environment-agency.gov.uk"); // Default value
        emailAddressDTO.setEmailAddressConfirmation("etregistryhelp@environment-agency.gov.uk"); // Default value
        return emailAddressDTO;
    }

    private PhoneNumberDTO getDefaultPhoneNumber() {
        PhoneNumberDTO phoneNumberDTO = new PhoneNumberDTO();
        phoneNumberDTO.setCountryCode1("+44"); // Default value
        phoneNumberDTO.setPhoneNumber1("1234567890"); // Default value
        return phoneNumberDTO;
    }

    private TrustedAccountListRulesDTO defaultTrustedAccountListRules() {
        TrustedAccountListRulesDTO rulesDTO = new TrustedAccountListRulesDTO();
        rulesDTO.setRule1(true);
        rulesDTO.setRule2(false);
        rulesDTO.setRule3(false);
        return rulesDTO;
    }
}
