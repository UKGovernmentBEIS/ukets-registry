package gov.uk.ets.registry.api.user;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.springframework.stereotype.Service;

import gov.uk.ets.registry.api.user.domain.User;

/**
 * Converts user management entities to transfer objects and vice versa.
 */
@Service
public class UserConversionService {

    /**
     * Creates a user transfer object.
     * @param user A user
     * @return a user transfer object
     */
    public UserDTO convert(User user) {
        UserDTO result = new UserDTO();
        result.setUrid(user.getUrid());
        result.setKeycloakId(user.getIamIdentifier());
        result.setStatus(user.getState());
        result.setFirstName(user.getFirstName());
        result.setLastName(user.getLastName());
        result.setAlsoKnownAs(user.getKnownAs());
        result.setAgent(user.getAgent());
        result.setCrc(user.getCrc());
        if (Objects.nonNull(user.getCrcIssuanceDate())) {
            OffsetDateTime crcIssuanceDate = OffsetDateTime.ofInstant(user.getCrcIssuanceDate().toInstant(), ZoneOffset.UTC);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SXXX");
            result.setCrcIssuanceDate(crcIssuanceDate.format(formatter));
        }
        return result;
    }

}
