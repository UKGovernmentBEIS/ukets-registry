package gov.uk.ets.registry.api.tal.web.model;

import gov.uk.ets.registry.api.tal.domain.types.TrustedAccountStatus;
import lombok.*;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * The trusted account transfer object.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TrustedAccountDTO implements Serializable {
    private static final long serialVersionUID = -5145186839665780664L;
    /**
     * The ID of the trusted account.
     */
    private Long id;
    /**
     * The full identifier of the trusted account.
     */
    @NotNull
    private String accountFullIdentifier;

    /**
     * Signifies if the trusted account is under the same account holder as the host account.
     */
    private Boolean underSameAccountHolder;

    /**
     * The trusted account description.
     */
    @NotEmpty
    @Size(min = 3, max = 256)
    private String description;

    /**
     * The trusted account name.
     */
    private String name;

    /**
     * The trusted account status.
     */
    private TrustedAccountStatus status;

    /**
     * The planned activation date of the trusted account.
     */
    private String activationDate;

    /**
     * The planned activation time of the trusted account.
     */
    private String activationTime;

    /**
     * Whether the account is KP or not.
     */
    private Boolean kyotoAccountType;
}
