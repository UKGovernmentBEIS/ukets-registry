import { Injectable } from '@angular/core';
import { Actions, concatLatestFrom, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, map } from 'rxjs/operators';
import { SearchActionPayload, UserProjection } from './user-list.model';
import { PagedResults } from '@shared/search/util/search-service.util';
import { ErrorSummary } from '@shared/error-summary/error-summary';
import { errors } from '../../shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { UserListService } from '@user-management/service';
import * as UserListActions from './user-list.actions';
import { selectPageParameters } from './user-list.selector';
import { Store } from '@ngrx/store';

@Injectable()
export class UserListEffect {
  constructor(
    private userListService: UserListService,
    private store: Store,
    private actions$: Actions
  ) {}

  searchUsers$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserListActions.searchUsers),
      map((action) => UserListActions.loadUsers(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        UserListActions.navigateToFirstPageOfResults,
        UserListActions.navigateToLastPageOfResults,
        UserListActions.navigateToNextPageOfResults,
        UserListActions.navigateToPreviousPageOfResults,
        UserListActions.navigateToPageOfResults
      ),
      map((action) => UserListActions.loadUsers(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserListActions.changePageSize, UserListActions.sortResults),
      map((action) => UserListActions.loadUsers(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserListActions.replaySearch),
      map((action) => UserListActions.loadUsers(action))
    );
  });

  loadUsers$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(UserListActions.loadUsers),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        console.log(action.loadPageParametersFromState, storedPageParameters);
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.userListService
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
    pagedResults: PagedResults<UserProjection>,
    actionPayload: SearchActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return UserListActions.usersLoaded({
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
    console.log(httpError);
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
