import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, tap, withLatestFrom } from 'rxjs/operators';
import { select, Store } from '@ngrx/store';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AccountStatusApiService } from '../../service/account-status-api.service';
import { AccountStatusActions } from '../actions';
import { selectAccountStatusAction } from '../reducers';
import { errors } from '@shared/shared.action';
import { ErrorDetail } from '@shared/error-summary';
import {
  clearAccountStatus,
  fetchAllowedAccountStatusActionsSuccess,
  loadAllowedAccountStatusActions,
  navigateTo,
  setComment,
  submitAccountStatusAction,
  submitAccountStatusActionSuccess,
} from '../actions/account-status.actions';
import { of } from 'rxjs';
import { ApiErrorHandlingService } from '@shared/services';

@Injectable()
export class AccountStatusEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountStatusApiService: AccountStatusApiService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  fetchLoadAndShowAllowedAccountStatusActions$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountStatusActions.fetchLoadAndShowAllowedAccountStatusActions),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => {
        return this.accountStatusApiService
          .getAllowedAccountStatusActions(accountId)
          .pipe(
            map((result) =>
              fetchAllowedAccountStatusActionsSuccess({
                changeAccountStatusActionTypes: result,
              })
            ),
            catchError((httpError: HttpErrorResponse) => {
              this.handleHttpError(httpError, accountId);
              return [];
            })
          );
      })
    );
  });

  fetchAllowedAccountStatusActionsSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountStatusActions.fetchAllowedAccountStatusActionsSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([action, accountId]) => [
        loadAllowedAccountStatusActions({
          changeAccountStatusActionTypes: action.changeAccountStatusActionTypes,
        }),
        navigateTo({ route: `/account/${accountId}/status` }),
      ])
    );
  });

  setCommentAndSubmitAccountStatusAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountStatusActions.setCommentAndSubmitAccountStatusAction),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectAccountStatusAction))
      ),
      mergeMap(([action, accountId, accountStatusAction]) => [
        setComment({
          comment: action.comment,
        }),
        submitAccountStatusAction({
          accountId,
          newStatus: accountStatusAction.newStatus,
          comment: action.comment,
        }),
      ])
    );
  });

  submitAccountStatusAction$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountStatusActions.submitAccountStatusAction),
      mergeMap((action) => {
        return this.accountStatusApiService
          .changeAccountStatus({
            accountId: action.accountId,
            status: action.newStatus,
            comment: action.comment,
          })
          .pipe(
            map((result) =>
              submitAccountStatusActionSuccess({
                newStatus: result,
              })
            ),
            catchError((httpError: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpError.error
                  ),
                })
              );
            })
          );
      })
    );
  });

  submitAccountStatusActionSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountStatusActions.submitAccountStatusActionSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([action, accountId]) => [
        clearAccountStatus(),
        navigateTo({ route: `/account/${accountId}` }),
      ])
    );
  });

  cancelChangeAccountStatus$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AccountStatusActions.cancelAccountStatus),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => [
        clearAccountStatus(),
        navigateTo({ route: `/account/${accountId}` }),
      ])
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AccountStatusActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route]);
        })
      );
    },
    { dispatch: false }
  );

  handleHttpError(httpError: HttpErrorResponse, accountId: string) {
    let errorDetails: ErrorDetail[];
    if (httpError.status === 404) {
      errorDetails = [];
      errorDetails.push(
        new ErrorDetail(
          null,
          'There are no allowed account status action types for this account'
        )
      );
    }

    this.store.dispatch(errors({ errorSummary: { errors: errorDetails } }));
    this.router.navigate([`/account/${accountId}/status`]);
  }
}
