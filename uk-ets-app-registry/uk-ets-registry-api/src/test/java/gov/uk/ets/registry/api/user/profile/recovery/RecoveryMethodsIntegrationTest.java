package gov.uk.ets.registry.api.user.profile.recovery;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import freemarker.template.Configuration;
import gov.uk.ets.lib.commons.security.oauth2.token.OAuth2ClaimNames;
import gov.uk.ets.registry.api.authz.DisabledKeycloakAuthorizationService;
import gov.uk.ets.registry.api.authz.ServiceAccountAuthorizationService;
import gov.uk.ets.registry.api.common.test.BaseIntegrationTest;
import gov.uk.ets.registry.api.notification.RecoveryEmailChangeNotification;
import gov.uk.ets.registry.api.user.admin.service.DisabledKeycloakUserAdministrationService;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.api.user.domain.UserStatus;
import gov.uk.ets.registry.api.user.profile.recovery.domain.SecurityCode;
import gov.uk.ets.registry.api.user.profile.recovery.repository.SecurityCodeRepository;
import gov.uk.ets.registry.api.user.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

@EmbeddedKafka(topics = {"sms.notification.topic", "group.notification.topic"},
    brokerPropertiesLocation = "classpath:integration-test-application.properties",
    brokerProperties = "auto.create.topics.enable=false",
    count = 3,
    ports = {0, 0, 0}
)
@TestPropertySource(locations = "/integration-test-application.properties", properties = {
    "keycloak.enabled=false"
})
@AutoConfigureMockMvc(addFilters = false)
class RecoveryMethodsIntegrationTest extends BaseIntegrationTest {

    private static final String currentUserKeycloakId = UUID.randomUUID().toString();

    private User currentUser;

    @SpyBean
    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityCodeRepository securityCodeRepository;
    @Autowired
    private Configuration freemarkerConfiguration;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceAccountAuthorizationService serviceAccountAuthorizationService;

    @SpyBean
    private DisabledKeycloakAuthorizationService disabledKeycloakAuthorizationService;

    @SpyBean
    private DisabledKeycloakUserAdministrationService disabledKeycloakUserAdministrationService;

    private Consumer<String, SmsNotification> smsConsumer;
    private Consumer<String, RecoveryEmailChangeNotification> emailConsumer;
    private String emailTemplatesPath = "/templates/email";

    @BeforeAll
    public void setup() {
        currentUser = new User();
        currentUser.setUrid("UK123456789");
        currentUser.setState(UserStatus.ENROLLED);
        currentUser.setIamIdentifier(currentUserKeycloakId);
        currentUser.setFirstName("Tony");
        currentUser.setLastName("Montana");
        userRepository.save(currentUser);

        Map<String, Object> smsConfig = new HashMap<>(KafkaTestUtils
                .consumerProps("sms_group", "false", embeddedKafkaBroker));
        smsConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        smsConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        smsConsumer = new DefaultKafkaConsumerFactory<>(
            smsConfig, new StringDeserializer(),
            new JsonDeserializer<>(SmsNotification.class, false)).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(smsConsumer, "sms.notification.topic");

        Map<String, Object> emailConfig = new HashMap<>(KafkaTestUtils
                .consumerProps("email_group", "false", embeddedKafkaBroker));
        emailConfig.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        emailConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        emailConsumer = new DefaultKafkaConsumerFactory<>(
            emailConfig, new StringDeserializer(),
            new JsonDeserializer<>(RecoveryEmailChangeNotification.class, false)).createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(emailConsumer, "group.notification.topic");
    }

