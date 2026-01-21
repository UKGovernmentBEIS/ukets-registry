package gov.uk.ets.registry.api.integration.service.metscontacts;

import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.validation.AccountHolderTypeMissmatchException;
import gov.uk.ets.registry.api.account.validation.AccountNotFoundValidationException;
import gov.uk.ets.registry.api.account.validation.AccountStatusValidationException;
import gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType;
import gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType;
import gov.uk.ets.registry.api.integration.service.account.validators.CommonAccountValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.netz.integration.model.metscontacts.MetsContactsMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gov.uk.ets.registry.api.account.web.model.accountcontact.MetsAccountContactType.*;
import static gov.uk.ets.registry.api.account.web.model.accountcontact.OperatorType.*;

@Component
@RequiredArgsConstructor
public class MetsContactsEventValidator {
    private static final Set<OperatorType> ALLOWED_USER_TYPES =
            Set.of(OPERATOR_ADMIN, OPERATOR, CONSULTANT_AGENT, EMITTER);

    private static final Set<MetsAccountContactType> ALLOWED_CONTACT_TYPES =
            Set.of(PRIMARY, SECONDARY, FINANCIAL, SERVICE);

    @Qualifier("metsContactsValidator")
    private final CommonAccountValidator metsContactsValidator;
    @Autowired
    private final AccountService accountService;

    public List<IntegrationEventErrorDetails> validate(MetsContactsEvent event,
                                                       gov.uk.ets.registry.api.account.web.model.OperatorType operatorType) throws AccountStatusValidationException {
        List<IntegrationEventErrorDetails> errorDetails = new ArrayList<>();
        validateOperatorId(event, errorDetails);
        String registryIdStr = event.getOperatorId();

        Long registryId = null;
        if (registryIdStr != null && registryIdStr.matches("^\\d+$")) {
            registryId = Long.valueOf(registryIdStr);
        } else {
            errorDetails.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0703, "Operator ID"));
        }

        // If registryId is invalid, skip the rest of the validation (no point calling DB)
        if (registryId == null) {
            return errorDetails;
        }
        validateAccountStatus(errorDetails, operatorType, registryId);
        for (MetsContactsMessage msg : event.getDetails()) {
            validateContact(msg, errorDetails);
        }
        return errorDetails;
    }

    private void validateOperatorId(MetsContactsEvent event,
                                    List<IntegrationEventErrorDetails> errors) {

        //TODO more complex validation here...
        metsContactsValidator.validateMandatoryField(event.getOperatorId(), "Operator ID", 256, errors);

    }

    private void validateContact(
            MetsContactsMessage msg,
            List<IntegrationEventErrorDetails> errors) {

        validateMandatoryFields(msg, errors);
        validateUserType(msg, errors);
        validateContactType(msg, errors);
    }

    private void validateMandatoryFields(
            MetsContactsMessage msg,
            List<IntegrationEventErrorDetails> errors) {

        metsContactsValidator.validateMandatoryField(msg.getFirstName(), "First name", 256, errors);
        metsContactsValidator.validateMandatoryField(msg.getLastName(), "Last name", 256, errors);

        metsContactsValidator.validateMandatoryField(msg.getTelephoneCountryCode(), "Telephone country code", 256, errors);
        metsContactsValidator.validateTelephoneCountryCode(msg.getTelephoneCountryCode(), "Telephone country code", IntegrationEventError.ERROR_0701, errors);

        metsContactsValidator.validateMandatoryField(msg.getTelephoneNumber(), "Telephone number", 256, errors);
        metsContactsValidator.validatePhoneNumber(msg.getTelephoneNumber(), "Telephone number", IntegrationEventError.ERROR_0701, errors);

        if (msg.getMobileNumber() != null && !msg.getMobileNumber().isBlank()) {
            // Validate country code as mandatory if phone number exists
            metsContactsValidator.validatePhoneNumber(msg.getMobileNumber(), "Mobile number", IntegrationEventError.ERROR_0701, errors);
            metsContactsValidator.validateMandatoryField(msg.getMobilePhoneCountryCode(), "Mobile phone country code", 256, errors);
            metsContactsValidator.validateTelephoneCountryCode(msg.getMobilePhoneCountryCode(), "Mobile phone country code", IntegrationEventError.ERROR_0701, errors);
        } else if(msg.getMobilePhoneCountryCode() != null && !msg.getMobilePhoneCountryCode().isBlank()) {
            // Validate no reason for mobile country code if not exists mobile -- logical case
            errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0701,"Mobile phone"));
        }

        metsContactsValidator.validateMandatoryField(msg.getEmail(), "Email", 256, errors);
        metsContactsValidator.validateEmail(msg.getEmail(), "Email", IntegrationEventError.ERROR_0701, errors);

    }

    private void validateUserType(
            MetsContactsMessage msg,
            List<IntegrationEventErrorDetails> errors) {

        String userType = msg.getUserType();
        if (userType == null || userType.isBlank()) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0702,
                    "User type"
            ));
            return;
        }

        OperatorType parsedType;
        try {
            parsedType = OperatorType.valueOf(userType);
        } catch (Exception ex) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0701,
                    "User type"
            ));
            return;
        }

        if (!ALLOWED_USER_TYPES.contains(parsedType)) {
            errors.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0702,
                    "User type"
            ));
        }
    }

    private void validateContactType(
            MetsContactsMessage msg,
            List<IntegrationEventErrorDetails> errors) {
        if (msg.getContactTypes() == null || msg.getContactTypes().isEmpty()) {
            return;
        }

        for (String contactType : msg.getContactTypes()) {

            MetsAccountContactType parsedType;
            if (contactType != null && !contactType.isBlank()) {
                try {
                    parsedType = MetsAccountContactType.valueOf(contactType);
                } catch (Exception ex) {
                    errors.add(new IntegrationEventErrorDetails(
                            IntegrationEventError.ERROR_0701,
                            "Contact type"
                    ));
                    return;
                }

                if (!ALLOWED_CONTACT_TYPES.contains(parsedType)) {
                    errors.add(new IntegrationEventErrorDetails(
                            IntegrationEventError.ERROR_0702,
                            "Contact type"
                    ));
                    return;
                }
            }
        }
    }

    private void validateAccountStatus(List<IntegrationEventErrorDetails> errorDetails,
                                      gov.uk.ets.registry.api.account.web.model.OperatorType operatorType,
                                      Long registryId) throws AccountStatusValidationException {
        try {
            boolean isValidAccountRules = accountService.validateAccountUpdateRules(operatorType, registryId);
            if(!isValidAccountRules) {
                errorDetails.add(new IntegrationEventErrorDetails(
                        IntegrationEventError.ERROR_0703,  // “Account not found”
                        "Operator ID"
                ));
            }
        } catch (AccountHolderTypeMissmatchException e) {
            return;
        } catch (AccountNotFoundValidationException ex) {
            errorDetails.add(new IntegrationEventErrorDetails(
                    IntegrationEventError.ERROR_0703,  // “Account not found”
                    "Operator ID"
            ));
        }
    }


}