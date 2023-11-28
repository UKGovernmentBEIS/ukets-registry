package gov.uk.ets.registry.api.auditevent;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class DomainObjectSerializer extends StdSerializer<DomainObject> {

    public DomainObjectSerializer() {
        this(null);
    }
   
    public DomainObjectSerializer(Class<DomainObject> t) {
        super(t);
    }

    @Override
    public void serialize(DomainObject value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("domainId", value.domainId());
        jgen.writeStringField("domainType", value.domainType());
        jgen.writeEndObject();
        
    }
}
