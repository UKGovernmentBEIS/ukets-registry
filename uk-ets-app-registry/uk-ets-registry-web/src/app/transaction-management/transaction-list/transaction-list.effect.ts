import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { catchError, concatMap, map } from 'rxjs/operators';
import { SearchActionPayload } from './transaction-list.model';
import { PagedResults } from '@shared/search/util/search-service.util';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { TransactionManagementService } from '../service/transaction-management.service';
import * as TransactionListActions from './transaction-list.actions';
import { Transaction } from '@shared/model/transaction';
import { createReportRequestSuccess } from '@reports/actions';
import { selectPageParameters } from './transaction-list.selector';
import { Store } from '@ngrx/store';

@Injectable()
export class TransactionListEffect {
  constructor(
    private transactionManagementService: TransactionManagementService,
    private store: Store,
    private actions$: Actions
  ) {}

  searchTransactions$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionListActions.searchTransactions),
      map((action) => TransactionListActions.loadTransactions(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionListActions.navigateToFirstPageOfResults,
        TransactionListActions.navigateToLastPageOfResults,
        TransactionListActions.navigateToNextPageOfResults,
        TransactionListActions.navigateToPreviousPageOfResults,
        TransactionListActions.navigateToPageOfResults
      ),
      map((action) => TransactionListActions.loadTransactions(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TransactionListActions.changePageSize,
        TransactionListActions.sortResults
      ),
      map((action) => TransactionListActions.loadTransactions(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionListActions.replaySearch),
      map((action) => TransactionListActions.loadTransactions(action))
    );
  });

  loadTransactions$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionListActions.loadTransactions),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.transactionManagementService
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
    pagedResults: PagedResults<Transaction>,
    actionPayload: SearchActionPayload
  ) {
    if (actionPayload.isReport) {
      return createReportRequestSuccess({ response: {} });
    }
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return TransactionListActions.transactionsLoaded({
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
