package gov.uk.ets.registry.api.user.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

@Entity
@Table(name = "iam_user_role")
@Getter
@Setter
@ToString
@SequenceGenerator(name = "iam_user_role_id_generator", sequenceName = "iam_user_role_seq", allocationSize = 1)
@NoArgsConstructor
public class IamUserRole {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "iam_user_role_id_generator")
    private Long id;

    private String iamIdentifier;

    private String roleName;

    public IamUserRole(String iamIdentifier, String roleName) {
        this.iamIdentifier = iamIdentifier;
        this.roleName = roleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        IamUserRole that = (IamUserRole) o;

        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
