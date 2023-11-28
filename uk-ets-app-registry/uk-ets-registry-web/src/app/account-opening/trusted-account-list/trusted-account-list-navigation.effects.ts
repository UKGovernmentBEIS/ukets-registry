import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { select, Store } from '@ngrx/store';
import { map, tap, withLatestFrom } from 'rxjs/operators';
import { Router } from '@angular/router';
import * as TransactionRulesActions from '@account-opening/trusted-account-list/trusted-account-list.actions';
import { selectIsOHAOrAOHA } from '@account-opening/account-opening.selector';
import { TrustedAccountListWizardRoutes } from '@account-opening/trusted-account-list/trusted-account-list-wizard-properties';

@Injectable()
export class TransactionRulesNavigationEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateToNextPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(TransactionRulesActions.navigateToNextPage),
      withLatestFrom(this.store.pipe(select(selectIsOHAOrAOHA))),
      map(([, isOHAOrAOHA]) => {
        if (isOHAOrAOHA) {
          return TransactionRulesActions.navigateTo({
            route:
              TrustedAccountListWizardRoutes.SINGLE_PERSON_SURRENDER_EXCESS_ALLOCATION,
            extras: {
              skipLocationChange: true,
            },
          });
        } else {
          return TransactionRulesActions.navigateTo({
            route: TrustedAccountListWizardRoutes.OVERVIEW,
            extras: {
              skipLocationChange: true,
            },
          });
        }
      })
    );
  });

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(TransactionRulesActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );
}
