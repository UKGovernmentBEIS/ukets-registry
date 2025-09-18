package gov.uk.ets.registry.api.transaction.domain;

import gov.uk.ets.registry.api.transaction.domain.type.TransactionStatus;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

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
    @AttributeOverrides({
        @AttributeOverride(column = @Column(name = "acquiring_account_identifier"), name = "accountIdentifier"),
        @AttributeOverride(column = @Column(name = "acquiring_account_type"), name = "accountType"),
        @AttributeOverride(column = @Column(name = "acquiring_account_registry_code"), name = "accountRegistryCode"),
        @AttributeOverride(column = @Column(name = "acquiring_account_full_identifier"),
            name = "accountFullIdentifier"),
    })
    @Embedded
    private AccountBasicInfo acquiringAccount;

    /**
     * Transferring account basic info.
     */
    @AttributeOverrides({
        @AttributeOverride(column = @Column(name = "transferring_account_identifier"), name = "accountIdentifier"),
        @AttributeOverride(column = @Column(name = "transferring_account_type"), name = "accountType"),
        @AttributeOverride(column = @Column(name = "transferring_account_registry_code"), name = "accountRegistryCode"),
        @AttributeOverride(column = @Column(name = "transferring_account_full_identifier"),
            name = "accountFullIdentifier"),
    })
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
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
    private List<TransactionBlock> blocks;

    /**
     * The transaction history entries.
     */
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
    private List<TransactionHistory> historyEntries;

    /**
     * The transaction response entries.
     */
    @OneToMany(mappedBy = "transaction", fetch = FetchType.LAZY)
    private List<TransactionResponse> responseEntries;
    

    /**
     * The transaction response entries.
     */
    @OneToMany(mappedBy = "objectTransaction", fetch = FetchType.LAZY)
    private List<TransactionConnection> transactionConnectionsOfTheObject;
    
    /**
     * The transaction response entries.
     */
    @OneToMany(mappedBy = "subjectTransaction", fetch = FetchType.LAZY)
    private List<TransactionConnection> transactionConnectionsOfTheSubject;

    /**
     * ITL notification identifier.
     */
    @Column(name = "notification_identifier")
    private Long notificationIdentifier;

    /**
     * Additional attributes.
     */
    @Column(name = "attributes")
    private String attributes;

    /**
     * Contains the signature, given by the signing-api during signing.
     */
    @Column(name = "signature")
    private String signature;

    /**
     * The signed data object. Contains a JSON representation of the transaction summary.
     */
    @Type(JsonType.class)
    @Column(name = "signed_data", columnDefinition = "json")
    private String signedData;

    /**
     * The transaction reference.
     */
    @Column(name = "reference")
    private String reference;
}
