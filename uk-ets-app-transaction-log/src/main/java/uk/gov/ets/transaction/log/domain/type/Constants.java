package uk.gov.ets.transaction.log.domain.type;


import org.springframework.util.StringUtils;

/**
 * Encapsulates some common constants.
 */
public final class Constants {

    /**
     * Constructor.
     */
    private Constants() {
        // nothing to implement here
    }

    /**
     * The registry code.
     */
    public static final String REGISTRY_CODE = "UK";

    /**
     * The Kyoto registry code.
     * This value applies for all functionality related to Kyoto and ITL,
     * e.g. Kyoto accounts, Kyoto transactions etc.
     */
    public static final String KYOTO_REGISTRY_CODE = "GB";

    /**
     * The algorithm used for producing random artifacts.
     */
    public static final String RANDOM_ALGORITHM = "SHA1PRNG";

    /**
     * The date format used in search results.
     */
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    /**
     * The maximum number of chars in comments.
     */
    public static final int COMMENT_MAX_LENGTH = 1024;

    /**
     * Retrieves the registry code, based on whether the context refers to the Kyoto protocol.
     * @param isKyoto whether the transaction, account etc. is a Kyoto one.
     * @return GB for Kyoto, UK otherwise
     */
    public static String getRegistryCode(boolean isKyoto) {
        return isKyoto ? KYOTO_REGISTRY_CODE : REGISTRY_CODE;
    }

    /**
     * Returns whether the provided registry code refers to the registry.
     * @param registryCode The registry code.
     * @return false/true
     */
    public static boolean isInternalRegistry(String registryCode) {
        return registryCode == null || REGISTRY_CODE.equals(registryCode) || KYOTO_REGISTRY_CODE.equals(registryCode);
    }

    /**
     * Returns whether the provided account is hosted inside the registry.
     * @param accountFullIdentifier The full identifier.
     * @return false/true
     */
    public static boolean accountIsInternal(String accountFullIdentifier) {
        return StringUtils.hasText(accountFullIdentifier)
            && accountFullIdentifier.length() >= 2
            && isInternalRegistry(accountFullIdentifier.substring(0, 2));
    }

}
