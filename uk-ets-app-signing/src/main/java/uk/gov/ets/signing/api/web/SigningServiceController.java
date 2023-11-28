package uk.gov.ets.signing.api.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.support.Signature;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ets.signing.api.service.OtpService;
import uk.gov.ets.signing.api.service.SigningService;
import uk.gov.ets.signing.api.web.model.SignRequest;
import uk.gov.ets.signing.api.web.model.VerificationRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api-signing", produces = MediaType.APPLICATION_JSON_VALUE)
public class SigningServiceController {

    private final SigningService signingService;

    private final OtpService otpService;

    @PostMapping(path = "sign", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Signature> sign(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken, @RequestBody
        SignRequest signRequest) {
        otpService.verifyOtp(signRequest.getOtpCode(), bearerToken);
        return new ResponseEntity<>(signingService.sign(signRequest.getData()), HttpStatus.OK);
    }

    @PostMapping(path = "sign-no-otp", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Signature> signNoOtp(@RequestBody SignRequest signRequest) {
        return new ResponseEntity<>(signingService.sign(signRequest.getData()), HttpStatus.OK);
    }

    @PostMapping(path = "verify", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> verify(@RequestBody VerificationRequest verificationRequest) {
        return new ResponseEntity<>(signingService.verify(verificationRequest.getSignature(), verificationRequest.getData()), HttpStatus.OK);
    }
}
