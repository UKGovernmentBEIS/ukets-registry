package uk.gov.ets.signing.api.config;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.AppRoleAuthentication;
import org.springframework.vault.authentication.AppRoleAuthenticationOptions;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;
import uk.gov.ets.signing.api.SigningProperties;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class VaultConfig extends AbstractVaultConfiguration {

    private final SigningProperties signingProperties;

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.from(URI.create(signingProperties.getVault().getUri()));
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        AppRoleAuthenticationOptions options = AppRoleAuthenticationOptions.builder()
            .roleId(AppRoleAuthenticationOptions.RoleId.provided(signingProperties.getVault().getRoleId()))
            .secretId(AppRoleAuthenticationOptions.SecretId.provided(signingProperties.getVault().getSecretId()))
            .build();
        log.debug("Signing API started for Vault server: {}, role-id:{}", signingProperties.getVault().getUri(),
            signingProperties.getVault().getRoleId());
        return new AppRoleAuthentication(options, restOperations());
    }
}
