package gov.uk.ets.registry.api.account.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.commons.lang3.tuple.Pair;


import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.account.web.model.OperatorType;
import gov.uk.ets.registry.api.account.web.model.PermitDTO;
import gov.uk.ets.registry.api.account.web.model.SalesContactDetailsDTO;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.NotEmpty;

/**
 * Validator for user registration.
 */
@Service
public class AccountValidator {

    private final AccountService accountService;

    private static int firstYear = 2021;
    private int minNumOfARs;
    private int maxNumOfARs;
    private  Map<String, Integer> minNumOfARsForAccountOpeningPerType;
    private  Map<String, Integer> maxNumOfARsForAccountOpeningPerType;

    // Added to bypass sonar checks.
    private String accountHolderKey = "account.accountHolder";

    /**
     * Constructor injection needed for @Value properties.
     */
    public AccountValidator(
        @NotEmpty @Value("${business.property.account.min.number.of.authorised.representatives}") Integer minNumOfARs,
        @NotEmpty @Value("${business.property.account.max.number.of.authorised.representatives}") Integer maxNumOfARs,
        @NotEmpty @Value("#{${business.property.account.opening.min.number.of.authorised.representatives.per.account.type.map}}") Map<String, Integer> minNumOfARsForAccountOpeningPerType,
        @NotEmpty @Value("#{${business.property.account.opening.max.number.of.authorised.representatives.per.account.type.map}}") Map<String, Integer> maxNumOfARsForAccountOpeningPerType,
        AccountService accountService) {
        this.minNumOfARs = minNumOfARs;
        this.maxNumOfARs = maxNumOfARs;
        this.minNumOfARsForAccountOpeningPerType = minNumOfARsForAccountOpeningPerType;
        this.maxNumOfARsForAccountOpeningPerType = maxNumOfARsForAccountOpeningPerType;
        this.accountService = accountService;
    }

    /**
     * Performs validation on the account input.
     *
     * @param account The account.
     */
    public void validate(AccountDTO account) {

        List<Violation> errors = new ArrayList<>();

        if (account != null) {
            //Validate account holder
            validateAccountHolder(errors, account.getAccountHolder());
            //Validate contacts
            validateContacts(errors, account.getAccountHolderContactInfo());

            String accountType = account.getAccountType();

            if (accountType != null &&
                (accountType.equals(AccountType.OPERATOR_HOLDING_ACCOUNT.name()) ||
                    accountType.equals(AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name()))) {
            	AccountType accountTypeEnum = AccountType.parse(accountType);
            	validateAccountDetails(errors, accountTypeEnum, account.getAccountDetails());
                validateOperator(errors, account.getOperator());
                validateAuthorisedRepresentatives(errors, account.getAuthorisedRepresentatives(), accountType);
            } else if (accountType != null && accountType.equals(AccountType.PERSON_HOLDING_ACCOUNT.name())) {
                validateAccountDetails(errors, AccountType.PERSON_HOLDING_ACCOUNT, account.getAccountDetails());
                validateAuthorisedRepresentatives(errors, account.getAuthorisedRepresentatives(),
                    AccountType.PERSON_HOLDING_ACCOUNT.name());
            } else if (accountType != null && accountType.equals(AccountType.TRADING_ACCOUNT.name())) {
            	validateAccountDetails(errors, AccountType.TRADING_ACCOUNT, account.getAccountDetails());
                validateAuthorisedRepresentatives(errors, account.getAuthorisedRepresentatives(),
                    AccountType.TRADING_ACCOUNT.name());
            } else {
                errors.add(new Violation("account.accountType.empty", "Empty or invalid account type"));
            }
        } else {
            errors.add(new Violation("account", "Account is mandatory"));
        }

        if (!errors.isEmpty()) {
            throw new AccountValidationException(errors);
        }
    }

    /**
     * Performs validation on the accountDetails input.
     *
     * @param accountDetails The accountDetails.
     */
    public void validate(AccountDetailsDTO accountDetails) {
        List<Violation> errors = new ArrayList<>();
    	
        if (accountDetails == null) {
            errors.add(new Violation("account.accountDetails.empty", "The account details is mandatory"));
            return;
        }
        
        AccountType accountType = AccountType.get(accountDetails.getAccountType());
        validateAccountDetails(errors, accountType, accountDetails);

        if (!errors.isEmpty()) {
            throw new AccountValidationException(errors);
        }
    }

