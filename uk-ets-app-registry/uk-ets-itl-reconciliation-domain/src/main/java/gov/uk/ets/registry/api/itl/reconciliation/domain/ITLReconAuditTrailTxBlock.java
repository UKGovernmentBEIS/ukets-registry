package gov.uk.ets.registry.api.itl.reconciliation.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.ProjectTrack;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"startBlock", "endBlock", "originatingCountryCode"})
@Table(name = "itl_recon_audit_trail_tx_block")
public class ITLReconAuditTrailTxBlock {

    @Id
    @SequenceGenerator(name = "itl_recon_audit_trail_tx_block_generator", sequenceName = "itl_recon_audit_trail_tx_block_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itl_recon_audit_trail_tx_block_generator")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audit_trail_tx_log_id")
    @JsonBackReference
    private ITLReconAuditTrailTxLog reconAuditTrailTxLog;
    
    @Column(name = "start_block")
    private Long startBlock;

    @Column(name = "end_block")
    private Long endBlock;

    @Column(name = "unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType type;
    
    @Column(name = "originating_country_code")
    private String originatingCountryCode;

    @Column(name = "original_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod originalPeriod;

    @Column(name = "applicable_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod applicablePeriod;

    @Column(name = "project_id")
    private String projectId;

    @Column(name = "track")
    @Enumerated(EnumType.STRING)
    private ProjectTrack projectTrack;

    @Column(name = "year_in_commitment_period")
    private Integer year;
    
    @Column(name = "acquiring_account_type")
    @Enumerated(EnumType.STRING)
    private KyotoAccountType acquiringAccountType;
    
    @Column(name = "transferring_account_type")
    @Enumerated(EnumType.STRING)
    private KyotoAccountType transferringAccountType;

    @Column(name = "expiry_date")
    private Date expiryDate;
 
    @Column(name = "lulucf_code")
    private Integer lulucfCode;

    @Column(name = "block_role")
    private String blockRole;
    
    @Column(name = "installation_id")
    private Long installationIdentifier;
}
