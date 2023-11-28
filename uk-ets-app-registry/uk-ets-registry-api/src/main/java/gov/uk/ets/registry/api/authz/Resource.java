package gov.uk.ets.registry.api.authz;

/**
 * Resources defined in Keycloak authorization.
 * @author fragkise
 * @since v0.3.0
 */
public enum Resource {

	TASK_RESOURCE("Task Resource");
	
	private final String resourceName;

	Resource(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getResourceName() {
		return resourceName;
	}	
	
}
