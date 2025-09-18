package gov.uk.ets.keycloak.recovery.spi;

public interface TokenService {

    String generateResetPasswordToken(String userId);
}
