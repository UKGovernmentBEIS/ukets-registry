package gov.uk.ets.compliance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dynamic_compliance_entity")
@EqualsAndHashCode(of = "compliantEntityId")
@Setter
@Getter
public class DynamicComplianceEntity {

    @Id
    private Long compliantEntityId;

    @Column(length = 65000)
    private String dynamicCompliance;

}
