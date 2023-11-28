package gov.uk.ets.registry.api.auditevent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(using = DomainObjectSerializer.class)
@JsonDeserialize(using = DomainObjectDeserializer.class)
public interface DomainObject {
    static DomainObject create(final Class<?> clazz, final String domainId) {
        return new DomainObject() {
            @Override
            public String domainId() {
                return domainId;
            }

            @Override
            public String domainType() {
                return clazz.getName();
            }
        };
    }

    String domainId();

    String domainType();
}
