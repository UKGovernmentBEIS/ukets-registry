package gov.uk.ets.registry.api.common.security;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = {"token"})
public class UsedToken {

    @Id
    @SequenceGenerator(name = "used_token_id_generator", sequenceName = "used_token_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "used_token_id_generator")
    private Long id;

    private String token;
    private Date expiredAt;
    private Date createdAt;
}
