package uk.gov.ets.transaction.log.checks;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.ets.transaction.log.checks.core.BusinessCheckContext;
import uk.gov.ets.transaction.log.checks.core.ParentBusinessCheck;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Service("check-1")
public class CheckForStoppedTransaction extends ParentBusinessCheck {

    @Value("${transaction.processing.max.period.in.hours}")
    private Integer maxPeriodInHours;

    @Override
    public void execute(BusinessCheckContext context) {
        Date startedDate = Date.from(LocalDateTime.now().minusHours(maxPeriodInHours).atZone(ZoneId.systemDefault()).toInstant());

        if (context.getTransaction().getStarted().before(startedDate)) {
            addError(context, "The transaction could not start because it is older than " + maxPeriodInHours + " hours");
        }
    }
}
