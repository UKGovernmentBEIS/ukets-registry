package gov.uk.ets.registry.api.transaction.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Represents a day off work, e.g. New Year’s Day, Early May bank holiday, Summer bank holidays etc.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"date"})
public class Holiday {

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "holiday_id_generator", sequenceName = "holiday_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "holiday_id_generator")
    private Long id;

    /**
     * The date.
     */
    @Column(name = "working_off_day")
    @Temporal(TemporalType.DATE)
    private Date date;

}
