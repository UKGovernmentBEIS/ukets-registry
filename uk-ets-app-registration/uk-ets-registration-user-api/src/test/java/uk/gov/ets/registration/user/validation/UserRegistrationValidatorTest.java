package uk.gov.ets.registration.user.validation;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import uk.gov.ets.registration.user.RestProxy;
import uk.gov.ets.registration.user.common.GeneratorServiceImpl;
import uk.gov.ets.registration.user.exception.UserSubmissionValidationException;

/**
 * TODO: Add tests for all the mandatory registration fields
 */
@SpringBootTest
@TestPropertySource(locations = "/application.properties")
class UserRegistrationValidatorTest {
    private UserRegistrationValidator validator;

    @Value("${user.registration.password.validator.serverUrl}")
    private String passwordValidatorURL;

    @MockBean
    private RestProxy restProxy;

    @BeforeEach
    void setUp() {
        validator = new UserRegistrationValidator(new GeneratorServiceImpl(), restProxy, passwordValidatorURL);
    }

    @Test
    @DisplayName("user representation without birthdate is invalid")
    void testValidateBirthDate() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("birthDate")).thenReturn(new ArrayList<>());
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation without strong password is invalid")
    void testValidatePassword() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        userRepresentation.getCredentials().get(0).setValue("test1234");

        ValidatePasswordResponse validatePasswordResponse = new ValidatePasswordResponse();
        validatePasswordResponse.setValid(false);
        validatePasswordResponse.setMessage("Enter strong password");
        when(restProxy.postToPasswordValidator(passwordValidatorURL, userRepresentation.getCredentials().get(0).getValue())).thenReturn(validatePasswordResponse);

        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with birthdate over 100 should throw exception")
    void testValidateBirthDateIntervalRight() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("birthDate")).thenReturn(List.of("12/12/1919"));
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with birthdate under 18 should throw exception")
    void testValidateBirthDateIntervalLeft() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("birthDate")).thenReturn(List.of("12/12/2020"));
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with empty work postcode and UK as country should throw exception")
    void testValidateWorkPostcodeCountryUk() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("workPostCode")).thenReturn(List.of(""));
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with empty work postcode and country selected not UK is valid")
    void testValidateWorkPostcode() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("workPostCode")).thenReturn(List.of(""));
        when(userRepresentation.getAttributes().get("workCountry")).thenReturn(List.of("GR"));
        assertAll(() -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with empty postcode and UK as country should throw exception")
    void testValidatePostcodeCountryUk() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("postCode")).thenReturn(List.of(""));
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with empty postcode and country selected not UK is valid")
    void testValidatePostcodeCountry() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("postCode")).thenReturn(List.of(""));
        when(userRepresentation.getAttributes().get("country")).thenReturn(List.of("GR"));
        assertAll(() -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation without invalid URID should throw exception")
    void testValidateURID() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("urid")).thenReturn(List.of("urid"));
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation without mobile phone number should throw exception")
    void testValidateWorkMobilePhoneNumber() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("workMobilePhoneNumber")).thenReturn(null);
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with reason but no mobile phone and no alternative phone should throw exception")
    void testValidateNoWorkMobilePhoneReasonNoAlternativePhoneNumber() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("workMobilePhoneNumber")).thenReturn(null);
        when(userRepresentation.getAttributes().get("workAlternativePhoneNumber")).thenReturn(null);
        when(userRepresentation.getAttributes().get("noMobilePhoneNumberReason")).thenReturn(List.of("Reason"));
        assertThrows(UserSubmissionValidationException.class, () -> validator.validate(userRepresentation));
    }

    @Test
    @DisplayName("user representation with reason and alternative phone number is valid")
    void testValidateNoWorkMobilePhoneReasonWithAlternativePhoneNumber() {
        UserRepresentation userRepresentation = mockValidUserRepresentation();
        assertAll(() -> validator.validate(userRepresentation));
        when(userRepresentation.getAttributes().get("workMobilePhoneNumber")).thenReturn(null);
        when(userRepresentation.getAttributes().get("noMobilePhoneNumberReason")).thenReturn(List.of("Reason"));
        assertAll(() -> validator.validate(userRepresentation));
    }

    private UserRepresentation mockValidUserRepresentation() {
        UserRepresentation userRepresentation = mock(UserRepresentation.class);
        when(userRepresentation.getId()).thenReturn("an id");
        when(userRepresentation.getUsername()).thenReturn("a username");
        when(userRepresentation.getFirstName()).thenReturn("a first name");
        when(userRepresentation.getLastName()).thenReturn("a last name");
        when(userRepresentation.getEmail()).thenReturn("email@email.email");
        Map<String, List<String>> attributes = mock(Map.class);
        when(attributes.get("workCountry")).thenReturn(List.of("UK"));
        when(attributes.get("workPostCode")).thenReturn(List.of("56789"));
        when(attributes.get("workBuildingAndStreet")).thenReturn(List.of("workBuildingAndStreet"));
        when(attributes.get("workTownOrCity")).thenReturn(List.of("workTownOrCity"));
        when(attributes.get("workStateOrProvince")).thenReturn(List.of("workStateOrProvince"));
        when(attributes.get("country")).thenReturn(List.of("UK"));
        when(attributes.get("postCode")).thenReturn(List.of("12345"));
        when(attributes.get("townOrCity")).thenReturn(List.of("townOrCity"));
        when(attributes.get("stateOrProvince")).thenReturn(List.of("stateOrProvince"));
        when(attributes.get("buildingAndStreet")).thenReturn(List.of("buildingAndStreet"));
        when(attributes.get("urid")).thenReturn(List.of("UK977538690871"));
        when(attributes.get("countryOfBirth")).thenReturn(List.of("countryOfBirth"));
        when(attributes.get("state")).thenReturn(List.of("state"));
        when(attributes.get("birthDate")).thenReturn(List.of("12/12/1980"));
        when(attributes.get("workMobileCountryCode")).thenReturn(List.of("UK (44)"));
        when(attributes.get("workMobilePhoneNumber")).thenReturn(List.of("7911123456"));
        when(attributes.get("workAlternativeCountryCode")).thenReturn(List.of("UK (44)"));
        when(attributes.get("workAlternativePhoneNumber")).thenReturn(List.of("7911123457"));
        when(userRepresentation.getAttributes()).thenReturn(attributes);
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue("test55@test55");
        when(userRepresentation.getCredentials()).thenReturn(List.of(credentialRepresentation));
        ValidatePasswordResponse validatePasswordResponse = new ValidatePasswordResponse();
        validatePasswordResponse.setValid(true);
        when(restProxy.postToPasswordValidator(passwordValidatorURL, userRepresentation.getCredentials().get(0).getValue())).thenReturn(validatePasswordResponse);
        return userRepresentation;
    }
}
