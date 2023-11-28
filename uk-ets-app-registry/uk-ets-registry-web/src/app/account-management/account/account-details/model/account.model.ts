import { ApiErrorBody, ApiErrorDetail } from '@shared/api-error/api-error';

export type AccountActionError = Required<
  Pick<ApiErrorDetail, 'code' | 'urid' | 'message'>
> & { accountFullIdentifier: number };

export interface AccountActionErrorResponse {
  message?: string;
  error?: AccountActionError;
}

export function apiErrorToBusinessError(
  apiErrorBody: ApiErrorBody
): AccountActionErrorResponse {
  return {
    error: {
      code: apiErrorBody.errorDetails[0].code,
      message: apiErrorBody.errorDetails[0].message,
      accountFullIdentifier: Number.parseInt(
        apiErrorBody.errorDetails[0].identifier,
        10
      ),
      urid: apiErrorBody.errorDetails[0].urid,
    },
  };
}
