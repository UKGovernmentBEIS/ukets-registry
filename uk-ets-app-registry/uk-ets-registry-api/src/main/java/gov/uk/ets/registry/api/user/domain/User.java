package gov.uk.ets.registry.api.user.domain;

import gov.uk.ets.registry.api.auditevent.DomainObject;
import gov.uk.ets.registry.api.common.model.services.converter.StringTrimConverter;
import gov.uk.ets.registry.api.task.domain.Task;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * The persistent class for the users database table.
 */
@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "urid")
@Table(name = "users")
public class User implements Serializable, DomainObject {

    /**
     * Serialisation version.
     */
    private static final long serialVersionUID = -7954060387582736719L;

    /**
     * The id.
     */
    @Id
    @SequenceGenerator(name = "users_id_generator", sequenceName = "users_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_generator")
    private Long id;

    /**
     * The urid.
     */
    private String urid;

    /**
     * The iam identifier.
     */
    @Column(name = "iam_identifier")
    private String iamIdentifier;

    /**
     * The first name.
     */
    @Column(name = "first_name")
    @Convert(converter = StringTrimConverter.class)
    private String firstName;

    /**
     * The last name.
     */
    @Column(name = "last_name")
    @Convert(converter = StringTrimConverter.class)
    private String lastName;

    @Column(name="email")
    @Convert(converter = StringTrimConverter.class)
    private String email;

    /**
     * The disclosed name.
     */
    @Column(name = "disclosed_name")
    @Convert(converter = StringTrimConverter.class)
    private String disclosedName;

    /**
     * The state.
     */
    @Enumerated(EnumType.STRING)
    private UserStatus state;

    /**
     * The state.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_state")
    private UserStatus previousState;
    
    /**
     * The known as name.
     */
    @Column(name = "known_as")
    @Convert(converter = StringTrimConverter.class)
    private String knownAs;

    /**
     * The enrolment key.
     */
    @Column(name = "enrolment_key")
    private String enrolmentKey;

    /**
     * The enrolment key date.
     */
    @Column(name = "enrolment_key_date")
    private Date enrolmentKeyDate;

    /**
     * The requests.
     */
    @OneToMany(mappedBy = "initiatedBy", fetch = FetchType.LAZY)
    private List<Task> tasks;

    @OneToMany(
        mappedBy = "user",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<UserRoleMapping> userRoles = new HashSet<>();

    public void addUserRole(IamUserRole role) {
        UserRoleMapping postTag = new UserRoleMapping(this, role);
        userRoles.add(postTag);
    }

    public void removeUserRole(IamUserRole role) {
        for (Iterator<UserRoleMapping> iterator = userRoles.iterator(); iterator.hasNext(); ) {
            UserRoleMapping userRoleMapping = iterator.next();
            if (userRoleMapping.getUser().equals(this) && userRoleMapping.getRole().equals(role)) {
                iterator.remove();
                userRoleMapping.setUser(null);
                userRoleMapping.setRole(null);
            }
        }
    }

    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", urid='" + urid + '\'' +
            ", iamIdentifier='" + iamIdentifier + '\'' +
            ", firstName='" + firstName + '\'' +
            ", lastName='" + lastName + '\'' +
            ", state=" + state +
            ", enrolmentKey='" + enrolmentKey + '\'' +
            ", enrolmentKeyDate=" + enrolmentKeyDate +
            ", tasks=" + tasks +
            '}';
    }

    @Override
    public String domainId() {
        return urid;
    }

    @Override
    public String domainType() {
        return this.getClass().getSimpleName();
    }
}
