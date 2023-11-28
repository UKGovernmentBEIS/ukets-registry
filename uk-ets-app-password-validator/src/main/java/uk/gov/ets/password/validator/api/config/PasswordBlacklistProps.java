package uk.gov.ets.password.validator.api.config;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix="password.blacklist")
public class PasswordBlacklistProps {

	@NotEmpty
	private String apiServiceUrl;
}
