import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';

import { catchError, map, mergeMap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { ItlReconciliationService } from '@kp-administration/reconciliation/service/itl-reconciliation.service';
import {
  fetchLatestKpReconciliation,
  updateLatestKpReconciliation,
} from '@kp-administration/store/actions/itl-reconcilation-actions';
import { ApiErrorHandlingService } from '@shared/services';

@Injectable()
export class ItlReconciliationEffects {
  constructor(
    private actions$: Actions,
    private reconciliationService: ItlReconciliationService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}

  fetchLatestKpReconciliation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchLatestKpReconciliation),
      mergeMap(() => {
        return this.reconciliationService.fetchLatestReconciliation().pipe(
          map((reconciliation) => {
            return updateLatestKpReconciliation({ reconciliation });
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
