import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { catchError, concatMap, map } from 'rxjs/operators';
import {
  PagedResults,
  SearchActionPayload,
} from '@shared/search/util/search-service.util';
import { ErrorSummary } from '@shared/error-summary';
import { errors } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import * as PaymentListActions from '@payment-management/payment-list/store/actions';
import { selectPageParameters } from '@payment-management/payment-list/store/reducer';
import { Store } from '@ngrx/store';
import { PaymentManagementService } from '@payment-management/service';
import {
  PaymentSearchCriteria,
  PaymentSearchResult,
} from '@payment-management/model';

@Injectable()
export class PaymentListEffects {
  constructor(
    private paymentManagementService: PaymentManagementService,
    private store: Store,
    private actions$: Actions
  ) {}

  searchPayments$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(PaymentListActions.searchPayments),
      map((action) => PaymentListActions.loadPayments(action))
    );
  });

  navigateToPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        PaymentListActions.navigateToFirstPageOfResults,
        PaymentListActions.navigateToLastPageOfResults,
        PaymentListActions.navigateToNextPageOfResults,
        PaymentListActions.navigateToPreviousPageOfResults,
        PaymentListActions.navigateToPageOfResults
      ),
      map((action) => PaymentListActions.loadPayments(action))
    );
  });

  changePageSize$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(PaymentListActions.changePageSize, PaymentListActions.sortResults),
      map((action) => PaymentListActions.loadPayments(action))
    );
  });

  replaySearch$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(PaymentListActions.replaySearch),
      map((action) => PaymentListActions.loadPayments(action))
    );
  });

  loadPayments$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(PaymentListActions.loadPayments),
      concatLatestFrom(() => this.store.select(selectPageParameters)),
      concatMap(([action, storedPageParameters]) => {
        const pageParameters = action.loadPageParametersFromState
          ? storedPageParameters
          : action.pageParameters;
        return this.paymentManagementService
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
    pagedResults: PagedResults<PaymentSearchResult>,
    actionPayload: SearchActionPayload<PaymentSearchCriteria>
  ) {
    const pageParam = actionPayload.pageParameters.page;
    const pageSizeParam = actionPayload.pageParameters.pageSize;
    return PaymentListActions.paymentsLoaded({
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
    action: SearchActionPayload<PaymentSearchCriteria>
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
