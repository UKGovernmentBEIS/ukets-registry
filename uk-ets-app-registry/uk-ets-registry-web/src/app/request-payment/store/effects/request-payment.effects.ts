import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { of } from 'rxjs';
import {
  map,
  exhaustMap,
  catchError,
  withLatestFrom,
  mergeMap,
} from 'rxjs/operators';
import {
  downloadInvoice,
  submitPaymentRequest,
  submitPaymentRequestSuccess,
} from '@request-payment/store/actions';
import { RequestPaymentService } from '@request-payment/services';
import * as fromRequestPayment from '@request-payment/store/reducers';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';
import { select, Store } from '@ngrx/store';
import { selectPaymentRequest } from '@request-payment/store/reducers';
import { ExportFileService } from '@shared/export-file/export-file.service';

@Injectable()
export class RequestPaymentEffects {
  private actions$ = inject(Actions);
  private store = inject(Store<fromRequestPayment.RequestPaymentState>);
  private requestPaymentService = inject(RequestPaymentService);
  private apiErrorHandlingService = inject(ApiErrorHandlingService);
  private exportFileService = inject(ExportFileService);

  submitPaymentRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitPaymentRequest),
      withLatestFrom(this.store.pipe(select(selectPaymentRequest))),
      exhaustMap(([action, request]) =>
        this.requestPaymentService.submitRequest(request).pipe(
          map((resultIdentifier) => {
            return submitPaymentRequestSuccess({
              submittedRequestIdentifier: resultIdentifier,
            });
          }),
          catchError((httpError: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpError.error
                ),
              })
            );
          })
        )
      )
    );
  });

  downloadInvoiceFile$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(downloadInvoice),
        withLatestFrom(this.store.pipe(select(selectPaymentRequest))),
        mergeMap(([, request]) =>
          this.requestPaymentService.downloadInvoice(request).pipe(
            map((result) =>
              this.exportFileService.export(
                result.body,
                this.exportFileService.getContentDispositionFilename(
                  result.headers.get('Content-Disposition')
                )
              )
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
        )
      ),
    { dispatch: false }
  );
}
