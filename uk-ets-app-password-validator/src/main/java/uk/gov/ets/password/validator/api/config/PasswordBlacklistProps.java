package uk.gov.ets.password.validator.api.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "password.blacklist")
public class PasswordBlacklistProps {

    @NotEmpty
    private String apiServiceUrl;
}
