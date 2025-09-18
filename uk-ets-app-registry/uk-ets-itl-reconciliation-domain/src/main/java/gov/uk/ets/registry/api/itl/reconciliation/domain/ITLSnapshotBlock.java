package gov.uk.ets.registry.api.itl.reconciliation.domain;

import gov.uk.ets.registry.api.transaction.domain.type.CommitmentPeriod;
import gov.uk.ets.registry.api.transaction.domain.type.KyotoAccountType;
import gov.uk.ets.registry.api.transaction.domain.type.UnitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "itl_snapshot_block")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ITLSnapshotBlock {

    @Id
    @SequenceGenerator(name = "itl_snapshot_block_id_generator", sequenceName = "itl_snapshot_block_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itl_snapshot_block_id_generator")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snap_log_id")
    private ITLSnapshotLog snapshotLog;

    /**
     * The kyoto account type.
     */
    @Column(name = "account_type")
    @Enumerated(EnumType.STRING)
    private KyotoAccountType accountType;

    /**
     * The account CPeriod.
     */
    @Column(name = "account_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod accountPeriod;

    /**
     * The identifier.
     */
    @Column(name = "account_id")
    private Long accountIdentifier;

    /**
     * The start serial number of the unit block.
     */
    @Column(name = "start_block")
    private Long startBlock;

    /**
     * The end serial number of the unit block.
     */
    @Column(name = "end_block")
    private Long endBlock;

    /**
     * The unit type.
     */
    @Column(name = "unit_type")
    @Enumerated(EnumType.STRING)
    private UnitType type;

    /**
     * The originating country code.
     */
    @Column(name = "originating_country_code")
    private String originatingCountryCode;

    /**
     * The applicable commitment period.
     */
    @Column(name = "applicable_period")
    @Enumerated(EnumType.STRING)
    private CommitmentPeriod applicablePeriod;

    /**
     * The unique business identifier, e.g. GB40140.
     */
    @Column(name = "transaction_id")
    private String transactionIdentifier;

    @Column(name = "data_issue_flg")
    private String dataIssueFlag;

}
