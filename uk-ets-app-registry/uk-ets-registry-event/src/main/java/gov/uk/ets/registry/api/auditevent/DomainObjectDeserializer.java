package gov.uk.ets.registry.api.auditevent;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class DomainObjectDeserializer extends StdDeserializer<DomainObject> {

    public DomainObjectDeserializer() { 
        this(null); 
    } 
 
    public DomainObjectDeserializer(Class<?> vc) { 
        super(vc); 
    }    
    
    @Override
    public DomainObject deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        
        JsonNode node = jp.getCodec().readTree(jp);
        
        String domainType = node.get("domainType").textValue();
        String domainId = node.get("domainId").textValue();
 
        try {
            return DomainObject.create(Class.forName(domainType), domainId);
        } catch (ClassNotFoundException e) {
            log.error("Error deserialize DomainObject.",e);
        }
        return DomainObject.create(null, domainId);
    }

}
