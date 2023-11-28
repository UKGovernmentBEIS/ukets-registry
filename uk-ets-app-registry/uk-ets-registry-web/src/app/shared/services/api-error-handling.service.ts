import { Injectable } from '@angular/core';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { ApiErrorBody } from '@shared/api-error';

@Injectable()
export class ApiErrorHandlingService {
  /**
   * transforms an api error to
   * @param apiErrorBody the incoming error body from the server
   */
  transform(apiErrorBody: ApiErrorBody): ErrorSummary {
    const errorDetails: ErrorDetail[] = [];
    if (apiErrorBody && apiErrorBody.errorDetails) {
      apiErrorBody.errorDetails.forEach((e) =>
        errorDetails.push({
          errorMessage: e.message,
          componentId: e.componentId,
          errorId: e.code,
          errorFileId: e.errorFileId,
          errorFilename: e.errorFilename,
        })
      );
      return {
        errors: errorDetails,
      };
    }
  }

  buildUiError(errorMessage: string): ErrorSummary {
    return {
      errors: [
        {
          componentId: '',
          errorMessage,
        },
      ],
    };
  }
}
