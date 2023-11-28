package gov.uk.ets.registry.api.notification.emailgeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.UserDeactivationNotification;
import gov.uk.ets.registry.api.user.domain.User;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import java.util.Objects;

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
@ContextConfiguration(classes = {MailConfiguration.class, FreeMarkerConfigurationFactoryBean.class,
    NotificationProperties.class})
@TestPropertySource(locations = "classpath:/mail.properties")
class UserDeactivationEmailGeneratorTest {

    @Autowired
    NotificationProperties notificationProperties;

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    MailConfiguration mailConfiguration;

    String emailTemplatesPath = "/templates/email";

    User user;
    Long requestId;
    String recipient;
    UserDeactivationNotification notification;
    
    private static final String EOL = System.getProperty("line.separator");

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
        user = new User();
        user.setUrid("UK123456");
        requestId = 100001L;
        recipient = "test1@email.com";
        notification = UserDeactivationNotification.builder()
                                                   .emailAddress(recipient)
                                                   .requestId(Objects.toString(requestId, null))
                                                   .userId(user.getUrid())
                                                   .type(GroupNotificationType.USER_DEACTIVATION_REQUEST)
                                                   .build();
    }

    @Test
    void testTrustedAccountUpdateDescriptionTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/user-deactivation-completed.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/user-deactivation-completed.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGenerateUserDeactivationRequestEmail() {
        UserDeactivationRequestEmailGenerator requestEmailGenerator =
            new UserDeactivationRequestEmailGenerator(notification, notificationProperties,
                freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = requestEmailGenerator.generate();
        assertEquals("User deactivation request received - UK Emissions Trading Registry", email.subject());
        //convert eol chars according to current platform
        String emailWithConvertedEolChars = email.bodyHtml().replaceAll("\\n|\\r\\n", EOL);
        assertEquals(emailWithConvertedEolChars,
                     "<html>" + EOL +
                     "<head></head>" + EOL +
                     "<body>" + EOL +
                     "<p>A request has been received to deactivate user ID UK123456.</p>" + EOL +
                     "<p>The request ID is 100001.</p>" + EOL +
                     "<p>If you did not request this change, you must " +
                     "<a href=\"mailto: ETRegistryHELP@environment-agency.gov.uk\">" +
                     "contact the Emissions Trading Registry Helpdesk</a>.</p>" + EOL +
                     "<p>This is an automatic email - please do not reply to this address.</p>" + EOL +
                     "</body>" + EOL +
                     "</html>");
    }

    @Test
    void shouldGenerateUserDeactivationCompletedEmail() {
        UserDeactivationCompletedEmailGenerator completedEmailGenerator =
            new UserDeactivationCompletedEmailGenerator(notification, notificationProperties,
                                                        freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = completedEmailGenerator.generate();
        assertEquals("User deactivated - UK Emissions Trading Registry", email.subject());
        //convert eol chars according to current platform
        String emailWithConvertedEolChars = email.bodyHtml().replaceAll("\\n|\\r\\n", EOL);
        assertEquals(emailWithConvertedEolChars,
                     "<html>" + EOL +
                     "<head></head>" + EOL +
                     "<body>" + EOL +
                     "<p>User ID UK123456 has been successfully deactivated.</p>" + EOL +
                     "<p>The request ID is 100001.</p>" + EOL +
                     "<p>If you did not request this change you must " +
                     "<a href=\"mailto: ETRegistryHELP@environment-agency.gov.uk\">" +
                     "contact the Emissions Trading Registry Helpdesk</a>.</p>" + EOL +
                     "<p>This is an automatic email - please do not reply to this address.</p>" + EOL +
                     "</body>" + EOL +
                     "</html>");
    }
}
