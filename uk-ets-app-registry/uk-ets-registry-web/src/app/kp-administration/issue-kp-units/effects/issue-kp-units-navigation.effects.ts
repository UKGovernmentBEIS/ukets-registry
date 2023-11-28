import { Injectable } from '@angular/core';
import { IssueKpUnitsService } from '@issue-kp-units/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ActivatedRoute, Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { IssueKpUnitsNavigationActions } from '@issue-kp-units/actions';
import { IssueKpUnitsRoutePathsModel } from '@issue-kp-units/model';

@Injectable()
export class IssueKpUnitsNavigationEffects {
  constructor(
    private issueKpUnitsService: IssueKpUnitsService,
    private actions$: Actions,
    private _router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  // TODO : See  UKETS-1789 regarding relative route navigation
  navigateToSelectCommitmentPeriod$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(IssueKpUnitsNavigationActions.navigateToSelectCommitmentPeriod),
        tap(() =>
          this._router.navigate(
            [
              `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['select-commitment-period']}`,
            ],
            { relativeTo: this.activatedRoute, skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );

  navigateToSelectUnitTypeAndQuantity$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(
          IssueKpUnitsNavigationActions.navigateToSelectUnitTypeAndQuantity
        ),
        tap(() =>
          this._router.navigate(
            [
              `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['select-units']}`,
            ],
            { relativeTo: this.activatedRoute, skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );

  navigateToSetTransactionReference$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(IssueKpUnitsNavigationActions.navigateToSetTransactionReference),
        tap(() =>
          this._router.navigate(
            [
              `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['set-transaction-reference']}`,
            ],
            { relativeTo: this.activatedRoute, skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );

  // transaction is now ready for signing so go to screen 3 - check request and sign
  navigateToCheckAndSign$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(IssueKpUnitsNavigationActions.navigateToCheckTransactionAndSign),
        tap(() =>
          this._router.navigate(
            [
              `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['check-request-and-sign']}`,
            ],
            { relativeTo: this.activatedRoute, skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );

  navigateToProposalSubmitted$ = createEffect(
    () =>
      this.actions$.pipe(
        ofType(IssueKpUnitsNavigationActions.navigateToProposalSubmitted),
        tap(() =>
          this._router.navigate(
            [
              `/kpadministration/issuekpunits/${IssueKpUnitsRoutePathsModel['proposal-submitted']}`,
            ],
            { relativeTo: this.activatedRoute, skipLocationChange: true }
          )
        )
      ),
    { dispatch: false }
  );
}
