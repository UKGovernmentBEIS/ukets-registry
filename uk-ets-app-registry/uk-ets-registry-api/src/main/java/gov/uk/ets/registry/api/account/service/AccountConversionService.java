package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.AircraftOperator;
import gov.uk.ets.registry.api.account.domain.Installation;
import gov.uk.ets.registry.api.account.domain.SalesContact;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegistrationNumberType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.DateInfo;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.MonitoringPlanDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.PermitDTO;
import gov.uk.ets.registry.api.account.web.model.SalesContactDetailsDTO;
import gov.uk.ets.registry.api.common.ConversionException;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.transaction.domain.data.AccountInfo;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service for converting account management entities to transfer objects and
 * vice versa.
 */
@Service
public class AccountConversionService {

    /**
     * Common conversion service.
     */
    private final ConversionService conversionService;

    public AccountConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Creates an {@link AccountHolder} entity.
     *
     * @param input The account holder transfer object.
     * @return an account holder.
     */
    AccountHolder convert(AccountHolderDTO input) {
        AccountHolder result = new AccountHolder();
        result.setType(input.getType());

        DetailsDTO details = input.getDetails();
        if (details != null) {
            try {
                BeanUtils.copyProperties(result, details);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConversionException(e.getMessage());
            }
            result.setRegistrationNumber(details.getRegistrationNumber());
            result.setNoRegNumjustification(details.getNoRegistrationNumJustification());
            if (details.getBirthDateInfo() != null) {
                try {
                    result.setBirthDate(details.getBirthDateInfo().toDate());
                } catch (ParseException e) {
                    // TODO: Fix the validation logic to return 400 http status code to the client
                    // when the
                    // dto is
                    // invalid.
                    throw new IllegalStateException("The birth is invalid" + details.getBirthDateInfo());
                }
            }
        }

        return result;
    }

    /**
     * Converts an {@link AccountHolderDTO} transfer object.
     *
     * @param input the account holder.
     * @return an account holder transfer object.
     */
    public AccountHolderDTO convert(AccountHolder input) {
        AccountHolderDTO result = new AccountHolderDTO();
        result.setType(input.getType());

        DetailsDTO details = new DetailsDTO();
        try {
            BeanUtils.copyProperties(details, input);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConversionException(e.getMessage());
        }
        details.setRegistrationNumber(Objects.toString(input.getRegistrationNumber(), ""));
        details.setNoRegistrationNumJustification(Objects.toString(input.getNoRegNumjustification(), ""));
        if (input.getNoRegNumjustification() != null && !input.getNoRegNumjustification().equals("")) {
            details.setRegNumTypeRadio(RegistrationNumberType.REGISTRATION_NUMBER_REASON.getType());
        } else {
            details.setRegNumTypeRadio(RegistrationNumberType.REGISTRATION_NUMBER.getType());
        }
        if (input.getBirthDate() != null) {
            details.setBirthDateInfo(DateInfo.of(input.getBirthDate()));
        }
        result.setDetails(details);
        result.getDetails().setName(result.actualName());
        result.setId(input.getIdentifier());

        Contact contact = input.getContact();
        if (contact != null) {
            result.setAddress(conversionService.getAddressFromContact(contact));
            result.setEmailAddress(conversionService.convertEmailAddress(contact));
            result.setPhoneNumber(conversionService.convertPhoneNumber(contact));
        }

        return result;
    }

    /**
     * Converts an {@link AccountHolderRepresentativeDTO} transfer object.
     *
     * @param input the account holder representative entity input
     * @return a new AccountHolderRepresentative DTO
     */
    public AccountHolderRepresentativeDTO convert(AccountHolderRepresentative input) {
        AccountHolderRepresentativeDTO result = new AccountHolderRepresentativeDTO();
        result.setId(input.getId());

        LegalRepresentativeDetailsDTO details = new LegalRepresentativeDetailsDTO();
        details.setFirstName(input.getFirstName());
        details.setLastName(input.getLastName());
        details.setAka(input.getAlsoKnownAs());
        if (input.getBirthDate() != null) {
            details.setBirthDateInfo(DateInfo.of(input.getBirthDate()));
        }

        result.setDetails(details);

        Contact contact = input.getContact();
        if (contact != null) {
            result.setAddress(conversionService.convertAddress(contact));
            result.setEmailAddress(conversionService.convertEmailAddress(contact));
            result.setPhoneNumber(conversionService.convertPhoneNumber(contact));
            result.setPositionInCompany(contact.getPositionInCompany());
        }

        return result;
    }

