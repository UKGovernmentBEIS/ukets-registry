package gov.uk.ets.registry.api.account.web.model;

import lombok.Getter;

/**
 * Possible account status change actions
 * 
 * @author fragkise
 */
@Getter
public enum AccountStatusAction {

	RESTRICT_SOME_TRANSACTIONS("Restrict some transactions",
	                           "This will not restrict Surrender, Reverse Allocation or " +
	                           "Return Excess Allocation transactions for the account."),
	REMOVE_RESTRICTIONS("Remove restrictions", "Allow unrestricted transactions for the account."),
	SUSPEND_PARTIALLY("Suspend account (partially)", "Authorised Representatives will not be able " +
                                "to access or view the account. The account will still be able to receive units."),
	SUSPEND("Suspend account (fully)", "Authorised Representatives will not be able " +
	                                   "to access or view the account. The account will not be able to receive units."),
	UNSUSPEND("Unsuspend account", "Authorised Representatives will be able to access the account. " +
	                               "The account will be able to receive units.");

	private final String description;
	private final String hint;

	AccountStatusAction(String description, String hint) {
		this.description = description;
		this.hint = hint;
	}
}
