package gov.uk.ets.reporting.metrics.domain;


import gov.uk.ets.reporting.metrics.types.ComplianceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "account_metrics", schema = "reporting_metrics")
@SequenceGenerator(name = "account_metrics_id_generator", schema = "reporting_metrics", sequenceName = "account_metrics_seq", allocationSize = 1)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class AccountMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_metrics_id_generator")
    private Long id;
    
    /**
     * The account identifier.
     */
    @Column(name = "account_identifier")
    private Long identifier;
    
    @Column(name = "total_emissions")
    private Long totalVerifiedEmissions;
    
    @Column(name = "quantity_surrendered")
    private Long quantitySurrendered;
    
    @Column(name = "quantity_reversed_surrendered")
    private Long quantityReversedSurrendered;
    
    /**
     * This column is materialised for performance purposes.
     * It is the quantitySurrendered - quantityReversedSurrendered - totalVerifiedEmissions
     */
    @Column(name = "surrender_balance")
    private Long surrenderBalance;
    
    @Column(name = "dynamic_compliance_status")
    @Enumerated(EnumType.STRING)
    private ComplianceStatus dynamicComplianceStatus;

}