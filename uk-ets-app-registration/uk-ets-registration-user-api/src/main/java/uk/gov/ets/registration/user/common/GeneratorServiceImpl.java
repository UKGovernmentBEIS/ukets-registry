package uk.gov.ets.registration.user.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

/**
 * Implements the service for random generation.
 */
@Service
public class GeneratorServiceImpl implements GeneratorService {

    /**
     * Logging facility.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorServiceImpl.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateURID() {
        String urid = null;
        try {
            SecureRandom random = SecureRandom.getInstance(Constants.RANDOM_ALGORITHM);
            long randomNumber = (long) Math.floor(random.nextDouble() * 9000000000L) + 1000000000L;
            urid = String.format("%s%d%s", Constants.REGISTRY_CODE, randomNumber, calculateCheckDigits(0, randomNumber, 0));
            urid = appendZeroIfNeeded(urid);
        } catch (NoSuchAlgorithmException exc) {
            LOGGER.error("Could not generate random URID: ", exc);
        }
        return urid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateURID(String urid) {
        String input = removeZeroIfNeeded(urid);
        if (StringUtils.isEmpty(input)) {
            return false;
        }
        if (!input.startsWith(Constants.REGISTRY_CODE)) {
            return false;
        }
        // The check digits can be either one or two.
        if (input.length() != 13 && input.length() != 14) {
            return false;
        }
        String identifier = input.substring(2, 12);
        if (!isNumber(identifier)) {
            return false;
        }
        String checkDigitsString = input.substring(12);
        if (!isNumber(checkDigitsString)) {
            return false;
        }
        return validateCheckDigits(0, Long.parseLong(identifier), 0, Integer.parseInt(checkDigitsString));
    }

    /**
     * Calculates the modulo of the provided properties
     * @param registry The registry code
     * @param type The type
     * @param identifier The identifier
     * @param cp The commitment period
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
     * Calculates the check digits.
     * @param type The type.
     * @param identifier The identifier.
     * @param cp The commitment period.
     * @return the check digits.
     */
    private int calculateCheckDigits(int type, long identifier, int cp) {
        return 97 + 1 - calculateModulo(Constants.REGISTRY_CODE, type, identifier, cp, 0);
    }

    /**
     * Validates the check digits.
     * @param type The type.
     * @param identifier The identifier.
     * @param cp The commitment period.
     * @param checkDigits The check digits.
     * @return false/true
     */
    private boolean validateCheckDigits(int type, long identifier, int cp, int checkDigits) {
        return calculateModulo(Constants.REGISTRY_CODE, type, identifier, cp, checkDigits) == 1L;
    }

    /**
     * Converts the provided character.
     * @param character The character.
     * @return the converted character.
     */
    private static String convertChar(char character) {
        return Integer.toString((int) character - 55);
    }

    /**
     * Checks whether the provided input is a number.
     * @param input The input.
     * @return false/true
     */
    private boolean isNumber(String input) {
        boolean result = true;
        try {
            NumberUtils.parseNumber(input, Long.class);
        } catch (Exception exc) {
            result = false;
        }
        return result;
    }

    private String appendZeroIfNeeded(String urid) {
        String result = urid;
        if (result.length() == 13) {
            result = String.format("%s%d%s", result.substring(0, result.length() - 1), 0, result.substring(result.length() - 1));
        }
        return result;
    }

    private static String removeZeroIfNeeded(String urid) {
        String result = urid;
        if (result.charAt(result.length() - 2) == '0') {
            result = String.format("%s%s", result.substring(0, result.length() - 2), result.substring(result.length() - 1));
        }
        return result;
    }

}
