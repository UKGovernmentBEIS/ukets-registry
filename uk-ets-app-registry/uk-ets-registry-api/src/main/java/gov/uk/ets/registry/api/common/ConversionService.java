package gov.uk.ets.registry.api.common;

import gov.uk.ets.registry.api.account.domain.SalesContact;
import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.ConversionParameters;
import gov.uk.ets.registry.api.common.view.DateDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import java.util.Date;

/**
 * Service for converting entities to transfer objects and vice versa.
 */
public interface ConversionService {

    /**
     * Creates an {@link Contact}.
     *
     * @param parameters {@link ConversionParameters } object that contains the email transfer object,
     *                   The address transfer object, the phone transfer object and the company position.
     * @return a contact
     */
    Contact convert(ConversionParameters parameters);

    /**
     * Converts a {@link DateDTO}.
     *
     * @param input the date input
     * @return a date DTO
     */
    DateDTO convert(Date input);

    /**
     * Converts a {@link Date}.
     *
     * @param input the dateDTO input
     * @return the converted date
     */
    Date convert(DateDTO input);

    /**
     * Converts an {@link AddressDTO}.
     *
     * @param input the contact entity input
     * @return the converted Address dto
     */
    AddressDTO getAddressFromContact(Contact input);

    /**
     * Converts an {@link BillingContactDetailsDTO}.
     *
     * @param input the contact entity input
     * @return the converted Address dto
     */
    BillingContactDetailsDTO getBillingDetailsFromContact(Contact input);

    /**
     * Extracts the e-mail address from the provided contact details.
     *
     * @param contact The contact details
     * @return an e-mail address transfer object
     */
    EmailAddressDTO convertEmailAddress(Contact contact);
    
    /**
     * Extracts the e-mail address from the provided sales contact details.
     *
     * @param contact The sales contact details
     * @return an e-mail address transfer object
     */
    EmailAddressDTO convertEmailAddress(SalesContact contact);

    /**
     * Extracts the address from the provided contact details.
     *
     * @param contact The contact details
     * @return an address transfer object
     */
    AddressDTO convertAddress(Contact contact);

    /**
     * Extracts the phone number from the provided contact details.
     *
     * @param contact The contact details
     * @return a phone number transfer object
     */
    PhoneNumberDTO convertPhoneNumber(Contact contact);

    /**
     * Converts an amount of bytes to a human readable form.
     * For example 450 B, 20 kB, 10 MB etc.
     *
     * @param amount The amount of bytes
     * @return a human readable string
     */
    String convertByteAmountToHumanReadable(long amount);
}
