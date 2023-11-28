package gov.uk.ets.registry.api.user;

import gov.uk.ets.registry.api.user.domain.User;
import org.springframework.stereotype.Service;

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
        return result;
    }

}
