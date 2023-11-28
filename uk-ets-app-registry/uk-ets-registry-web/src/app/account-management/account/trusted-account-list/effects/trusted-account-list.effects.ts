import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import {
  TrustedAccountListApiService,
  TrustedAccountListUiError,
} from '@trusted-account-list/services';
import {
  selectAccountsToUpdate,
  selectUpdateType,
  selectUserDefinedTrustedAccountFullIdentifier,
} from '@trusted-account-list/reducers';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { TrustedAccountListActions } from '@trusted-account-list/actions';
import { fetchTrustedAccountsToRemoveSuccess } from '../actions/trusted-account-list.actions';
import { errors } from '@shared/shared.action';
import { of } from 'rxjs';
import { TrustedAccountListUpdateType } from '@trusted-account-list/model';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';
import { selectGoBackRoute } from '@shared/shared.selector';
import { Router } from '@angular/router';
import { MenuItemEnum } from '@account-management/account/account-details/model';

@Injectable()
export class TrustedAccountListEffects {
  constructor(
    private trustedAccountListService: TrustedAccountListApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  fetchTrustedAccountsEligibleForRemoval$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.fetchTrustedAccountsToRemove),
      mergeMap((action) => {
        return this.trustedAccountListService
          .getApprovedOrActivatedTrustedAccounts(action.accountId)
          .pipe(
            map((data) => {
              if (data.length === 0) {
                return errors({
                  errorSummary: this.apiErrorHandlingService.buildUiError(
                    TrustedAccountListUiError.NO_ELIGIBLE_ACCOUNTS_FOR_REMOVAL
                  ),
                });
              } else {
                return fetchTrustedAccountsToRemoveSuccess({
                  trustedAccounts: data,
                });
              }
            })
          );
      })
    );
  });

  cancelTrustedAccountListUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.cancelTrustedAccountListUpdateRequest),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      mergeMap(([, accountId]) => [
        TrustedAccountListActions.clearTrustedAccountListUpdateRequest(),
        TrustedAccountListActions.navigateTo({
          route: `/account/${accountId}`,
        }),
      ])
    );
  });

  selectTrustedAccountsForRemoval$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.selectTrustedAccountsForRemoval),
      map((action) => {
        if (action.trustedAccountForRemoval.length === 0) {
          return errors({
            errorSummary: this.apiErrorHandlingService.buildUiError(
              TrustedAccountListUiError.NO_TRUSTED_ACCOUNT_SELECTED_FOR_REMOVAL
            ),
          });
        }
        return TrustedAccountListActions.selectTrustedAccountsForRemovalSuccess(
          { trustedAccountForRemoval: action.trustedAccountForRemoval }
        );
      })
    );
  });

  setUserDefinedTrustedAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.setUserDefinedTrustedAccount),
      withLatestFrom(
        this.store.pipe(select(selectUserDefinedTrustedAccountFullIdentifier))
      ),
      mergeMap(([, trustedAccountFullIdentifier]) => {
        return this.trustedAccountListService
          .validate(trustedAccountFullIdentifier)
          .pipe(
            map((validateAccount) => {
              return TrustedAccountListActions.setUserDefinedTrustedAccountSuccess(
                { kyotoAccountType: validateAccount.kyotoAccountType }
              );
            }),
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

  submitUpdateRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.submitUpdateRequest),
      withLatestFrom(
        this.store.select(selectUpdateType),
        this.store.select(selectAccountId),
        this.store.select(selectAccountsToUpdate)
      ),
      exhaustMap(([, updateType, accountId, trustedAccounts]) => {
        if (updateType === TrustedAccountListUpdateType.ADD) {
          return of(
            TrustedAccountListActions.submitUpdateRequestToAdd({
              trustedAccount: trustedAccounts[0],
              accountId,
            })
          );
        } else {
          return of(
            TrustedAccountListActions.submitUpdateRequestToRemove({
              trustedAccounts,
              accountId,
            })
          );
        }
      })
    );
  });

  submitUpdateRequestToAdd$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.submitUpdateRequestToAdd),
      exhaustMap((action) => {
        return this.trustedAccountListService
          .submitToAddTrustedAccount(action.trustedAccount, action.accountId)
          .pipe(
            map((data) => {
              return TrustedAccountListActions.submitUpdateRequestSuccess({
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

  submitUpdateRequestToRemove$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.submitUpdateRequestToRemove),
      exhaustMap((action) => {
        return this.trustedAccountListService
          .submitToRemoveTrustedAccount(
            action.trustedAccounts,
            action.accountId
          )
          .pipe(
            map((data) => {
              return TrustedAccountListActions.submitUpdateRequestSuccess({
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

  cancelPendingActivationRequested$ = createEffect(() =>
    this.actions$.pipe(
      ofType(TrustedAccountListActions.cancelPendingActivationRequested),
      withLatestFrom(this.store.select(selectGoBackRoute)),
      exhaustMap(([action, goBackRoute]) =>
        this.trustedAccountListService
          .cancelPendingActivationTrustedAccount(
            action.accountIdentifier,
            action.trustedAccountFullIdentifier
          )
          .pipe(
            map(() =>
              TrustedAccountListActions.cancelPendingActivationRequestedSuccess()
            ),
            tap(() =>
              this.router.navigate([goBackRoute], {
                state: {
                  selectedSideMenu: MenuItemEnum.TRUSTED_ACCOUNTS,
                },
              })
            ),
            catchError((error: HttpErrorResponse) =>
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
}
