import { Injectable } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import {
  BulkClaimAccountActions,
  BulkClaimAccountNavigationActions,
} from '@bulk-claim-account/store/actions';
import { catchError, map, mergeMap, switchMap } from 'rxjs';
import { BulkClaimAccountService } from '@bulk-claim-account/services';

@Injectable()
export class BulkClaimAccountEffects {
  constructor(
    private bulkClaimAccountService: BulkClaimAccountService,
    private actions$: Actions,
    private _router: Router,
    private activatedRoute: ActivatedRoute,
    private store: Store
  ) {}

  countEligibleBulkClaimAccounts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(BulkClaimAccountActions.countEligibleBulkClaimAccounts),
      mergeMap(() => {
        return this.bulkClaimAccountService
          .countEligibleBulkClaimAccounts()
          .pipe(
            switchMap((numberOfAffectedAccounts) => [
              BulkClaimAccountActions.countEligibleBulkClaimAccountsSuccess({
                numberOfAffectedAccounts,
              }),
              BulkClaimAccountNavigationActions.navigateToBulkAccountClaimCheckRequestAndSubmit(),
            ]),
            catchError((httpError) => {
              return [
                BulkClaimAccountActions.countEligibleBulkClaimAccountsError(
                  httpError.error ||
                    'An error occurred while counting eligible bulk claim accounts. Please try again later.'
                ),
              ];
            })
          );
      })
    );
  });

  sendBulkClaimAccount$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(BulkClaimAccountActions.sendBulkClaimAccount),
      mergeMap(() => {
        return this.bulkClaimAccountService.sendBulkAccountClaims().pipe(
          switchMap(() => [
            BulkClaimAccountActions.sendBulkClaimAccountSuccess(),
            BulkClaimAccountNavigationActions.navigateToBulkAccountClaimSubmitted(),
          ]),
          catchError((httpError) => {
            console.log('Error sending bulk claim account:', httpError);
            return [
              BulkClaimAccountActions.sendBulkClaimAccountError(
                httpError.error ||
                  'An error occurred while sending bulk claim accounts. Please try again later.'
              ),
            ];
          })
        );
      })
    );
  });

  cancelBulkClaimAccountRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(BulkClaimAccountActions.cancelBulkClaimAccountRequest),
      mergeMap(() => [
        BulkClaimAccountActions.clearBulkClaimAccountRequest(),
        BulkClaimAccountNavigationActions.navigateToBulkAccountClaim(),
      ])
    );
  });
}
