package gov.uk.ets.registry.api.account.service;

import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Service for random generation regarding User Management.
 */

@Service
public class AccountGeneratorService extends GeneratorService {

    private static final String ACCOUNT_CLAIM_CODE_PREFIX = "ACC";
    private static final String CLAIM_CODE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ACCOUNT_CLAIM_CODE_LENGTH = 12;

    /**
     * Produces a random account claim code.
     *
     * @return an account claim code.
     */
    public String generateAccountClaimCode() throws NoSuchAlgorithmException {
        SecureRandom random = getSecureRandom();

        int randomPartLength = ACCOUNT_CLAIM_CODE_LENGTH - ACCOUNT_CLAIM_CODE_PREFIX.length();
        StringBuilder sb = new StringBuilder(ACCOUNT_CLAIM_CODE_PREFIX);

        for (int i = 0; i < randomPartLength; i++) {
            int index = random.nextInt(CLAIM_CODE_CHARACTERS.length());
            sb.append(CLAIM_CODE_CHARACTERS.charAt(index));
        }
        return sb.toString();
    }
}
