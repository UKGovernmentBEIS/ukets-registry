import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  catchError,
  exhaustMap,
  filter,
  map,
  mergeMap,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';
import { Router } from '@angular/router';
import { selectTransaction } from '@transaction-management/transaction-details/transaction-details.selector';
import { HttpErrorResponse } from '@angular/common/http';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import { TransactionManagementService } from '@transaction-management/service/transaction-management.service';
import { createReportRequestSuccess } from '@registry-web/reports/actions';

@Injectable()
export class TransactionDetailsEffects {
  constructor(
    private transactionManagementService: TransactionManagementService,
    private store: Store,
    private actions$: Actions,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private _router: Router
  ) {}

  fetchTransaction$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionDetailsActions.fetchTransaction),
      switchMap((action: { transactionIdentifier: string }) =>
        this.transactionManagementService
          .fetchOneTransaction(action.transactionIdentifier)
          .pipe(
            mergeMap((result) => [
              TransactionDetailsActions.fetchTransactionEventHistory({
                transactionIdentifier: action.transactionIdentifier,
              }),
              TransactionDetailsActions.loadTransaction({
                transactionDetails: result,
              }),
            ])
          )
      )
    )
  );

  fetchTransactionDetailsReport$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionDetailsActions.fetchTransactionDetailsReport),
      switchMap((action: { transactionIdentifier: string }) =>
        this.transactionManagementService
          .fetchTransactionDetailsReport(action.transactionIdentifier)
          .pipe(
            map((reportId) =>
              createReportRequestSuccess({ response: { reportId } })
            )
          )
      )
    );
  });

  fetchTransactionEventHistory$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionDetailsActions.fetchTransactionEventHistory),
      filter(
        (action: { transactionIdentifier: string }) =>
          !!action.transactionIdentifier
      ),
      mergeMap((action: { transactionIdentifier: string }) =>
        this.transactionManagementService
          .transactionEvents(action.transactionIdentifier)
          .pipe(
            map((results) =>
              TransactionDetailsActions.fetchTransactionEventHistorySuccess({
                results,
              })
            ),
            catchError((error) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              )
            )
          )
      )
    )
  );

  navigateToCancelTransaction$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TransactionDetailsActions.navigateToCancelTransaction),
        withLatestFrom(this.store.pipe(select(selectTransaction))),
        tap(([action, transaction]) =>
          this._router.navigate(
            [`/transaction-details/${transaction.identifier}/cancel`],
            {
              skipLocationChange: true,
              queryParams: {
                goBackRoute: `/transaction-details/${transaction.identifier}/`,
              },
            }
          )
        )
      ),
    { dispatch: false }
  );

  cancelTransaction$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TransactionDetailsActions.cancelTransaction),
      withLatestFrom(this.store.pipe(select(selectTransaction))),
      exhaustMap(([action, transactionDetails]) =>
        this.transactionManagementService
          .manuallyCancel(transactionDetails.identifier, action.comment)
          .pipe(
            map((taskCompleteResponse) =>
              TransactionDetailsActions.cancelTransactionSuccess({
                comment: transactionDetails.identifier,
              })
            ),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          )
      )
    )
  );

  navigateToCancelTransactionCompleted$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(TransactionDetailsActions.cancelTransactionSuccess),
        withLatestFrom(this.store.pipe(select(selectTransaction))),
        tap(([action, transaction]) =>
          this._router.navigate(
            [`/transaction-details/${transaction.identifier}/cancelled`],
            { skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );
}
