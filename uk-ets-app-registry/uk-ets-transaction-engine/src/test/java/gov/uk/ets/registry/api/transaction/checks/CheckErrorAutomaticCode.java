package gov.uk.ets.registry.api.transaction.checks;

import org.springframework.stereotype.Service;

@Service("check3")
@BusinessCheckGrouping(groups = BusinessCheckGroup.TRANSFERRING_ACCOUNT)
public class CheckErrorAutomaticCode extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        addError(context, "Message");
    }

}
