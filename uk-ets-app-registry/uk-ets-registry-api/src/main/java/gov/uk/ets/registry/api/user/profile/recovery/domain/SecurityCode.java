package gov.uk.ets.registry.api.user.profile.recovery.domain;

import gov.uk.ets.registry.api.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SecurityCode {

    @Id
    @SequenceGenerator(name = "security_code_id_generator", sequenceName = "security_code_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "security_code_id_generator")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String email;
    private String countryCode;
    private String phoneNumber;
    private String code;
    private Boolean valid;
    private Date expiredAt;
    private Date createdAt;

}
