package gov.uk.ets.registry.api.account.shared;

import gov.uk.ets.registry.api.account.web.model.AccountStatusActionOptionDTO;
import gov.uk.ets.registry.api.account.web.model.AccountStatusActionOptionEnum;
import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;

public final class AccountStatusActionOptionsFactory {

	private AccountStatusActionOptionsFactory() {

	}

	public static Options<AccountStatusActionOptionDTO> createAvailableAccountStatusActionOptions(AccountStatus status,
			AccountType accountType) {

		AccountStatusActionOptions options = new AccountStatusActionOptions();

		switch (status) {
		case OPEN -> {
			options.addOptions(AccountStatusActionOptionEnum.OPTION_SUSPEND_ACCOUNT.getOption(),
					           AccountStatusActionOptionEnum.OPTION_SUSPEND_PARTIALLY.getOption());
			if (Boolean.FALSE.equals(accountType.getKyoto())) {
				options.addOption(AccountStatusActionOptionEnum.OPTION_BLOCK_ACCOUNT.getOption());
			}
		}
		case SOME_TRANSACTIONS_RESTRICTED ->
			options.addOptions(AccountStatusActionOptionEnum.OPTION_SUSPEND_ACCOUNT.getOption(),
					           AccountStatusActionOptionEnum.OPTION_SUSPEND_PARTIALLY.getOption(),
					           AccountStatusActionOptionEnum.OPTION_UNBLOCK.getOption());
		case SUSPENDED_PARTIALLY ->
			options.addOptions(AccountStatusActionOptionEnum.OPTION_UNSUSPEND_ACCOUNT.getOption(),
			                   AccountStatusActionOptionEnum.OPTION_SUSPEND_ACCOUNT.getOption(),
					           AccountStatusActionOptionEnum.OPTION_BLOCK_ACCOUNT.getOption());
		case SUSPENDED -> options.addOptions(AccountStatusActionOptionEnum.OPTION_UNSUSPEND_ACCOUNT.getOption(),
                                             AccountStatusActionOptionEnum.OPTION_SUSPEND_PARTIALLY.getOption(),
				                             AccountStatusActionOptionEnum.OPTION_BLOCK_ACCOUNT.getOption());
		default -> throw new IllegalArgumentException("No account status options found for status: " + status);
		}

		return options;
	}

}
