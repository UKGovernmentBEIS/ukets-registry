package gov.uk.ets.registry.api.transaction.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"transactionIdentifier"})
public class TransactionAccountBalance implements Serializable {

    private static final long serialVersionUID = -530570059326163383L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_account_balance_id_generator", sequenceName = "transaction_account_balance_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_account_balance_id_generator")
    private Long id;

    /**
     * The unique business account identifier, e.g. 10000050.
     */
    @Column(name = "transferring_account_identifier")
    private Long transferringAccountIdentifier;
    
    /**
     * The unique business account identifier, e.g. 10000050.
     */
    @Column(name = "acquiring_account_identifier")
    private Long acquiringAccountIdentifier;    
    
    
    /**
     * The unique business transaction identifier, e.g. GB40140.
     */
    @Column(name = "transaction_identifier")
    private String transactionIdentifier;
    
    /**
     * The transferring account balance after completion of the transaction.
     */
    @Column(name = "transferring_account_balance")
    private Long transferringAccountBalance;
    
    /**
     * The transferring account balance unit type.
     * An indication of what unit types an account holds.
     * For example:
     * <ul>
     * <li>MULTIPLE means that the account holds units of multiple types,</li>
     * <li>AAU means that the account holds only AAUs etc.</li>
     * </ul>
     */
    @Column(name = "transferring_balance_unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType transferringAccountBalanceUnitType;
    
    /**
     * The acquiring account balance after completion of the transaction.
     */
    @Column(name = "acquiring_account_balance")
    private Long acquiringAccountBalance;
    
    /**
     * The acquiring account balance unit type.
     * An indication of what unit types an account holds.
     * For example:
     * <ul>
     * <li>MULTIPLE means that the account holds units of multiple types,</li>
     * <li>AAU means that the account holds only AAUs etc.</li>
     * </ul>
     */
    @Column(name = "acquiring_balance_unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType acquiringAccountBalanceUnitType;
    
    /**
     * The date when the transaction status and hence balance was last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date lastUpdated;
}
