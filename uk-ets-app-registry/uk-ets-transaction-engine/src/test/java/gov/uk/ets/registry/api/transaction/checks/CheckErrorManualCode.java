package gov.uk.ets.registry.api.transaction.checks;

import org.springframework.stereotype.Service;

@Service("check1")
@BusinessCheckGrouping(groups = BusinessCheckGroup.TRANSFERRING_ACCOUNT)
public class CheckErrorManualCode extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        context.addError(1, "Some message");
    }

}
