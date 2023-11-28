package gov.uk.ets.registry.api.notification.emailgeneration;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
@ContextConfiguration(classes = { MailConfiguration.class, FreeMarkerConfigurationFactoryBean.class,
    NotificationProperties.class })
@TestPropertySource(locations = "classpath:/mail.properties")
class TransactionProposalEmailGeneratorTest {

    @Autowired
    Configuration freemarkerConfiguration;

    @Autowired
    NotificationProperties notificationProperties;

    @Autowired
    MailConfiguration mailConfiguration;

    String emailTemplatesPath = "/templates/email";

    String testTransactionId = "GB12345";
    String testAccountFullIdentifier = "UK-100-10000001-2-50";
    String requestId = "100005";
    Long amount = 50L;

    @BeforeEach
    public void setup() {
        freemarkerConfiguration.setClassLoaderForTemplateLoading(getClass().getClassLoader(), emailTemplatesPath);
    }

    @Test
    void testTransactionProposalTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/transaction-proposal.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/transaction-proposal.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test()
    void shouldGenerateTransactionProposalEmail() {
        TransactionRelatedGroupNotification
            notification = TransactionRelatedGroupNotification.builder().transactionId(testTransactionId)
            .accountFullId(testAccountFullIdentifier).amount(amount).requestId(requestId).approvalRequired(true).build();

        TransactionProposalEmailGenerator generator =
            new TransactionProposalEmailGenerator(this.notificationProperties, notification, freemarkerConfiguration, mailConfiguration);
        MultipartEmailWithSubject email = generator.generate();
        assertEquals("Notification of UK Registry transaction proposal - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>A request has been made to transfer 50 units from account UK-100-10XXXX01-2-50.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The request ID is 100005.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The transaction ID is GB12345.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
