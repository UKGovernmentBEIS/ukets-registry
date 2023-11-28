package gov.uk.ets.registry.api.notification.emailgeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.notification.TransactionRelatedGroupNotification;
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
class TransactionManuallyCancelledEmailGeneratorTest {

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
    void testTransactionManuallyCancelledTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/transaction-manually-cancelled.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/transaction-manually-cancelled.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void shouldGenerateTransactionManuallyCancelledEmail() {
        String testTransactionId = "GB12345";
        String testAccountFullIdentifier = "UK-100-10000001-2-50";
        String requestId = "100005";
        Long amount = 50L;

        TransactionRelatedGroupNotification
            notification = TransactionRelatedGroupNotification.builder().transactionId(testTransactionId)
            .accountFullId(testAccountFullIdentifier).amount(amount).requestId(requestId).build();

        TransactionManuallyCancelledEmailGenerator generator =
            new TransactionManuallyCancelledEmailGenerator(notificationProperties, notification, freemarkerConfiguration, mailConfiguration);

        MultipartEmailWithSubject email = generator.generate();
        assertEquals("Notification of UK Registry transaction cancellation - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>A request to transfer 50 units from account UK-100-10XXXX01-2-50 has been manually cancelled before completion.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The request ID is 100005.</p>"));
        assertTrue(email.bodyHtml().contains("<p>If you think this request should not have been made, you must <a href=\"mailto: ETRegistryHELP@environment-agency.gov.uk\">contact the UK Registry Helpdesk</a></p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
