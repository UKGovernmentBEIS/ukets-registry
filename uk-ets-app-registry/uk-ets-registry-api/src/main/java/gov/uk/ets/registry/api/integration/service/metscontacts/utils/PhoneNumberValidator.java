package gov.uk.ets.registry.api.integration.service.metscontacts.utils;

import java.util.List;
import org.springframework.stereotype.Component;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;

/** 
 * No validation on the phones is done see JIRA ticket UKETS-8750.
 * 
 */
@Component
public class PhoneNumberValidator {

    public void validateMandatoryPhone(String countryPhoneCode,
                                     String phone,
                                     String countryPhoneCodeFieldName,
                                     String phoneFieldName,
                                     List<IntegrationEventErrorDetails> errors,
                                     boolean isMobile) {

    if (!validateMandatoryCodeAndPhone(countryPhoneCode, phone, countryPhoneCodeFieldName, phoneFieldName, errors)) {
      return;
    }
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

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}


