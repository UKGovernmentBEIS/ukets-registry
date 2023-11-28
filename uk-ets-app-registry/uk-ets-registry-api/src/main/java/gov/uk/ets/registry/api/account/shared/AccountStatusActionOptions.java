package gov.uk.ets.registry.api.account.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.uk.ets.registry.api.account.web.model.AccountStatusActionOptionDTO;

public class AccountStatusActionOptions implements Options<AccountStatusActionOptionDTO> {

	private List<AccountStatusActionOptionDTO> options = new ArrayList<>();
	
	@Override
	public List<AccountStatusActionOptionDTO> getOptions() {
		return options;
	}
	
	public AccountStatusActionOptions addOption(AccountStatusActionOptionDTO option) {
		options.add(option);
		return this;
	}

	public AccountStatusActionOptions addOptions(AccountStatusActionOptionDTO ...availableOptions) {
		options.addAll(Arrays.asList(availableOptions));
		return this;
	}
}
