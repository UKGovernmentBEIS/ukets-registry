package gov.uk.ets.registry.api.common;

import gov.uk.ets.registry.api.account.domain.SalesContact;
import gov.uk.ets.registry.api.account.web.model.BillingContactDetailsDTO;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.ConversionParameters;
import gov.uk.ets.registry.api.common.view.DateDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import java.lang.reflect.InvocationTargetException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import java.util.Optional;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * Implements the conversion service.
 */
@Service
public class ConversionServiceImpl implements ConversionService {

    /**
     * {@inheritDoc}
     */
    @Override
    public Contact convert(ConversionParameters parameters) {
        Contact result = new Contact();
        if (parameters.getEmail() != null) {
            try {
                BeanUtils.copyProperties(result, parameters.getEmail());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConversionException(e.getMessage());
            }
        }
        if (parameters.getAddress() != null) {
            try {
                BeanUtils.copyProperties(result, parameters.getAddress());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConversionException(e.getMessage());
            }
        }
        if (parameters.getPhone() != null) {
            try {
                BeanUtils.copyProperties(result, parameters.getPhone());
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConversionException(e.getMessage());
            }
        }
        if (parameters.getPositionInCompany() != null) {
            result.setPositionInCompany(parameters.getPositionInCompany());
        }

        if (parameters.getBillingContactDetails() != null) {
            BillingContactDetailsDTO billingContactDetails = parameters.getBillingContactDetails();
            try {
                BeanUtils.copyProperties(result, billingContactDetails);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConversionException(e.getMessage());
            }
            Optional.ofNullable(billingContactDetails.getEmail()).ifPresent(result::setEmailAddress);
            Optional.ofNullable(billingContactDetails.getPhoneNumberCountryCode()).ifPresent(result::setCountryCode1);
            Optional.ofNullable(billingContactDetails.getPhoneNumber()).ifPresent(result::setPhoneNumber1);
        }

        // todo: remove these deprecated values.
        if (parameters.getBillingEmail1() != null && result.getEmailAddress() == null) {
            result.setEmailAddress(parameters.getBillingEmail1());
        }
        if (parameters.getBillingEmail2() != null && result.getEmailAddress() == null) {
            result.setEmailAddress(parameters.getBillingEmail2());
        }
        return result;
    }

    @Override
    public DateDTO convert(Date input) {
        DateDTO result = new DateDTO();
        Calendar cal = Calendar.getInstance();
        cal.setTime(input);
        result.setDay(cal.get(Calendar.DAY_OF_MONTH));
        result.setMonth(cal.get(Calendar.MONTH) + 1);
        result.setYear(cal.get(Calendar.YEAR));

        return result;
    }

    @Override
    public Date convert(DateDTO input) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(input.getYear(),
            input.getMonth() - 1,
            input.getDay());

        return calendar.getTime();
    }

    @Override
    public AddressDTO getAddressFromContact(Contact input) {
        AddressDTO result = new AddressDTO();
        result.setCity(input.getCity());
        result.setCountry(input.getCountry());
        result.setLine1(Objects.toString(input.getLine1(), ""));
        result.setLine2(Objects.toString(input.getLine2(), ""));
        result.setLine3(Objects.toString(input.getLine3(), ""));
        result.setPostCode(Objects.toString(input.getPostCode(), ""));
        result.setStateOrProvince(Objects.toString(input.getStateOrProvince(), ""));

        return result;
    }

    @Override
    public BillingContactDetailsDTO getBillingDetailsFromContact(Contact input) {
        BillingContactDetailsDTO result = new BillingContactDetailsDTO();
        result.setContactName(input.getContactName());
        result.setPhoneNumber(input.getPhoneNumber1());
        result.setPhoneNumberCountryCode(input.getCountryCode1());
        result.setEmail(input.getEmailAddress());
        result.setSopCustomerId(Objects.toString(input.getSopCustomerId(), ""));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmailAddressDTO convertEmailAddress(Contact contact) {
        EmailAddressDTO result = new EmailAddressDTO();
        try {
            BeanUtils.copyProperties(result, contact);
            result.setEmailAddressConfirmation(contact.getEmailAddress());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConversionException(e.getMessage());
        }
        if (result.isEmpty()) {
            result = null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AddressDTO convertAddress(Contact contact) {
        AddressDTO result = new AddressDTO();
        try {
            BeanUtils.copyProperties(result, contact);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConversionException(e.getMessage());
        }
        if (result.isEmpty()) {
            result = null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PhoneNumberDTO convertPhoneNumber(Contact contact) {
        PhoneNumberDTO result = new PhoneNumberDTO();
        try {
            BeanUtils.copyProperties(result, contact);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConversionException(e.getMessage());
        }
        if (result.isEmpty()) {
            result = null;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertByteAmountToHumanReadable(long amount) {
        long absoluteBytes = amount == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(amount);
        if (absoluteBytes < 1024) {
            return amount + " B";
        }
        long value = absoluteBytes;
        CharacterIterator iterator = new StringCharacterIterator("kMGTPE");
        for (int index = 40; index >= 0 && absoluteBytes > 0xfffccccccccccccL >> index; index -= 10) {
            value >>= 10;
            iterator.next();
        }
        value *= Long.signum(amount);
        return String.format(Locale.ENGLISH, "%.1f %cB", value / 1024.0, iterator.current());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EmailAddressDTO convertEmailAddress(SalesContact contact) {
        EmailAddressDTO result = new EmailAddressDTO();
        try {
            BeanUtils.copyProperties(result, contact);
            result.setEmailAddressConfirmation(contact.getEmailAddress());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ConversionException(e.getMessage());
        }
        if (result.isEmpty()) {
            result = null;
        }
        return result;
    }

}