    /**
     * This method checks the validity of account contacts.
     *
     * @param errors  the errors list.
     * @param accountType the account type enum
     * @param accountDetails the accountDetails.
     */
    private void validateAccountDetails(List<Violation> errors, AccountType accountType, AccountDetailsDTO accountDetails) {
    	final String source = "account.accountDetails";
        
        if(accountType == null) {
            errors.add(new Violation(String.format("%s.accountType.empty", source), "Empty or invalid account type"));
            return;
        }
    	
    	if (accountDetails == null) {
            errors.add(new Violation(String.format("%s.empty", source), "The account details is mandatory"));
            return;
        }

        if(AccountType.getTypesWithSalesContactDetails().contains(accountType)) {
        	validateSalesContactDetails(errors, accountDetails.getSalesContactDetails(), source);
        }

        if (AccountType.getTypesWithBillingDetails().contains(accountType)) {
            validateAddress(errors, accountDetails.getAddress(), source); //billing address
            validateBillingDetails(errors, accountDetails.getBillingContactDetails(), source);
        }
    }

    /**
     * This method checks the validity of account holder contacts.
     *
     * @param errors                      the errors list.
     * @param accountHolderContactInfoDTO the accountHolderContactInfoDTO.
     */
    private void validateContacts(List<Violation> errors, AccountHolderContactInfoDTO accountHolderContactInfoDTO) {
        //Validate account holder primary contact info

        AccountHolderRepresentativeDTO accountHolderPrimaryContact =
            accountHolderContactInfoDTO.getPrimaryContact();
        if (accountHolderContactInfoDTO.getPrimaryContact() != null) {
            validateContact(errors, accountHolderPrimaryContact, "account.accountHolderContactInfo.primaryContact");
        } else {
            errors.add(new Violation("account.accountHolderContactInfo.empty",
                "The account holder primary contact is mandatory"));
        }

        //Validate account holder alternative contact info, if exists
        AccountHolderRepresentativeDTO accountHolderAlternativeContact =
            accountHolderContactInfoDTO.getAlternativeContact();
        if (accountHolderAlternativeContact != null) {
            validateContact(errors, accountHolderAlternativeContact,
                "account.accountHolderContactInfo.alternativeContact");
        }
    }

    /**
     * This method checks the validity of account holder contacts.
     *
     * @param errors   the errors list.
     * @param operator the account operator.
     */
    private void validateOperator(List<Violation> errors, InstallationOrAircraftOperatorDTO operator) {
        if (operator == null) {
            errors.add(new Violation("account.operator.empty", "Operator is required for operator holding accounts"));
            return;
        }
        if (OperatorType.INSTALLATION.name().equals(operator.getType())) {
            validateInstallation(errors, operator);
        } else if (OperatorType.INSTALLATION_TRANSFER.name().equals(operator.getType())) {
            validateInstallationTransfer(errors, operator);
        } else if (OperatorType.AIRCRAFT_OPERATOR.name().equals(operator.getType())) {
            validateAircraftOperatingHoldingAccount(errors, operator);
        } else {
            errors.add(new Violation("account.operator.type.invalid", "The account operator type is invalid"));
        }

    }

    private void validateInstallation(List<Violation> errors, InstallationOrAircraftOperatorDTO operator) {
        if (operator.getRegulator() == null) {
            errors.add(new Violation("account.operator.activityType.empty", "Operator activity type is required"));
        }
        validateRegulator(errors, operator);
        if (operator.getActivityType() == null) {
            errors.add(new Violation("account.operator.activityType.empty", "Operator activity type is required"));
        } else if (!EnumUtils
            .isValidEnum(InstallationActivityType.class, operator.getActivityType().name())) {
            errors.add(new Violation("account.operator.activityType.invalid",
                "The account operator activity type is invalid"));
        }

        validatePermitId(errors, operator.getPermit());
        validatePermitDate(errors, operator.getPermit());
        //validate first year
        if (operator.getFirstYear() == null) {
            errors.add(new Violation("account.operator.firstyear.empty",
                "First year of verified emission submission is required"));
        } else if (operator.getFirstYear() < firstYear) {
            errors.add(new Violation("account.operator.firstyear.invalid",
                String.format("First year of verified emission submission must be %s or later", firstYear)));
        }

    }

