package gov.uk.ets.registry.api.authz.ruleengine.features.account.rules;

import gov.uk.ets.registry.api.authz.ruleengine.BusinessSecurityStore;
import gov.uk.ets.registry.api.authz.ruleengine.features.AbstractBusinessRule;
import gov.uk.ets.registry.api.common.error.ErrorBody;
import gov.uk.ets.registry.api.common.error.UkEtsException;
import gov.uk.ets.registry.api.transaction.domain.type.AccountType;

import java.util.Optional;

public class NotApplicableToETSGovernmentAccountRule extends AbstractBusinessRule {

    public NotApplicableToETSGovernmentAccountRule(
            BusinessSecurityStore businessSecurityStore) {
        super(businessSecurityStore);
    }

    @Override
    public ErrorBody error() {
        return ErrorBody.from("ETS Government account cannot be closed.");
    }

    @Override
    public Outcome permit() {
        AccountType accountType = Optional.ofNullable(AccountType.get(account.getRegistryAccountType(),
                account.getKyotoAccountType()))
                .orElseThrow(() -> new UkEtsException("Account type is invalid"));

        Optional<AccountType> isETSGovernment = AccountType.getAllRegistryGovernmentTypes().stream()
                .filter(at -> at.equals(accountType)).findFirst();
        if (isETSGovernment.isEmpty()) {
            return Outcome.NOT_APPLICABLE_OUTCOME;
        }
        return forbiddenOutcome();
    }
}
