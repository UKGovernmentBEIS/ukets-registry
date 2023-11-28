package gov.uk.ets.registry.api.transaction.domain.util;

import gov.uk.ets.registry.api.transaction.domain.Transaction;
import gov.uk.ets.registry.api.transaction.domain.data.TransactionSummary;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Locale;
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
     * The day format.
     */
    public static final String DAY_FORMAT = "dd MMM yyyy";

    /**
     * The day format (full month).
     */
    public static final String DAY_FORMAT_FULL_MONTH = "dd MMMM yyyy";

    /**
     * The time format.
     */
    public static final String TIME_FORMAT = "hh:mma";

    /**
     * The day time format 24h with seconds.
     */
    public static final String DATE_TIME_FORMAT_24H_WITH_SECONDS = "dd/MM/yyyy HH:mm:ss";


    /**
     * The locale.
     */
    public static final Locale LOCALE = Locale.ENGLISH;

    /**
     * The International Transaction Log (ITL).
     */
    public static final String ITL_TO = "ITL";

    /**
     * ITL protocol major version.
     */
    public static final Integer ITL_MAJOR_VERSION = 1;

    /**
     * ITL protocol minor version.
     */
    public static final Integer ITL_MINOR_VERSION = 1;

    /**
     * The maximum number of unit blocks involved in a transaction, as imposed by ITL.
     */
    public static final Integer ITL_MAXIMUM_NUMBER_OF_BLOCKS = 3000;

    /**
     * Retrieves the registry code, based on whether the context refers to the Kyoto protocol.
     *
     * @param isKyoto whether the transaction, account etc. is a Kyoto one.
     * @return GB for Kyoto, UK otherwise
     */
    public static String getRegistryCode(boolean isKyoto) {
        return isKyoto ? KYOTO_REGISTRY_CODE : REGISTRY_CODE;
    }

    /**
     * Returns whether the provided registry code refers to the registry.
     *
     * @param registryCode The registry code.
     * @return false/true
     */
    public static boolean isInternalRegistry(String registryCode) {
        return registryCode == null || REGISTRY_CODE.equals(registryCode) || KYOTO_REGISTRY_CODE.equals(registryCode);
    }

    /**
     * Returns whether the provided account is hosted inside the registry.
     *
     * @param accountFullIdentifier The full identifier.
     * @return false/true
     */
    public static boolean accountIsInternal(String accountFullIdentifier) {
        return StringUtils.hasText(accountFullIdentifier)
            && accountFullIdentifier.length() >= 2
            && isInternalRegistry(accountFullIdentifier.substring(0, 2));
    }

    /**
     * Returns whether the provided transaction is inbound.
     *
     * @param transaction The transaction.
     * @return false/true
     */
    public static boolean isInboundTransaction(TransactionSummary transaction) {
        return TransactionType.ExternalTransfer.equals(transaction.getType()) &&
            StringUtils.hasText(transaction.getTransferringRegistryCode()) &&
            !Constants.isInternalRegistry(transaction.getTransferringRegistryCode());
    }

    /**
     * Returns whether the provided transaction is inbound.
     *
     * @param transaction The transaction.
     * @return false/true
     */
    public static boolean isInboundTransaction(Transaction transaction) {
        return TransactionType.ExternalTransfer.equals(transaction.getType()) &&
            transaction.getTransferringAccount() != null &&
            StringUtils.hasText(transaction.getTransferringAccount().getAccountRegistryCode()) &&
            !Constants.isInternalRegistry(transaction.getTransferringAccount().getAccountRegistryCode());
    }


}
