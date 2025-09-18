package gov.uk.ets.registry.api.lock;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RegistryTaskLock {

    @Id
    private Long id;

    private boolean active;
    private Date acquiredAt;
    private String acquiredBy;
}
