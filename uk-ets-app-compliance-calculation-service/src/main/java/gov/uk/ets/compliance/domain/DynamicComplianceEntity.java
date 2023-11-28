package gov.uk.ets.compliance.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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
