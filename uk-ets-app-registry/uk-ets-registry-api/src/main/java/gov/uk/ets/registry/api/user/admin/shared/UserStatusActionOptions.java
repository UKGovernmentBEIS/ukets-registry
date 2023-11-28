package gov.uk.ets.registry.api.user.admin.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.uk.ets.registry.api.account.shared.Options;
import gov.uk.ets.registry.api.user.admin.web.model.UserStatusActionOptionDTO;

public class UserStatusActionOptions implements Options<UserStatusActionOptionDTO> {

	private List<UserStatusActionOptionDTO> options = new ArrayList<>();
	
	@Override
	public List<UserStatusActionOptionDTO> getOptions() {
		return options;
	}

	public UserStatusActionOptions addOption(UserStatusActionOptionDTO option) {
		options.add(option);
		return this;
	}

	public UserStatusActionOptions addOptions(UserStatusActionOptionDTO ...availableOptions) {
		options.addAll(Arrays.asList(availableOptions));
		return this;
	}
}