    @BeforeEach
    void init() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);

        doReturn(ResponseEntity.ok(Boolean.TRUE)).when(restTemplate).exchange(anyString(), eq(HttpMethod.POST), any(), eq(Boolean.class));

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setAttributes(new HashMap<>());
        when(disabledKeycloakUserAdministrationService.findByIamId(currentUserKeycloakId)).thenReturn(userRepresentation);

        // the following code part sets a keycloak id for the current user
        when(disabledKeycloakAuthorizationService.getClaim(OAuth2ClaimNames.SUBJECT)).thenReturn(currentUserKeycloakId);

    }

    @AfterEach
    protected void cleanUp() {
        smsConsumer.commitSync();
        securityCodeRepository.deleteAll();
    }

    @AfterAll
    protected void tearDown() {
        smsConsumer.close();
        emailConsumer.close();
        super.tearDown();
    }

    @Test
    void testGenerateSecurityCode_Email() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/user-profile.request.security-code")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"test@emai.com\",\"otpCode\":\"123456\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();

        String expectedCode = securityCodeRepository.findByUserAndValidIsTrue(currentUser).stream().findFirst().map(SecurityCode::getCode).get();
        ConsumerRecords<String, RecoveryEmailChangeNotification> records = KafkaTestUtils.getRecords(emailConsumer, Duration.ofMillis(10000), 1);
        Assertions.assertTrue(records.iterator().next().value().bodyPlain().contains(expectedCode));
    }

    @Test
    void testGenerateSecurityCode_PhoneNumber() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/user-profile.request.security-code")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"countryCode\":\"GR (30)\",\"phoneNumber\":\"6987456321\",\"otpCode\":\"123456\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();

        String expectedCode = securityCodeRepository.findByUserAndValidIsTrue(currentUser).stream().findFirst().map(SecurityCode::getCode).get();
        ConsumerRecords<String, SmsNotification> records = KafkaTestUtils.getRecords(smsConsumer, Duration.ofMillis(10000), 1);
        SmsNotification smsNotification = records.iterator().next().value();
        String securityCode = smsNotification.getMetadata().values().stream().findFirst().get();
        String phoneNumber = smsNotification.getPhoneNumber();
        Assertions.assertEquals(expectedCode, securityCode);
        Assertions.assertEquals("+306987456321", phoneNumber);
    }

    @Test
    void testResendSecurityCode_PhoneNumber() throws Exception {
        // given
        SecurityCode existingSecurityCode = new SecurityCode();
        existingSecurityCode.setCode("123456");
        existingSecurityCode.setCountryCode("GR (30)");
        existingSecurityCode.setPhoneNumber("6987456321");
        existingSecurityCode.setValid(true);
        existingSecurityCode.setCreatedAt(new Date());
        existingSecurityCode.setExpiredAt(Date.from(Instant.now().minusSeconds(1000)));
        existingSecurityCode.setUser(currentUser);

        securityCodeRepository.save(existingSecurityCode);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/user-profile.resend.security-code")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"countryCode\":\"GR (30)\",\"phoneNumber\":\"6987456321\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();

        String expectedCode = securityCodeRepository.findByUserAndValidIsTrue(currentUser).stream().findFirst().map(SecurityCode::getCode).get();
        ConsumerRecords<String, SmsNotification> records = KafkaTestUtils.getRecords(smsConsumer, Duration.ofMillis(10000), 1);
        String securityCode = records.iterator().next().value().getMetadata().values().stream().findFirst().get();
        Assertions.assertEquals(expectedCode, securityCode);
    }

    @Test
    void testUpdateSecurityCode_PhoneNumber() throws Exception {
        // given
        SecurityCode existingSecurityCode = new SecurityCode();
        existingSecurityCode.setCode("123456");
        existingSecurityCode.setCountryCode("GR (30)");
        existingSecurityCode.setPhoneNumber("6987456321");
        existingSecurityCode.setValid(true);
        existingSecurityCode.setCreatedAt(new Date());
        existingSecurityCode.setExpiredAt(Date.from(Instant.now().plusSeconds(100)));
        existingSecurityCode.setUser(currentUser);

        securityCodeRepository.save(existingSecurityCode);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/user-profile.update.recovery")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"countryCode\":\"GR (30)\",\"phoneNumber\":\"6987456321\",\"securityCode\":\"123456\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();
        verify(disabledKeycloakUserAdministrationService, times(1)).updateUserDetails(any());
        Assertions.assertEquals(Collections.emptyList(), securityCodeRepository.findAll());
    }

    @Test
    void testRemoveSecurityCode_PhoneNumber() throws Exception {
        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/user-profile.remove.recovery")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"phoneNumber\":true,\"otpCode\":\"123456\"}")
            .accept(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk()).andReturn();
        verify(disabledKeycloakUserAdministrationService, times(1)).updateUserDetails(any());
    }

    @Test
    void testHideRecoveryMethodsNotification() throws Exception {
        // given
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setAttributes(new HashMap<>());
        when(disabledKeycloakUserAdministrationService.findByIamId(currentUserKeycloakId)).thenReturn(userRepresentation);

        // when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders
            .post("/api-registry/user-profile.hide.recovery-methods"));

        // then
        result.andExpect(status().isOk()).andReturn();
        verify(disabledKeycloakUserAdministrationService, times(1)).updateUserDetails(userRepresentation);
    }
}
