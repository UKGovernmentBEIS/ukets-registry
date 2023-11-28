package gov.uk.ets.registry.api.auditevent;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainEvent<T extends DomainObject> implements Serializable {
    T domainObject;
    String who;
    String what;
    Date when;
    String description;
}
