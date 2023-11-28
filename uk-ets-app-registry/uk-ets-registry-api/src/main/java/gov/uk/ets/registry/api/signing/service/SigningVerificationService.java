package gov.uk.ets.registry.api.signing.service;

import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.signing.SigningProperties;
import gov.uk.ets.registry.api.transaction.domain.data.SignatureInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Verify signed data by invoking the signing api.
 */
@Service
@RequiredArgsConstructor
public class SigningVerificationService {

    private final RestTemplate restTemplate;

    private final SigningProperties signingProperties;

    /**
     * Invoke the signing api and verify signed data.
     *
     * @param signatureInfo the signature info
     */
    public void verify(SignatureInfo signatureInfo) {
        if (!signingProperties.isEnabled()) {
            return;
        }
        String validatorEndpoint = signingProperties.getVerifySignatureEndpoint();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignatureInfo> request = new HttpEntity<>(signatureInfo, headers);
        ResponseEntity<Boolean> exchange =
            restTemplate.exchange(validatorEndpoint, HttpMethod.POST, request, Boolean.class);
        if (Boolean.FALSE.equals(exchange.getBody())) {
            throw new UkEtsException("Could not verify signature");
        }
    }
}
