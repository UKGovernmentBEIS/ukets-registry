package uk.gov.ets.transaction.log.checks.core;

import org.springframework.stereotype.Service;

@Service("check5")
public class CheckNoErrorMessage extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        context.addError(5, "");
    }

}
