package gov.uk.ets.registry.api.regulatornotice.domain;

import gov.uk.ets.registry.api.task.domain.Task;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "regulator_notice")
@Table(name = "regulator_notice")
public class RegulatorNotice extends Task {

    @Column(name = "process_type")
    private String processType;
}
