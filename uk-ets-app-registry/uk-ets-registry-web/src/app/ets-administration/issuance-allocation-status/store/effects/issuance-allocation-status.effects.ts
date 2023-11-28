import { Injectable } from '@angular/core';
import { IssuanceAllocationStatusService } from '../../service';
import * as IssuanceAllocationStatusActions from '../actions';
import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, mergeMap, map } from 'rxjs/operators';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { loadAllocationTableEventHistorySuccess } from '../actions';

@Injectable()
export class IssuanceAllocationStatusEffects {
  constructor(
    private issuanceAllocationStatusService: IssuanceAllocationStatusService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions
  ) {}

  /**
   * load Issuance and Allocation Status table data
   */
  loadIssuanceAllocationStatuses$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssuanceAllocationStatusActions.loadIssuanceAllocationStatus),
      mergeMap(action =>
        this.issuanceAllocationStatusService
          .getIssuanceAllocationStatuses()
          .pipe(
            map(results =>
              IssuanceAllocationStatusActions.loadIssuanceAllocationStatusSuccess(
                {
                  results
                }
              )
            ),
            catchError(error =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              )
            )
          )
      )
    )
  );

  /**
   * load Allocation Table events history
   */
  loadAllocationTableEventHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(IssuanceAllocationStatusActions.loadAllocationTableEventHistory),
      mergeMap(action =>
        this.issuanceAllocationStatusService.getAllocationTableEvents().pipe(
          map(results => loadAllocationTableEventHistorySuccess({ results })),
          catchError(error =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            )
          )
        )
      )
    )
  );
}
