import { Injectable } from '@angular/core';
import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { catchError, concatMap, map, tap } from 'rxjs/operators';
import { NotificationsListActions } from '@notifications/notifications-list/actions';
import {
  NotificationProjection,
  SearchActionPayload,
} from '@notifications/notifications-list/model';
import { NotificationsResultsService } from '@notifications/notifications-list/service';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';
import { ErrorSummary } from '@shared/error-summary';
import { PagedResults } from '@shared/search/util/search-service.util';
import { selectPageParameters } from '../reducers';

@Injectable()
export class NotificationsListEffect {
  constructor(
    private notificationsResultsService: NotificationsResultsService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(NotificationsListActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  searchNotifications$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        NotificationsListActions.searchNotifications,
        NotificationsListActions.navigateToNextPageOfResults,
        NotificationsListActions.navigateToPreviousPageOfResults,
        NotificationsListActions.navigateToLastPageOfResults,
        NotificationsListActions.navigateToFirstPageOfResults,
        NotificationsListActions.changePageSize,
        NotificationsListActions.sortResults
      ),
      map((action) => {
        return NotificationsListActions.loadNotifications(action);
      })
    );
  });

  loadNotifications$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsListActions.loadNotifications),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        console.log(action.loadPageParametersFromState, storedPageParameters);
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.notificationsResultsService
          .search(action.criteria, pageParameters, action.sortParameters)
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

  private mapToAction(
    pagedResults: PagedResults<NotificationProjection>,
    actionPayload: SearchActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return NotificationsListActions.notificationsLoaded({
      results: pagedResults.items,
      pagination: {
        currentPage: pagedResults.items.length ? pageParam + 1 : 1,
        pageSize: pageSizeParam ? pageSizeParam : pagedResults.totalResults,
        totalResults: pagedResults.totalResults,
      },
      sortParameters: actionPayload.sortParameters,
      criteria: actionPayload.criteria,
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
