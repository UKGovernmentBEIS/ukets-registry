package gov.uk.ets.registry.api.itl.reconciliation.domain;

import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.TransactionType;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"transactionId"})
@Table(name = "itl_recon_audit_trail_tx_log")
public class ITLReconAuditTrailTxLog {

    @Id
    @SequenceGenerator(name = "itl_recon_audit_trail_tx_log_generator", sequenceName = "itl_recon_audit_trail_tx_log_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itl_recon_audit_trail_tx_log_generator")
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recon_id")
    private ITLReconciliationLog reconciliationLog;
    
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "acquiring_registry_account")
    private Long acquiringRegistryAccount;
    
    @Column(name = "acquiring_registry_code")
    private String accountRegistryCode;
    
    @Column(name = "acquiring_account_type")
    @Enumerated(EnumType.STRING)
    private KyotoAccountType acquiringAccountType;
    
    @Column(name = "transferring_registry_account")
    private Long transferringRegistryAccount;
    
    @Column(name = "transferring_registry_code")
    private String transferringRegistryCode;
    
    @Column(name = "transferring_account_type")
    @Enumerated(EnumType.STRING)
    private KyotoAccountType transferringAccountType;
    
    @Column(name = "notification_identifier")
    private Long notificationIdentifier;

    @OneToMany(mappedBy = "reconAuditTrailTxLog", fetch = FetchType.LAZY , cascade = javax.persistence.CascadeType.ALL)
    private List<ITLReconAuditTrailTxBlock> blocks;
}
