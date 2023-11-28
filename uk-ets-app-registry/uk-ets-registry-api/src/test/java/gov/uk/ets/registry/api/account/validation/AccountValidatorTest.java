package gov.uk.ets.registry.api.account.validation;

import gov.uk.ets.registry.api.account.domain.types.AccountAccessRight;
import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.domain.types.InstallationActivityType;
import gov.uk.ets.registry.api.account.domain.types.RegulatorType;
import gov.uk.ets.registry.api.account.service.AccountService;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderContactInfoDTO;
import gov.uk.ets.registry.api.account.web.model.AccountHolderRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.AuthorisedRepresentativeDTO;
import gov.uk.ets.registry.api.account.web.model.DateInfo;
import gov.uk.ets.registry.api.account.web.model.DetailsDTO;
import gov.uk.ets.registry.api.account.web.model.InstallationOrAircraftOperatorDTO;
import gov.uk.ets.registry.api.account.web.model.LegalRepresentativeDetailsDTO;
import gov.uk.ets.registry.api.account.web.model.MonitoringPlanDTO;
import gov.uk.ets.registry.api.account.web.model.PermitDTO;
import gov.uk.ets.registry.api.account.web.model.SalesContactDetailsDTO;
import gov.uk.ets.registry.api.common.model.types.Status;
import gov.uk.ets.registry.api.common.view.AddressDTO;
import gov.uk.ets.registry.api.common.view.DateDTO;
import gov.uk.ets.registry.api.common.view.EmailAddressDTO;
import gov.uk.ets.registry.api.common.view.PhoneNumberDTO;
import gov.uk.ets.registry.api.transaction.domain.data.TrustedAccountListRulesDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

public class AccountValidatorTest {

    private AccountValidator validator;

    @Mock
    private AccountService accountService;
    
    static final Map<String, Integer> minNumOfARsForAccountOpeningPerType = Map.of(
    		"OPERATOR_HOLDING_ACCOUNT", 0, "AIRCRAFT_OPERATOR_HOLDING_ACCOUNT", 0, "TRADING_ACCOUNT", 2,"PERSON_HOLDING_ACCOUNT",2
    	);
    
