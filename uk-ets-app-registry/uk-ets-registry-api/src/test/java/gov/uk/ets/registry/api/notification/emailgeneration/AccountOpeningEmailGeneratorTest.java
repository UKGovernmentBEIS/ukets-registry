package gov.uk.ets.registry.api.notification.emailgeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.AccountOpeningGroupNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.transaction.domain.type.TaskOutcome;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import gov.uk.ets.registry.usernotifications.MultipartEmailWithSubject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class AccountOpeningEmailGeneratorTest {

    private static final String TEST_REQUEST_ID = "123";
    private static final String TEST_ACCOUN_FULL_IDENTIFIER = "UK-100-10000001-2-50";
    private static final String TEST_REJECTION_COMMENT = "rejection comment";

    @Autowired
    NotificationProperties notificationProperties;

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
    void testAccountOpeningTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/account-opening.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/account-opening.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGenerateAccountOpeningAcceptedEmail() {
        AccountOpeningEmailGenerator generator =
            new AccountOpeningEmailGenerator(notificationProperties, AccountOpeningGroupNotification.builder()
                .requestId(TEST_REQUEST_ID)
                .type(GroupNotificationType.ACCOUNT_OPENING_FINALISATION)
                .taskOutcome(TaskOutcome.APPROVED)
                .accountFullIdentifier(TEST_ACCOUN_FULL_IDENTIFIER)
                .build(), freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Account opening request approved - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>Your request to open a new UK Registry account has been approved.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The new account ID is UK-100-10000001-2-50.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }

    @Test
    void shouldGenerateAccountOpeningRejectedEmail() {
        AccountOpeningEmailGenerator generator =
            new AccountOpeningEmailGenerator(notificationProperties, AccountOpeningGroupNotification.builder()
                .requestId(TEST_REQUEST_ID)
                .type(GroupNotificationType.ACCOUNT_OPENING_FINALISATION)
                .taskOutcome(TaskOutcome.REJECTED)
                .accountFullIdentifier(TEST_ACCOUN_FULL_IDENTIFIER)
                .rejectionComment(TEST_REJECTION_COMMENT)
                .build(), freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Account opening request rejected - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml()
            .contains("<p>Your request to open a new UK Registry account has been rejected for the following reason:</p>"));
        assertTrue(email.bodyHtml().contains("<p>If you did not make this request you must <a href=\"mailto: ETRegistryHELP@environment-agency.gov.uk\">contact the UK Registry Helpdesk</a></p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
