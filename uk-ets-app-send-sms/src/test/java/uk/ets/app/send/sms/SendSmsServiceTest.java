package uk.ets.app.send.sms;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import uk.ets.app.send.sms.domain.SmsNotification;
import uk.ets.app.send.sms.service.SmsService;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
	    "notification.service.api.key=RANDOM_API_KEY"
	})
class SendSmsServiceTest {

    @Mock
    private NotificationClient notificationClient;

    @InjectMocks
    private SmsService service;

    @Test
    void shouldSendSms() {
        // given
        SmsNotification smsNotification = new SmsNotification();
        smsNotification.setPhoneNumber("+306987456321");
        smsNotification.setSmsTemplate("templateId");

        // when
        try {
			service.sendSms(smsNotification);
			
	        // then
	        verify(notificationClient).sendSms(
	            "templateId",
	            "+306987456321",
	            null,
	            null
	        );
		} catch (NotificationClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

