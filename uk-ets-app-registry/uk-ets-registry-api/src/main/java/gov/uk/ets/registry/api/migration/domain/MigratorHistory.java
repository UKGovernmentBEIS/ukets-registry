package gov.uk.ets.registry.api.migration.domain;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "migrator_history")
@Getter
@Setter
@SequenceGenerator(name = "migrator_history_generator", sequenceName = "migrator_history_seq", allocationSize = 1)
public class MigratorHistory {

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "migrator_history_generator")
    private Long id;

    /**
     * The migrator name.
     */
    @Column(name = "migrator_name")
    @Enumerated(EnumType.STRING)
    private MigratorName migratorName;

    /**
     * The created on date.
     */
    @Column(name = "created_on")
    private LocalDateTime createdOn;
}
