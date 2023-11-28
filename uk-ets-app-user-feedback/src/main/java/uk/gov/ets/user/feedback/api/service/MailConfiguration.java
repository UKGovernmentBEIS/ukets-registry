package uk.gov.ets.user.feedback.api.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:mail.properties"})
@Getter
@Setter
public class MailConfiguration {
    @Value("${mail.fromAddress}")
    private String fromAddress;
    @Value("${mail.toAddress}")
    private String toAddress;
    @Value("${mail.subject}")
    private String subject;

}

