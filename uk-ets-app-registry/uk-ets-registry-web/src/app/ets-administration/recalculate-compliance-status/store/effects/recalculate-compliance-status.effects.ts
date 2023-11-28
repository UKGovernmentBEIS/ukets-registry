import { Injectable } from '@angular/core';
import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { catchError, map, switchMap } from 'rxjs/operators';
import { RecalculateComplianceStatusService } from '@recalculate-compliance-status/services';
import {
  startRecalculatioComplianceStatuAllCompliantEntities,
  startRecalculatioComplianceStatuAllCompliantEntitiesSuccess,
} from '@recalculate-compliance-status/store/actions';
import { HttpErrorResponse } from '@angular/common/http';
import { errors, navigateTo } from '@shared/shared.action';
import { of } from 'rxjs';

@Injectable()
export class RecalculateComplianceStatusEffects {
  constructor(
    private recalculateComplianceStatusService: RecalculateComplianceStatusService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store
  ) {}

  startRecalculatioComplianceStatuAllCompliantEntities$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(startRecalculatioComplianceStatuAllCompliantEntities),
      switchMap(() => {
        return this.recalculateComplianceStatusService
          .recalculateDynamicStatusAllCompliantEntities()
          .pipe(
            map(() => {
              return startRecalculatioComplianceStatuAllCompliantEntitiesSuccess();
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

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(startRecalculatioComplianceStatuAllCompliantEntitiesSuccess),
      map(() =>
        navigateTo({
          route: `/ets-administration/recalculate-compliance-status`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });
}
