package uk.gov.ets.password.validator.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.ets.password.validator.api.config.PasswordBlacklistProps;
import uk.gov.ets.password.validator.api.config.PasswordValidatorProperties;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = {PasswordBlacklistProps.class, PasswordValidatorProperties.class})
@TestPropertySource({"classpath:password-blacklist-test.properties",
                     "classpath:password-validator-props-test.properties"})
public class PasswordValidatorServiceTest {

	private PasswordValidatorService passwordBlacklistService;

	@Autowired
	private PasswordBlacklistProps passwordBlacklistProps;

    @Autowired
    private PasswordValidatorProperties passwordValidatorProperties;
	
	@Mock
	private RestTemplate restTemplate;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.initMocks(this);
		passwordBlacklistService = new PasswordValidatorService(passwordBlacklistProps, passwordValidatorProperties, restTemplate);
	}
	
    @Test
    void givenBlacklistedPasswd_whenIsPwnedIsCalled_shouldReturnTrue() {
        String passwd = "qwerty";
        String response ="7377BA15B8D5E12FCCBA32B074D45503D67:2\r\n"
        		+ "7387376AFD1B3DAB553D439C8A7D7CDDED1:2\r\n"
        		+ "73A05C0ED0176787A4F1574FF0075F7521E:3946737\r\n"
        		+ "748186F058DA83745B80E70B66D36B216A4:4\r\n"
        		+ "753423BBA7CAF0BED484AB0B14E5AA03EB8:1";

        Mockito
          .when(restTemplate.getForObject(passwordBlacklistProps.getApiServiceUrl(), String.class,"B1B37"))
          .thenReturn(response);

        Assertions.assertTrue(passwordBlacklistService.isPwned(passwd));
    }
    
    @Test
    void givenGoodPasswd_whenIsPwnedIsCalled_shouldReturnFalse() {
        String passwd = "OU5tdV8GolSytPvIQslJ";
        String response ="01186FF5B6929AA8031F709BA6F38A5D35F:6\r\n"
        		+ "025248557202F687757A5F3EF92D496750E:1\r\n"
        		+ "038AB1AC4122D730454CCD93AC3A2D00F09:10\r\n"
        		+ "03CEBB819A7588345D2E55917DDA1B07E7F:7\r\n"
        		+ "042E5254D8F0BB1E16C2C7C112B64B02B7A:2\r\n"
        		+ "0449464A13781B081CB0C425F0224839AC0:1\r\n"
        		+ "0450E0146715D046F7A2F92A003B73A2D7A:3\r\n"
        		+ "04C472A424DA31874D6F2CC5535BF027350:1";

        Mockito
          .when(restTemplate.getForObject(passwordBlacklistProps.getApiServiceUrl(), String.class,"61920"))
          .thenReturn(response);

        Assertions.assertFalse(passwordBlacklistService.isPwned(passwd));
    }
    
    @Test
    void givenPasswd_whenNullResponseIsReturned_shouldReturnFalse() {
        String passwd = "OU5tdV8GolSytPvIQslJ";

        Mockito
          .when(restTemplate.getForObject(passwordBlacklistProps.getApiServiceUrl(), String.class,"61920"))
          .thenReturn(null);

        Assertions.assertFalse(passwordBlacklistService.isPwned(passwd));
    }    
    
    @Test
    void givenPasswd_whenRemoteServiceIsDown_shouldReturnFalse() {
        String passwd = "OU5tdV8GolSytPvIQslJ";

        Mockito
          .when(restTemplate.getForObject(passwordBlacklistProps.getApiServiceUrl(), String.class,"61920"))
          .thenThrow(new RestClientException("Service is down."));

        Assertions.assertFalse(passwordBlacklistService.isPwned(passwd));
    }

    @Test
    void failWhenPasswordDoesNotConformToUpdatedPoliciesMinimumChars() {
        String password = "enroled_user";
        Assertions.assertFalse(passwordBlacklistService.compliesWithPasswordPolicies(password));
    }
    
    
    @Test
    void givenPasswd_whenPasswordStrength_shouldReturnStrength() {
        Assertions.assertEquals(4,passwordBlacklistService.passwordStrength("OU5tdV8GolSytPvIQslJ"));
        Assertions.assertEquals(1,passwordBlacklistService.passwordStrength("glqwerty12345"));
        Assertions.assertEquals(0,passwordBlacklistService.passwordStrength(null));
    }
}
