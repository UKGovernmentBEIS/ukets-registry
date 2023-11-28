package gov.uk.ets.keycloak.users.service.infrastructure;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

 
public enum UserStatus {
	REGISTERED,

	VALIDATED,

	ENROLLED,

	UNENROLLEMENT_PENDING,

	UNENROLLED,

	SUSPENDED,

	DEACTIVATION_PENDING,

	DEACTIVATED;
	
    /**
     * Parses the input string to an enumeration value.
     *
     * @param input The input string
     * @return an enumeration value or null if the input does not correspond to a value
     */
	public static UserStatus parse(String input) {
		UserStatus result;
		try {
			result = UserStatus.valueOf(input);
		} catch (Exception exc) {
			result = null;
		}
		return result;
	}
	
	public static List<UserStatus> getAllExceptDeactivated() {
		return Arrays.stream(values())
				     .filter(r -> r != DEACTIVATED)
				     .collect(toList());
	}
}
