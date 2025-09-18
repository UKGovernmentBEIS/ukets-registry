package uk.ets.app.send.sms.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ets.app.send.sms.domain.SmsNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@Service
public class SmsService {

    public SmsService(@Value("${notification.service.api.key}") String apiKey) {
        this.client = new NotificationClient(apiKey);
    }

    private final NotificationClient client;

    public void sendSms(SmsNotification smsNotification) throws NotificationClientException {
        client.sendSms(smsNotification.getSmsTemplate(), smsNotification.getPhoneNumber(), smsNotification.getMetadata(), null);
    }

}
