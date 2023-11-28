import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { TrustedAccountListApiService } from '@trusted-account-list/services';
import { map, tap, withLatestFrom } from 'rxjs/operators';
import { TrustedAccountListActions } from '@trusted-account-list/actions';
import { Router } from '@angular/router';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { TrustedAccountListUpdateType } from '@trusted-account-list/model';

@Injectable()
export class TrustedAccountListNavigationEffects {
  constructor(
    private trustedAccountListService: TrustedAccountListApiService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateToAddOrRemoveAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.setRequestUpdateType),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) => {
        if (action.updateType === TrustedAccountListUpdateType.ADD) {
          return TrustedAccountListActions.navigateTo({
            route: `/account/${accountId}/trusted-account-list/add-account`,
            extras: {
              skipLocationChange: true
            }
          });
        } else {
          return TrustedAccountListActions.fetchTrustedAccountsToRemove({
            accountId
          });
        }
      })
    );
  });

  navigateToRemoveAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.fetchTrustedAccountsToRemoveSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        TrustedAccountListActions.navigateTo({
          route: `/account/${accountId}/trusted-account-list/remove-account`,
          extras: {
            skipLocationChange: true
          }
        })
      )
    );
  });

  navigateToCheckTheUpdateRequestAndSubmit$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        TrustedAccountListActions.selectTrustedAccountsForRemovalSuccess,
        TrustedAccountListActions.setUserDefinedTrustedAccountSuccess
      ),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        TrustedAccountListActions.navigateTo({
          route: `/account/${accountId}/trusted-account-list/check-update-request`,
          extras: {
            skipLocationChange: true
          }
        })
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        TrustedAccountListActions.navigateTo({
          route: `/account/${accountId}/trusted-account-list/request-submitted`,
          extras: {
            skipLocationChange: true
          }
        })
      )
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TrustedAccountListActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) =>
        TrustedAccountListActions.navigateTo({
          route: `/account/${accountId}/trusted-account-list/cancel`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true
          }
        })
      )
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TrustedAccountListActions.navigateTo),
        tap(action => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );
}
