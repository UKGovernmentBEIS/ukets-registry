package gov.uk.ets.keycloak.events.service.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.idm.UserRepresentation;
import gov.uk.ets.keycloak.logger.CustomLogger;

public class KafkaEventListenerProvider implements EventListenerProvider {

    private static final String CLIENT_ID = "keycloak-event-listener";
    private static final String TOPIC = "keycloak.events.in";
    private static final String SENSITIVE_DATA_TEXT = "SENSITIVE DATA REMOVED";
    public static final List<String> SENSITIVE_DATA_LIST = List.of(SENSITIVE_DATA_TEXT);
    public static final List<String> SENSITIVE_USER_ATTRIBUTES = List.of(
        "buildingAndStreet",
        "buildingAndStreetOptional",
        "buildingAndStreetOptional2",
        "postCode",
        "townOrCity",
        "stateOrProvince",
        "country",
        "countryOfBirth",
        "birthDate",
        "alsoKnownAs",

        "workBuildingAndStreet",
        "workBuildingAndStreetOptional",
        "workBuildingAndStreetOptional2",
        "workPostCode",
        "workTownOrCity",
        "workStateOrProvince",
        "workCountry",
        "workCountryCode",
        "workPhoneNumber",
        "workMobileCountryCode",
        "workMobilePhoneNumber",
        "workAlternativeCountryCode",
        "workAlternativePhoneNumber",
        "noMobilePhoneNumberReason",
        "workEmailAddress",
        "workEmailAddressConfirmation",
        "recoveryCountryCode",
        "recoveryPhoneNumber",
        "recoveryEmailAddress"
    );

    private final String bootstrapServers;
    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;
    private KeycloakSession session;
    private static final Set<EventType> PUBLISHABLE_EVENT_TYPES = Set.of(EventType.LOGIN_ERROR,EventType.CUSTOM_REQUIRED_ACTION,
        EventType.UPDATE_PASSWORD,EventType.UPDATE_TOTP);
    private static final Set<ResourceType> PUBLISHABLE_RESOURCE_TYPES = Set.of(ResourceType.CLIENT_ROLE,ResourceType.CLIENT_ROLE_MAPPING);
    
    public KafkaEventListenerProvider(KeycloakSession session) {
        this.bootstrapServers = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        this.producer = this.createProducer(CLIENT_ID, bootstrapServers);
        this.objectMapper = new ObjectMapper();
        this.session = session;
    }

    public void onEvent(Event event) {
        try {
            if(PUBLISHABLE_EVENT_TYPES.contains(event.getType())) {
                produceEvent(objectMapper.writeValueAsString(event), TOPIC);                
            }
        } catch (ExecutionException | JsonProcessingException | IllegalStateException e) {
            logError(e.getMessage(), e);
        }
    }

    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        try {
            if(PUBLISHABLE_RESOURCE_TYPES.contains(event.getResourceType())) {
                AdminEvent sanitizedEvent = sanitizeUserEvent(event);
                produceEvent(objectMapper.writeValueAsString(sanitizedEvent), TOPIC);  
            }
        } catch (ExecutionException | JsonProcessingException | IllegalStateException e) {
            logError(e.getMessage(), e);
        }
    }

    private void logError(String error, Exception e){
        CustomLogger.print(Logger.Level.ERROR, getXRequestId(), null, null, null, error, e);
    }

    private String getXRequestId() {
        return session.getContext().getRequestHeaders().getHeaderString("X-Request-ID");
    }

    public void close() {
        // nothing here
    }

    private Producer<String, String> createProducer(String clientId, String bootstrapServer) {
        KafkaProducer<String, String> stringStringKafkaProducer = null;
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        String kafkaAuthEnabled = System.getenv("KAFKA_AUTHENTICATION_ENABLED");
        // with the default class loader we get error:
        // javax.security.auth.login.LoginException: No LoginModule found
        // this is the fix (workaround) found in various places:
        // https://developer.jboss.org/thread/279980
        // https://github.com/ermadan/kafkalytic/commit/5db5214d252d4bdef4616075ffb3236f7f9f7023
        // https://stackoverflow.com/questions/57574901/kafka-java-client-classloader-doesnt-find-sasl-scram-login-class
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            if (kafkaAuthEnabled != null && kafkaAuthEnabled.equals("true")) {
                props.put("security.protocol", "SASL_PLAINTEXT");
                props.put("sasl.mechanism", "SCRAM-SHA-512");
                props.put("sasl.jaas.config", System.getenv("KAFKA_SASL_JAAS_CONFIG"));
            }
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Class.forName(StringSerializer.class.getName()));
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Class.forName(StringSerializer.class.getName()));
            stringStringKafkaProducer = new KafkaProducer<>(props);
        } catch (ClassNotFoundException e) {
            logError(e.getMessage(), e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
        return stringStringKafkaProducer;
    }

    private void produceEvent(String eventAsString, String topic) throws ExecutionException {
        if (producer == null) {
            IllegalStateException illegalStateException = new IllegalStateException("Kafka Producer was not setup correctly, please check logs.");
            logError("Kafka Producer was not setup correctly, please check logs.", illegalStateException);
            throw illegalStateException;
        }
        CustomLogger.print(Logger.Level.DEBUG, getXRequestId(), null, null, "Producing to topic: " + topic + " ...", null, null);
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, eventAsString);
        Future<RecordMetadata> metaData = producer.send(record);
        RecordMetadata recordMetadata = null;
        try {
            recordMetadata = metaData.get();
        } catch (InterruptedException e) {
            logError(e.getMessage(), e);
            // this fixes sonar issue java:S2142: "InterruptedException" should not be ignored
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
        if (recordMetadata != null) {
            CustomLogger.print(Logger.Level.DEBUG, getXRequestId(), null, null, "Produced to topic: " + recordMetadata.topic(), null, null);
        }
    }

    /**
     * For USER events only, replaces sensitive UserRepresentation properties and attributes with  {@link this#SENSITIVE_DATA_TEXT}.
     */
    private AdminEvent sanitizeUserEvent(AdminEvent event) {

        if (event.getResourceType() == ResourceType.USER) {
            UserRepresentation userRepresentation;
            if (event.getRepresentation() == null) {
                CustomLogger.print(Logger.Level.WARN, getXRequestId(), null, null, String.format("Representation is null for event: %s", event), null, null);
                return event;
            }
            try {
                userRepresentation = objectMapper.readValue(event.getRepresentation(), UserRepresentation.class);

                userRepresentation.setUsername(SENSITIVE_DATA_TEXT);
                userRepresentation.setFirstName(SENSITIVE_DATA_TEXT);
                userRepresentation.setLastName(SENSITIVE_DATA_TEXT);
                userRepresentation.setEmail(SENSITIVE_DATA_TEXT);
                userRepresentation.setCredentials(new ArrayList<>()); // remove plain-text password

                Map<String, List<String>> attributes = userRepresentation.getAttributes();
                SENSITIVE_USER_ATTRIBUTES.forEach(a -> attributes.replace(a, SENSITIVE_DATA_LIST));

                event.setRepresentation(objectMapper.writeValueAsString(userRepresentation));
            } catch (JsonProcessingException e) {
                CustomLogger.print(Logger.Level.WARN, getXRequestId(), null, null,
                        String.format("Error occurred during processing of User Representation for event: %s ", event), null, null);
                return event;
            }
        }
        return event;
    }
}