    /**
     * Converts a list of {@link AccountHolderRepresentative}.
     *
     * @param input the list of account holder representative entities
     * @return a list of account holder representative dtos
     * @deprecated use primary and alternative contacts
     */
    @Deprecated(since = "v0.8.0", forRemoval = true)
    List<AccountHolderRepresentativeDTO> convert(List<AccountHolderRepresentative> input) {
        List<AccountHolderRepresentativeDTO> result = new ArrayList<>();
        for (AccountHolderRepresentative lr : input) {
            result.add(this.convert(lr));
        }

        return result;
    }

    /**
     * Converts an {@link InstallationOrAircraftOperatorDTO}.
     *
     * @param input an installation.
     * @return a transfer object.
     */
    InstallationOrAircraftOperatorDTO convert(Installation input) {
        InstallationOrAircraftOperatorDTO result = new InstallationOrAircraftOperatorDTO();
        result.setIdentifier(input.getIdentifier());
        result.setType(OperatorType.INSTALLATION.name());
        result.setActivityType(InstallationActivityType.valueOf(input.getActivityType()));
        result.setFirstYear(input.getStartYear());
        result.setLastYear(input.getEndYear());
        result.setName(input.getInstallationName());
        PermitDTO permitDTO = new PermitDTO();
        permitDTO.setId(input.getPermitIdentifier());
        result.setPermit(permitDTO);
        result.setRegulator(input.getRegulator());
        result.setChangedRegulator(input.getChangedRegulator());

        return result;
    }

    /**
     * Converts an {@link InstallationOrAircraftOperatorDTO}.
     *
     * @param input an aircraft operator.
     * @return a transfer object.
     */
    InstallationOrAircraftOperatorDTO convert(AircraftOperator input) {
        InstallationOrAircraftOperatorDTO result = new InstallationOrAircraftOperatorDTO();
        result.setIdentifier(input.getIdentifier());
        result.setType(OperatorType.AIRCRAFT_OPERATOR.name());
        result.setFirstYear(input.getStartYear());
        result.setLastYear(input.getEndYear());
        MonitoringPlanDTO monitoringPlanDTO = new MonitoringPlanDTO();
        monitoringPlanDTO.setId(input.getMonitoringPlanIdentifier());
        result.setMonitoringPlan(monitoringPlanDTO);
        result.setRegulator(input.getRegulator());
        result.setChangedRegulator(input.getChangedRegulator());
        return result;
    }

    /**
     * Converts account information to {@link AccountInfo}.
     *
     * @param accountIdentifier     the account identifier.
     * @param accountFullIdentifier the account full identifier.
     * @param accountDTO            the Account DTO
     * @return account info.
     * @deprecated replaced by {@link AccountService#getAccountInfo(Long)}
     */
    @Deprecated(since = "v0.8.0", forRemoval = true)
    public AccountInfo getAccountInfo(Long accountIdentifier, String accountFullIdentifier, AccountDTO accountDTO) {
        AccountInfo.AccountInfoBuilder builder = AccountInfo.builder();
        builder.identifier(accountIdentifier).fullIdentifier(accountFullIdentifier);
        if (accountDTO.getAccountDetails() != null) {
            builder.accountName(accountDTO.getAccountDetails().getName())
                .accountHolderName(accountDTO.getAccountDetails().getAccountHolderName());
        }
        return builder.build();
    }
    
    /**
     * Converts an {@link SalesContactDetailsDTO} transfer object.
     *
     * @param input the sales contact.
     * @return a sales contact transfer object.
     */
    public SalesContactDetailsDTO convert(SalesContact input) {
    	SalesContactDetailsDTO result = new SalesContactDetailsDTO();
        result.setEmailAddress(conversionService.convertEmailAddress(input));
        result.setPhoneNumber(Objects.toString(input.getPhoneNumber(), ""));
        result.setPhoneNumberCountryCode(Objects.toString(input.getPhoneNumberCountry(), ""));
        return result;
    }
}
