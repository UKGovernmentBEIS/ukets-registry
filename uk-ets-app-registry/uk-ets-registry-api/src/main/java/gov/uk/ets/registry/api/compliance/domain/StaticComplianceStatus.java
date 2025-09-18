package gov.uk.ets.registry.api.compliance.domain;

import gov.uk.ets.registry.api.account.domain.CompliantEntity;
import gov.uk.ets.registry.api.account.domain.types.ComplianceStatus;
import java.io.Serializable;
import java.util.Objects;
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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

@Entity
@Getter
@Setter
@Table(name = "static_compliance_status")
public class StaticComplianceStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "static_compliance_status_generator", sequenceName = "static_compliance_status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "static_compliance_status_generator")
    private Long id;

    /**
     * The compliant entity (installation / aircraft operator).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliant_entity_id")
    private CompliantEntity compliantEntity;

    /**
     * The year of the emission.
     */
    private Long year;

    /**
     * The compliance status
     */
    @Column(name = "compliance_status")
    @Enumerated(EnumType.STRING)
    private ComplianceStatus complianceStatus;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        StaticComplianceStatus that = (StaticComplianceStatus) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 399713474;
    }
}