    private void validateRegulator(List<Violation> errors, InstallationOrAircraftOperatorDTO operator) {
        if (operator.getRegulator() == null) {
            errors.add(new Violation("account.operator.regulator.empty", "operator.regulator must not be null"));
        }
    }


    private void validateInstallationTransfer(List<Violation> errors, InstallationOrAircraftOperatorDTO operator) {
        if( !operator.getPermit().getPermitIdUnchanged()) {
            validatePermitId(errors, operator.getPermit());
        }
    }


    private void validatePermitId(List<Violation> errors, PermitDTO permit) {
        if (permit == null) {
            errors.add(new Violation("account.operator.permit.empty", "Permit is required"));
        } else if (permit.getId().isEmpty()) {
            errors.add(new Violation("account.operator.permit.id.empty", "Permit ID is required"));
        } else if (accountService.installationPermitIdExists(permit.getId())) {
            errors.add(new Violation("account.operator.permit.id.exists",
                "An account with the same permit ID already exists. " +
                    "You cannot create another account with the same permit ID"));
        }

    }

    private void validatePermitDate(List<Violation> errors, PermitDTO permit) {
        if (permit == null) {
            errors.add(new Violation("account.operator.permit.empty", "Permit is required"));
        }
    }


    /**
     * This method checks the validity of account holder contacts.
     *
     * @param errors   the errors list.
     * @param operator the operator.
     */
    private void validateAircraftOperatingHoldingAccount(List<Violation> errors,
                                                         InstallationOrAircraftOperatorDTO operator) {
        if (operator == null) {
            errors.add(new Violation("account.operator.type.empty",
                "Operator is required for aircraft operator holding accounts"));
            return;
        }

        validateRegulator(errors, operator);

        if (operator.getMonitoringPlan() == null) {
            errors.add(new Violation("account.monitoringPlan.empty", "Monitoring plan is required"));
        } else if (operator.getMonitoringPlan().getId().isEmpty()) {
            errors.add(new Violation("account.monitoringPlan.id.empty", "Monitoring plan ID is required"));
        } else if (accountService.monitoringPlanIdExists(operator.getMonitoringPlan().getId())) {
            errors.add(new Violation("account.monitoringPlan.id.invalid",
                "An account with the same monitoring plan ID already exists"));
        }

        //validate first year
        if (operator.getFirstYear() == null) {
            errors.add(new Violation("account.operator.firstyear.empty",
                "First year of verified emission submission is required"));
        } else if (operator.getFirstYear() < firstYear) {
            errors.add(new Violation("account.operator.firstyear.invalid",
                String.format("First year of verified emission submission must be %s or later", firstYear)));
        }

    }


    /**
     * This method checks the validity of account holder contacts.
     *
     * @param errors        the errors list.
     * @param accountHolder the account holder.
     */
    private void validateAccountHolder(List<Violation> errors, AccountHolderDTO accountHolder) {
        if (accountHolder == null) {
            errors.add(new Violation("account.accountHolder.empty", "The account holder is mandatory"));
        } else {
            DetailsDTO accountHolderDetails = accountHolder.getDetails();
            if (accountHolderDetails == null) {
                errors.add(
                    new Violation("account.accountHolder.details.empty", "The account holder details is mandatory"));
            } else {
                AccountHolderType accountHolderType = accountHolder.getType();

                if (accountHolderType == null) {
                    errors
                        .add(new Violation("account.accountHolder.type.empty", "The account holder type is mandatory"));
                } else if (!EnumUtils.isValidEnum(AccountHolderType.class, accountHolderType.name())) {
                    //Validate that account holder type is a valid enum value
                    errors
                        .add(new Violation("account.accountHolder.type.invalid", "The account holder type is invalid"));
                } else if (EnumUtils.isValidEnum(AccountHolderType.class, accountHolderType.name()) &&
                    accountHolderType.equals(AccountHolderType.INDIVIDUAL)) {
                    validateAccountHolderIndividual(errors, accountHolder);
                } else if (EnumUtils.isValidEnum(AccountHolderType.class, accountHolderType.name()) &&
                    accountHolderType.equals(AccountHolderType.ORGANISATION)) {
                    validateAccountHolderOrganisation(errors, accountHolder);
                }
            }
            validateAddress(errors, accountHolder.getAddress(), accountHolderKey);
        }
    }

