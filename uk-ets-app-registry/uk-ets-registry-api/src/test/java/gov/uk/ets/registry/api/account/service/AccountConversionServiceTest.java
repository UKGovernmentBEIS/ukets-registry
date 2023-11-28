package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.account.domain.AccountHolder;
import gov.uk.ets.registry.api.account.domain.AccountHolderRepresentative;
import gov.uk.ets.registry.api.account.domain.types.RegistrationNumberType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.*;
import gov.uk.ets.registry.api.common.ConversionService;
import gov.uk.ets.registry.api.common.model.entities.Contact;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;

@DisplayName("Testing about converting objects which concern bidirectional convertion of " +
        "AccountHolderDTO to AccountHolder and AccountHolderRepresentativeDTO to AccountHolderRepresentative")
class AccountConversionServiceTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Mock
    private ConversionService conversionService;

    private AccountConversionService accountConversionService;

    private Contact contact;
    private AddressDTO addressDTO;
    private EmailAddressDTO emailAddressDTO;
    private PhoneNumberDTO phoneNumberDTO;
    private DateInfo dateInfo;
    private Calendar calendarInstance;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        accountConversionService = new AccountConversionService(conversionService);

        contact = new Contact();
        contact.setPositionInCompany("6");

        calendarInstance = Calendar.getInstance();
        calendarInstance.set(1999, Calendar.MAY, 23, 0, 0, 0);
        calendarInstance.set(Calendar.MILLISECOND, 0);

        dateInfo = new DateInfo();
        dateInfo.setDay("23");
        dateInfo.setMonth("05");
        dateInfo.setYear("1999");

        addressDTO = new AddressDTO();
        addressDTO.setCity("London");
        addressDTO.setLine1("31 Fleet Street");
        addressDTO.setLine3("31 Lime Street");
        addressDTO.setCountry("United Kingdom");
        addressDTO.setPostCode("SW1 1AA");
        addressDTO.setStateOrProvince("London State");

        emailAddressDTO = new EmailAddressDTO();
        emailAddressDTO.setEmailAddress("test@trasys.gr");
        emailAddressDTO.setEmailAddressConfirmation("test@trasys.gr");

        phoneNumberDTO = new PhoneNumberDTO();
        phoneNumberDTO.setCountryCode1("GR");
        phoneNumberDTO.setPhoneNumber1("6971111111");
        phoneNumberDTO.setCountryCode2("UK");
        phoneNumberDTO.setPhoneNumber2("7975777666");
    }

    @Test
    @DisplayName("case test when all the field data are properly filled")
    void testcaseWhenAllIsAsExpectedWithFilledAllFields() {

        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setContact(contact);
        accountHolder.setBirthCountry("Greece");
        accountHolder.setNoRegNumjustification("234563748756789568967896");
        accountHolder.setBirthDate(calendarInstance.getTime());

        Mockito.when(conversionService.getAddressFromContact(ArgumentMatchers.any())).thenReturn(addressDTO);
        Mockito.when(conversionService.convertEmailAddress(ArgumentMatchers.any(Contact.class))).thenReturn(emailAddressDTO);
        Mockito.when(conversionService.convertPhoneNumber(ArgumentMatchers.any())).thenReturn(phoneNumberDTO);

        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);

        AccountHolderDTO accountHolderDTOExpected = new AccountHolderDTO();
        DetailsDTO detailsExpected = new DetailsDTO();
        detailsExpected.setBirthCountry("Greece");
        detailsExpected.setRegNumTypeRadio(RegistrationNumberType.REGISTRATION_NUMBER_REASON.getType());
        detailsExpected.setBirthDateInfo(dateInfo);
        accountHolderDTOExpected.setDetails(detailsExpected);
        accountHolderDTOExpected.setAddress(addressDTO);
        accountHolderDTOExpected.setEmailAddress(emailAddressDTO);
        accountHolderDTOExpected.setPhoneNumber(phoneNumberDTO);

        Assert.assertEquals(accountHolderDTO.getType(), accountHolderDTOExpected.getType());
        Assert.assertNotNull(accountHolderDTO.getDetails());
        Assert.assertEquals(accountHolderDTO.getDetails().getRegNumTypeRadio(), accountHolderDTOExpected.getDetails().getRegNumTypeRadio());
        Assert.assertEquals(accountHolderDTO.getDetails().getBirthCountry(), accountHolderDTOExpected.getDetails().getBirthCountry());
        Assert.assertEquals(accountHolderDTO.getDetails().getBirthDateInfo().getDay(), accountHolderDTOExpected.getDetails().getBirthDateInfo().getDay());
        Assert.assertEquals(accountHolderDTO.getDetails().getBirthDateInfo().getMonth(), accountHolderDTOExpected.getDetails().getBirthDateInfo().getMonth());
        Assert.assertEquals(accountHolderDTO.getDetails().getBirthDateInfo().getYear(), accountHolderDTOExpected.getDetails().getBirthDateInfo().getYear());
        Assert.assertNotNull(accountHolderDTO.getAddress());
        Assert.assertNotNull(accountHolderDTO.getEmailAddress());
        Assert.assertNotNull(accountHolderDTO.getPhoneNumber());
        Assert.assertEquals(accountHolderDTO.getPhoneNumber().getCountryCode1(), accountHolderDTOExpected.getPhoneNumber().getCountryCode1());
        Assert.assertEquals(accountHolderDTO.getPhoneNumber().getCountryCode2(), accountHolderDTOExpected.getPhoneNumber().getCountryCode2());
        Assert.assertEquals(accountHolderDTO.getPhoneNumber().getPhoneNumber1(), accountHolderDTOExpected.getPhoneNumber().getPhoneNumber1());
        Assert.assertEquals(accountHolderDTO.getPhoneNumber().getPhoneNumber2(), accountHolderDTOExpected.getPhoneNumber().getPhoneNumber2());
        Assert.assertEquals(accountHolderDTO.getEmailAddress().getEmailAddress(), accountHolderDTOExpected.getEmailAddress().getEmailAddress());
        Assert.assertEquals(accountHolderDTO.getEmailAddress().getEmailAddressConfirmation(), accountHolderDTOExpected.getEmailAddress().getEmailAddressConfirmation());
        Assert.assertEquals(accountHolderDTO.getAddress().getCity(), accountHolderDTOExpected.getAddress().getCity());
        Assert.assertEquals(accountHolderDTO.getAddress().getStateOrProvince(), accountHolderDTOExpected.getAddress().getStateOrProvince());
        Assert.assertEquals(accountHolderDTO.getAddress().getCountry(), accountHolderDTOExpected.getAddress().getCountry());
        Assert.assertEquals(accountHolderDTO.getAddress().getLine1(), accountHolderDTOExpected.getAddress().getLine1());
        Assert.assertEquals(accountHolderDTO.getAddress().getLine2(), accountHolderDTOExpected.getAddress().getLine2());
        Assert.assertEquals(accountHolderDTO.getAddress().getLine3(), accountHolderDTOExpected.getAddress().getLine3());
        Assert.assertEquals(accountHolderDTO.getAddress().getPostCode(), accountHolderDTOExpected.getAddress().getPostCode());
    }

    @Test
    @DisplayName("case test when noRegNumjustification is empty")
    void testcaseWhenRegNoNumjustificationIsEmpty() {

        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setNoRegNumjustification("");
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);

        AccountHolderDTO accountHolderDTOExpected = new AccountHolderDTO();
        DetailsDTO detailsExpected = new DetailsDTO();
        detailsExpected.setRegNumTypeRadio(RegistrationNumberType.REGISTRATION_NUMBER.getType());
        accountHolderDTOExpected.setDetails(detailsExpected);

        Assert.assertNotNull(accountHolderDTO.getDetails());
        Assert.assertEquals(accountHolderDTO.getDetails().getRegNumTypeRadio(), accountHolderDTOExpected.getDetails().getRegNumTypeRadio());

    }

    @Test
    @DisplayName("case test when RegNumjustification is null")
    void testcaseWhenRegNumjustificationIsNull() {

        AccountHolder accountHolder = new AccountHolder();
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);

        AccountHolderDTO accountHolderDTOExpected = new AccountHolderDTO();
        DetailsDTO detailsExpected = new DetailsDTO();
        detailsExpected.setRegNumTypeRadio(RegistrationNumberType.REGISTRATION_NUMBER.getType());
        accountHolderDTOExpected.setDetails(detailsExpected);

        Assert.assertNotNull(accountHolderDTO.getDetails());
        Assert.assertEquals(accountHolderDTO.getDetails().getRegNumTypeRadio(), accountHolderDTOExpected.getDetails().getRegNumTypeRadio());

    }

    @Test
    @DisplayName("case test when date of birth is null")
    void testcaseWhenDateOfBirthIsNull() {

        AccountHolder accountHolder = new AccountHolder();
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);

        Assert.assertNotNull(accountHolderDTO.getDetails());
        Assert.assertNull(accountHolderDTO.getDetails().getBirthDateInfo());
    }

    @Test
    @DisplayName("test case when email is null")
    void testcaseWhenEmailIsNull() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setContact(contact);
        Mockito.when(conversionService.convertEmailAddress(ArgumentMatchers.any(Contact.class))).thenReturn(null);
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);
        Assert.assertNull(accountHolderDTO.getEmailAddress());
    }

    @Test
    @DisplayName("test case when address is null")
    void testcaseWhenAddressIsNull() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setContact(contact);
        Mockito.when(conversionService.convertAddress(ArgumentMatchers.any())).thenReturn(null);
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);
        Assert.assertNull(accountHolderDTO.getAddress());
    }

    @Test
    @DisplayName("test case when address is null")
    void testcaseWhenPhoneIsNull() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setContact(contact);
        Mockito.when(conversionService.convertPhoneNumber(ArgumentMatchers.any())).thenReturn(null);
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);
        Assert.assertNull(accountHolderDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("test case when contact is null")
    void testcaseWhenContactIsNull() {
        AccountHolder accountHolder = new AccountHolder();
        AccountHolderDTO accountHolderDTO = accountConversionService.convert(accountHolder);
        Assert.assertNull(accountHolderDTO.getAddress());
        Assert.assertNull(accountHolderDTO.getPhoneNumber());
        Assert.assertNull(accountHolderDTO.getEmailAddress());
    }

    @Test
    @DisplayName("test case when input is null")
    void testcaseInputIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> accountConversionService.convert((AccountHolder) null), "");
    }

    @Test
    @DisplayName("test case when converting account holder representative to respective dto")
    void testcaseWhenConvertingAccountHolderRepresentativeToDto() {
        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        accountHolderRepresentative.setBirthDate(calendarInstance.getTime());
        accountHolderRepresentative.setContact(contact);

        Mockito.when(conversionService.convertAddress(ArgumentMatchers.any())).thenReturn(addressDTO);
        Mockito.when(conversionService.convertEmailAddress(ArgumentMatchers.any(Contact.class))).thenReturn(emailAddressDTO);
        Mockito.when(conversionService.convertPhoneNumber(ArgumentMatchers.any())).thenReturn(phoneNumberDTO);

        AccountHolderRepresentativeDTO accountHolderRepresentativeDTO = accountConversionService.convert(accountHolderRepresentative);
        AccountHolderRepresentativeDTO accountHolderRepresentativeDTOExpected = new AccountHolderRepresentativeDTO();
        accountHolderRepresentativeDTOExpected.setPositionInCompany("6");
        accountHolderRepresentativeDTOExpected.setEmailAddress(emailAddressDTO);
        accountHolderRepresentativeDTOExpected.setAddress(addressDTO);
        accountHolderRepresentativeDTOExpected.setPhoneNumber(phoneNumberDTO);
        LegalRepresentativeDetailsDTO legalRepresentativeDetailsDTOExpected = new LegalRepresentativeDetailsDTO();
        legalRepresentativeDetailsDTOExpected.setBirthDateInfo(dateInfo);
        accountHolderRepresentativeDTOExpected.setDetails(legalRepresentativeDetailsDTOExpected);

        Assert.assertNotNull(accountHolderRepresentativeDTO.getDetails());
        Assert.assertEquals(accountHolderRepresentativeDTO.getDetails().getBirthDateInfo().getDay(), accountHolderRepresentativeDTOExpected.getDetails().getBirthDateInfo().getDay());
        Assert.assertEquals(accountHolderRepresentativeDTO.getDetails().getBirthDateInfo().getMonth(), accountHolderRepresentativeDTOExpected.getDetails().getBirthDateInfo().getMonth());
        Assert.assertEquals(accountHolderRepresentativeDTO.getDetails().getBirthDateInfo().getYear(), accountHolderRepresentativeDTOExpected.getDetails().getBirthDateInfo().getYear());
        Assert.assertNotNull(accountHolderRepresentativeDTO.getAddress());
        Assert.assertNotNull(accountHolderRepresentativeDTO.getEmailAddress());
        Assert.assertNotNull(accountHolderRepresentativeDTO.getPhoneNumber());
        Assert.assertEquals(accountHolderRepresentativeDTO.getPhoneNumber().getCountryCode1(), accountHolderRepresentativeDTOExpected.getPhoneNumber().getCountryCode1());
        Assert.assertEquals(accountHolderRepresentativeDTO.getPhoneNumber().getCountryCode2(), accountHolderRepresentativeDTOExpected.getPhoneNumber().getCountryCode2());
        Assert.assertEquals(accountHolderRepresentativeDTO.getPhoneNumber().getPhoneNumber1(), accountHolderRepresentativeDTOExpected.getPhoneNumber().getPhoneNumber1());
        Assert.assertEquals(accountHolderRepresentativeDTO.getPhoneNumber().getPhoneNumber2(), accountHolderRepresentativeDTOExpected.getPhoneNumber().getPhoneNumber2());
        Assert.assertEquals(accountHolderRepresentativeDTO.getEmailAddress().getEmailAddress(), accountHolderRepresentativeDTOExpected.getEmailAddress().getEmailAddress());
        Assert.assertEquals(accountHolderRepresentativeDTO.getEmailAddress().getEmailAddressConfirmation(), accountHolderRepresentativeDTOExpected.getEmailAddress().getEmailAddressConfirmation());
        Assert.assertEquals(accountHolderRepresentativeDTO.getAddress().getCity(), accountHolderRepresentativeDTOExpected.getAddress().getCity());
        Assert.assertEquals(accountHolderRepresentativeDTO.getAddress().getCountry(), accountHolderRepresentativeDTOExpected.getAddress().getCountry());
        Assert.assertEquals(accountHolderRepresentativeDTO.getAddress().getLine1(), accountHolderRepresentativeDTOExpected.getAddress().getLine1());
        Assert.assertEquals(accountHolderRepresentativeDTO.getAddress().getLine2(), accountHolderRepresentativeDTOExpected.getAddress().getLine2());
        Assert.assertEquals(accountHolderRepresentativeDTO.getAddress().getLine3(), accountHolderRepresentativeDTOExpected.getAddress().getLine3());
        Assert.assertEquals(accountHolderRepresentativeDTO.getAddress().getPostCode(), accountHolderRepresentativeDTOExpected.getAddress().getPostCode());
    }

    @Test
    @DisplayName("test case when email is null while converting AccountHolderRepresentative")
    void testcaseAccountHolderRepresentativeWhenEmailIsNull() {
        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        accountHolderRepresentative.setContact(contact);
        Mockito.when(conversionService.convertEmailAddress(ArgumentMatchers.any(Contact.class))).thenReturn(null);
        AccountHolderRepresentativeDTO accountHolderDTO = accountConversionService.convert(accountHolderRepresentative);
        Assert.assertNull(accountHolderDTO.getEmailAddress());
    }

    @Test
    @DisplayName("test case when address is null while converting AccountHolderRepresentative")
    void testcaseAccountHolderRepresentativeWhenAddressIsNull() {
        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        accountHolderRepresentative.setContact(contact);
        Mockito.when(conversionService.convertAddress(ArgumentMatchers.any())).thenReturn(null);
        AccountHolderRepresentativeDTO accountHolderDTO = accountConversionService.convert(accountHolderRepresentative);
        Assert.assertNull(accountHolderDTO.getAddress());
    }

    @Test
    @DisplayName("test case when address is null while converting AccountHolderRepresentative")
    void testcaseAccountHolderRepresentativeWhenPhoneIsNull() {
        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        accountHolderRepresentative.setContact(contact);
        Mockito.when(conversionService.convertPhoneNumber(ArgumentMatchers.any())).thenReturn(null);
        AccountHolderRepresentativeDTO accountHolderDTO = accountConversionService.convert(accountHolderRepresentative);
        Assert.assertNull(accountHolderDTO.getPhoneNumber());
    }

    @Test
    @DisplayName("test case when contact is null while converting AccountHolderRepresentative")
    void testcaseAccountHolderRepresentativeWhenContactIsNull() {
        AccountHolderRepresentative accountHolderRepresentative = new AccountHolderRepresentative();
        AccountHolderRepresentativeDTO accountHolderRepresentativeDTO = accountConversionService.convert(accountHolderRepresentative);
        Assert.assertNull(accountHolderRepresentativeDTO.getAddress());
        Assert.assertNull(accountHolderRepresentativeDTO.getPhoneNumber());
        Assert.assertNull(accountHolderRepresentativeDTO.getEmailAddress());
    }

    @Test
    @DisplayName("test case when input is null")
    void testcaseAccountHolderRepresentativeInputIsNull() {
        Assertions.assertThrows(NullPointerException.class, () -> accountConversionService.convert((AccountHolderRepresentative) null), "");
    }

    @Test
    @DisplayName("test case when converting account holder representative to respective dto")
    void testcaseWhenAccountHolderDTOToDomain() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        DetailsDTO detailsDTO = new DetailsDTO();
        detailsDTO.setRegistrationNumber("2345632335");
        detailsDTO.setNoRegistrationNumJustification("234563748756789568967896");
        detailsDTO.setBirthDateInfo(dateInfo);
        accountHolderDTO.setDetails(detailsDTO);

        AccountHolder accountHolder = accountConversionService.convert(accountHolderDTO);
        AccountHolder accountHolderExpected = new AccountHolder();
        accountHolderExpected.setNoRegNumjustification("234563748756789568967896");
        accountHolderExpected.setRegistrationNumber("2345632335");
        accountHolderExpected.setBirthDate(calendarInstance.getTime());

        Assert.assertNotNull(accountHolder.getBirthDate());
        Assert.assertEquals(accountHolder.getBirthDate().getTime(), accountHolderExpected.getBirthDate().getTime());
        Assert.assertEquals(accountHolder.getRegistrationNumber(), accountHolderExpected.getRegistrationNumber());
        Assert.assertEquals(accountHolder.getNoRegNumjustification(), accountHolderExpected.getNoRegNumjustification());

    }

    @Test
    @DisplayName("test case when converting account holder representative to respective dto and detailsDTO is null")
    void testcaseWhenAccountHolderDTOToDomainAndDetailsDTOIsNull() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();

        AccountHolder accountHolder = accountConversionService.convert(accountHolderDTO);
        AccountHolder accountHolderExpected = new AccountHolder();

        Assert.assertNull(accountHolder.getBirthDate());
        Assert.assertNull(accountHolder.getNoRegNumjustification());
        Assert.assertNull(accountHolder.getRegistrationNumber());

    }

    @Test
    @DisplayName("test case when converting account holder representative to respective dto and date of birth is null")
    void testcaseWhenAccountHolderDTOToDomainAndDateOfBirthIsNull() {
        AccountHolderDTO accountHolderDTO = new AccountHolderDTO();
        DetailsDTO detailsDTO = new DetailsDTO();
        detailsDTO.setRegistrationNumber("2345632335");
        detailsDTO.setNoRegistrationNumJustification("234563748756789568967896");
        accountHolderDTO.setDetails(detailsDTO);

        AccountHolder accountHolder = accountConversionService.convert(accountHolderDTO);
        AccountHolder accountHolderExpected = new AccountHolder();
        accountHolderExpected.setRegistrationNumber("2345632335");
        accountHolderExpected.setNoRegNumjustification("234563748756789568967896");

        Assert.assertNull(accountHolder.getBirthDate());
        Assert.assertEquals(accountHolder.getRegistrationNumber(), accountHolderExpected.getRegistrationNumber());
        Assert.assertEquals(accountHolder.getNoRegNumjustification(), accountHolderExpected.getNoRegNumjustification());

    }
}
