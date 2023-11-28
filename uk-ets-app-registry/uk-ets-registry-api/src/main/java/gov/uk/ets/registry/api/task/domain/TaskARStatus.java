package gov.uk.ets.registry.api.task.domain;

import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "task_ar_status")
public class TaskARStatus {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -2429814803730532121L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "task_ar_status_generator", sequenceName = "task_ar_status_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "task_ar_status_generator")
    private Long id;

    /**
     * The Task.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;

    /**
     * The User.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * The Authorised Representative Status.
     */
    @Column(name = "ar_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus state;

}
