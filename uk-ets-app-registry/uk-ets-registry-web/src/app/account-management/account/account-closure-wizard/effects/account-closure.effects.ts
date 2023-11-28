import { Injectable } from '@angular/core';
import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { AccountClosureApiService } from '@account-management/account/account-closure-wizard/services';
import {
  cancelAccountClosureRequest,
  cancelClicked,
  fetchAccountAllocationForAccountClosure,
  fetchAccountAllocationForAccountClosureSuccess,
  fetchAccountPendingAllocationTaskExistsForAccountClosure,
  fetchAccountPendingAllocationTaskExistsForAccountClosureSuccess,
  navigateTo,
  setClosureComment,
  setClosureCommentSuccess,
  submitClosureRequest,
  submitClosureRequestSuccess,
} from '@account-management/account/account-closure-wizard/actions';
import {
  catchError,
  exhaustMap,
  map,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import {
  selectAccountDetails,
  selectAccountId,
} from '@account-management/account/account-details/account.selector';
import { getRouteFromArray } from '@shared/utils/router.utils';
import { AccountClosureWizardPathsModel } from '@account-management/account/account-closure-wizard/models';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { selectClosureDetails } from '@account-management/account/account-closure-wizard/reducers';
import { AccountAllocationService } from '@account-management/service/account-allocation.service';

@Injectable({ providedIn: 'root' })
export class AccountClosureEffects {
  constructor(
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private accountClosureApiService: AccountClosureApiService,
    private allocationService: AccountAllocationService
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountClosureWizardPathsModel.BASE_PATH,
            AccountClosureWizardPathsModel.CANCEL_CLOSURE_REQUEST,
          ]),
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelAccountClosureRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(cancelAccountClosureRequest),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        navigateTo({
          route: getRouteFromArray(['account', accountId]),
        })
      )
    );
  });

  setClosureComment$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setClosureComment),
      withLatestFrom(this.store.select(selectAccountDetails)),
      map(([action, accountDetails]) => {
        return setClosureCommentSuccess({
          closureComment: action.closureComment,
          accountDetails: accountDetails,
        });
      })
    );
  });

  proceedToFetchAllocation$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setClosureCommentSuccess),
      withLatestFrom(this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return fetchAccountAllocationForAccountClosure({
          accountId: accountId,
        });
      })
    );
  });

  proceedToFetchAccountPendingAllocationTaskExists$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(setClosureCommentSuccess),
      withLatestFrom(this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return fetchAccountPendingAllocationTaskExistsForAccountClosure({
          accountId: accountId,
        });
      })
    );
  });

  fetchAccountAllocationForAccountClosureOperations$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchAccountAllocationForAccountClosure),
      switchMap((action: { accountId: string }) => {
        return this.allocationService.fetchAllocation(action.accountId).pipe(
          map((result) => {
            return fetchAccountAllocationForAccountClosureSuccess({
              allocation: result,
            });
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

  fetchAccountPendingAllocationTaskForAccountClosureOperations$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(fetchAccountPendingAllocationTaskExistsForAccountClosure),
        switchMap((action: { accountId: string }) => {
          return this.allocationService
            .fetchPendingAllocationTaskExists(action.accountId)
            .pipe(
              map((result) => {
                return fetchAccountPendingAllocationTaskExistsForAccountClosureSuccess(
                  {
                    pendingAllocationTaskExists: result,
                  }
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
    }
  );

  navigateToCheckClosureDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(fetchAccountAllocationForAccountClosureSuccess),
      withLatestFrom(this.store.select(selectAccountId)),
      map(([, accountId]) => {
        return navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountClosureWizardPathsModel.BASE_PATH,
            AccountClosureWizardPathsModel.CHECK_CLOSURE_REQUEST,
          ]),
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  submitClosureRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitClosureRequest),
      withLatestFrom(this.store.select(selectClosureDetails)),
      exhaustMap(([action, accountDetails]) => {
        return this.accountClosureApiService
          .closureRequest(
            accountDetails.accountNumber,
            accountDetails,
            action.closureComment,
            action.allocationClassification,
            action.noActiveAR
          )
          .pipe(
            map((result) => {
              return submitClosureRequestSuccess({
                requestId: result,
              });
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

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(submitClosureRequestSuccess),
      withLatestFrom(this.store.select(selectAccountId)),
      map(([, accountId]) =>
        navigateTo({
          route: getRouteFromArray([
            'account',
            accountId,
            AccountClosureWizardPathsModel.BASE_PATH,
            AccountClosureWizardPathsModel.REQUEST_SUBMITTED,
          ]),
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });
}
