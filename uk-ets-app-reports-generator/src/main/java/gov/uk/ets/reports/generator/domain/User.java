package gov.uk.ets.reports.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private String urid;
    // matches keycloak id;
    private String iamIdentifier;
    private String firstName;
    private String lastName;
    private String email;
}
