import { ErrorDetail } from '@shared/error-summary';
import { AuthenticationErrorsMap } from '@shared/model/authentication-error-messages';

export * from './payment-list.model';

/**
 * Factory method for potential errors map in Payment List component
 */
export function createPaymentListErrorMap() {
  const errorMap = new Map<string | number, ErrorDetail>();
  errorMap.set(
    400,
    new ErrorDetail(null, 'Enter your search term in the correct format')
  );
  errorMap.set(404, new ErrorDetail(null, '404 not found backend service'));
  // errorMap.set(0, new ErrorDetail(null, 'Timeout error'));
  errorMap.set(
    500,
    new ErrorDetail(null, '500 Server error on fetching the payment results')
  );
  errorMap.set(401, AuthenticationErrorsMap.USER_UNAUTHENTICATED);
  errorMap.set(
    'other',
    new ErrorDetail(null, 'Unexpected error on fetching the payment results')
  );

  return errorMap;
}
