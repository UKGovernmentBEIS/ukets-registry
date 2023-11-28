package gov.uk.ets.registry.api.transaction.checks;

import org.springframework.stereotype.Service;

@Service("check2")
@BusinessCheckGrouping(groups = BusinessCheckGroup.ACCOUNT)
public class CheckSuccessful extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {

    }

}
