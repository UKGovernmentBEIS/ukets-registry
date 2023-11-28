import { ApiErrorBody, ApiErrorDetail } from '@shared/api-error/api-error';

type BusinessCheckError = Required<Pick<ApiErrorDetail, 'code' | 'message'>>;

export interface BusinessCheckResult {
  requestIdentifier: number;
  transactionIdentifier: string;
  approvalRequired: boolean;
  executionDate: string;
  executionTime: string;
  transactionTypeDescription: string;
}

export interface BusinessCheckErrorResult extends BusinessCheckResult {
  errors: BusinessCheckError[];
}

export function apiErrorToStringMessage(error: ApiErrorBody): string {
  return error.errorDetails[0].message;
}

export function apiErrorToBusinessError(
  apiErrorBody: ApiErrorBody
): BusinessCheckErrorResult {
  const businessCheckErrors: BusinessCheckError[] = [];
  apiErrorBody.errorDetails.forEach((value: ApiErrorDetail) => {
    businessCheckErrors.push({
      code: value.code,
      message: value.message
    });
  });

  return {
    errors: businessCheckErrors,
    transactionIdentifier: '',
    requestIdentifier: 0,
    approvalRequired: false,
    executionDate: '',
    executionTime: '',
    transactionTypeDescription: ''
  };
}
