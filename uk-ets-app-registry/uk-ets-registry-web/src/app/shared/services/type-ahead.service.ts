import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { errors } from '@shared/shared.action';
import { ErrorDetail } from '@shared/error-summary';
import { ApiErrorBody, ApiErrorDetail } from '@shared/api-error';

@Injectable()
export class TypeAheadService {
  constructor(private http: HttpClient, private store: Store) {}

  search(url: string, params: HttpParams): Observable<never[]> {
    return this.http
      .get<[]>(url, { params })
      .pipe(
        map((result) => {
          return result.slice(0, 10);
        }),
        catchError((httpError: HttpErrorResponse) => {
          this.handleHttpError(httpError);
          return [];
        })
      );
  }

  private handleHttpError(httpError: HttpErrorResponse) {
    let errorDetails: ErrorDetail[];
    if (httpError.status === 400) {
      errorDetails = [];
      const errorResponse: ApiErrorDetail[] = this.apiErrorToBusinessError(
        httpError.error
      );

      errorResponse.forEach((actionError) =>
        errorDetails.push(new ErrorDetail(null, actionError.message))
      );
    }
    this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
  }

  private apiErrorToBusinessError(
    apiErrorBody: ApiErrorBody
  ): ApiErrorDetail[] {
    const actionErrors: ApiErrorDetail[] = [];
    apiErrorBody.errorDetails.forEach((errorDetail) => {
      actionErrors.push({
        code: errorDetail.code,
        message: errorDetail.message,
      });
    });
    return actionErrors;
  }
}
