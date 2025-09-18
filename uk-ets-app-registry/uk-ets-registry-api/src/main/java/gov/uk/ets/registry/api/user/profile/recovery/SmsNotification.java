package gov.uk.ets.registry.api.user.profile.recovery;

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
