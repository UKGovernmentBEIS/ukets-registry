package uk.gov.ets.password.validator.api.config;

import javax.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix="password.policy.props")
public class PasswordValidatorProperties {

	@NotEmpty
	private boolean flagEnabled;

	@NotEmpty
	private Integer minimumChars;

	@NotEmpty
	private Integer maximumChars;
}
