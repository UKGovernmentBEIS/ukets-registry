package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;

/**
 * Possible options when changing an account status.
 * 
 * @author fragkise
 */
public enum AccountStatusActionOptionEnum {

	OPTION_UNBLOCK(AccountStatusActionOptionDTO
		               .builder()
		               .value(AccountStatusAction.REMOVE_RESTRICTIONS)
		               .label(AccountStatusAction.REMOVE_RESTRICTIONS.getDescription())
		               .hint(AccountStatusAction.REMOVE_RESTRICTIONS.getHint())
		               .newStatus(AccountStatus.OPEN).build()),
	OPTION_SUSPEND_PARTIALLY(AccountStatusActionOptionDTO
		                         .builder()
		                         .value(AccountStatusAction.SUSPEND_PARTIALLY)
		                         .label(AccountStatusAction.SUSPEND_PARTIALLY.getDescription())
		                         .hint(AccountStatusAction.SUSPEND_PARTIALLY.getHint())
		                         .newStatus(AccountStatus.SUSPENDED_PARTIALLY).build()),
	OPTION_SUSPEND_ACCOUNT(AccountStatusActionOptionDTO
		                       .builder()
		                       .value(AccountStatusAction.SUSPEND)
		                       .label(AccountStatusAction.SUSPEND.getDescription())
		                       .hint(AccountStatusAction.SUSPEND.getHint())
		                       .newStatus(AccountStatus.SUSPENDED).build()),
	OPTION_UNSUSPEND_ACCOUNT(AccountStatusActionOptionDTO
		                         .builder()
		                         .value(AccountStatusAction.UNSUSPEND)
		                         .label(AccountStatusAction.UNSUSPEND.getDescription())
		                         .hint(AccountStatusAction.UNSUSPEND.getHint())
		                         .newStatus(AccountStatus.OPEN).build()),
	OPTION_BLOCK_ACCOUNT(AccountStatusActionOptionDTO
		                     .builder()
		                     .value(AccountStatusAction.RESTRICT_SOME_TRANSACTIONS)
		                     .label(AccountStatusAction.RESTRICT_SOME_TRANSACTIONS.getDescription())
		                     .hint(AccountStatusAction.RESTRICT_SOME_TRANSACTIONS.getHint())
		                     .newStatus(AccountStatus.SOME_TRANSACTIONS_RESTRICTED).build());
	
	private final AccountStatusActionOptionDTO option;
	
	AccountStatusActionOptionEnum(AccountStatusActionOptionDTO option) {
		this.option = option;
	}

	public AccountStatusActionOptionDTO getOption() {
		return option;
	}
	
	
}
