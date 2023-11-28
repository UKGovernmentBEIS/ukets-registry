package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents the basic information of an account (transferring or acquiring) participating in
 * a transaction.
 */
@Getter
@Setter
@Embeddable
public class AccountBasicInfo implements AccountIdentifier {
    /**
     * Account: The unique account business identifier, e.g. 10455.
     */
    private Long accountIdentifier;

    /**
     * Account: The Kyoto account type, e.g. PARTY_HOLDING_ACCOUNT.
     */
    @Enumerated(EnumType.STRING)
    private KyotoAccountType accountType;

    /**
     * Account: The registry code, e.g. GB, JP etc.
     */
    private String accountRegistryCode;

    /**
     * Account: The full account identifier, e.g. GB-100-10455-0-61, JP-100-23213 etc.
     */
    private String accountFullIdentifier;

    @Override
    public Long getIdentifier() {
        return accountIdentifier;
    }

    @Override
    public String getFullIdentifier() {
        return accountFullIdentifier;
    }
}
