import { AuthenticationErrorsMap } from '@registry-web/shared/model/authentication-error-messages';
import { ErrorDetail } from '@registry-web/shared/error-summary/error-detail';

/**
 * Factory method for potential errors map in Task List component
 */
export function createAccountListErrorMap() {
  const errorMap = new Map<string | number, ErrorDetail>();
  errorMap.set(
    400,
    new ErrorDetail(null, 'Enter your search term in the correct format')
  );
  errorMap.set(404, new ErrorDetail(null, '404 not found backend service'));
  // errorMap.set(0, new ErrorDetail(null, 'Timeout error'));
  errorMap.set(
    500,
    new ErrorDetail(null, '500 Server error on fetching the account results')
  );
  errorMap.set(401, AuthenticationErrorsMap.USER_UNAUTHENTICATED);
  errorMap.set(
    'other',
    new ErrorDetail(null, 'Unexpected error on fetching the account results')
  );

  return errorMap;
}
