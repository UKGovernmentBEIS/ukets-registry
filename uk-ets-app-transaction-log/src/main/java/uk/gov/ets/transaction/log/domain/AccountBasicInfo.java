package uk.gov.ets.transaction.log.domain;


import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.type.KyotoAccountType;

/**
 * Represents the basic information of an account (transferring or acquiring) participating in
 * a transaction.
 */
@Getter
@Setter
@Embeddable
public class AccountBasicInfo {
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
     * Account: The full account identifier, e.g. UK-100-10455-0-61, UK-100-23213 etc.
     */
    private String accountFullIdentifier;
}
