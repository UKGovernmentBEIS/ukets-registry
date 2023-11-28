package gov.uk.ets.registry.api.transaction.checks;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents an erroneous businessCheckResult
 */
@Getter
@Setter
public class BusinessCheckErrorResult extends BusinessCheckResult {

  /**
   * The errors identified during the checking.
   */
  private List<BusinessCheckError> errors = new ArrayList<>();

}
