import { Injectable, inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { navigateTo } from '@shared/shared.action';
import { map, mergeMap, withLatestFrom } from 'rxjs/operators';
import {
  cancelRequestPaymentConfirmed,
  cancelRequestPayment,
  clearRequestPayment,
  enterRequestPaymentWizard,
  submitPaymentRequestSuccess,
  setPaymentDetails,
} from '@request-payment/store/actions';
import * as fromRequestPayment from '@request-payment/store/reducers';
import { select, Store } from '@ngrx/store';
import { selectOriginatingPath } from '@request-payment/store/reducers';

@Injectable()
export class RequestPaymentNavigationEffects {
  private actions$ = inject(Actions);
  private store = inject(Store<fromRequestPayment.RequestPaymentState>);

  enterRequestPaymentWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(enterRequestPaymentWizard),
      map(() =>
        navigateTo({
          route: `/request-payment`,
        })
      )
    );
  });

  setPaymentDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setPaymentDetails),
      map(() =>
        navigateTo({
          route: `/request-payment/check-payment-request`,
        })
      )
    );
  });

  cancelRequestDocuments$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelRequestPayment),
      map((action) =>
        navigateTo({
          route: `/request-payment/cancel-request`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelRequestDocumentsConfirmed$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelRequestPaymentConfirmed),
      withLatestFrom(this.store.select(selectOriginatingPath)),
      mergeMap(([, originatingPath]) => [
        clearRequestPayment(),
        navigateTo({
          route: originatingPath,
        }),
      ])
    );
  });

  submitPaymentRequestSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitPaymentRequestSuccess),
      map(() => {
        return navigateTo({
          route: `/request-payment/request-submitted`,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });
}