    static final Map<String, Integer> maxNumOfARsForAccountOpeningPerType = Map.of(
    		"OPERATOR_HOLDING_ACCOUNT", 2, "AIRCRAFT_OPERATOR_HOLDING_ACCOUNT", 2, "TRADING_ACCOUNT", 2,"PERSON_HOLDING_ACCOUNT",2
    	);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        validator = new AccountValidator(2, 8, minNumOfARsForAccountOpeningPerType, maxNumOfARsForAccountOpeningPerType, accountService);
    }

    @Test
    @DisplayName("account holder individual with age under 18 is invalid")
    void test_account_holder_individual_age_under_18() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountHolder().getType()).thenReturn(AccountHolderType.INDIVIDUAL);
        DateInfo dateInfo = Mockito.mock(DateInfo.class);
        Mockito.when(dateInfo.getDay()).thenReturn("1");
        Mockito.when(dateInfo.getMonth()).thenReturn("12");
        Mockito.when(dateInfo.getYear()).thenReturn("2005");
        Mockito.when(account.getAccountHolder().getDetails().getBirthDateInfo()).thenReturn(dateInfo);
        Mockito.when(account.getAccountHolder().getDetails().getBirthCountry()).thenReturn("Country");
        AddressDTO addressDTO = Mockito.mock(AddressDTO.class);
        Mockito.when(addressDTO.getCountry()).thenReturn("UK");
        Mockito.when(addressDTO.getPostCode()).thenReturn("12211");
        Mockito.when(addressDTO.getCity()).thenReturn("London");
        Mockito.when(addressDTO.getLine1()).thenReturn("Line 1");
        Mockito.when(account.getAccountHolder().getAddress()).thenReturn(addressDTO);
        PhoneNumberDTO phoneNumberDTO = Mockito.mock(PhoneNumberDTO.class);
        Mockito.when(phoneNumberDTO.getCountryCode1()).thenReturn("UK (44)");
        Mockito.when(phoneNumberDTO.getPhoneNumber1()).thenReturn("7792777777");
        Mockito.when(account.getAccountHolder().getPhoneNumber()).thenReturn(phoneNumberDTO);
        EmailAddressDTO emailAddressDTO = Mockito.mock(EmailAddressDTO.class);
        Mockito.when(emailAddressDTO.getEmailAddress()).thenReturn("test@mail.com");
        Mockito.when(emailAddressDTO.getEmailAddressConfirmation()).thenReturn("test@mail.com");
        Mockito.when(account.getAccountHolder().getEmailAddress()).thenReturn(emailAddressDTO);
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }


    @Test
    @DisplayName("account holder address with empty postcode and selected country UK is invalid")
    void test_account_holder_address_empty_postcode_country_uk() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountHolder().getAddress().getCountry()).thenReturn("UK");
        Mockito.when(account.getAccountHolder().getAddress().getPostCode()).thenReturn("");
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("account holder address with empty postcode and selected country UK is invalid")
    void test_operator_account_holder_contact_info_invalid_phone_country_uk() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountHolder().getAddress().getCountry()).thenReturn("UK");
        Mockito.when(account.getAccountHolderContactInfo().getPrimaryContact().getPhoneNumber().getPhoneNumber1()).thenReturn("3445");
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("operator holding account with operator first year of verified emission before 2021 is invalid")
    void test_operator_holding_account_operator_firstyear_before_current_year() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getOperator().getFirstYear()).thenReturn(2020);
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("operator holding account with more than maximum authorised representatives is invalid")
    void test_operator_holding_account_representatives_more_than_max() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        ArrayList<AuthorisedRepresentativeDTO> list = new ArrayList<AuthorisedRepresentativeDTO>();
        IntStream.range(1, 4).forEach(nbr -> list.add(mockValidAuthorisedRepresentative()));
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(list);
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("operator holding account with valid minimum number of authorised representatives")
    void test_operator_holding_account_representatives_with_valid_min() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(new ArrayList<>());
        validator.validate(account);
    }

    @Test
    @DisplayName("trading account with less than minimum authorised representatives is invalid")
    void test_trading_account_representatives_less_than_min() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("TRADING_ACCOUNT");
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(new ArrayList<>());
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("operator holding account with same operator permit ID is invalid")
    void test_operator_holding_account_with_same_permit_id() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        Mockito.when(accountService.installationPermitIdExists(any())).thenReturn(true);
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("operator holding account with readOnly authorised representatives is not permitted")
    void test_operator_holding_account_representatives_readonly() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        ArrayList<AuthorisedRepresentativeDTO> list = new ArrayList<AuthorisedRepresentativeDTO>();
        IntStream.range(1, 3).forEach(nbr -> list.add(mockValidAuthorisedRepresentative()));
        list.add(mockAuthorisedRepresentative(AccountAccessRight.READ_ONLY));
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(list);
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    @DisplayName("operator holding account with no longer four eyes principle violated (only initiators exist)")
    void test_operator_holding_account_without_approver() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        ArrayList<AuthorisedRepresentativeDTO> list = new ArrayList<AuthorisedRepresentativeDTO>();
        IntStream.range(1, 3).forEach(nbr -> list.add(mockAuthorisedRepresentative(AccountAccessRight.INITIATE)));
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(list);
        validator.validate(account);
    }

    @Test
    @DisplayName("operator holding account with no longer four eyes principle violated (only approvers exist)")
    void test_operator_holding_account_without_initiator() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        ArrayList<AuthorisedRepresentativeDTO> list = new ArrayList<AuthorisedRepresentativeDTO>();
        IntStream.range(1, 3).forEach(nbr -> list.add(mockAuthorisedRepresentative(AccountAccessRight.APPROVE)));
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(list);
        validator.validate(account);
    }

    @Test
    @DisplayName("operator holding account compliant with four eyes principle")
    void test_operator_holding_accountcompliant_with_4_eyes_principle() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        ArrayList<AuthorisedRepresentativeDTO> list = new ArrayList<AuthorisedRepresentativeDTO>();
        list.add(mockAuthorisedRepresentative(AccountAccessRight.APPROVE));
        list.add(mockAuthorisedRepresentative(AccountAccessRight.INITIATE_AND_APPROVE));
        Mockito.when(account.getAuthorisedRepresentatives()).thenReturn(list);
        validator.validate(account);
    }

    @Test
    @DisplayName("aircraft operator holding account with same monitoring plan ID is invalid")
    void test_aircraft_operator_holding_account_with_same_monitoring_plan_id() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT");
        Mockito.when(account.getOperator().getType()).thenReturn("AIRCRAFT_OPERATOR");
        MonitoringPlanDTO planDTO = Mockito.mock(MonitoringPlanDTO.class);
        Mockito.when(planDTO.getId()).thenReturn("77666666");
        Mockito.when(account.getOperator().getMonitoringPlan()).thenReturn(planDTO);

        Mockito.when(accountService.monitoringPlanIdExists(any())).thenReturn(true);

        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    void test_oha_with_inconsistent_email_in_sales_contact_details() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        SalesContactDetailsDTO salesContactDetailsDTO = Mockito.mock(SalesContactDetailsDTO.class);
        EmailAddressDTO emailAddressDTO = Mockito.mock(EmailAddressDTO.class);
        Mockito.when(emailAddressDTO.getEmailAddress()).thenReturn("p@trasys.gr");
        Mockito.when(emailAddressDTO.getEmailAddressConfirmation()).thenReturn("p1@trasys.gr");
        Mockito.when(account.getAccountDetails().getSalesContactDetails()).thenReturn(salesContactDetailsDTO);
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getEmailAddress()).thenReturn(emailAddressDTO);
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    void test_oha_with_invalid_phone_in_sales_contact_details() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        SalesContactDetailsDTO salesContactDetailsDTO = Mockito.mock(SalesContactDetailsDTO.class);
        EmailAddressDTO emailAddressDTO = Mockito.mock(EmailAddressDTO.class);
        Mockito.when(emailAddressDTO.getEmailAddress()).thenReturn("p@trasys.gr");
        Mockito.when(emailAddressDTO.getEmailAddressConfirmation()).thenReturn("p@trasys.gr");
        Mockito.when(account.getAccountDetails().getSalesContactDetails()).thenReturn(salesContactDetailsDTO);
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getEmailAddress()).thenReturn(emailAddressDTO);
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getPhoneNumberCountryCode()).thenReturn("UK(44)");
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getPhoneNumber()).thenReturn("6909999999");
        assertThrows(AccountValidationException.class, () -> validator.validate(account));
    }

    @Test
    void test_aoha_with_valid_sales_contact_details() {
        AccountDTO account = mockValidAccountDTO();
        Mockito.when(account.getAccountType()).thenReturn("AIRCRAFT_OPERATOR_HOLDING_ACCOUNT");
        SalesContactDetailsDTO salesContactDetailsDTO = Mockito.mock(SalesContactDetailsDTO.class);
        EmailAddressDTO emailAddressDTO = Mockito.mock(EmailAddressDTO.class);
        Mockito.when(emailAddressDTO.getEmailAddress()).thenReturn("p@trasys.gr");
        Mockito.when(emailAddressDTO.getEmailAddressConfirmation()).thenReturn("p@trasys.gr");
        Mockito.when(account.getAccountDetails().getSalesContactDetails()).thenReturn(salesContactDetailsDTO);
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getEmailAddress()).thenReturn(emailAddressDTO);
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getPhoneNumberCountryCode()).thenReturn("GR(30)");
        Mockito.when(account.getAccountDetails().getSalesContactDetails().getPhoneNumber()).thenReturn("6909999999");
        validator.validate(account);
    }

    @Test
    @DisplayName("Country codes without parentheses are normalized correctly")
    void testCountryCodeExtraction() {
        AccountDTO account = mockValidAccountDTO();
        PhoneNumberDTO phoneNumberDTO = Mockito.mock(PhoneNumberDTO.class);
        Mockito.when(phoneNumberDTO.getCountryCode1()).thenReturn("44");
        Mockito.when(phoneNumberDTO.getCountryCode2()).thenReturn("44");
        account.getAccountHolder().setPhoneNumber(phoneNumberDTO);
        validator.validate(account);
    }

    private AccountDTO mockValidAccountDTO() {
        AccountHolderDTO accountHolderDTO = Mockito.mock(AccountHolderDTO.class);
        AddressDTO addressDTO = Mockito.mock(AddressDTO.class);
        Mockito.when(addressDTO.getCountry()).thenReturn("UK");
        Mockito.when(addressDTO.getPostCode()).thenReturn("12211");
        Mockito.when(addressDTO.getCity()).thenReturn("London");
        Mockito.when(addressDTO.getLine1()).thenReturn("Line 1");
        Mockito.when(accountHolderDTO.getAddress()).thenReturn(addressDTO);
        Mockito.when(accountHolderDTO.getStatus()).thenReturn(Status.ACTIVE);
        DetailsDTO detailsDto = Mockito.mock(DetailsDTO.class);
        Mockito.when(detailsDto.getName()).thenReturn("Test Name Co");
        Mockito.when(detailsDto.getRegistrationNumber()).thenReturn("223312");
        Mockito.when(accountHolderDTO.getDetails()).thenReturn(detailsDto);
        Mockito.when(accountHolderDTO.getType()).thenReturn(AccountHolderType.ORGANISATION);
        AccountDetailsDTO accountDetails = Mockito.mock(AccountDetailsDTO.class);
        Mockito.when(accountDetails.getName()).thenReturn("New acc1");
        TrustedAccountListRulesDTO rulesDTO = Mockito.mock(TrustedAccountListRulesDTO.class);
        Mockito.when(rulesDTO.getRule1()).thenReturn(false);
        Mockito.when(rulesDTO.getRule2()).thenReturn(false);
        InstallationOrAircraftOperatorDTO operator = Mockito.mock(InstallationOrAircraftOperatorDTO.class);
        PermitDTO permitDTO = Mockito.mock(PermitDTO.class);
        DateDTO dateDTO = Mockito.mock(DateDTO.class);
        Mockito.when(dateDTO.getDay()).thenReturn(1);
        Mockito.when(dateDTO.getMonth()).thenReturn(12);
        Mockito.when(dateDTO.getYear()).thenReturn(1980);
        Mockito.when(permitDTO.getId()).thenReturn("77666666");
        Mockito.when(operator.getName()).thenReturn("installation");
        Mockito.when(operator.getActivityType()).thenReturn(InstallationActivityType.MANUFACTURE_OF_MINERAL_WOOL);
        Mockito.when(operator.getRegulator()).thenReturn(RegulatorType.DAERA);
        Mockito.when(operator.getType()).thenReturn("INSTALLATION");
        Mockito.when(operator.getFirstYear()).thenReturn(2021);
        Mockito.when(operator.getPermit()).thenReturn(permitDTO);
        AccountHolderRepresentativeDTO representativeDTO = Mockito.mock(AccountHolderRepresentativeDTO.class);
        Mockito.when(representativeDTO.getAddress()).thenReturn(addressDTO);
        EmailAddressDTO emailAddressDTO = Mockito.mock(EmailAddressDTO.class);
        Mockito.when(emailAddressDTO.getEmailAddress()).thenReturn("test@mail.com");
        Mockito.when(emailAddressDTO.getEmailAddressConfirmation()).thenReturn("test@mail.com");
        Mockito.when(representativeDTO.getEmailAddress()).thenReturn(emailAddressDTO);
        PhoneNumberDTO phoneNumberDTO = Mockito.mock(PhoneNumberDTO.class);
        Mockito.when(phoneNumberDTO.getCountryCode1()).thenReturn("UK (44)");
        Mockito.when(phoneNumberDTO.getPhoneNumber1()).thenReturn("7792777777");
        LegalRepresentativeDetailsDTO legalRepresentativeDetailsDTO = Mockito.mock(LegalRepresentativeDetailsDTO.class);
        Mockito.when(legalRepresentativeDetailsDTO.getFirstName()).thenReturn("Firstname");
        Mockito.when(legalRepresentativeDetailsDTO.getLastName()).thenReturn("Lastname");
        Mockito.when(representativeDTO.getDetails()).thenReturn(legalRepresentativeDetailsDTO);
        Mockito.when(representativeDTO.getPositionInCompany()).thenReturn("Head");
        Mockito.when(representativeDTO.getEmailAddress()).thenReturn(emailAddressDTO);
        Mockito.when(representativeDTO.getPhoneNumber()).thenReturn(phoneNumberDTO);
        AddressDTO anotherAddrDTO = Mockito.mock(AddressDTO.class);
        Mockito.when(anotherAddrDTO.getCountry()).thenReturn("UK");
        Mockito.when(anotherAddrDTO.getPostCode()).thenReturn("12211");
        Mockito.when(anotherAddrDTO.getCity()).thenReturn("London");
        Mockito.when(anotherAddrDTO.getLine1()).thenReturn("Line 1");
        Mockito.when(representativeDTO.getAddress()).thenReturn(anotherAddrDTO);
        AccountHolderContactInfoDTO contactInfoDTO = Mockito.mock(AccountHolderContactInfoDTO.class);
        Mockito.when(contactInfoDTO.getPrimaryContact()).thenReturn(representativeDTO);
        AccountDTO accountDTO = Mockito.mock(AccountDTO.class);
        Mockito.when(accountDTO.getAccountType()).thenReturn("OPERATOR_HOLDING_ACCOUNT");
        Mockito.when(accountDTO.getAccountHolder()).thenReturn(accountHolderDTO);
        Mockito.when(accountDTO.getAccountDetails()).thenReturn(accountDetails);
        Mockito.when(accountDTO.getTrustedAccountListRules()).thenReturn(rulesDTO);
        Mockito.when(accountDTO.getAccountHolderContactInfo()).thenReturn(contactInfoDTO);
        Mockito.when(accountDTO.getOperator()).thenReturn(operator);

        return accountDTO;
    }


    private AuthorisedRepresentativeDTO mockValidAuthorisedRepresentative() {
        AuthorisedRepresentativeDTO arDTO = Mockito.mock(AuthorisedRepresentativeDTO.class);
        Mockito.when(arDTO.getUrid()).thenReturn("UK123456789");
        Mockito.when(arDTO.getRight()).thenReturn(AccountAccessRight.INITIATE_AND_APPROVE);
        return arDTO;
    }
    
    private AuthorisedRepresentativeDTO mockAuthorisedRepresentative(AccountAccessRight accessRight) {
        AuthorisedRepresentativeDTO arDTO = Mockito.mock(AuthorisedRepresentativeDTO.class);
        Mockito.when(arDTO.getUrid()).thenReturn("UK11111111");
        Mockito.when(arDTO.getRight()).thenReturn(accessRight);
        return arDTO;
    }
}
