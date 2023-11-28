package gov.uk.ets.registry.api.transaction.checks;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;

/**
 * Represents a business check failure.
 */
@Getter
public class BusinessCheckException extends RuntimeException {

  /**
   * Serialisation version.
   */
  private static final long serialVersionUID = -3353116845579187958L;

  /**
   * The error result.
   */
  private final BusinessCheckErrorResult businessCheckErrorResult;

  /**
   * Constructs an exception based on the provided result.
   * @param businessCheckErrorResult the result.
   */
  public BusinessCheckException(BusinessCheckErrorResult businessCheckErrorResult) {
    this.businessCheckErrorResult = businessCheckErrorResult;
  }

  /**
   * Constructs an exception based on the provided errors.
   * @param errors The business check errors.
   */
  public BusinessCheckException(BusinessCheckError... errors) {
    BusinessCheckErrorResult result = new BusinessCheckErrorResult();
    result.setErrors(Arrays.asList(errors));
    this.businessCheckErrorResult = result;
  }

  /**
   * Constructs an exception based on the provided errors.
   *
   * @param errors The business check errors.
   */
  public BusinessCheckException(List<BusinessCheckError> errors) {
    BusinessCheckErrorResult result = new BusinessCheckErrorResult();
    result.setErrors(errors);
    this.businessCheckErrorResult = result;
  }

  public List<BusinessCheckError> getErrors() {
    return businessCheckErrorResult.getErrors();
  }

  public Integer getFirstErrorCode() {
    Integer result = null;
    if (getErrors() != null) {
      result = getErrors().get(0).getCode();
    }
    return result;
  }

}
