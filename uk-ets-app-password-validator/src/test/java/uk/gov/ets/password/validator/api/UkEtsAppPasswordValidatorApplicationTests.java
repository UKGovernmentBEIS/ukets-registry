package uk.gov.ets.password.validator.api;

import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
	"PASS_VALIDATOR_APPLICATION_URL=http://localhost:4200",
	"PASS_VALIDATOR_APPLICATION_PORT=1234"
})
@SpringBootTest
class UkEtsAppPasswordValidatorApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
