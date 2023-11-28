import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  switchMap,
  withLatestFrom,
} from 'rxjs/operators';
import { setCurrentRulesSuccess } from '../actions/tal-transaction-rules.actions';
import { errors } from '@shared/shared.action';
import { of } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';
import { TalTransactionRulesApiService } from '@tal-transaction-rules/services';
import { selectUpdatedRules } from '@tal-transaction-rules/reducers';
import { TalTransactionRulesActions } from '@tal-transaction-rules/actions';
import { selectAccountId } from '@account-management/account/account-details/account.selector';

@Injectable()
export class TalTransactionRulesEffects {
  constructor(
    private talTransactionRulesService: TalTransactionRulesApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store
  ) {}

  fetchCurrentTalTransactionRules$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TalTransactionRulesActions.fetchCurrentRules),
      switchMap((action) => {
        return this.talTransactionRulesService
          .getCurrentTalTransactionRules(action.accountId)
          .pipe(
            map((data) =>
              setCurrentRulesSuccess({
                currentRules: data,
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
          );
      })
    );
  });

  submitTalTransactionRulesUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TalTransactionRulesActions.submitUpdateRequest),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectUpdatedRules))
      ),
      exhaustMap(([, accountId, rules]) => {
        return this.talTransactionRulesService
          .updateCurrentTalTransactionRules(accountId, rules)
          .pipe(
            map((data) => {
              return TalTransactionRulesActions.submitUpdateRequestSuccess({
                requestId: data,
              });
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
  });

  cancelTalTransactionRulesUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TalTransactionRulesActions.cancelTalTransactionRulesUpdateRequest),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => [
        TalTransactionRulesActions.clearTalTransactionRulesUpdateRequest(),
        TalTransactionRulesActions.navigateTo({
          route: `/account/${accountId}`,
        }),
      ])
    );
  });
}
