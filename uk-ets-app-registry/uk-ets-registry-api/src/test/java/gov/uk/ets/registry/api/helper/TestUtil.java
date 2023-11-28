package gov.uk.ets.registry.api.helper;

import com.fasterxml.jackson.core.JsonProcessingException;

import gov.uk.ets.registry.api.account.domain.types.AccountHolderType;
import gov.uk.ets.registry.api.account.shared.AccountHolderDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDTO;
import gov.uk.ets.registry.api.account.web.model.AccountDetailsDTO;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;

public class TestUtil {

	public static final AccountDTO sampleAccountDTO() throws JsonProcessingException {
		
		AccountDTO account = new AccountDTO();
		account.setAccountType(AccountType.PERSON_HOLDING_ACCOUNT.toString());
		
		AccountHolderDTO holder = new AccountHolderDTO();
		holder.setType(AccountHolderType.INDIVIDUAL);
		account.setAccountHolder(holder);
		
		AccountDetailsDTO details = new AccountDetailsDTO();
		details.setName("Account 1");
		account.setAccountDetails(details);
		
		return account;
	}
}
