package gov.uk.ets.registry.api.task.domain;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
