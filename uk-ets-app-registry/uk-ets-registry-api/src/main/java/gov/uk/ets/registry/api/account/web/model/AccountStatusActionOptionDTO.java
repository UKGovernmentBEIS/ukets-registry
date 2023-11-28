package gov.uk.ets.registry.api.account.web.model;

import gov.uk.ets.registry.api.transaction.domain.type.AccountStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AccountStatusActionOptionDTO {

	  private String label;
	  private String hint;
	  private AccountStatusAction value;
	  private final boolean enabled= true;
	  private AccountStatus newStatus;
	  private String message;
}
