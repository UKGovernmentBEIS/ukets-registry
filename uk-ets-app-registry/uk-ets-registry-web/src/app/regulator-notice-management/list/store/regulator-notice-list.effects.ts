import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, debounceTime, map } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary';
import { PagedResults } from '@shared/search/util/search-service.util';
import { createReportRequestSuccess } from '@reports/actions';
import { TaskService } from '@shared/services/task-service';
import { Store } from '@ngrx/store';
import { RegulatorNoticeListActions } from '@regulator-notice-management/list/store/regulator-notice-list.actions';
import { selectPageParameters } from '@regulator-notice-management/list/store/regulator-notice-list.selectors';
import { concatLatestFrom } from '@ngrx/operators';
import {
  RegulatorNoticeTask,
  SearchRegulatorNoticesActionPayload,
} from '@shared/task-and-regulator-notice-management/model';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions';

@Injectable()
export class RegulatorNoticeListEffects {
  searchTasks$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(RegulatorNoticeListActions.SEARCH_REGULATOR_NOTICES),
      debounceTime(100),
      map((action) => RegulatorNoticeListActions.LOAD_NOTICES(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        RegulatorNoticeListActions.SELECT_FIRST_RESULTS_PAGE,
        RegulatorNoticeListActions.SELECT_LAST_RESULTS_PAGE,
        RegulatorNoticeListActions.SELECT_NEXT_RESULTS_PAGE,
        RegulatorNoticeListActions.SELECT_PREVIOUS_RESULTS_PAGE,
        RegulatorNoticeListActions.SELECT_RESULTS_PAGE
      ),
      map((action) => RegulatorNoticeListActions.LOAD_NOTICES(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        RegulatorNoticeListActions.CHANGE_PAGE_SIZE,
        RegulatorNoticeListActions.SORT_RESULTS
      ),
      map((action) => RegulatorNoticeListActions.LOAD_NOTICES(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(RegulatorNoticeListActions.REPLAY_SEARCH),
      map((action) => {
        const pagedResults: PagedResults<RegulatorNoticeTask> = {
          totalResults: 0,
          items: [],
        };
        return this.mapToAction(pagedResults, {
          ...action,
          pageParameters: action.pageParameters,
        });
      })
    );
  });

  loadTasks$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(RegulatorNoticeListActions.LOAD_NOTICES),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.taskService
          .searchRegulatorNotices(
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

  clearBulkActionsStore$ = createEffect(() =>
    this.actions$.pipe(
      ofType(RegulatorNoticeListActions.CLEAR_SEARCH_STATE),
      map(() => BulkActions.CLEAR_SUCCESS())
    )
  );

  constructor(
    private taskService: TaskService,
    private store: Store,
    private actions$: Actions
  ) {}

  private mapToAction(
    pagedResults: PagedResults<RegulatorNoticeTask>,
    actionPayload: SearchRegulatorNoticesActionPayload
  ) {
    if (actionPayload.isReport) {
      return createReportRequestSuccess({ response: {} });
    }
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return RegulatorNoticeListActions.SEARCH_RESULTS_PAGE_LOADED({
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
    action: SearchRegulatorNoticesActionPayload
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
