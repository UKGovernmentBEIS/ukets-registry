import { ErrorDetail } from '@registry-web/shared/error-summary';

export const AuthenticationErrorsMap: Record<string, ErrorDetail> = {
  USER_UNAUTHENTICATED: new ErrorDetail(
    null,
    'You are either not authenticated or you do not have access to the requested resource. Please make sure that you have not signed in from another place.'
  ),
};
