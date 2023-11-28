package gov.uk.ets.registry.api.user.profile.service;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.common.security.GenerateTokenCommand;
import gov.uk.ets.registry.api.common.security.TokenVerifier;
import gov.uk.ets.registry.api.notification.TokenChangeNotification;
import gov.uk.ets.registry.api.task.web.model.TokenTaskDetailsDTO;
import gov.uk.ets.registry.usernotifications.EmitsGroupNotifications;
import gov.uk.ets.registry.usernotifications.GroupNotificationType;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TokenChangeService {

    private final TokenVerifier tokenVerifier;
    private final Long expiration;
    private final String applicationUrl;
    private final String verificationPath;

    public TokenChangeService(
        TokenVerifier tokenVerifier,
        @Value("${application.url}") String applicationUrl,
        @Value("${change.token.path:/token-change/email-clicked/}") String verificationPath,
        @Value("${token.change.url.expiration:60}") Long expiration) {
        this.tokenVerifier = tokenVerifier;
        this.applicationUrl = applicationUrl;
        this.verificationPath = verificationPath;
        this.expiration = expiration;
    }

    @EmitsGroupNotifications(GroupNotificationType.TOKEN_CHANGE_REQUEST)
    public TokenChangeNotification sendEmailMessage(TokenTaskDetailsDTO details) {
        String token = tokenVerifier.generateToken(GenerateTokenCommand
            .builder()
            .payload(Utils.serialiseToJson(details.getInitiatorUrid()))
            .expiration(expiration)
            .build());

        String verificationUrl = applicationUrl + verificationPath + token;

        return TokenChangeNotification.builder()
            .url(verificationUrl)
            .emailAddress(details.getEmail())
            .expiration(expiration)
            .build();
    }

}
