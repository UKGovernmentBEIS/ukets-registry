package uk.gov.ets.transaction.log.domain;


import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.gov.ets.transaction.log.domain.type.TransactionStatus;
import uk.gov.ets.transaction.log.domain.type.TransactionType;
import uk.gov.ets.transaction.log.domain.type.UnitType;

@Getter
@Setter
@EqualsAndHashCode(of = {"identifier"})
@MappedSuperclass
public class BaseTransactionEntity {
    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "transaction_id_generator", sequenceName = "transaction_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_generator")
    private Long id;

    /**
     * The unique business identifier, e.g. GB40140.
     */
    private String identifier;

    /**
     * The type.
     */
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    /**
     * The status.
     */
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    /**
     * The total quantity transferred in the context of this transaction.
     */
    private Long quantity;

    /**
     * Acquiring account basic info.
     */
    @AttributeOverride(column = @Column(name = "acquiring_account_identifier"), name = "accountIdentifier")
    @AttributeOverride(column = @Column(name = "acquiring_account_type"), name = "accountType")
    @AttributeOverride(column = @Column(name = "acquiring_account_full_identifier"),name = "accountFullIdentifier")
    @Embedded
    private AccountBasicInfo acquiringAccount;

    /**
     * Transferring account basic info.
     */
    @AttributeOverride(column = @Column(name = "transferring_account_identifier"), name = "accountIdentifier")
    @AttributeOverride(column = @Column(name = "transferring_account_type"), name = "accountType")
    @AttributeOverride(column = @Column(name = "transferring_account_full_identifier"),name = "accountFullIdentifier")
    @Embedded
    private AccountBasicInfo transferringAccount;

    /**
     * The date when the transaction started.
     */
    @Column(name = "started")
    @Temporal(TemporalType.TIMESTAMP)
    private Date started;

    /**
     * The date when the transaction status was last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date lastUpdated;

    /**
     * The date when the transaction will be executed.
     */
    @Column(name = "execution_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime executionDate;

    /**
     * The unit types involved in this transaction.
     * <ol>
     *     <li>Multiple, if many unit types are transacted.</li>
     *     <li>A specific unit type otherwise.</li>
     * </ol>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "unit_type")
    private UnitType unitType;

    /**
     * The transaction blocks.
     */
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST})
    private List<TransactionBlock> blocks;

    /**
     * The transaction history entries.
     */
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST})
    private List<TransactionHistory> historyEntries;

    /**
     * The transaction response entries.
     */
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST})
    private List<TransactionResponse> responseEntries;
}
