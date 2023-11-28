package uk.gov.ets.password.validator.api.service;

import com.nulabinc.zxcvbn.Zxcvbn;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.ets.password.validator.api.config.PasswordBlacklistProps;
import uk.gov.ets.password.validator.api.config.PasswordValidatorProperties;

@Service
@RequiredArgsConstructor
@Log4j2
public class PasswordValidatorService {

    private final PasswordBlacklistProps passwordBlacklistProps;
    private final PasswordValidatorProperties passwordValidatorProperties;
    private final RestTemplate restTemplate;

    public boolean isPwned(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            String hashedPasswd = toHexString(digest.digest(password.getBytes()));
            String prefix = hashedPasswd.substring(0, 5);
            String suffix = hashedPasswd.substring(5);
            String response =
                restTemplate.getForObject(passwordBlacklistProps.getApiServiceUrl(), String.class, prefix);

            if (Optional.ofNullable(response).isPresent()) {
                return response.contains(suffix);
            } else {
                return false;
            }
        } catch (RestClientException | NoSuchAlgorithmException e) {
            log.error("Communication with blacklist service url: '{}' failed",
                passwordBlacklistProps.getApiServiceUrl(), e);
            return false;
        }
    }

    public boolean compliesWithPasswordPolicies(String password) {

        boolean compliesWithMinChars = true;
        boolean compliesWithMaxChars;

        compliesWithMaxChars = password.length() <= passwordValidatorProperties.getMaximumChars();

        if (passwordValidatorProperties.isFlagEnabled()) {
            compliesWithMinChars = password.length() >= passwordValidatorProperties.getMinimumChars();
        }
        return compliesWithMinChars && compliesWithMaxChars;
    }

    /**
     * Calculates the password strength. 
     * @param password to measure the strength
     * @return The password strength score.
     */
    public int passwordStrength(String password) {

        if (Optional.ofNullable(password).isEmpty()) {
            return 0;
        }
        if (!compliesWithPasswordPolicies(password)) {
            return 1;
        }
        Zxcvbn zxcvbn = new Zxcvbn();
        return zxcvbn.measure(password).getScore();
    }

    /*
     * Converts a byte array to hex string
     */
    private static String toHexString(byte[] block) {
        StringBuilder buf = new StringBuilder();
        for (byte b : block) {
            byte2hex(b, buf);
        }
        return buf.toString();
    }

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuilder buf) {
        char[] hexChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int high = (b & 0xf0) >> 4;
        int low = b & 0x0f;
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
}
