import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { map, tap, withLatestFrom } from 'rxjs/operators';
import { TalTransactionRulesActions } from '@tal-transaction-rules/actions';
import { Router } from '@angular/router';
import {
  selectAccountId,
  selectIsOHAOrAOHA,
} from '@account-management/account/account-details/account.selector';

@Injectable()
export class TalTransactionRulesNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TalTransactionRulesActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  navigateToTransfersOutsideTal$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TalTransactionRulesActions.setNewRules),
      withLatestFrom(
        this.store.pipe(select(selectAccountId)),
        this.store.pipe(select(selectIsOHAOrAOHA))
      ),
      map(([action, accountId, isOHAOrAOHA]) => {
        if (action.newRules.rule1 != null) {
          return TalTransactionRulesActions.navigateTo({
            route: `/account/${accountId}/tal-transaction-rules/select-transfers-outside-list`,
            extras: {
              skipLocationChange: true,
            },
          });
        }
        if (action.newRules.rule2 != null && isOHAOrAOHA) {
          return TalTransactionRulesActions.navigateTo({
            route: `/account/${accountId}/tal-transaction-rules/single-person-surrender-excess-allocation`,
            extras: {
              skipLocationChange: true,
            },
          });
        } else {
          return TalTransactionRulesActions.navigateTo({
            route: `/account/${accountId}/tal-transaction-rules/check-update-request`,
            extras: {
              skipLocationChange: true,
            },
          });
        }
      })
    );
  });

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TalTransactionRulesActions.submitUpdateRequestSuccess),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([, accountId]) =>
        TalTransactionRulesActions.navigateTo({
          route: `/account/${accountId}/tal-transaction-rules/request-submitted`,
          extras: {
            skipLocationChange: true,
          },
        })
      )
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TalTransactionRulesActions.cancelClicked),
      withLatestFrom(this.store.pipe(select(selectAccountId))),
      map(([action, accountId]) =>
        TalTransactionRulesActions.navigateTo({
          route: `/account/${accountId}/tal-transaction-rules/cancel`,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        })
      )
    );
  });
}
