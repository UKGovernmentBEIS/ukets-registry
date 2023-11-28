package gov.uk.ets.registry.api.transaction.common;

import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import org.springframework.stereotype.Service;

/**
 * Common service for random generation.
 */
@Service
public class GeneratorService {

    /**
     * Returns a secure random instance.
     *
     * @return a secure random instance.
     * @throws NoSuchAlgorithmException In case the algorithm is not available.
     */
    protected SecureRandom getSecureRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
    }

    /**
     * {@inheritDoc}
     */
    public int calculateCheckDigits(int type, long identifier, int cp) {
        return 97 + 1 - calculateModulo(Constants.REGISTRY_CODE, type, identifier, cp, 0);
    }

    /**
     * Validates the check digits.
     *
     * @param type        The type.
     * @param identifier  The identifier.
     * @param cp          The commitment period.
     * @param checkDigits The check digits.
     * @return false/true
     */
    public boolean validateCheckDigits(int type, long identifier, int cp, int checkDigits) {
        boolean result = calculateModulo(Constants.REGISTRY_CODE, type, identifier, cp, checkDigits) == 1L;
        if (!result) {
            result = calculateModulo(Constants.KYOTO_REGISTRY_CODE, type, identifier, cp, checkDigits) == 1L;
        }
        return result;
    }

    /**
     * Calculates the modulo of the provided properties
     *
     * @param registry    The registry code
     * @param type        The type
     * @param identifier  The identifier
     * @param cp          The commitment period
     * @param checkDigits The check digits
     * @return the modulo
     */
    private int calculateModulo(String registry, int type, long identifier, int cp, int checkDigits) {
        DecimalFormat twoZeros = new DecimalFormat("00");
        DecimalFormat threeZeros = new DecimalFormat("000");

        String builder = identifier +
            convertChar(registry.charAt(0)) +
            convertChar(registry.charAt(1)) +
            threeZeros.format(type) +
            twoZeros.format(cp) +
            twoZeros.format(checkDigits);
        return new BigInteger(builder).mod(BigInteger.valueOf(97)).intValue();
    }

    /**
     * Converts the provided character.
     *
     * @param character The character.
     * @return the converted character.
     */
    private static String convertChar(char character) {
        return Integer.toString((int) character - 55);
    }

}
