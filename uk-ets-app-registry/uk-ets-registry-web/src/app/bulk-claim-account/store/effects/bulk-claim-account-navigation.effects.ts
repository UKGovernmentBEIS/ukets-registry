import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { BulkClaimAccountNavigationActions } from '@bulk-claim-account/store/actions/bulk-claim-account-navigation.actions';
import { tap } from 'rxjs';

@Injectable()
export class BulkClaimAccountNavigationEffects {
  constructor(
    private actions$: Actions,
    private _router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  navigateToBulkAccountClaimCheckRequestAndSubmit$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(
          BulkClaimAccountNavigationActions.navigateToBulkAccountClaimCheckRequestAndSubmit
        ),
        tap(() =>
          this._router.navigate(
            [`/bulk-claim-account/check-request-and-submit`],
            {
              relativeTo: this.activatedRoute,
              skipLocationChange: true,
            }
          )
        )
      ),
    { dispatch: false }
  );

  navigateToBulkAccountClaimCancel$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(
          BulkClaimAccountNavigationActions.navigateToBulkAccountClaimCancel
        ),
        tap(() =>
          this._router.navigate([`/bulk-claim-account/cancel`], {
            relativeTo: this.activatedRoute,
            skipLocationChange: true,
          })
        )
      ),
    { dispatch: false }
  );

  navigateToBulkAccountClaimSubmitted$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(
          BulkClaimAccountNavigationActions.navigateToBulkAccountClaimSubmitted
        ),
        tap(() =>
          this._router.navigate([`/bulk-claim-account/request-submitted`], {
            relativeTo: this.activatedRoute,
            skipLocationChange: true,
          })
        )
      ),
    { dispatch: false }
  );

  navigateToBulkAccountClaim$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(BulkClaimAccountNavigationActions.navigateToBulkAccountClaim),
        tap(() =>
          this._router.navigate([`/bulk-claim-account`], {
            relativeTo: this.activatedRoute,
            skipLocationChange: true,
          })
        )
      ),
    { dispatch: false }
  );
}
