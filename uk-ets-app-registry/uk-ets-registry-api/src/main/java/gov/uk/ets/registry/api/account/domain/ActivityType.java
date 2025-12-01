package gov.uk.ets.registry.api.account.domain;

import jakarta.persistence.Entity;
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
@Table
@Getter
@Setter
@EqualsAndHashCode(of = {"description"})
@SequenceGenerator(name = "activity_type_generator", sequenceName = "activity_type_seq", allocationSize = 1)
public class ActivityType {

    /**
     * The id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "activity_type_generator")
    private Long id;

    /**
     * The installation.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compliant_entity_id")
    private Installation installation;

    private String description;

}
