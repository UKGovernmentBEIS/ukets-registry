package gov.uk.ets.registry.api.common.display;

import gov.uk.ets.registry.api.common.Utils;
import gov.uk.ets.registry.api.user.domain.User;
import org.apache.commons.lang3.StringUtils;

public class DisplayNameUtils {

    private DisplayNameUtils() {
    }
    
    /**
     * Helper that returns the display name for a user.
     * Display name is the known as name if it exists, else the concatenation of first and last name
     * @param user User
     * @return the name to be displayed
     */
    public static String getDisplayName(User user) {
        return !StringUtils.isEmpty(user.getKnownAs()) ? user.getKnownAs()
                : Utils.concat(" ", user.getFirstName(), user.getLastName());
    }
}
