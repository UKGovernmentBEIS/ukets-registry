package gov.uk.ets.registry.api.user;

import java.io.Serializable;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EnrolmentKeyDTO implements Serializable {

    private static final long serialVersionUID = -3825788239425306346L;

    private String urid;
    private String enrolmentKey;
    private Date enrolmentKeyDateCreated;
    private Date enrolmentKeyDateExpired;

    public EnrolmentKeyDTO(String urid, String enrolmentKey, Date enrolmentKeyDateCreated) {
        this.urid = urid;
        this.enrolmentKey = enrolmentKey;
        if (enrolmentKey != null) {
            this.enrolmentKeyDateCreated = enrolmentKeyDateCreated;
        }
    }
}
