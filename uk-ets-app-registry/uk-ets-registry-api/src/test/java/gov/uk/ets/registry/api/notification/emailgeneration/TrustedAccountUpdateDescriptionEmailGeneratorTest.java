package gov.uk.ets.registry.api.notification.emailgeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.TrustedAccountUpdateDescriptionGroupNotification;
import java.util.Set;

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
class TrustedAccountUpdateDescriptionEmailGeneratorTest {

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
    void testTrustedAccountUpdateDescriptionTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/trusted-account-update.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/trusted-account-update.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGenerateTrustedAccountUpdateDescriptionEmail() {
        String testAccountIdentifier = "1000078";
        String testAccountFullIdentifier = "UK-100-10000001-2-50";
        String testDescription = "test description";
        Set<String> setRecipients = Set.of("test3@email.com", "test4@email.com");
        Long amount = 50L;

        TrustedAccountUpdateDescriptionGroupNotification notification =
            TrustedAccountUpdateDescriptionGroupNotification.builder()
                .accountFullIdentifier(testAccountFullIdentifier)
                .accountIdentifier(testAccountIdentifier)
                .description(testDescription)
                .recipients(setRecipients)
                .build();

        TrustedAccountUpdateDescriptionEmailGenerator generator =
            new TrustedAccountUpdateDescriptionEmailGenerator(notificationProperties, notification,
                freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();
        assertEquals("Update Trusted accounts description - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains(
            "<p>The description of the trusted account with id UK-100-10000001-2-50 of the account with id 1000078"));
        assertTrue(email.bodyHtml().contains("If you think this request should not have been made, you must"));
        assertTrue(email.bodyHtml()
            .contains("<a href=\"mailto: ETRegistryHELP@environment-agency.gov.uk\">contact the UK"));
        assertTrue(email.bodyHtml().contains("has been updated to test description."));
        assertTrue(
            email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
