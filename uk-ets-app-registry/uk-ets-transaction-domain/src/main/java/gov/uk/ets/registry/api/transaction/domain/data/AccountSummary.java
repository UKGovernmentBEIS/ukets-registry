package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.transaction.domain.AccountIdentifier;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Represents a summary of an account.
 */
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"identifier"})
public class AccountSummary implements Serializable,AccountIdentifier {

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 7396574161743158709L;

    /**
     * The unique business identifier.
     */
    private Long identifier;

    /**
     * The cumulative type.
     */
    private AccountType type;

    /**
     * The registry account type.
     */
    private RegistryAccountType registryAccountType;

    /**
     * The Kyoto account type.
     */
    private KyotoAccountType kyotoAccountType;

    /**
     * The account status.
     */
    private AccountStatus accountStatus;

    /**
     * The registry code.
     */
    private String registryCode;

    /**
     * The full identifier.
     */
    private String fullIdentifier;

    /**
     * The commitment period.
     */
    private Integer commitmentPeriod;

    /**
     * The balance.
     */
    private Long balance;

    /**
     * The unit type.
     */
    private UnitType unitType;

    /**
     * Constructor.
     * @param identifier The identifier.
     * @param registryAccountType The registry account type.
     * @param kyotoAccountType The kyoto account type.
     * @param accountStatus The account status.
     * @param registryCode The registry code.
     * @param fullIdentifier The account full identifier.
     */
    public AccountSummary(Long identifier, RegistryAccountType registryAccountType, KyotoAccountType kyotoAccountType, AccountStatus accountStatus, String registryCode, String fullIdentifier, Integer commitmentPeriod) {
        this(identifier, registryAccountType, kyotoAccountType, accountStatus, registryCode, fullIdentifier, commitmentPeriod, null, null);
    }

    /**
     * Constructor.
     * @param identifier The identifier.
     * @param registryAccountType The registry account type.
     * @param kyotoAccountType The kyoto account type.
     * @param accountStatus The account status.
     * @param registryCode The registry code.
     * @param fullIdentifier The account full identifier.
     * @param balance The balance.
     * @param unitType The unit type.
     */
    public AccountSummary(Long identifier, RegistryAccountType registryAccountType, KyotoAccountType kyotoAccountType,
                          AccountStatus accountStatus, String registryCode, String fullIdentifier, Integer commitmentPeriod,
                          Long balance, UnitType unitType) {
        this.identifier = identifier;
        this.registryAccountType = registryAccountType;
        this.kyotoAccountType = kyotoAccountType;
        this.type = AccountType.get(registryAccountType, kyotoAccountType);
        this.accountStatus = accountStatus;
        this.registryCode = registryCode;
        this.fullIdentifier = fullIdentifier;
        this.commitmentPeriod = commitmentPeriod;
        this.balance = balance;
        this.unitType = unitType;
    }

    /**
     * Parses the provided full identifier.
     * @param fullIdentifier The full identifier.
     * @param registryAccountType The registry account type.
     * @param accountStatus The account status.
     * @return an account summary.
     */
    public static AccountSummary parse(String fullIdentifier, RegistryAccountType registryAccountType, AccountStatus accountStatus) {
        if (!StringUtils.hasText(fullIdentifier)) {
            return null;
        }
        final String[] elements = fullIdentifier.split("-");
        String registryCode = null;
        if (elements.length > 0) {
            registryCode = elements[0].trim().toUpperCase();
        }
        KyotoAccountType kyotoAccountType = null;
        if (elements.length > 1) {
            kyotoAccountType = KyotoAccountType.parse(NumberUtils.createInteger(elements[1].trim()));
        }
        Long identifier = null;
        if (elements.length > 2 && NumberUtils.isDigits(elements[2])) {
            identifier = NumberUtils.createLong(elements[2].trim());
        }
        Integer commitmentPeriod = null;
        if (elements.length > 3 && NumberUtils.isDigits(elements[3])) {
            commitmentPeriod = NumberUtils.createInteger(elements[3].trim());
        }
        return new AccountSummary(
            identifier,
            registryAccountType,
            kyotoAccountType,
            accountStatus,
            registryCode,
            fullIdentifier,
            commitmentPeriod);
    }

    /**
     * Parses the identifier from the full account identifier.
     * @param fullIdentifier The full account identifier.
     * @return a number.
     */
    public static Long parseIdentifier(String fullIdentifier) {
        Long result = null;
        if (StringUtils.countOccurrencesOf(fullIdentifier, "-") > 1) {
            final String[] elements = fullIdentifier.split("-");
            if (elements.length > 2 && NumberUtils.isDigits(elements[2])) {
                result = NumberUtils.createLong(elements[2]);
            }
        }
        return result;
    }


    /**
     * Returns the check digits of this account.
     * @return the check digits
     */
    public Integer getCheckDigits() {
        return NumberUtils.createInteger(fullIdentifier.substring(fullIdentifier.lastIndexOf('-') + 1));
    }

    /**
     * Returns whether the account belongs to the registry.
     * @return false/true.
     */
    public boolean belongsToRegistry() {
        return Constants.KYOTO_REGISTRY_CODE.equals(registryCode) || Constants.REGISTRY_CODE.equals(registryCode);
    }

}
