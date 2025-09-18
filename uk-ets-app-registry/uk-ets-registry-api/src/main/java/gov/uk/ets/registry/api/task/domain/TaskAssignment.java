package gov.uk.ets.registry.api.task.domain;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Builder
public class TaskAssignment {

    @Id
    @SequenceGenerator(name = "task_assignment_id_generator", sequenceName = "task_assignment_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_assignment_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * The User identifier
     */
    @JoinColumn(name = "urid")
    private String urid;

    @Column(name = "assignment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date assignmentDate;

    /**
     * The IamUserRoles that the user had at the point of the assignment separated by comma (,)
     */
    @JoinColumn(name = "roles")
    private String roles;
}
