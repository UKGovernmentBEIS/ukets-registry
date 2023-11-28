package uk.ets.lib.kafka.deadletter.transactional;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Entity
public class TxTestEntity {
    @Id
    private String id;
}
