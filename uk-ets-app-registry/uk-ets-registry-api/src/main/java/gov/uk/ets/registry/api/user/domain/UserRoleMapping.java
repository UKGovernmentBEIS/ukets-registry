package gov.uk.ets.registry.api.user.domain;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;


@Entity
@Table(name = "user_role_mapping")
@SequenceGenerator(name = "user_role_mapping_id_generator", sequenceName = "user_role_mapping_seq", allocationSize = 1)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserRoleMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_role_mapping_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private IamUserRole role;

    private LocalDateTime mappedOn = LocalDateTime.now();

    public UserRoleMapping(User user, IamUserRole role) {
        this.user = user;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        UserRoleMapping that = (UserRoleMapping) o;

        return Objects.equals(user, that.user) && Objects.equals(role, that.role);

    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

