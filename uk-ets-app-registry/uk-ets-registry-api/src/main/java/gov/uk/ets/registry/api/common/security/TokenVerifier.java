package gov.uk.ets.registry.api.common.security;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Responsible for producing token and read a token
 */
@Component
public class TokenVerifier {
    private final String keycloakUrl;
    private final String clientId;
    private final String secret;

    public TokenVerifier(
        @Value("${keycloak.auth-server-url}") String ukEtsRegistryUrl,
        @Value("${keycloak.clientId: uk-ets-web-app}") String clientId,
        @Value("${verification.url.secret: uk-ets}") String secret) {
        this.keycloakUrl = ukEtsRegistryUrl;
        this.clientId = clientId;
        this.secret = secret;
    }

    /**
     * Generates and returns a new token
     * @param command The {@link GenerateTokenCommand}
     * @return String
     */
    public String generateToken(GenerateTokenCommand command) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        Date issued = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        Date expires = Date.from(now.plusMinutes(command.getExpiration()).atZone(ZoneId.systemDefault()).toInstant());
        return JWT.create().withIssuer(keycloakUrl)
            .withIssuedAt(issued).withSubject(command.getPayload())
            .withAudience(clientId)
            .withExpiresAt(expires)
            .sign(algorithm);
    }

    /**
     * Decodes the payload / subject of the token and returns this payload.
     * @param token The token
     * @return The payload of token
     */
    public String getPayload(String token) { // throws expiration exception
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(keycloakUrl).build();
        DecodedJWT jwt = verifier.verify(token);
        return jwt.getSubject();
    }
}
