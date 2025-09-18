package gov.uk.ets.registry.api.transaction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AccountBalanceHistory {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "account_balance_history_generator", sequenceName = "account_balance_history_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_balance_history_generator")
    private Long id;

    /**
     * The unique business account identifier, e.g. 10000050.
     */
    @Column(name = "account_identifier")
    private Long accountIdentifier;
    
    /**
     * The last processed transaction identifier, e.g. GB40140.
     */
    @Column(name = "transaction_identifier")
    private String transactionIdentifier;
    
    /**
     * The balance after completion of the transaction.
     */
    private Long balance;
    
    /**
     * The account balance unit type.
     * An indication of what unit types an account holds.
     * For example:
     * <ul>
     * <li>MULTIPLE means that the account holds units of multiple types,</li>
     * <li>AAU means that the account holds only AAUs etc.</li>
     * </ul>
     */
    @Column(name = "unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType unitType;
}
