package gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhocemail;

import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.IdentifiableEmailNotification;
import gov.uk.ets.registry.api.notification.userinitiated.messaging.model.NotificationParameterHolder;
import gov.uk.ets.registry.api.notification.userinitiated.services.StringEmailTemplateProcessor;
import gov.uk.ets.registry.api.notification.userinitiated.services.scheduling.adhoc.email.AdHocEmailNotificationGenerator;
import gov.uk.ets.registry.api.notification.userinitiated.util.HtmlToPlainTextConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdHocEmailNotificationGeneratorTest {

    @Mock
    private StringEmailTemplateProcessor templateProcessor;

    @InjectMocks
    private AdHocEmailNotificationGenerator generator;

    private NotificationParameterHolder recipient;

    @BeforeEach
    void setUp() {
        recipient = mock(NotificationParameterHolder.class);
        lenient().when(recipient.getNotificationId()).thenReturn(123L);
        lenient().when(recipient.getNotificationInstanceId()).thenReturn("instance-456");
        lenient().when(recipient.getCsvRowData()).thenReturn(Map.of("email", "test@example.com"));
    }

    @Test
    void testGenerate() {
        String emailSubject = "Test Subject";
        String emailBody = "<p>Hello, this is a test.</p>";
        String processedBody = "Hello, this is a test.";
        when(templateProcessor.processTemplate(emailBody, recipient.getCsvRowData())).thenReturn(processedBody);

        IdentifiableEmailNotification notification = generator.generate(recipient, emailSubject, emailBody);

        assertNotNull(notification);
        assertEquals(123L, notification.getNotificationId());
        assertEquals("instance-456", notification.getNotificationInstanceId());
        assertEquals(Set.of("test@example.com"), notification.getRecipients());
        assertEquals("(instance-456) Test Subject", notification.getSubject());
        assertEquals(processedBody, notification.getBodyHtml());
        assertEquals(HtmlToPlainTextConverter.convertHtmlToPlainText(processedBody), notification.getBodyPlain());
        assertTrue(notification.isIncludeBcc());
    }
}