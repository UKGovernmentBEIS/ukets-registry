package gov.uk.ets.registry.api.lock;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RegistryTaskLog {

    /**
     * This is the identifier of a task.
     * Currently, for Authorisations we use the MD5 of the json as identifier.
     */
    @Id
    private String id;

    private Date executedDate;
}
