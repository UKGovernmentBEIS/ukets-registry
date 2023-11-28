package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.EmailChangeUserStatusNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
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
    NotificationProperties.class, EmailChangeUserStatusNotification.class })
@TestPropertySource(locations = {"classpath:/mail.properties"})
class ARAccountOpeningEmailGeneratorTest {

    @Autowired
    NotificationProperties notificationProperties;

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    MailConfiguration mailConfiguration;

    @Autowired
    EmailChangeUserStatusNotification notification;

    String emailTemplatesPath = "/templates/email";

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testArAccountOpeningTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/ar-account-opening.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/ar-account-opening.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGenerateAccountOpeningArValidatedEmail() {
        ARAccountOpeningEmailGenerator generator =
            new ARAccountOpeningEmailGenerator(notificationProperties, notification, freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("User validated - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains(
            "<p>Your request to create a sign in for the UK Registry has been validated by the " +
                "Registry Administrator or National Administrator.</p>"));
        assertTrue(email.bodyHtml().contains(
            "<p>You will receive a Registry Activation Code (RAC) by email within 10 days." +
                " When you sign in to the UK Registry, you must use the RAC to activate " +
                "your sign in and become enrolled. You should do this as soon as possible " +
                "to access the full functionality of your UK Registry account.</p>"));
        assertTrue(
            email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));

    }
}
