package gov.uk.ets.registry.api.user.profile.service;

import static org.junit.jupiter.api.Assertions.*;

import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.notification.TokenChangeNotification;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.api.user.UserGeneratorService;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

class TokenChangeServiceTest {

    TokenVerifier tokenVerifier;

    TokenChangeService service;

    @BeforeEach
    void setUp() {
        tokenVerifier = new TokenVerifier("http://localhost:8080/auth", "uk-ets-web-app", "uk-ets");
        service = new TokenChangeService(
            tokenVerifier,
            "http://localhost:4200",
            "/token-change/email-clicked/",
            60L);
    }

    @Test
    void sendEmailMessage() throws NoSuchAlgorithmException {

        TokenTaskDetailsDTO details = new TokenTaskDetailsDTO();
        details.setEmail("email@email.gr");
        details.setInitiatorUrid(new UserGeneratorService().generateURID());
        details.setFirstName("First name");
        details.setLastName("Last name");

        TokenChangeNotification notification = service.sendEmailMessage(details);
        assertEquals(notification.getEmailAddress(), details.getEmail());
        assertEquals(60L, notification.getExpiration());
        assertTrue(notification.getUrl().startsWith("http://localhost:4200/token-change/email-clicked/"));
    }
}
