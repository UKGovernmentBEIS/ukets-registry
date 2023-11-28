import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  SearchActionPayload,
  TrustedAccount,
} from '@registry-web/shared/model/account/trusted-account';
import { PagedResults } from '@registry-web/shared/search/util/search-service.util';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import {
  selectTrustedAccountCriteria,
  selectTrustedAccountPageParameters,
  selectTrustedAccountSortParameters,
} from './trusted-accounts.selector';
import * as TrustedAccountsActions from './trusted-accounts.actions';
import { Store } from '@ngrx/store';
import { catchError, concatMap, map, withLatestFrom } from 'rxjs/operators';
import { AccountApiService } from '@registry-web/account-management/service/account-api.service';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@registry-web/shared/shared.action';
import { ErrorSummary } from '@registry-web/shared/error-summary';

@Injectable()
export class TrustedAccountsEffect {
  constructor(
    private actions$: Actions,
    private store: Store,
    private accountService: AccountApiService
  ) {}

  fetchTrustedAcounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountsActions.fetchTAL),
      withLatestFrom(
        this.store.select(selectTrustedAccountSortParameters),
        this.store.select(selectTrustedAccountPageParameters),
        this.store.select(selectTrustedAccountCriteria)
      ),
      map(([, sortParams, pageParams, criteria]) => {
        const actionPayload: SearchActionPayload = {
          criteria: criteria,
          pageParameters: pageParams,
          sortParameters: sortParams,
          potentialErrors: null,
        };
        return TrustedAccountsActions.searchTAL(actionPayload);
      })
    );
  });

  searchTrustedAccounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountsActions.searchTAL),
      map((action) => TrustedAccountsActions.loadTAL(action))
    );
  });

  navigateToTrustedAccountsPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TrustedAccountsActions.navigateToFirstPageOfResults,
        TrustedAccountsActions.navigateToLastPageOfResults,
        TrustedAccountsActions.navigateToNextPageOfResults,
        TrustedAccountsActions.navigateToPreviousPageOfResults,
        TrustedAccountsActions.navigateToPageOfResults
      ),
      map((action) => TrustedAccountsActions.loadTAL(action))
    );
  });

  changeTrustedAccountPageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TrustedAccountsActions.changePageSize,
        TrustedAccountsActions.sortResults
      ),
      map((action) => TrustedAccountsActions.loadTAL(action))
    );
  });

  loadTrustedAccounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountsActions.loadTAL),
      withLatestFrom(this.store.select(selectAccountId)),
      concatMap(([action, id]) => {
        const criteria = { ...action.criteria, accountId: id };
        return this.accountService
          .searchTAL(criteria, action.pageParameters, action.sortParameters)
          .pipe(
            map((pagedResults) => this.mapToAction(pagedResults, action)),
            catchError((httpError: any) =>
              this.handleHttpError(httpError, action)
            )
          );
      })
    );
  });

  private mapToAction(
    pagedResults: PagedResults<TrustedAccount>,
    actionPayload: SearchActionPayload
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;

    return TrustedAccountsActions.trustedAccountListLoaded({
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
