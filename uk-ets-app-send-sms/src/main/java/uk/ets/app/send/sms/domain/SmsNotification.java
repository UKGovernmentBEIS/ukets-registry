package uk.ets.app.send.sms.domain;

import java.io.Serializable;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsNotification implements Serializable {

    private String phoneNumber;
    private String smsTemplate;
    private Map<String, String> metadata;
}
