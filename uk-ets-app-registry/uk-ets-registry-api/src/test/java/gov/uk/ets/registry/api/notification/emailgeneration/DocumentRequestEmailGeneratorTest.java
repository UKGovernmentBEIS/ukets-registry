package gov.uk.ets.registry.api.notification.emailgeneration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
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

import freemarker.template.Configuration;
import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.mail.MailConfiguration;
import gov.uk.ets.registry.api.file.upload.requesteddocs.model.DocumentsRequestType;
import gov.uk.ets.registry.api.notification.DocumentRequestGroupNotification;
import gov.uk.ets.registry.api.notification.NotificationProperties;
import gov.uk.ets.registry.api.task.domain.types.RequestType;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import gov.uk.ets.registry.usernotifications.MultipartEmailWithSubject;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = NotificationProperties.class)
@ContextConfiguration(classes = { MailConfiguration.class, FreeMarkerConfigurationFactoryBean.class,
    NotificationProperties.class })
@TestPropertySource(locations = "classpath:/mail.properties")
class DocumentRequestEmailGeneratorTest {

    private static final String TEST_REQUEST_ID = "012345";
    private static final String ACCOUNT_NAME = "Feel good inc";
    private static final String ACCOUNT_HOLDER_NAME = "Gorillaz";
    private static final String USER_ID = "UK40282084687";
    private static final String USER_FULL_NAME = "John Doe";

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
    void testDocumentRequestTemplatesNotNull() {
        Assertions.assertNotNull(freemarkerConfiguration);
        try {
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/html/document-request.ftl"));
            Assertions.assertNotNull(freemarkerConfiguration.getTemplate("/text/document-request.ftl"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGenerateDocumentRequestEmailForUser() {

        DocumentRequestEmailGenerator documentRequestEmailGenerator =
            new DocumentRequestEmailGenerator(notificationProperties,
                DocumentRequestGroupNotification.builder()
                    .type(GroupNotificationType.DOCUMENT_REQUEST)
                    .requestId(TEST_REQUEST_ID)
                    .documentsRequestType(DocumentsRequestType.USER)
                    .userId(Utils.maskUserId(USER_ID))
                    .userFullName(USER_FULL_NAME)
                    .deadline(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                    .build(), freemarkerConfiguration, mailConfiguration);
        MultipartEmailWithSubject email = documentRequestEmailGenerator.generate();

        assertEquals("Notification of documentation request - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>A Registry Administrator has requested documents to be uploaded for the user with ID ending in 87.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The task ID is 012345.</p>"));
    }

    @Test
    void testGenerateDocumentRequestEmailForAccountHolder() {

        DocumentRequestEmailGenerator documentRequestEmailGenerator =
            new DocumentRequestEmailGenerator(notificationProperties,
                DocumentRequestGroupNotification.builder()
                    .type(GroupNotificationType.DOCUMENT_REQUEST)
                    .requestId(TEST_REQUEST_ID)
                    .documentsRequestType(DocumentsRequestType.fromTaskRequestType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD))
                    .accountName(ACCOUNT_NAME)
                    .accountHolderName(ACCOUNT_HOLDER_NAME)
                    .deadline(Date.from(Instant.now().plus(1, ChronoUnit.DAYS)))
                    .build(), freemarkerConfiguration, mailConfiguration);
        MultipartEmailWithSubject email = documentRequestEmailGenerator.generate();

        assertEquals("Notification of documentation request - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>A Registry Administrator has requested documents to be uploaded for Gorillaz of the Feel good inc Account.</p>"));
        assertTrue(email.bodyHtml().contains("<p>The task ID is 012345.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }

    @Test
    void testGenerateDocumentRequestFinalisationEmailForUser() {

        DocumentRequestEmailGenerator documentRequestEmailGenerator =
            new DocumentRequestEmailGenerator(notificationProperties,
                DocumentRequestGroupNotification.builder()
                    .type(GroupNotificationType.DOCUMENT_REQUEST_FINALISATION)
                    .requestId(TEST_REQUEST_ID)
                    .documentsRequestType(DocumentsRequestType.USER)
                    .userId(Utils.maskUserId(USER_ID))
                    .userFullName(USER_FULL_NAME)
                    .build(), freemarkerConfiguration, mailConfiguration);
        MultipartEmailWithSubject email = documentRequestEmailGenerator.generate();

        assertEquals("Documentation request completed - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>The request with ID 012345 to submit documentation for the user with ID ending in 87 has been completed.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }

    @Test
    void testGenerateDocumentRequestFinalisationEmailForAccountHolder() {
    	
        DocumentRequestEmailGenerator documentRequestEmailGenerator =
            new DocumentRequestEmailGenerator(notificationProperties,
                DocumentRequestGroupNotification.builder()
                    .type(GroupNotificationType.DOCUMENT_REQUEST_FINALISATION)
                    .requestId(TEST_REQUEST_ID)
                    .documentsRequestType(DocumentsRequestType.fromTaskRequestType(RequestType.AH_REQUESTED_DOCUMENT_UPLOAD))
                    .accountName(ACCOUNT_NAME)
                    .accountHolderName(ACCOUNT_HOLDER_NAME)
                    .build(), freemarkerConfiguration, mailConfiguration);
        MultipartEmailWithSubject email = documentRequestEmailGenerator.generate();

        assertEquals("Documentation request completed - UK Emissions Trading Registry", email.subject());
        assertTrue(email.bodyHtml().contains("<p>The request with ID 012345 to submit documentation for Gorillaz of the Feel good inc Account has been completed.</p>"));
        assertTrue(email.bodyHtml().contains("<p>This is an automatic email - please do not reply to this address.</p>"));
    }
}
