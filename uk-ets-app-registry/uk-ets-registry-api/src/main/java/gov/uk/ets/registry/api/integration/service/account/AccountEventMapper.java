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
import uk.gov.netz.integration.model.account.*;

import java.util.Optional;

@Log4j2
@Component
public class AccountEventMapper {

    private static final String UK = "UK";

    public AccountDTO convert(AccountOpeningEvent event, OperatorType operatorType) {
        AccountDTO accountDTO = new AccountDTO();

        AccountDetailsMessage details = event.getAccountDetails();
        accountDTO.setAccountDetails(buildAccountDetails(details.getEmitterId()));
        accountDTO.setAccountType(resolveAccountType(operatorType));

        accountDTO.setOperator(buildOperator(details, operatorType));
        accountDTO.setAccountHolder(buildAccountHolder(event.getAccountHolder()));
        accountDTO.setAccountHolderContactInfo(defaultPrimaryContact(buildAddress(event.getAccountHolder())));
        accountDTO.setTrustedAccountListRules(defaultTrustedAccountListRules());
        return accountDTO;
    }

    public AccountDTO convert(AccountUpdatingEvent event, OperatorType operatorType) {
        AccountDTO accountDTO = new AccountDTO();

        UpdateAccountDetailsMessage details = event.getAccountDetails();

        // Only the fields used in UpdateAccountService
        accountDTO.setIdentifier(Long.valueOf(details.getRegistryId()));
        accountDTO.setAccountHolder(buildAccountHolderForUpdate(event.getAccountHolder()));
        accountDTO.setOperator(buildOperatorForUpdate(details, operatorType));

        return accountDTO;
    }

    private AccountDetailsDTO buildAccountDetails(String name) {
        AccountDetailsDTO dto = new AccountDetailsDTO();
        dto.setName(name);
        return dto;
    }

    private OperatorDTO buildOperator(AccountDetailsMessage details, OperatorType type) {
        OperatorDTO dto = new OperatorDTO();
        dto.setType(type.name());
        dto.setRegulator(RegulatorType.valueOf(details.getRegulator()));
        dto.setFirstYear(details.getFirstYearOfVerifiedEmissions());
        dto.setEmitterId(details.getEmitterId());

        switch (type) {
            case INSTALLATION -> {
                dto.setName(details.getInstallationName());
                Set<InstallationActivityType> activityTypes = details.getInstallationActivityTypes()
                        .stream()
                        .map(InstallationActivityType::valueOf)
                        .collect(Collectors.toSet());
                dto.setActivityTypes(activityTypes);

                PermitDTO permit = new PermitDTO();
                permit.setId(details.getPermitId());
                dto.setPermit(permit);
            }

            case AIRCRAFT_OPERATOR -> {
                MonitoringPlanDTO plan = new MonitoringPlanDTO();
                plan.setId(details.getMonitoringPlanId());
                dto.setMonitoringPlan(plan);
                setupMonitoringPlan(details, dto);
            }

            case MARITIME_OPERATOR -> {
                MonitoringPlanDTO plan = new MonitoringPlanDTO();
                String planId = Optional.ofNullable(details.getMonitoringPlanId())
                        .orElseGet(details::getEmitterId);
                plan.setId(planId);
                dto.setMonitoringPlan(plan);
                setupMonitoringPlan(details, dto);
                dto.setImo(details.getCompanyImoNumber());
            }

            default -> throw new IllegalArgumentException("Unsupported operator type: " + type);
        }

        return dto;
    }

