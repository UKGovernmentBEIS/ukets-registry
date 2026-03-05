import { AuthenticationErrorsMap } from '@registry-web/shared/model/authentication-error-messages';
import { ErrorDetail } from '@shared/error-summary';

/**
 * Factory method for potential errors map in Regulator Notices List component
 */
export function createRegulatorNoticesListErrorMap() {
  const errorMap = new Map<string | number, ErrorDetail>();
  errorMap.set(
    400,
    new ErrorDetail(null, 'Enter your search term in the correct format')
  );
  errorMap.set(401, AuthenticationErrorsMap.USER_UNAUTHENTICATED);
  errorMap.set(404, new ErrorDetail(null, '404 not found backend service'));
  // errorMap.set(0, new ErrorDetail(null, 'Timeout error'));
  errorMap.set(
    500,
    new ErrorDetail(
      null,
      '500 Server error while fetching regulator notices results'
    )
  );
  errorMap.set(
    'other',
    new ErrorDetail(
      null,
      'Unexpected error while fetching regulator notices results'
    )
  );

  return errorMap;
}
