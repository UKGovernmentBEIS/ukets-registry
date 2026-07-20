package uk.ets.app.send.sms.service;

import org.springframework.stereotype.Service;

import uk.ets.app.send.sms.domain.SmsNotification;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;

@Service
public class SmsService {


    private final NotificationClient client;

    
    public SmsService(NotificationClient client) {
        this.client = client;
    }

    
    public void sendSms(SmsNotification smsNotification) throws NotificationClientException {
        client.sendSms(smsNotification.getSmsTemplate(), smsNotification.getPhoneNumber(), smsNotification.getMetadata(), null);
    }

}
