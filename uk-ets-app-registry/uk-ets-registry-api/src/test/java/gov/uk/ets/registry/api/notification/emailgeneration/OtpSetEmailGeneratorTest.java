package gov.uk.ets.registry.api.notification.emailgeneration;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.OtpSetNotification;
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
    NotificationProperties.class, OtpSetNotification.class })
@TestPropertySource(locations = "classpath:/mail.properties")
class OtpSetEmailGeneratorTest {

    @Autowired
    NotificationProperties notificationProperties;

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    MailConfiguration mailConfiguration;

    @Autowired
    OtpSetNotification otpSetNotification;

    String emailTemplatesPath = "/templates/email";

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testOtpSetTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/login-error.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/login-error.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test()
    void shouldGenerateSetOTPEmail() {
        OtpSetEmailGenerator generator =
            new OtpSetEmailGenerator(notificationProperties.getOtpSet(), otpSetNotification, freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Two factor authentication device activated - UK Emissions Trading Registry", email.subject());
        assertTrue(
            email.bodyHtml().contains("<p>Your two factor authentication update request has been activated.</p>"));
        assertTrue(email.bodyHtml().contains(
            "<p>When you sign in to the UK Emissions Trade Registry you will be asked to enter a verification code from an authenticator app.</p>"));
        assertTrue(email.bodyHtml().contains(
            "<p>If you did not request this change you must <a href=\"mailto: ETRegistryHELP@environment-agency.gov.uk\">contact the UK Registry Helpdesk</a></p>"));
        assertTrue(
            email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
