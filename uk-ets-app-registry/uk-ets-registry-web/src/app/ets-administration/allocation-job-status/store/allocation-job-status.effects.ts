/* eslint-disable ngrx/no-dispatch-in-effects */
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { catchError, map, concatMap, tap, switchMap, mergeMap, of } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@registry-web/shared/shared.action';
import { AllocationJobApiService } from '../services/allocation-job-api.service';
import { PageParameters } from '@registry-web/shared/search/paginator';
import {
  AllocationJob,
  SearchAllocationJobActionPayload,
} from '../models/allocation-job.model';
import { PagedResults } from '@registry-web/shared/search/util/search-service.util';
import { ErrorSummary } from '@registry-web/shared/error-summary';
import { selectPageParameters } from './allocation-job-status.selectors';
import * as AllocationJobActions from './allocation-job-status.actions';
import * as TaskDetailsActions from '@registry-web/task-management/task-details/actions/task-details.actions';
import { ExportFileService } from '../../../shared/export-file/export-file.service';

@Injectable()
export default class AllocationJobStatusEffect {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private allocationJobService: AllocationJobApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private exportFileService: ExportFileService
  ) {}

  navigateToTask$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AllocationJobActions.navigateToTask),
        tap((action) => this.store.dispatch(TaskDetailsActions.resetState())), //NOTE: needed for the task prefetch to function correctly
        tap((action) =>
          this.router.navigate(['/task-details/' + action.requestIdentifier])
        )
      );
    },
    { dispatch: false }
  );

  searchAllocationJobs$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AllocationJobActions.searchAllocationJobs),
      map((action) => AllocationJobActions.loadAllocationJobs(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AllocationJobActions.navigateToFirstPageOfResults,
        AllocationJobActions.navigateToLastPageOfResults,
        AllocationJobActions.navigateToNextPageOfResults,
        AllocationJobActions.navigateToPreviousPageOfResults,
        AllocationJobActions.navigateToPageOfResults
      ),
      map((action) => AllocationJobActions.loadAllocationJobs(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AllocationJobActions.changePageSize,
        AllocationJobActions.sortResults
      ),
      map((action) => AllocationJobActions.loadAllocationJobs(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AllocationJobActions.replaySearch),
      map((action) => AllocationJobActions.loadAllocationJobs(action))
    );
  });

  loadAllocationJobs$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AllocationJobActions.loadAllocationJobs),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters: PageParameters =
          action.loadPageParametersFromState
            ? storedPageParameters
            : action.pageParameters;
        return this.allocationJobService
          .search(
            action.criteria,
            pageParameters,
            action.sortParameters,
            action.isReport
          )
          .pipe(
            map((pagedResults) =>
              this.mapToAction(pagedResults, { ...action, pageParameters })
            ),
            catchError((httpError: any) =>
              this.handleHttpError(httpError, action)
            )
          );
      })
    );
  });

  cancelPendingAllocationById = createEffect(() => {
    return this.actions$.pipe(
      ofType(AllocationJobActions.cancelPendingAllocationById),
      mergeMap((action) => {
        return this.allocationJobService
          .cancelPendingAllocationById(action.jobId)
          .pipe(
            map((result) => {
              return AllocationJobActions.cancelPendingAllocationByIdSuccess();
            }),
            catchError((httpError) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpError.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  navigateToList$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AllocationJobActions.cancelPendingAllocationByIdSuccess),
        tap((action) =>
          this.router.navigate([
            '/ets-administration/view-allocation-job-status',
          ])
        )
      );
    },
    { dispatch: false }
  );

  downloadAllocationReport$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AllocationJobActions.downloadAllocationReportById),
        mergeMap((action) => {
          return this.allocationJobService
            .downloadAllocationReportById(action.jobId)
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

  private mapToAction(
    pagedResults: PagedResults<AllocationJob>,
    actionPayload: SearchAllocationJobActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return AllocationJobActions.allocationJobsLoaded({
      results: pagedResults.items,
      criteria: actionPayload.criteria,
      pagination: {
        currentPage: pagedResults.items.length ? pageParam + 1 : 1,
        pageSize: pageSizeParam ? pageSizeParam : pagedResults.totalResults,
        totalResults: pagedResults.totalResults,
      },
      sortParameters: actionPayload.sortParameters,
    });
  }

  private handleHttpError(
    httpError: HttpErrorResponse,
    action: SearchAllocationJobActionPayload
  ) {
    return action.potentialErrors.has(httpError.status)
      ? [
          errors({
            errorSummary: new ErrorSummary(
              Array.of(action.potentialErrors.get(httpError.status))
            ),
          }),
        ]
      : [
          errors({
            errorSummary: new ErrorSummary(
              Array.of(action.potentialErrors.get('other'))
            ),
          }),
        ];
  }
}
