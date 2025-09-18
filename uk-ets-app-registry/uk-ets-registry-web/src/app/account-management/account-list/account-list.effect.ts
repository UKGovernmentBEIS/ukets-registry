import { Injectable } from '@angular/core';
import { AccountApiService } from '../service/account-api.service';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, map } from 'rxjs/operators';
import { AccountSearchResult, SearchActionPayload } from './account-list.model';
import { PagedResults } from '@shared/search/util/search-service.util';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import * as AccountListActions from './account-list.actions';
import { createReportRequestSuccess } from '@reports/actions';
import { selectPageParameters } from './account-list.selector';
import { Store } from '@ngrx/store';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { concatLatestFrom } from '@ngrx/operators';

@Injectable()
export class AccountListEffect {
  constructor(
    private accountListService: AccountApiService,
    private store: Store,
    private actions$: Actions
  ) {}

  searchAccounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountListActions.searchAccounts),
      map((action) => AccountListActions.loadAccounts(action))
    );
  });

  clearStatePerRole$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountListActions.clearState),
      concatLatestFrom(() => this.store.select(isAdmin)),
      map(([action, isAdmin]) =>
        AccountListActions.clearStatePerRole({ isAdmin })
      )
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        AccountListActions.navigateToFirstPageOfResults,
        AccountListActions.navigateToLastPageOfResults,
        AccountListActions.navigateToNextPageOfResults,
        AccountListActions.navigateToPreviousPageOfResults,
        AccountListActions.navigateToPageOfResults
      ),
      map((action) => AccountListActions.loadAccounts(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountListActions.changePageSize, AccountListActions.sortResults),
      map((action) => AccountListActions.loadAccounts(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountListActions.replaySearch),
      map((action) => {
        const pagedResults: PagedResults<AccountSearchResult> = {
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

  loadAccounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountListActions.loadAccounts),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.accountListService
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

  private mapToAction(
    pagedResults: PagedResults<AccountSearchResult>,
    actionPayload: SearchActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    if (actionPayload.isReport) {
      return createReportRequestSuccess({ response: {} });
    }
    return AccountListActions.accountsLoaded({
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
