package uk.gov.ets.password.validator.api.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.ets.password.validator.api.service.PasswordValidatorService;
import uk.gov.ets.password.validator.api.web.model.PasswordStrengthRequest;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:password-blacklist-test.properties")
public class PasswordValidatorServiceControllerTest {

    @Autowired
	private MockMvc mockMvc;

	@Mock
	private PasswordValidatorService passwordValidatorService;

	@Test
	@Disabled("Until we find a password that is string but pwned.")
	void testIsPwnedPasswordReponse() throws Exception {
        Mockito.when(passwordValidatorService.isPwned("azerty98765")).thenReturn(true);

		mockMvc.perform(post("/api-password-validate")
				.content( "azerty98765")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.valid", is(false)))
				.andExpect(jsonPath("$.errorCode", is("pwned")))
				.andExpect(jsonPath("$.message", is("Password has been pwned.")));
	}
	
	@Test
	void testIsNotPwnedPasswordReponse() throws Exception {
        Mockito.when(passwordValidatorService.isPwned("03d73UpfR76eNjJwb0rk")).thenReturn(false);

		mockMvc.perform(post("/api-password-validate")
				.content("03d73UpfR76eNjJwb0rk")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.valid", is(true)))
				.andExpect(jsonPath("$.errorCode", is(nullValue())))
				.andExpect(jsonPath("$.message", is(nullValue())));
	}
	
	@Test
	void testPasswordStrengthResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        PasswordStrengthRequest request = new PasswordStrengthRequest();
        String passwd = "glqwerty12345";
        request.setPassword(passwd);
        Mockito.when(passwordValidatorService.passwordStrength(passwd)).thenReturn(1);
        
		mockMvc.perform(post("/api-password-validate/strength.calculate")
				.content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.score", is(1)));
	}
	
	@Test
	void testNullPasswordStrengthResponse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        PasswordStrengthRequest request = new PasswordStrengthRequest();
        request.setPassword(null);
        
		mockMvc.perform(post("/api-password-validate/strength.calculate")
				.content(mapper.writeValueAsString(request))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}	
}
