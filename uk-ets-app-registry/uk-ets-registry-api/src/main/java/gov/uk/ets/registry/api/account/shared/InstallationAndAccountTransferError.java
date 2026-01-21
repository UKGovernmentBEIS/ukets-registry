package gov.uk.ets.registry.api.account.shared;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InstallationAndAccountTransferError {

	// BR1: Installation ID must belong to an OHA account.
	INSTALLATION_ID_NOT_ASSOCIATED_TO_OHA("1", "You can only transfer the installation of Operator Holding Accounts."),
	// BR2: The installation ID must exist in the system.
	INVALID_INSTALLATION_ID("2", "The installation ID does not exist. Enter a valid Installation ID."),
	// BR3: Installation transfer cannot be requested from accounts for which
	// pending transactions (either pending to be approved or pending due to delay)
	// exist
	ACCOUNT_WITH_PENDING_TRANSACTIONS("3",
			"The account has outstanding transactions. You cannot proceed with the transfer."),
	// BR4: You cannot create another account with the same permit ID.
	MULTIPLE_INSTALLATION_PERMIT_IDS_NOT_ALLOWED("4",
			"An account with this permit ID already exists. You cannot create another account with the same permit ID"),
	// BR5: Request account opening with installation transfer or account transfer
	// must not be allowed for accounts which are in status CLOSED.
	CLOSED_ACCOUNT("5", "The account is closed. The transfer cannot be requested."),
	// BR6: Request account opening with installation transfer must not be allowed
	// accounts which are in status TRANSFER_PENDING.
	ACCOUNT_WITH_PENDING_TRANSFER("6", "The account has outstanding transfers. The transfer cannot be requested."),
	// BR8: if any of the following tasks is outstanding (pending or
	// delayed) for the AH of the old (existing account),
	// the account opening with installation transfer cannot be triggered:
	OUTSTANDING_TASKS_EXIST_FOR_THE_OLD_AH("8",
			"The account holder has outstanding tasks. You cannot proceed with the transfer."),
	// BR9: Request cannot be initiated for accounts in status Transfer Pending.
	// This should apply to all places where a Task Create takes place
	ACCOUNT_WITH_PENDING_OUTSTANDING_TASKS("9", "The account has outstanding tasks. The transfer cannot be requested."),
	// BR10: Account transfer to the same AH cannot be requested.
	ACCOUNT_TRANSFER_TO_THE_SAME_AH_IS_NOT_PERMITTED("10",
			"An installation cannot be transferred to the same account holder."),
	// BR11: Only OHA can be transferred to another AH
	TRANSFER_ONLY_OHAS("11", "You can only transfer Operator Holding Accounts."),
	// BR12: AH ID must exist in the system
	UNKNOWN_ACCOUNT_HOLDER_ID("12", "The Account Holder does not exist. Enter a valid Account Holder ID."),
	// BR13: If the task with reference to the TAL activation is in process, the
	// request for account opening with installation transfer or the account
	// transfer cannot be triggered. (i.e. TAL activation)
	TRUSTED_ACC_ADDITION_TASK_IS_PENDING_ACTIVATION("13",
			"A Trusted Account Addition task is Pending for activation. The transfer cannot be completed."),
	// BR14: Request account opening with installation transfer must not be allowed
	// for accounts which are in status CLOSURE_PENDING.
	ACCOUNT_WITH_PENDING_CLOSURE_REQUEST("14",
			"The account has closure pending requests. The transfer cannot be requested."),

    MULTIPLE_MARITIME_IMO_COMPANY_NUMBERS_NOT_ALLOWED("15",
         "An account with this IMO company number already exists. You cannot create another account with the same IMO company number"),

    MULTIPLE_MARITIME_MONITORING_PLAN_ID_NUMBERS_NOT_ALLOWED("16",
         "An account with this monitoring plan id already exists. You cannot create another account with the same monitoring plan id"),

    MULTIPLE_AIRCRAFT_MONITORING_PLAN_ID_NUMBERS_NOT_ALLOWED("17",
         "An account with this monitoring plan id already exists. You cannot create another account with the same monitoring plan id");

	private final String ruleID;
	private final String message;

}
