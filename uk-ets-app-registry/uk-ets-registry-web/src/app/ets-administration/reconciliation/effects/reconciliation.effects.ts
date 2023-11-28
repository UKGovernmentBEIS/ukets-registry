import { Injectable } from '@angular/core';
import { ReconciliationService } from '@reconciliation-administration/service/reconciliation.service';
import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { catchError, map, mergeMap, switchMap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import {
  fetchLastStartedReconciliation,
  startReconciliation,
  updateLatestReconciliation,
} from '@reconciliation-administration/actions/reconciliation.actions';
import { errors } from '@shared/shared.action';
import { ApiErrorBody } from '@registry-web/shared/api-error';

@Injectable()
export class ReconciliationEffects {
  constructor(
    private reconciliationService: ReconciliationService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store
  ) {}

  fetchLatestReconciliation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchLastStartedReconciliation),
      mergeMap(() => {
        return this.reconciliationService.fetchLatestReconciliation().pipe(
          map((result) => {
            return updateLatestReconciliation({ reconciliation: result });
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        );
      })
    );
  });

  startReconciliation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(startReconciliation),
      switchMap(() => {
        return this.reconciliationService.startReconciliation().pipe(
          map((result) => {
            return updateLatestReconciliation({ reconciliation: result });
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        );
      })
    );
  });
}
