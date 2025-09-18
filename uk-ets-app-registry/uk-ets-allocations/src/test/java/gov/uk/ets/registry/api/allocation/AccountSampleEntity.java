package gov.uk.ets.registry.api.allocation;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an account.
 *
 */
@Entity
@Getter
@Setter
@Table(name = "account")
@EqualsAndHashCode(of = {"identifier"})
public class AccountSampleEntity implements Serializable {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -5024047143003655395L;

    /**
     * The id.
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * The identifier.
     */
    private Long identifier;    
    
    /**
     * The account name.
     */
    @Column(name = "compliant_entity_id")
    private Long compliantEntityId;

    /**
     * The account status.
     */
    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    /**
     * The full account identifier.
     */
    @Column(name = "full_identifier")
    private String fullIdentifier;

}