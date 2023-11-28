package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.transaction.common.GeneratorService;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Service for random generation regarding User Management.
 */
@Log4j2
@Service
public class UserGeneratorService extends GeneratorService {

    /**
     * The excluded characters from an enrolment key.
     */
    private static final char[] ENROLMENT_KEY_ILLEGAL_CHARACTERS = new char[] {'0', 'O', '1', 'I', '5', 'S'};

    /**
     * The enrolment key length.
     */
    private static final int ENROLMENT_KEY_LENGTH = 24;

    /**
     * Produces a random user id (URID).
     *
     * @return a URID.
     */
    public String generateURID() throws NoSuchAlgorithmException {
        SecureRandom random = getSecureRandom();
        long randomNumber = (long) Math.floor(random.nextDouble() * 9000000000L) + 1000000000L;
        String result = String.format("%s%d%s", Constants.REGISTRY_CODE, randomNumber, calculateCheckDigits(0, randomNumber, 0));
        return appendZeroIfNeeded(result);
    }

    /**
     * Validates the provided URID.
     *
     * @param urid The user id.
     * @return false/true
     */
    public boolean validateURID(String urid) {
        String input = removeZeroIfNeeded(urid);
        if (!StringUtils.hasText(input)) {
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
        if (!NumberUtils.isDigits(identifier)) {
            return false;
        }
        String checkDigitsString = input.substring(12);
        if (!NumberUtils.isDigits(checkDigitsString)) {
            return false;
        }
        return validateCheckDigits(0, NumberUtils.createLong(identifier), 0,
            NumberUtils.createInteger(checkDigitsString));
    }

    /**
     * Produces a random enrolment key.
     *
     * @return an enrollment key.
     */
    public String generateEnrolmentKey() throws NoSuchAlgorithmException {
        SecureRandom random = getSecureRandom();
        String randomBase36String =
            base36Encode(random.nextLong()) + base36Encode(random.nextLong()) + base36Encode(random.nextLong());
        StringBuilder result = new StringBuilder();

        // insert a dash every 4 characters
        int index = 0;
        while (result.length() < ENROLMENT_KEY_LENGTH && randomBase36String.length() > index) {
            if (!ArrayUtils.contains(ENROLMENT_KEY_ILLEGAL_CHARACTERS, randomBase36String.charAt(index))) {
                result.append(randomBase36String.charAt(index));
            }
            if ((result.length() + 1) % 5 == 0 && result.length() < ENROLMENT_KEY_LENGTH) {
                result.append("-");
            }
            index++;
        }
        return result.length() == ENROLMENT_KEY_LENGTH ? result.toString() : generateEnrolmentKey();
    }

    /**
     * Validates the provided enrolment key.
     *
     * @param enrolmentKey The input enrolment key.
     * @return false/true
     */
    public boolean validateEnrolmentKey(String enrolmentKey) {
        if (!StringUtils.hasText(enrolmentKey)) {
            log.debug("The provided enrolment key is empty");
            return false;
        }
        if (enrolmentKey.length() != ENROLMENT_KEY_LENGTH) {
            log.debug("The provided enrolment key does not have the required length: {}", enrolmentKey.length());
            return false;
        }
        if (org.apache.commons.lang3.StringUtils.countMatches(enrolmentKey, "-") != 4) {
            log.debug("The provided enrolment key {} does not have the required number of dashes", enrolmentKey);
            return false;
        }
        for (char illegalCharacter : ENROLMENT_KEY_ILLEGAL_CHARACTERS) {
            if (enrolmentKey.indexOf(illegalCharacter) > -1) {
                log.debug("The provided enrolment key {} contains the illegal character {}", enrolmentKey,
                    illegalCharacter);
                return false;
            }
        }
        int dashPosition = 4;
        for (int ordinal = 1; ordinal <= 4; ordinal++) {
            if (org.apache.commons.lang3.StringUtils.ordinalIndexOf(enrolmentKey, "-", ordinal) != dashPosition) {
                log.debug("The provided enrolment key {} does not have a dash on position {}", enrolmentKey,
                    dashPosition);
                return false;
            }
            dashPosition += 5;
        }

        AtomicBoolean result = new AtomicBoolean(true);
        enrolmentKey.chars().forEach(
            c -> {
                char character = (char) c;
                if (!Character.isDigit(character) && character != '-' && !Character.isUpperCase(character)) {
                    log.debug("The provided enrolment key {} contains a not valid character {}", enrolmentKey,
                        character);
                    result.set(false);
                }
            }
        );
        return result.get();
    }

    /**
     * Encodes the provided number.
     *
     * @param number The input number.
     * @return a base 36 string representation of the provided number.
     */
    private static String base36Encode(long number) {
        StringBuilder ret = new StringBuilder();
        long theLong;
        if (number < 0) {
            theLong = -1 * number;
        } else {
            theLong = number;
        }
        while (theLong > 0) {
            if ((theLong % 36) < 10) {
                ret.append((char) (((int) '0') + (int) (theLong % 36)));
            } else {
                ret.append((char) (((int) 'A') + (int) (theLong % 36 - 10)));
            }
            theLong /= 36;
        }
        return ret.toString();
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
