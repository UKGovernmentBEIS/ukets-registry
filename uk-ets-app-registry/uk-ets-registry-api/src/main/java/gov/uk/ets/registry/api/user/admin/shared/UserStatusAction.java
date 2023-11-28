package gov.uk.ets.registry.api.user.admin.shared;

/**
 * Possible user status change actions
 * 
 * @author fragkise
 */
public enum UserStatusAction {

	VALIDATE("Validate user"), UNEROLL("Unenroll user"), SUSPEND("Suspend user"),
	RESTORE("Restore user");

	private String description;

	UserStatusAction(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

}
