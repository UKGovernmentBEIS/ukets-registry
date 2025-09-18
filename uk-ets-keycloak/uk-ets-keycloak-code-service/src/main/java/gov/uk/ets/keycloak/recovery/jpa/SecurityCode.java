package gov.uk.ets.keycloak.recovery.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "security_code")
@Getter
@Setter
public class SecurityCode {

    @Id
    @SequenceGenerator(name = "security_code_id_generator", sequenceName = "security_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "security_code_id_generator")
    private Long id;

    @Column(name = "user_id")
    private String userId;
    @Column(name = "email")
    private String email;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "code")
    private String code;
    @Column(name = "attempts")
    private Integer attempts;
    @Column(name = "valid")
    private Boolean valid;
    @Column(name = "logged_in")
    private Boolean loggedIn;
    @Column(name = "expired_at")
    private Date expiredAt;
    @Column(name = "created_at")
    private Date createdAt;

}
