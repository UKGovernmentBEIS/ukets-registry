package gov.uk.ets.registry.api.notification.emailgeneration;

import static org.junit.jupiter.api.Assertions.*;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.TokenChangeNotification;
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
class TokenChangeEmailGeneratorTest {

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
    void testTokenChangeTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/token-change.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/token-change.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGenerateTokenChangeEmail() {
        TokenChangeNotification notification =
            new TokenChangeNotification("to@email.com", "https://ukets.registry/change-2fa", 60L);

        TokenChangeEmailGenerator generator =
            new TokenChangeEmailGenerator(notification, notificationProperties.getTokenChange(),
                freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();

        assertEquals("Two factor authentication device update request - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>Your two factor authentication update request has been approved.</p>"));
        assertTrue(email.bodyHtml().contains("<p>Use this link to sign in to the registry and update your authenticator app:</p>"));
        assertTrue(email.bodyHtml().contains("<p>This link will expire in 60 minutes.</p>"));
    }
}
