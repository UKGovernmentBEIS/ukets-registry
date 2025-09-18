package uk.gov.ets.registration.user.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import uk.gov.ets.registration.user.RestProxy;
import uk.gov.ets.registration.user.common.GeneratorService;
import uk.gov.ets.registration.user.exception.UserSubmissionValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Validator for user registration.
 */
@Service
public class UserRegistrationValidator {
    private GeneratorService generatorService;
    private RestProxy restProxy;

    /**
     * Maximum length.
     */
    private static Integer MAXIMUM_LENGTH = 300;

    private String passwordValidatorURL;


    /**
     * Added to bypass sonar checks.
     */
    private static String NO_MOBILE_PHONE_NUMBER_REASON = "noMobilePhoneNumberReason";
    private static String WORK_MOBILE_COUNTRY_CODE = "workMobileCountryCode";
    private static String WORK_MOBILE_PHONE_NUMBER = "workMobilePhoneNumber";
    private static String WORK_ALTERNATIVE_COUNTRY_CODE = "workAlternativeCountryCode";
    private static String WORK_ALTERNATIVE_PHONE_NUMBER = "workAlternativePhoneNumber";
    private static String POSTCODE_ATTR = "postCode";
    private static String WORKPOSTCODE_ATTR = "workPostCode";
    private static String COUNTRY_ATTR = "country";
    private static String MANDATORY_MSG = "The %s is mandatory";
    private static String EMPTY_FIELD = "user.%s.empty";

    private static List<String> NON_MANDATORY_FIELDS = List.of(NO_MOBILE_PHONE_NUMBER_REASON, WORK_MOBILE_COUNTRY_CODE,
        WORK_MOBILE_PHONE_NUMBER, WORK_ALTERNATIVE_COUNTRY_CODE, WORK_ALTERNATIVE_PHONE_NUMBER, POSTCODE_ATTR, WORKPOSTCODE_ATTR);

    public UserRegistrationValidator(GeneratorService generatorService, RestProxy restProxy,
                                     @Value("${user.registration.password.validator.serverUrl}") String passwordValidatorURL) {
        this.generatorService = generatorService;
        this.passwordValidatorURL = passwordValidatorURL;
        this.restProxy = restProxy;
    }

    /**
     * Performs validation on the user input.
     * @param user The user.
     */
    public void validate(UserRepresentation user) {
        List<Violation> errors = new ArrayList<>();

        checkField(errors, "id", user.getId(), "IAM identifier");
        checkField(errors, "username", user.getUsername(), "username");
        checkField(errors, "firstName", user.getFirstName(), "first name");
        checkField(errors, "lastName", user.getLastName(), "last name");
        checkField(errors, "email", user.getEmail(), "e-mail address");

        final Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null || attributes.isEmpty()) {
            errors.add(new Violation("user.attributes.empty", "The user attributes are mandatory (i.e. personal details etc.)"));

        } else {
            checkMapAttribute(errors, attributes, "workCountry", "work country");
            checkMapAttribute(errors, attributes, WORKPOSTCODE_ATTR, "work postal code or zip");
            checkMapAttribute(errors, attributes, "workBuildingAndStreet", "work building and street");
            checkMapAttribute(errors, attributes, "workTownOrCity", "work town or city");
            checkMapAttribute(errors, attributes, COUNTRY_ATTR, COUNTRY_ATTR);
            checkMapAttribute(errors, attributes, POSTCODE_ATTR, "postal code or zip");
            checkMapAttribute(errors, attributes, "townOrCity", "town or city");
            checkMapAttribute(errors, attributes, "buildingAndStreet", "building and street");
            checkMapAttribute(errors, attributes, "urid", "urid");
            checkMapAttribute(errors, attributes, "countryOfBirth", "country of birth");
            checkMapAttribute(errors, attributes, "state", "state");
            checkMapAttribute(errors, attributes, "birthDate", "date of birth");
            checkMapAttribute(errors, attributes, WORK_MOBILE_COUNTRY_CODE, "work mobile country code");
            checkMapAttribute(errors, attributes, WORK_MOBILE_PHONE_NUMBER, "work mobile phone number");
            checkMapAttribute(errors, attributes, WORK_ALTERNATIVE_COUNTRY_CODE, "work alternative country code");
            checkMapAttribute(errors, attributes, WORK_ALTERNATIVE_PHONE_NUMBER, "work alternative phone number");
        }

        if (!errors.isEmpty()) {
            throw new UserSubmissionValidationException(errors);
        }

