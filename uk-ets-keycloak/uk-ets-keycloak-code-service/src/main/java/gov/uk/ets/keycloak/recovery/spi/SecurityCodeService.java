package gov.uk.ets.keycloak.recovery.spi;

import gov.uk.ets.keycloak.recovery.dto.SecurityCodeDTO;
import org.keycloak.provider.Provider;

import java.util.List;

public interface SecurityCodeService extends Provider {

    List<SecurityCodeDTO> findSecurityCodesByUserId(String userId);

    SecurityCodeDTO requestSecurityCode(String userId, String email);

    SecurityCodeDTO requestSecurityCode(String userId, String countryCode, String phoneNumber);

    SecurityCodeDTO addAttempt(String userId);

    void clearSecurityCodes(String userId);

}
