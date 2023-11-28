package uk.gov.ets.transaction.log.checks.core;

import org.springframework.stereotype.Service;

@Service("check3")
public class CheckErrorAutomaticCode extends ParentBusinessCheck {

    @Override
    public void execute(BusinessCheckContext context) {
        addError(context, "Message");
    }

}