    private OperatorDTO buildOperatorForUpdate(UpdateAccountDetailsMessage details, OperatorType type) {
        OperatorDTO dto = new OperatorDTO();
        dto.setType(type.name());
        dto.setRegulator(RegulatorType.valueOf(details.getRegulator()));
        dto.setFirstYear(details.getFirstYearOfVerifiedEmissions());

        switch (type) {
            case INSTALLATION -> {
                dto.setName(details.getInstallationName());
                dto.setActivityTypes(details.getInstallationActivityTypes().stream()
                        .map(String::trim)
                        .map(InstallationActivityType::valueOf)
                        .collect(Collectors.toSet()));
                PermitDTO permit = new PermitDTO();
                permit.setId(details.getPermitId());
                dto.setPermit(permit);
            }
            case AIRCRAFT_OPERATOR -> {
                MonitoringPlanDTO plan = new MonitoringPlanDTO();
                plan.setId(details.getMonitoringPlanId());
                dto.setMonitoringPlan(plan);
            }
            case MARITIME_OPERATOR -> {
                MonitoringPlanDTO plan = new MonitoringPlanDTO();
                plan.setId(details.getMonitoringPlanId());
                dto.setMonitoringPlan(plan);
                dto.setImo(details.getCompanyImoNumber());
            }
        }
        return dto;
    }


    private AccountHolderDTO buildAccountHolder(AccountHolderMessage msg) {
        AccountHolderDTO holder = new AccountHolderDTO();
        AccountHolderType type = AccountHolderType.valueOf(msg.getAccountHolderType());
        holder.setType(type);
        holder.setDetails(buildDetails(msg, type));
        holder.setAddress(buildAddress(msg));
        if (type == AccountHolderType.INDIVIDUAL) {
            holder.setPhoneNumber(getDefaultPhoneNumber());
            holder.setEmailAddress(getDefaultEmailAddress());
        }
        return holder;
    }

    private DetailsDTO buildDetails(AccountHolderMessage msg, AccountHolderType type) {
        DetailsDTO details = new DetailsDTO();
        details.setName(msg.getName());
        if (type == AccountHolderType.INDIVIDUAL) {
            details.setFirstName(msg.getName());
            details.setLastName(msg.getName());
            details.setBirthCountry("-");
        } else if (Boolean.TRUE.equals(msg.getCrnNotExist())) {
            details.setNoRegistrationNumJustification(msg.getCrnJustification());
        } else {
            details.setRegistrationNumber(msg.getCompanyRegistrationNumber());
        }
        return details;
    }

    private AddressDTO buildAddress(AccountHolderMessage msg) {
        AddressDTO addr = new AddressDTO();
        addr.setLine1(msg.getAddressLine1());
        addr.setLine2(msg.getAddressLine2());
        addr.setCity(msg.getTownOrCity());
        addr.setStateOrProvince(msg.getStateOrProvince());
        addr.setCountry(msg.getCountry());
        if (UK.equals(msg.getCountry()) && (msg.getPostalCode() == null || msg.getPostalCode().isBlank())) {
            addr.setPostCode("Not Provided by METS");
        } else {
            addr.setPostCode(msg.getPostalCode());
        }
        return addr;
    }

    private AccountHolderDTO buildAccountHolderForUpdate(AccountHolderMessage msg) {
        AccountHolderDTO holder = new AccountHolderDTO();
        holder.setAddress(buildAddress(msg));
        holder.setDetails(buildDetailsForUpdate(msg));
        return holder;
    }

    private DetailsDTO buildDetailsForUpdate(AccountHolderMessage msg) {
        DetailsDTO details = new DetailsDTO();
        details.setName(msg.getName());
        details.setRegistrationNumber(msg.getCompanyRegistrationNumber());
        details.setNoRegistrationNumJustification(msg.getCrnJustification());
        return details;
    }


    private String resolveAccountType(OperatorType type) {
        return switch (type) {
            case INSTALLATION -> "OPERATOR_HOLDING_ACCOUNT";
            case INSTALLATION_TRANSFER -> null;
            case AIRCRAFT_OPERATOR -> "AIRCRAFT_OPERATOR_HOLDING_ACCOUNT";
            case MARITIME_OPERATOR -> "MARITIME_OPERATOR_HOLDING_ACCOUNT";
        };
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
