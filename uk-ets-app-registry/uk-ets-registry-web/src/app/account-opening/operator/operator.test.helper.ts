import { SharedState } from '../../shared/shared.reducer';
import { ErrorSummary } from '../../shared/error-summary/error-summary';
import { aTestCountrySet } from '../../registration/registration.test.helper';
import { AccountOpeningState } from '../account-opening.model';

export function aTestOperatorState(): Partial<AccountOpeningState> {
  return {
    operator: null,
    operatorCompleted: false
  };
}

export function aTestSharedState(): Partial<SharedState> {
  return {
    countries: aTestCountrySet(),
    errorSummary: anEmptyErrorSummary()
  };
}

export function anEmptyErrorSummary(): ErrorSummary {
  return new ErrorSummary([]);
}
