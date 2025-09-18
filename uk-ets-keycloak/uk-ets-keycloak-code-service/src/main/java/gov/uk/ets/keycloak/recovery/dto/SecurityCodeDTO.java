package gov.uk.ets.keycloak.recovery.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SecurityCodeDTO {

    private Long id;
    private String userId;
    private String email;
    private String countryCode;
    private String phoneNumber;
    private String code;
    private Integer attempts;
    private Boolean valid;
    private Boolean loggedIn;
    private Long expiresInMillis;
    private Date createdAt;
}

