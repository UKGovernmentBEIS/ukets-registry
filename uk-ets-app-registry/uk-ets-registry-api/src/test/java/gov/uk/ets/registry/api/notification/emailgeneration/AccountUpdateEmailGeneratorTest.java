package gov.uk.ets.registry.api.notification.emailgeneration;

import static gov.uk.ets.registry.api.notification.AccountManagementNotificationProperties.ACCOUNT_MANAGEMENT_REQUEST_TYPES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.AccountUpdateGroupNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.stream.Stream;

import gov.uk.ets.registry.usernotifications.MultipartEmailWithSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
@ContextConfiguration(classes = { MailConfiguration.class, FreeMarkerConfigurationFactoryBean.class,
    NotificationProperties.class })
@TestPropertySource(locations = "classpath:/mail.properties")
class AccountUpdateEmailGeneratorTest {

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    MailConfiguration mailConfiguration;

    String emailTemplatesPath = "/templates/email";

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testAccountUpdateTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/account-update.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/account-update.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String TEST_REQUEST_ID = "123";
    public static final String TEST_ACCOUNT_IDENTIFIER = "12345";
    public static final String TEST_ACCOUNT_FULL_IDENTIFIER = "UK-100-10000003-2-40";

    @Autowired
    NotificationProperties notificationProperties;

    @DisplayName("Test email template for Account Update task outcome")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0}")
    void shouldGenerateEmailForApproveTALUpdateRequest(RequestType requestType) {

        AccountUpdateEmailGenerator generator = new AccountUpdateEmailGenerator(notificationProperties,
            AccountUpdateGroupNotification.builder()
                .type(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
                .requestId(TEST_REQUEST_ID)
                .accountIdentifier(TEST_ACCOUNT_IDENTIFIER)
                .accountFullIdentifier(TEST_ACCOUNT_FULL_IDENTIFIER)
                .requestType(requestType)
                .taskOutcome(TaskOutcome.APPROVED)
                .build(), freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Account update request accepted - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>A request to update the " + ACCOUNT_MANAGEMENT_REQUEST_TYPES.get(requestType) +" for account UK-100-10XXXX03-2-40 has been approved.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The request ID is 123.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }

    @DisplayName("Test email template for Account Update request")
    @MethodSource("getArguments")
    @ParameterizedTest(name = "#{index} - {0}")
    void shouldGenerateEmailForTALUpdateRequest(RequestType requestType) {

        AccountUpdateEmailGenerator generator = new AccountUpdateEmailGenerator(notificationProperties,
            AccountUpdateGroupNotification.builder()
                .type(GroupNotificationType.ACCOUNT_UPDATE_PROPOSAL)
                .requestId(TEST_REQUEST_ID)
                .accountIdentifier(TEST_ACCOUNT_FULL_IDENTIFIER)
                .accountFullIdentifier(TEST_ACCOUNT_FULL_IDENTIFIER)
                .requestType(requestType)
                .build(), freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Account update request submitted - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>A request has been made to update the " + ACCOUNT_MANAGEMENT_REQUEST_TYPES.get(requestType) +" for account UK-100-10XXXX03-2-40.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The request ID is 123.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
            Arguments.of(RequestType.ADD_TRUSTED_ACCOUNT_REQUEST),
            Arguments.of(RequestType.DELETE_TRUSTED_ACCOUNT_REQUEST),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_RESTORE_REQUEST),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_UPDATE_ACCESS_RIGHTS_REQUEST),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_SUSPEND_REQUEST),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REMOVAL_REQUEST),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_ADDITION_REQUEST),
            Arguments.of(RequestType.AUTHORIZED_REPRESENTATIVE_REPLACEMENT_REQUEST),
            Arguments.of(RequestType.TRANSACTION_RULES_UPDATE_REQUEST),
            Arguments.of(RequestType.ACCOUNT_HOLDER_UPDATE_DETAILS),
            Arguments.of(RequestType.ACCOUNT_HOLDER_PRIMARY_CONTACT_DETAILS),
            Arguments.of(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_UPDATE),
            Arguments.of(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_DELETE),
            Arguments.of(RequestType.ACCOUNT_HOLDER_ALTERNATIVE_PRIMARY_CONTACT_DETAILS_ADD)
        );
    }
}