    /**
     * This method checks the contact phone number validity.
     *
     * @param errors        the error list.
     * @param accountHolder the accountHolder.
     */
    private void validateAccountHolderIndividual(List<Violation> errors, AccountHolderDTO accountHolder) {
        if (StringUtils.isBlank(accountHolder.getDetails().getFirstName())) {
            errors.add(new Violation("account.accountHolder.details.firstName.empty",
                "Individual account holder first and middle names are mandatory"));
        }
        if (StringUtils.isBlank(accountHolder.getDetails().getLastName())) {
            errors.add(new Violation("account.accountHolder.details.lastName.empty",
                "Individual account holder last name is mandatory"));
        }
        //Validate not null birth country
        if (StringUtils.isBlank(accountHolder.getDetails().getBirthCountry())) {
            errors.add(new Violation("account.accountHolder.details.birthcountry.empty",
                "Individual account holder birth country is mandatory"));
        }

        if (accountHolder.getPhoneNumber() == null) {
            errors.add(new Violation("account.accountHolder.phonenumber.empty", "Phone number is required"));
        } else {
            //Validate individual phones
            validatePhoneNumber(errors, accountHolder.getPhoneNumber(), accountHolderKey);
        }

        //Validate individual email
        validateEmailAddress(errors, accountHolder.getEmailAddress(), accountHolderKey);
    }

    /**
     * This method checks the contact phone number validity.
     *
     * @param errors        the error list.
     * @param accountHolder the accountHolder.
     */
    private void validateAccountHolderOrganisation(List<Violation> errors, AccountHolderDTO accountHolder) {

        if (StringUtils.isBlank(accountHolder.getDetails().getRegistrationNumber()) &&
            StringUtils.isBlank(accountHolder.getDetails().getNoRegistrationNumJustification())) {
            errors.add(new Violation("account.accountHolder.registrationNumber.error",
                "Account Holder registration number and no registration number justification " +
                    "cannot be empty at the same time"));
        }

        if (StringUtils.isBlank(accountHolder.getDetails().getName())) {
            errors.add(new Violation("account.accountHolder.details.name.empty", "Account " +
                "holder name is mandatory"));
        }
    }

    /**
     * This method checks the contact phone number validity.
     *
     * @param errors      the error list.
     * @param phoneNumber the phoneNumber.
     * @param source      The source property of the phone number.
     */
    private void validatePhoneNumber(List<Violation> errors, PhoneNumberDTO phoneNumber, String source) {

        if (phoneNumber != null) {
            validatePhone(errors, String.format("%s.phoneNumber1", source), phoneNumber.getCountryCode1(), phoneNumber.getPhoneNumber1());

            if (!StringUtils.isBlank(phoneNumber.getCountryCode2()) && !StringUtils.isBlank(phoneNumber.getPhoneNumber2())) {
                validatePhone(errors, String.format("%s.phoneNumber2", source), phoneNumber.getCountryCode2(), phoneNumber.getPhoneNumber2());
            }
        }
    }

    /**
     * This method checks the validity of an account holder contact.
     *
     * @param contact the account holder contact.
     * @param source  The source property of the contact.
     */
    private void validateContact(List<Violation> errors, AccountHolderRepresentativeDTO contact, String source) {
        if (contact != null) {
            validateAddress(errors, contact.getAddress(), source);
            validateEmailAddress(errors, contact.getEmailAddress(), source);

            if (StringUtils.isBlank(contact.getPositionInCompany())) {
                errors.add(new Violation(String.format("%s.position.empty", source), "Company position is required"));
            }

            if (contact.getPhoneNumber() == null) {
                errors.add(new Violation(String.format("%s.phonenumber.empty", source), "Phone number is required"));
            } else {
                //Validate contact's phone numbers
                validatePhoneNumber(errors, contact.getPhoneNumber(), source);
            }
        }
    }

