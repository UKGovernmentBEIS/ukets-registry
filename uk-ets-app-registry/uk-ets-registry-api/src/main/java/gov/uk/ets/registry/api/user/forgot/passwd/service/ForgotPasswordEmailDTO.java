package gov.uk.ets.registry.api.user.forgot.passwd.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class ForgotPasswordEmailDTO {
    
    private boolean success;
    private String email;
    /**
     * The confirmation url sent to user new email
     */
    private String confirmationUrl;
    /**
     * The expiration time in minutes of the email change request.
     */
    private Long expiration;
}
