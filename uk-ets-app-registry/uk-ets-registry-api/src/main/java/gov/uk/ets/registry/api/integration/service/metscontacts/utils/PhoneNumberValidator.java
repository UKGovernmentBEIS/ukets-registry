package gov.uk.ets.registry.api.integration.service.metscontacts.utils;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;

import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

@Component
public class PhoneNumberValidator {

  private final PhoneNumberUtil util = PhoneNumberUtil.getInstance();

  public void validateMandatoryPhone(String countryPhoneCode,
                                     String phone,
                                     String countryPhoneCodeFieldName,
                                     String phoneFieldName,
                                     List<IntegrationEventErrorDetails> errors,
                                     boolean isMobile) {

    if (!validateMandatoryCodeAndPhone(countryPhoneCode, phone, countryPhoneCodeFieldName, phoneFieldName, errors)) {
      return;
    }
    validatePhoneInternal(countryPhoneCode, phone, countryPhoneCodeFieldName, phoneFieldName, errors, isMobile);
  }

  public void validateOptionalPhone(String countryPhoneCode,
                                    String phone,
                                    String countryPhoneCodeFieldName,
                                    String phoneFieldName,
                                    List<IntegrationEventErrorDetails> errors,
                                    boolean isMobile) {

    if (isBlank(countryPhoneCode) && isBlank(phone)) return;

    if (!validateMandatoryCodeAndPhone(countryPhoneCode, phone, countryPhoneCodeFieldName, phoneFieldName, errors)) {
      return;
    }
    validatePhoneInternal(countryPhoneCode, phone, countryPhoneCodeFieldName, phoneFieldName, errors, isMobile);
  }

  private boolean validateMandatoryCodeAndPhone(String countryPhoneCode,
                                                String phone,
                                                String countryPhoneCodeFieldName,
                                                String phoneFieldName,
                                                List<IntegrationEventErrorDetails> errors) {

    if (isBlank(countryPhoneCode)) {
      errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0702, countryPhoneCodeFieldName));
      return false;
    }

    if (isBlank(phone)) {
      errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0702, phoneFieldName));
      return false;
    }

    return true;
  }

  private void validatePhoneInternal(String countryPhoneCode,
                                     String phone,
                                     String countryPhoneCodeFieldName,
                                     String phoneFieldName,
                                     List<IntegrationEventErrorDetails> errors,
                                     boolean isMobile
  ) {

    Integer cc = normalizeCallingCode(countryPhoneCode);
    if (cc == null || !util.getSupportedCallingCodes().contains(cc)) {
      errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0701, countryPhoneCodeFieldName));
      return;
    }

    String nsn = normalizeDigits(phone);
    if (nsn == null) {
      errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0701, phoneFieldName));
      return;
    }

    Phonenumber.PhoneNumber num;
    try {
      num = new Phonenumber.PhoneNumber()
              .setCountryCode(cc)
              .setNationalNumber(Long.parseLong(nsn));
    } catch (NumberFormatException e) {
      errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0701, phoneFieldName));
      return;
    }

    if (!util.isValidNumber(num)) {
      String errorMessage = isMobile ?
              "Invalid Mobile Phone Country Code and Mobile Phone number combination"
              : "Invalid Phone Country Code and Phone number combination";
      errors.add(new IntegrationEventErrorDetails(IntegrationEventError.ERROR_0701,
              errorMessage));
    }
  }

  private static boolean isBlank(String s) {
    return s == null || s.isBlank();
  }

  private static Integer normalizeCallingCode(String raw) {
    if (raw == null) return null;
    String s = raw.trim();
    if (s.isBlank()) return null;
    if (s.startsWith("+")) s = s.substring(1);
    if (!s.matches("\\d+")) return null;
    return Integer.parseInt(s);
  }

  private static String normalizeDigits(String raw) {
    if (raw == null) return null;
    String s = raw.replaceAll("[^0-9]", "");
    return s.isBlank() ? null : s;
  }
}


