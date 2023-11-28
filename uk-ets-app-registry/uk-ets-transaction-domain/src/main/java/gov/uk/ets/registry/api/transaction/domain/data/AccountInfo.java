package gov.uk.ets.registry.api.transaction.domain.data;

import gov.uk.ets.registry.api.transaction.domain.AccountIdentifier;
import lombok.*;

import java.io.Serializable;

/**
 * A minimal version of the account as Α DTO object.
 */


@Getter
@Setter
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountInfo implements AccountIdentifier, Serializable {
    private Long identifier;
    private String fullIdentifier; // GB-100-1002-1-84 -
    private String accountName;   // Party Holding 1
    private String accountHolderName;
    private String registryCode;
    private String accountType;
    private Boolean isGovernment;
    private Boolean kyotoAccountType;

    /**
     * An AccountInfo constructor.
     * @param fullIdentifier the account full identifier.
     * @param accountName the account name.
     * @param accountHolderName the account holder name.
     */
    public AccountInfo(String fullIdentifier, String accountName, String accountHolderName) {
        this.fullIdentifier = fullIdentifier;
        this.accountName = accountName;
        this.accountHolderName = accountHolderName;
    }

    /**
     * An AccountInfo constructor.
     * @param identifier the account identifier.
     * @param fullIdentifier the account full identifier.
     * @param accountName the account name.
     * @param accountHolderName the account holder name.
     * @param registryCode the the registry code.
     */
    public AccountInfo(Long identifier, String fullIdentifier, String accountName, String accountHolderName, String registryCode) {
        this.identifier = identifier;
        this.fullIdentifier = fullIdentifier;
        this.accountName = accountName;
        this.accountHolderName = accountHolderName;
        this.registryCode = registryCode;
    }

    /**
     * An AccountInfo constructor.
     * @param identifier the account identifier.
     * @param fullIdentifier the account full identifier.
     * @param accountName the account name.
     * @param accountHolderName the account holder name.
     * @param registryCode the the registry code.
     */
    public AccountInfo(Long identifier, String fullIdentifier, String accountName, String accountHolderName, String registryCode, String accountType) {
        this.identifier = identifier;
        this.fullIdentifier = fullIdentifier;
        this.accountName = accountName;
        this.accountHolderName = accountHolderName;
        this.registryCode = registryCode;
        this.accountType = accountType;
    }
}
