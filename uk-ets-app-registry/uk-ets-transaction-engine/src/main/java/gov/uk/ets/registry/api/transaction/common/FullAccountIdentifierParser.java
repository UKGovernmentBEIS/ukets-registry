package gov.uk.ets.registry.api.transaction.common;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryCode;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.util.Constants;
import lombok.Setter;

import java.util.Optional;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Provides parsing services of full account identifiers (e.g. JP-100-123, GB-100-12345-2-14 etc.).
 */
@Setter
public class FullAccountIdentifierParser {

    /**
     * The registry code.
     */
    private String registryCode;

    /**
     * The Kyoto account type.
     */
    private String type;

    /**
     * The account identifier.
     */
    private String identifier;

    /**
     * The commitment period.
     */
    private String commitmentPeriod;

    /**
     * The check digits.
     */
    private String checkDigits;

    /**
     * Instantiates the parser.
     * @param fullIdentifier The full account identifier.
     * @return a parser.
     */
    public static FullAccountIdentifierParser getInstance(String fullIdentifier) {
        FullAccountIdentifierParser result = new FullAccountIdentifierParser();

        if (!StringUtils.hasText(fullIdentifier)) {
            return result;
        }

        final String[] elements = fullIdentifier.split("-");
        if (elements.length > 0) {
            result.setRegistryCode(elements[0].trim().toUpperCase());
        }
        if (elements.length > 1) {
            result.setType(elements[1].trim());
        }
        if (elements.length > 2) {
            result.setIdentifier(elements[2].trim());
        }
        if (elements.length > 3) {
            result.setCommitmentPeriod(elements[3].trim());
        }
        if (elements.length > 4) {
            result.setCheckDigits(elements[4].trim());
        }
        return  result;
    }

    /**
     * Checks whether the provided input is empty.
     * @return false/true
     */
    public boolean isEmpty() {
        return ObjectUtils.isEmpty(registryCode) &&
            ObjectUtils.isEmpty(type) &&
            ObjectUtils.isEmpty(identifier) &&
            ObjectUtils.isEmpty(commitmentPeriod) &&
            ObjectUtils.isEmpty(checkDigits);
    }

    /**
     * Whether the full account identifier contains a commitment period.
     * @return false/true
     */
    public boolean hasCommitmentPeriod() {
        return !ObjectUtils.isEmpty(commitmentPeriod);
    }

    /**
     * Whether the full account identifier defines a valid registry code.
     * @return false/true
     */
    public boolean hasValidRegistryCode() {
        return RegistryCode.isValidRegistryCode(registryCode);
    }

    /**
     * Whether the full account identifier defines a valid registry code.
     * @param transactionType the transaction type
     * @return false true
     */
    public boolean hasValidRegistryCode(TransactionType transactionType) {
        if (transactionType.isKyoto()) {
            return RegistryCode.isValidRegistryCode(registryCode) &&
                   !Constants.REGISTRY_CODE.equals(registryCode);
        }
        return RegistryCode.isValidRegistryCode(registryCode);
    }

    /**
     * Whether the full account identifier defines a valid account type.
     * @return false/true
     */
    public boolean hasValidType() {
        return !ObjectUtils.isEmpty(type) && NumberUtils.isDigits(type) &&
            KyotoAccountType.parse(NumberUtils.createInteger(type)) != null;
    }

    /**
     * Whether the full account identifier defines a valid commitment period.
     * @return false/true
     */
    public boolean hasValidCommitmentPeriod() {
        return ObjectUtils.isEmpty(commitmentPeriod) ||
            NumberUtils.isDigits(commitmentPeriod) &&
                CommitmentPeriod.findByCode(NumberUtils.createInteger(commitmentPeriod)) != null;
    }

    /**
     * Whether the full account identifier contains check digits.
     * @return false/true
     */
    public boolean hasCheckDigits() {
        return ObjectUtils.isEmpty(checkDigits) ||
            NumberUtils.isDigits(checkDigits) &&
                checkDigits.length() <= 2;
    }

    /**
     * Whether the full account identifier defines valid check digits.
     * @return false/true
     */
    public boolean hasValidCheckDigits() {
        return new GeneratorService().validateCheckDigits(
            NumberUtils.createInteger(type),
            NumberUtils.createInteger(identifier),
            NumberUtils.createInteger(commitmentPeriod),
            NumberUtils.createInteger(checkDigits)
        );
    }

    /**
     * Returns whether the defined account belongs to the registry or not.
     * @return false/true
     */
    public boolean belongsToRegistry() {
        return Constants.KYOTO_REGISTRY_CODE.equals(registryCode) || Constants.REGISTRY_CODE.equals(registryCode);
    }

    /**
     * Returns whether no check digits were provided.
     * @return false/true
     */
    public boolean isEmptyCheckDigits() {
        return ObjectUtils.isEmpty(checkDigits);
    }

    /**
     * Whether the full account identifier defines a valid account identifier.
     * @return false/true
     */
    public boolean hasValidIdentifier() {
        return !ObjectUtils.isEmpty(identifier) && NumberUtils.isDigits(identifier);
    }

    /**
     * Whether the full account identifier defines a valid account identifier.
     * @return false/true
     */
    public Optional<Long> getIdentifier() {
    	
    	if(hasValidIdentifier()) {
    		return Optional.of(Long.valueOf(identifier));
    	}
        return Optional.empty();
    }
    
    /**
     * Whether the account identifier has a valid format for registry accounts.
     * @return false/true
     */
    public boolean hasValidIdentifierInRegistry() {
        if (!belongsToRegistry()) {
            return true;
        }
        return hasValidIdentifier() && identifier.length() <= 8;
    }

}
