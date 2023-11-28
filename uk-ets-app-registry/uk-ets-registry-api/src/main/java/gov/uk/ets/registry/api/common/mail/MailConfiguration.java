package gov.uk.ets.registry.api.common.mail;

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
    @Value("${mail.etrAddress}")
    private String etrAddress;
    @Value("${mail.html.templates.folder}")
    private String htmlTemplatesFolder;
    @Value("${mail.text.templates.folder}")
    private String textTemplatesFolder;
}
