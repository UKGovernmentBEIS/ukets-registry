package gov.uk.ets.registry.api.transaction.checks;

public class RequiredFieldException extends RuntimeException {

  public RequiredFieldException(String requiredField) {
    super(requiredField);
  }
}
