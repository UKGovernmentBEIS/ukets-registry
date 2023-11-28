package uk.gov.ets.signing.api.service;

import gov.uk.ets.commons.logging.SecurityLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Plaintext;
import org.springframework.vault.support.Signature;
import org.springframework.vault.support.VaultSignRequest;
import uk.gov.ets.signing.api.SigningProperties;

@Service
@RequiredArgsConstructor
@Log4j2
public class SigningService {

    private final VaultOperations vaultTemplate;

    private final SigningProperties signingProperties;

    /**
     * Return the cryptographic signature of the given data
     *
     * @return
     */
    public Signature sign(String data) {
        log.info("Received data for signing:\n{}", data);
        VaultSignRequest vaultSignRequest = VaultSignRequest.create(Plaintext.of(data));
        return vaultTemplate.opsForTransit(signingProperties.getVault().getEngine())
            .sign(signingProperties.getVault().getKey(), vaultSignRequest);
    }

    /**
     * Check whether the provided signature is valid for the given data.
     *
     * @param signature the signature received when signing
     * @return true
     */
    public boolean verify(String signature, String data) {
        boolean result = vaultTemplate.opsForTransit(signingProperties.getVault().getEngine())
            .verify(signingProperties.getVault().getKey(), Plaintext.of(data), Signature.of(signature));
        if(!result) {
            SecurityLog.log(log, String.format("Signature: %s could not be verified", signature));
        }
        return result;
    }
}
