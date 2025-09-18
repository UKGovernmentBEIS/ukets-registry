package uk.gov.ets.registration.user;

import org.springframework.stereotype.Component;
import uk.gov.ets.registration.user.validation.ValidatePasswordResponse;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;

@Component
public class RestProxy {

    private Client client;

    @PostConstruct
    public void base() {
        this.client = ClientBuilder.newClient();
    }

    @PreDestroy
    protected void close() {
        if (this.client != null) {
            this.client.close();
        }
    }

    public ValidatePasswordResponse postToPasswordValidator(String url, String password) {
        return client
                .target(url)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(password, MediaType.TEXT_PLAIN), ValidatePasswordResponse.class);
    }
}
