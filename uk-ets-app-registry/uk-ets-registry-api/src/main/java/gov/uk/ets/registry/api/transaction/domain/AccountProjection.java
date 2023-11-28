package gov.uk.ets.registry.api.transaction.domain;

import com.querydsl.core.annotations.QueryProjection;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.RegistryAccountType;
import lombok.Getter;

/**
 * The projection of an account that participates in a transaction.
 */
@Getter
public class AccountProjection {

    /**
     * Account: The full account identifier, e.g. GB-100-10455-0-61, JP-100-23213 etc.
     */
    private String accountFullIdentifier;

    /**
     * The kyoto account type.
     */
    private KyotoAccountType kyotoAccountType;

    /**
     * The registry account type.
     */
    private RegistryAccountType registryAccountType;

    /**
     * The account holder name of the account
     */
    private String accountHolderName;

    /**
     * The uk registry account identifier.
     */
    private Long ukRegistryAccountIdentifier;

    /**
     * The uk registry account name
     */
    private String ukRegistryAccountName;

    /**
     * The account type label
     */
    private String accountTypeLabel;

    private AccountStatus accountStatus;
    /**
     * Constructs a {@link QAccountProjection a query project} of an {@link gov.uk.ets.registry.api.account.domain.Account account}
     *
     * @param accountFullIdentifier the full identifier of the account
     * @param kyotoAccountType the {@link AccountType account type} of the kyoto {@link gov.uk.ets.registry.api.account.domain.Account account}
     * @param registryAccountType the {@link AccountType account type} of the UKETS {@link gov.uk.ets.registry.api.account.domain.Account account}
     * @param accountTypeLabel the account type label of the {@link gov.uk.ets.registry.api.account.domain.Account account}
     */
    @QueryProjection
    public AccountProjection(String accountFullIdentifier,
                             String ukRegistryAccountName,
                             KyotoAccountType kyotoAccountType,
                             RegistryAccountType registryAccountType,
                             String accountHolderName,
                             Long ukRegistryAccountIdentifier, String accountTypeLabel,
                             AccountStatus accountStatus) {
        this.accountFullIdentifier = accountFullIdentifier;
        this.ukRegistryAccountName = ukRegistryAccountName;
        this.kyotoAccountType = kyotoAccountType;
        this.registryAccountType = registryAccountType;
        this.ukRegistryAccountIdentifier = ukRegistryAccountIdentifier;
        this.accountHolderName = accountHolderName;
        this.accountTypeLabel = accountTypeLabel;
        this.accountStatus = accountStatus;
    }

    /**
     * returns the boolean flag which indicates if the account represented by this projection is
     * government or not
     *
     * @return true/false
     */
    public boolean isGovernmentAccount() {
        if (kyotoAccountType == null) {
            throw new IllegalStateException("The kyoto account type cannot be null in the account projection");
        }

        if (registryAccountType == null) {
            return false;
        }

        return AccountType.get(registryAccountType, kyotoAccountType).isGovernmentAccount();
    }

    public boolean isExternalAccount() {
        return registryAccountType == null;
    }
}
