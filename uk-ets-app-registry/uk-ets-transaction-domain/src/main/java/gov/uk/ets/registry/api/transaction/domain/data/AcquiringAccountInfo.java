package gov.uk.ets.registry.api.transaction.domain.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Adds trusted information to an acquiring account.
 */
@Getter
@Setter
@AllArgsConstructor(onConstructor_={@JsonCreator})
public class AcquiringAccountInfo extends AccountInfo {

    private boolean trusted;

    @Builder(builderMethodName = "acquiringAccountInfoBuilder")
    public AcquiringAccountInfo(Long identifier, String fullIdentifier, String accountName,
                                String accountHolderName, String registryCode, boolean trusted, boolean isGovernment) {
        super(identifier, fullIdentifier, accountName, accountHolderName, registryCode, null, isGovernment, null);
        this.trusted = trusted;
    }

    public AcquiringAccountInfo(Long identifier, String fullIdentifier, String accountName,
                                String accountHolderName, String registryCode, boolean trusted) {
        super(identifier, fullIdentifier, accountName, accountHolderName, registryCode, null, null, null);
        this.trusted = trusted;
    }

    public AcquiringAccountInfo(String fullIdentifier, String accountName, String accountHolderName, boolean trusted) {
        super(fullIdentifier, accountName, accountHolderName);
        this.trusted = trusted;
    }

}
