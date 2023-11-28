package uk.gov.ets.registration.user;

/**
 * Enumerates various custom Keycloak User Attributes.
 * @author P35036
 *
 */
public enum UserAttributes {

	URID("urid"),
	REGISTERED_ON_DATE("registeredOnDate"),
	REGISTRATION_IN_PROGRESS("registration_in_progress");

	
	private final String name;

	UserAttributes(String name) {
		this.name=name;
	}

	public String getName() {
		return name;
	}
	
	
}
