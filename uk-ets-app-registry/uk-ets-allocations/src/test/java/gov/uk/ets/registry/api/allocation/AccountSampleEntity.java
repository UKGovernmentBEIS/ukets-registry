package gov.uk.ets.registry.api.allocation;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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