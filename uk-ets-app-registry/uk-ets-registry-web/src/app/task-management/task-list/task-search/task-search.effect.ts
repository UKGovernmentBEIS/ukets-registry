import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { catchError, concatMap, debounceTime, map } from 'rxjs/operators';
import * as TaskListActions from '@task-management/task-list/store/task-list.actions';
import {
  SearchActionPayload,
  Task,
} from '@shared/task-and-regulator-notice-management/model';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary';
import { PagedResults } from '@shared/search/util/search-service.util';
import { createReportRequestSuccess } from '@reports/actions';
import { TaskService } from '@shared/services/task-service';
import { Store } from '@ngrx/store';
import { selectPageParameters } from '../store/task-list.selector';
import { BulkActions } from '@shared/task-and-regulator-notice-management/bulk-actions';

@Injectable()
export class TaskListEffect {
  constructor(
    private taskService: TaskService,
    private store: Store,
    private actions$: Actions
  ) {}

  searchTasks$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskListActions.searchTasks),
      debounceTime(100),
      map((action) => TaskListActions.loadTasks(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TaskListActions.navigateToFirstPageOfResults,
        TaskListActions.navigateToLastPageOfResults,
        TaskListActions.navigateToNextPageOfResults,
        TaskListActions.navigateToPreviousPageOfResults,
        TaskListActions.navigateToPageOfResults
      ),
      map((action) => TaskListActions.loadTasks(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskListActions.changePageSize, TaskListActions.sortResults),
      map((action) => TaskListActions.loadTasks(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskListActions.replaySearch),
      map((action) => {
        const pagedResults: PagedResults<Task> = {
          totalResults: 0,
          items: [],
        };
        // ΝΟΤΕ: Needed to avoid extra API call
        return this.mapToAction(pagedResults, {
          ...action,
          pageParameters: action.pageParameters,
        });
      })
    );
  });

  loadTasks$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TaskListActions.loadTasks),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.taskService
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

  clearBulkActionsStore$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TaskListActions.clearState),
      map(() => BulkActions.CLEAR_SUCCESS())
    )
  );

  private mapToAction(
    pagedResults: PagedResults<Task>,
    actionPayload: SearchActionPayload
  ) {
    if (actionPayload.isReport) {
      return createReportRequestSuccess({ response: {} });
    }
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return TaskListActions.tasksLoaded({
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
    action: SearchActionPayload
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
