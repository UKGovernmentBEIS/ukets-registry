package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.user.profile.domain.PasswordChangeSuccessNotification;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
@ContextConfiguration(classes = { MailConfiguration.class, FreeMarkerConfigurationFactoryBean.class,
    NotificationProperties.class, PasswordChangeSuccessNotification.class })
@TestPropertySource(locations = "classpath:/mail.properties")
class PasswordChangeSuccessEmailGeneratorTest {

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    NotificationProperties notificationProperties;

    @Autowired
    PasswordChangeSuccessNotification notification;

    @Autowired
    MailConfiguration mailConfiguration;

    String emailTemplatesPath = "/templates/email";

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testPasswordChangedTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/password-changed.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/password-changed.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test()
    void shouldGeneratePasswordChangeEmail() {
        PasswordChangeSuccessEmailGenerator generator =
            new PasswordChangeSuccessEmailGenerator(this.notificationProperties, notification, freemarkerConfiguration, mailConfiguration);
        MultipartEmailWithSubject email = generator.generate();
        assertEquals("Password changed - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>Your UK Registry password has been changed.</p>"));
        assertTrue(email.bodyHtml().contains("<p>Remember to use your new password the next time you want to sign in to the UK Registry.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