    /**
     * This method checks the account authorised representatives list.
     *
     * @errors The error list.
     * @representatives the authorised representatives list.
     * @accountType the account type.
     */
    private void validateAuthorisedRepresentatives(List<Violation> errors,
                                                   List<AuthorisedRepresentativeDTO> representatives,
                                                   String accountType) {
        int numOfRepresentatives         = representatives != null ? representatives.size() : 0;
        int maxNumOfARsForAccountOpening = calculateMinMaxNumOfARsForAccountOpening(accountType).getRight();
        int minNumOfARsForAccountOpening = calculateMinMaxNumOfARsForAccountOpening(accountType).getLeft();
        

        if (numOfRepresentatives > maxNumOfARsForAccountOpening) {
            errors.add(new Violation("account.authorisedRepresentatives.max.number.violated",
                String.format("A maximum of %s authorised representatives can be nominated", maxNumOfARsForAccountOpening)));
        }

        if (numOfRepresentatives < minNumOfARsForAccountOpening) {
            errors.add(new Violation("account.authorisedRepresentatives.min.number.violated",
                String.format("A minimum of %s authorised representatives should be nominated", minNumOfARsForAccountOpening)));
        }
        
        if(numOfRepresentatives > 0 && arWithReadOnlyAccessExists(representatives)) {
        	errors.add(new Violation("account.authorisedRepresentatives.accessRight.violated",
        					"Read Only privileges for authorised representatives cannot be selected for account opening requests."));
        }

        // We don't need to check for Four Eyes Principle in OHA/AOHA types.
        if (AccountType.OPERATOR_HOLDING_ACCOUNT.name().equals(accountType) ||
            AccountType.AIRCRAFT_OPERATOR_HOLDING_ACCOUNT.name().equals(accountType)) {
            return;
        }

        checkCompliantWithFourEyesPrinciple(errors, representatives);
    }
    
    private boolean arWithReadOnlyAccessExists(List<AuthorisedRepresentativeDTO> representatives) {
    	if(CollectionUtils.isEmpty(representatives)) {
    		return false;
    	}
    	return representatives.stream()
							  .map(AuthorisedRepresentativeDTO::getRight)
							  .anyMatch(AccountAccessRight.READ_ONLY::equals);
    }

	private Pair<Integer, Integer> calculateMinMaxNumOfARsForAccountOpening(String accountType) {
		int minNumOfARsForAccountOpening = minNumOfARsForAccountOpeningPerType.get(accountType) != null
				? minNumOfARsForAccountOpeningPerType.get(accountType)
				: minNumOfARs;
		int maxNumOfARsForAccountOpening = maxNumOfARsForAccountOpeningPerType.get(accountType) != null
				&& maxNumOfARsForAccountOpeningPerType.get(accountType) <= this.maxNumOfARs
						? maxNumOfARsForAccountOpeningPerType.get(accountType)
						: maxNumOfARs;
		return Pair.of(minNumOfARsForAccountOpening, maxNumOfARsForAccountOpening);
	}
    
	private void checkCompliantWithFourEyesPrinciple(List<Violation> errors, List<AuthorisedRepresentativeDTO> representatives) {
		if (CollectionUtils.isEmpty(representatives) || representatives.size() < 2) {
			return;
		}

		boolean initiatorExists = representatives.stream()
				                                 .map(AuthorisedRepresentativeDTO::getRight)
				                                 .anyMatch(right -> right.containsRight(AccountAccessRight.INITIATE));
		if (!initiatorExists) {
			errors.add(new Violation("account.authorisedRepresentatives.four.eyes.principle.violated",
					"At least one of the Authorised Representatives needs to have permission to initiate transactions and Trusted Account List (TAL) updates."));

		}

		boolean approverExists = representatives.stream()
				                                .map(AuthorisedRepresentativeDTO::getRight)
				                                .anyMatch(right -> right.containsRight(AccountAccessRight.APPROVE));
		if (!approverExists) {
			errors.add(new Violation("account.authorisedRepresentatives.four.eyes.principle.violated",
					"At least one of the Authorised Representatives needs to have permission to approve transactions and Trusted Account List (TAL) updates."));

		}
	}

    /**
     * This method checks the validity of an address.
     *
     * @param errors  The error list.
     * @param address The address.
     * @param source  The source property of the address.
     */
    private void validateAddress(List<Violation> errors, AddressDTO address, String source) {
        if (address != null) {
            if (StringUtils.isBlank(address.getPostCode()) && address.getCountry() != null &&
                address.getCountry().equals("UK")) {
                errors.add(new Violation(String.format("%s.address.postcode.empty", source), "Postal Code or ZIP is mandatory"));
            }
        } else {
            errors.add(new Violation(String.format("%s.address.empty", source), "Address is mandatory"));
        }

    }

