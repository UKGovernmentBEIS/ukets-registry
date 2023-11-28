package uk.gov.ets.transaction.log.checks.core;

import org.springframework.stereotype.Service;

@Service("check1")
public class CheckErrorManualCode extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        context.addError(1, "Some message");
    }

}