        if (CollectionUtils.isEmpty(user.getCredentials())) {
            errors.add(new Violation("password", "Empty password"));
            throw new UserSubmissionValidationException(errors);
        }
        ValidatePasswordResponse response = restProxy.postToPasswordValidator(passwordValidatorURL, user.getCredentials().get(0).getValue());
        if (!response.isValid()) {
            errors.add(new Violation("password", response.getMessage()));
            throw new UserSubmissionValidationException(errors);
        }
    }

    /**
     * Checks the provided field, by making sure its value is not empty and its length does not extend the maximum length.
     * @param errors The errors list.
     * @param fieldName The field.
     * @param fieldDescription The field description.
     */
    private void checkField(List<Violation> errors, String fieldName, String fieldValue, String fieldDescription) {
        checkMandatory(errors, fieldName, fieldValue, fieldDescription);
        checkLength(errors, fieldName, fieldValue, fieldDescription);
    }

    /**
     * Checks whether the provided user attribute has a value.
     * @param errors The errors list.
     * @param map The map with all attributes.
     * @param attributeName The attribute name.
     * @param attributeDescription The attribute description.
     */
    private void checkMapAttribute(List<Violation> errors, Map<String, List<String>> map, String attributeName, String attributeDescription) {
        List<String> valueList = map.get(attributeName);
        if (!fieldHasValue(valueList)) {
            if(!NON_MANDATORY_FIELDS.contains(attributeName)){
                errors.add(new Violation(String.format(EMPTY_FIELD, attributeName), String.format(MANDATORY_MSG, attributeDescription)));
            }
        } else {
            checkLength(errors, attributeName, valueList.get(0), attributeDescription);
            checkFieldValidity(errors, attributeName, valueList.get(0), attributeDescription);
        }

        if(valueList != null && attributeName.equals(POSTCODE_ATTR) && errors.stream().noneMatch(v -> v.getFieldName().equals("user.country.empty"))){
            checkPostcodeValidity(errors, attributeName, valueList.get(0), map.get(COUNTRY_ATTR).get(0), attributeDescription);
        }

        if(valueList != null && attributeName.equals(WORKPOSTCODE_ATTR) && errors.stream().noneMatch(v -> v.getFieldName().equals("user.workCountry.empty"))){
            checkPostcodeValidity(errors, attributeName, valueList.get(0), map.get("workCountry").get(0), attributeDescription);
        }

        if (attributeName.equals(WORK_MOBILE_PHONE_NUMBER)) {
            checkPhone(errors, attributeName, WORK_MOBILE_COUNTRY_CODE, map, attributeDescription, true);
            checkMobilePhoneCombinations(errors, map, attributeName, attributeDescription);
        }

        if (attributeName.equals(WORK_ALTERNATIVE_PHONE_NUMBER)) {
            checkPhone(errors, attributeName, WORK_ALTERNATIVE_COUNTRY_CODE, map, attributeDescription, false);
            checkMobilePhoneCombinations(errors, map, attributeName, attributeDescription);
        }
    }

    /*
     * User should provide a mobile number or a reason for not providing one along with an alternative phone.
     */
    private void checkMobilePhoneCombinations(List<Violation> errors,
                                              Map<String, List<String>> map,
                                              String attributeName,
                                              String attributeDescription) {

        if (fieldHasValue(NO_MOBILE_PHONE_NUMBER_REASON, map)) {
            if (!fieldHasValue(WORK_ALTERNATIVE_PHONE_NUMBER, map) && WORK_ALTERNATIVE_PHONE_NUMBER.equals(attributeName)) {
                errors.add(new Violation(String.format(EMPTY_FIELD, attributeName), String.format(MANDATORY_MSG, attributeDescription)));
            }
        } else if (!fieldHasValue(WORK_MOBILE_PHONE_NUMBER, map) && WORK_MOBILE_PHONE_NUMBER.equals(attributeName)) {
            errors.add(new Violation(String.format(EMPTY_FIELD, attributeName), String.format(MANDATORY_MSG, attributeDescription)));
        }
    }

    private boolean fieldHasValue(String attributeName, Map<String, List<String>> map) {
        return fieldHasValue(map.get(attributeName));
    }

    private boolean fieldHasValue(List<String> valueList) {
        return Optional.ofNullable(valueList).stream().flatMap(Collection::stream).anyMatch(s -> !s.isBlank());
    }


    /**
     * This method checks the maximum length of the provided attribute, to avoid denial of service attacks.
     * @param errors The error list.
     * @param fieldName the field name.
     * @param fieldValue The field value.
     * @param fieldDescription The field description.
     */
    private void checkLength(List<Violation> errors, String fieldName, String fieldValue, String fieldDescription) {
        if (fieldValue != null && fieldValue.length() > MAXIMUM_LENGTH) {
            errors.add(new Violation(String.format("user.%s.too.long", fieldName), String.format("The field %s is too long", fieldDescription)));
        }
    }

    /**
     * This method checks the maximum length of the provided attribute, to avoid denial of service attacks.
     * Please note that checking whether the input is empty / null, must be performed before calling this method.
     * @param errors The error list.
     * @param fieldName the field name.
     * @param fieldValue The field value.
     * @param fieldDescription The field description.
     */
    private void checkMandatory(List<Violation> errors, String fieldName, String fieldValue, String fieldDescription) {
        if (!StringUtils.hasLength(fieldValue)) {
            errors.add(new Violation(String.format(EMPTY_FIELD, fieldName), String.format(MANDATORY_MSG, fieldDescription)));
        }
    }

    /**
     * This method checks validity criteria for specific fields.
     * @param errors The error list.
     * @param fieldName The field name.
     * @param fieldValue The field value.
     * @param fieldDescription The field description.
     */
    private void checkFieldValidity(List<Violation> errors, String fieldName, String fieldValue, String fieldDescription) {
        if ("urid".equals(fieldName) && !generatorService.validateURID(fieldValue)) {
            errors.add(new Violation(String.format("user.%s.invalid", fieldName), String.format("The %s is invalid", fieldDescription)));
        }
        if("birthDate".equals(fieldName)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "dd/MM/yyyy" );
            if ((Year.now().getValue() - LocalDate.parse(fieldValue , formatter).getYear() < 18) ||
                (Year.now().getValue() - LocalDate.parse(fieldValue , formatter).getYear() > 100)) {
                errors.add(new Violation(String.format("user.%s.invalid", fieldName), String.format("The %s is invalid", fieldDescription)));
            }
        }
    }

    /**
     * This method checks validity of postcode if
     * @param errors The error list.
     * @param postcodeFieldName The postcode field name.
     * @param postcodeFieldValue The postcode field value.
     * @param countryFieldValue The country code field value.
     * @param fieldDescription The field description.
     */
    private void checkPostcodeValidity(List<Violation> errors, String postcodeFieldName, String postcodeFieldValue, String countryFieldValue,String fieldDescription) {
        if("UK".equals(countryFieldValue) && (postcodeFieldValue == null || !StringUtils.hasLength(postcodeFieldValue))){
            errors.add(new Violation(String.format(EMPTY_FIELD, postcodeFieldName), String.format(MANDATORY_MSG, fieldDescription)));
        }else{
            checkLength(errors, postcodeFieldName, postcodeFieldValue, fieldDescription);
        }
    }

    private void checkPhone(List<Violation> errors,
                            String phoneFieldName,
                            String codeFieldName,
                            Map<String, List<String>> map,
                            String attributeDescription,
                            boolean fixedLineNotAllowed) {

        List<String> valueList = map.get(phoneFieldName);
        if (!fieldHasValue(valueList)) {
            return;
        }

        checkPhoneValidity(errors, phoneFieldName, valueList.get(0), map.get(codeFieldName).get(0), attributeDescription, fixedLineNotAllowed);
    }

    /**
     * This method checks validity of postcode if
     * @param errors The error list.
     * @param fieldName The field name.
     * @param phoneFieldValue The phone number field value.
     * @param codeFieldValue The phone code field value.
     * @param fieldDescription The field description.
     */
    private void checkPhoneValidity(List<Violation> errors,
                                    String fieldName,
                                    String phoneFieldValue,
                                    String codeFieldValue,
                                    String fieldDescription,
                                    boolean fixedLineNotAllowed) {
        /**
         * Phone number validation utility class.
         */
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        try {
            String countryCode = codeFieldValue != null ? codeFieldValue
                    .substring(codeFieldValue.indexOf("(") + 1, codeFieldValue.indexOf(")")) : "";
            Phonenumber.PhoneNumber number = phoneUtil.parse("+"+countryCode+phoneFieldValue, "");
            if(!phoneUtil.isValidNumber(number)){
                errors.add(new Violation(String.format("user.%s.invalid", fieldName), String.format("The %s is invalid", fieldDescription)));
            }
            if(fixedLineNotAllowed && phoneUtil.getNumberType(number) == PhoneNumberUtil.PhoneNumberType.FIXED_LINE){
                errors.add(new Violation(String.format("user.%s.invalid", fieldName), String.format("The %s is not a valid mobile number", fieldDescription)));
            }
        } catch (NumberParseException e) {
            errors.add(new Violation(String.format("user.%s.error", fieldName), String.format("Parse error while parsing %s", fieldDescription)));
        }
    }
}