    /**
     * Validates the billing contact details.
     *
     * @param errors The error list.
     * @param billingDetails The account billing details.
     * @param source The source property of the address.
     */
    private void validateBillingDetails(List<Violation> errors, BillingContactDetailsDTO billingDetails, String source) {

        if (Objects.isNull(billingDetails)) {
            errors.add(new Violation(String.format("%s.billingContactDetails.empty", source),
                "The billing contact details is mandatory"));
            return;
        }

        if (StringUtils.isBlank(billingDetails.getContactName())) {
            errors.add(new Violation(String.format("%s.billingContactDetails.contactName.empty", source),
                "Billing contact name is mandatory"));
        }

        if (StringUtils.isBlank(billingDetails.getEmail())) {
            errors.add(new Violation(String.format("%s.billingContactDetails.email.empty", source),
                "Billing email is mandatory"));
        }

        validatePhone(errors, String.format("%s.billingContactDetails.phoneNumber", source),
            billingDetails.getPhoneNumberCountryCode(), billingDetails.getPhoneNumber());

    }

    /**
     * Validates the sales contact details.
     *
     * @param errors The error list.
     * @param salesContact The account sales contact details.
     * @param source The source property.
     */
	private void validateSalesContactDetails(List<Violation> errors, SalesContactDetailsDTO salesContact,
			String source) {
		
		if(salesContact == null) {
			return;
		}

		if (salesContact.getEmailAddress() != null && !salesContact.getEmailAddress().isEmpty()) {
			EmailAddressDTO email = salesContact.getEmailAddress();
			if (!email.getEmailAddress().equals(email.getEmailAddressConfirmation())) {
				errors.add(new Violation(String.format("%s.salesContactDetails.invalid", source),
						"Email address and email confirmation are different"));
			}
		}

		if (StringUtils.isNotEmpty(salesContact.getPhoneNumber())
				|| StringUtils.isNotEmpty(salesContact.getPhoneNumberCountryCode())) {
			validatePhone(errors, String.format("%s.salesContactDetails.phoneNumber", source),
					salesContact.getPhoneNumberCountryCode(), salesContact.getPhoneNumber());
		}
	}

    /**
     * Validates the phone number.
     *
     * @param errors The error list.
     * @param source The source property of the address.
     * @param countryCode The phone number country code.
     * @param phoneNumber The phone number.
     */
    private void validatePhone(List<Violation> errors, String source, String countryCode, String phoneNumber) {

        String code = Optional.ofNullable(countryCode).map(PhoneNumberUtil::normalizeDigitsOnly).orElse("");

        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber number = phoneUtil.parse("+" + code + phoneNumber, "");

            if (!phoneUtil.isValidNumber(number)) {
                errors.add(new Violation(String.format("%s.invalid", source), "Invalid phone number format"));
            }
        } catch (NumberParseException e) {
            errors.add(new Violation(String.format("%s.invalid", source), "Phone number is invalid"));
        }
    }

    /**
     * This method checks if email address and email confirmation address.
     *
     * @param errors       The error list.
     * @param emailAddress The email address object.
     * @param source       The source property of the email emailAddress.
     */
    private void validateEmailAddress(List<Violation> errors, EmailAddressDTO emailAddress, String source) {
        if (emailAddress != null) {
            if (emailAddress.getEmailAddress() == null ||
                emailAddress.getEmailAddressConfirmation() == null ||
                emailAddress.getEmailAddress().isEmpty() ||
                emailAddress.getEmailAddressConfirmation().isEmpty()) {
                errors.add(new Violation(String.format("%s.emailAddress.empty", source),
                    "Email address and email confirmation are mandatory"));
            } else if (!emailAddress.getEmailAddress().equals(emailAddress.getEmailAddressConfirmation())) {
                errors.add(new Violation(String.format("%s.emailAddress.invalid", source),
                    "Email address and email confirmation are different"));
            }
        } else {
            errors.add(new Violation(String.format("%s.empty", source), "Email address is mandatory"));
        }
    }
}
