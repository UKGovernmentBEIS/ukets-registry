import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType, concatLatestFrom } from '@ngrx/effects';
import {
  catchError,
  concatMap,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { of } from 'rxjs';

import * as RequestAllocationActions from '../actions/request-allocation.actions';
import { RequestAllocationService } from '../services/request-allocation.service';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';
import {
  selectedAllocationCategory,
  selectedAllocationYear,
} from '@request-allocation/reducers';
import { ApiErrorHandlingService } from '@shared/services';
import { ExportFileService } from '@shared/export-file/export-file.service';
import { selectGoBackRoute } from '@shared/shared.selector';

@Injectable()
export class RequestAllocationEffects {
  navigateToCheckAllocationRequest$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RequestAllocationActions.selectAllocationYear),
        tap((action) => {
          this.router.navigate(
            ['/ets-administration/request-allocation/check-allocation-request'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  cancelRequestAllocationRequested$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RequestAllocationActions.cancelRequestAllocationRequested),
        tap((action) => {
          this.router.navigate(
            ['/ets-administration/request-allocation/cancel-request'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  cancelRequestAllocation$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RequestAllocationActions.cancelRequestAllocationConfirmed),
        tap((action) => {
          this.router.navigate(['/ets-administration/request-allocation'], {
            skipLocationChange: true,
          });
        })
      );
    },
    { dispatch: false }
  );

  submitAllocationRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(RequestAllocationActions.submitAllocationRequest),
      concatLatestFrom(() => [
        this.store.pipe(select(selectedAllocationYear)),
        this.store.pipe(select(selectedAllocationCategory)),
      ]),
      mergeMap(([action, allocationYear, allocationCategory]) =>
        this.requestAllocationService
          .submitRequest(allocationYear, allocationCategory)
          .pipe(
            map((businessCheckResult) =>
              RequestAllocationActions.submitAllocationRequestSuccess({
                businessCheckResult,
              })
            ),
            catchError((httpErrorResponse: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpErrorResponse.error
                  ),
                })
              )
            )
          )
      )
    );
  });

  navigateToRequestAllocationSubmitted$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RequestAllocationActions.submitAllocationRequestSuccess),
        tap((action) => {
          this.router.navigate(
            ['/ets-administration/request-allocation/request-submitted'],
            { skipLocationChange: true }
          );
        })
      );
    },
    { dispatch: false }
  );

  downloadAllocationFile$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(RequestAllocationActions.downloadAllocationFile),
        concatLatestFrom(() => [
          this.store.pipe(select(selectedAllocationYear)),
          this.store.pipe(select(selectedAllocationCategory)),
        ]),
        mergeMap(([, allocationYear, allocationCategory]) => {
          return this.requestAllocationService
            .fetchRequestedFile(allocationYear, allocationCategory)
            .pipe(
              map((result) => {
                this.exportFileService.export(
                  result.body,
                  this.exportFileService.getContentDispositionFilename(
                    result.headers.get('Content-Disposition')
                  )
                );
              }),
              catchError((error: HttpErrorResponse) =>
                of(
                  errors({
                    errorSummary: this.apiErrorHandlingService.transform(
                      error.error
                    ),
                  })
                )
              )
            );
        })
      );
    },
    { dispatch: false }
  );

  isAllocationPending$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RequestAllocationActions.isPendingAllocationsRequested),
      switchMap(() =>
        this.requestAllocationService.getIsPendingAllocation().pipe(
          map((isPending) =>
            RequestAllocationActions.isPendingAllocationsRequestedSuccess({
              isPending,
            })
          ),
          catchError((error: HttpErrorResponse) =>
            of(
              RequestAllocationActions.isPendingAllocationsRequestedFailure({
                error,
              })
            )
          )
        )
      )
    )
  );

  cancelPendingAllocationsRequested$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RequestAllocationActions.cancelPendingAllocationsRequested),
      withLatestFrom(this.store.select(selectGoBackRoute)),
      exhaustMap(([_, goBackRoute]) =>
        this.requestAllocationService.cancelPendingAllocation().pipe(
          map(() =>
            RequestAllocationActions.cancelPendingAllocationsRequestedSuccess()
          ),
          tap(() => this.router.navigate([goBackRoute])),
          catchError(({ error }: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(error),
              }),
              RequestAllocationActions.cancelPendingAllocationsRequestedFailure()
            );
          })
        )
      )
    )
  );

  constructor(
    private actions$: Actions,
    private requestAllocationService: RequestAllocationService,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store,
    private exportFileService: ExportFileService
  ) {}
}
