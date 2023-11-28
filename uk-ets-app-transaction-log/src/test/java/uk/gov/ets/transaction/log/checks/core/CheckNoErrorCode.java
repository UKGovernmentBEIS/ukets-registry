package uk.gov.ets.transaction.log.checks.core;

import org.springframework.stereotype.Service;

@Service("check4")
public class CheckNoErrorCode extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        context.addError(null, "Some message");
    }

}
