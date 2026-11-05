import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { AutoClaimWizardActions } from '@auto-claim-wizard/store';
import { getRouteFromArray } from '@shared/utils/router.utils';
import { catchError, exhaustMap, map, of, tap } from 'rxjs';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { AUTO_CLAIM_BASE_PATH } from '@auto-claim-wizard/auto-claim-wizard-routing.module';
import { errors, navigateTo } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { AutoClaimChangeService } from '@auto-claim-wizard/service';
import { MenuItemEnum } from '@account-management/account/account-details/model/account-side-menu.model';
import { submitAutoSendClaimInvitationUpdatedSuccess } from '@account-management/account/account-details/account.actions';

@Injectable()
export class AutoClaimWizardEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private autoClaimChangeService: AutoClaimChangeService
  ) {}

  submitRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AutoClaimWizardActions.submitRequest),
      concatLatestFrom(() => [this.store.select(selectAccountId)]),
      exhaustMap(([, accountIdentifier]) =>
        this.autoClaimChangeService.submitRequest(accountIdentifier).pipe(
          map(() => submitAutoSendClaimInvitationUpdatedSuccess()),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: {
                  errors: [
                    {
                      componentId: '',
                      errorMessage:
                        JSON.parse(error.error)?.errorDetails[0]?.message ||
                        'An error occurred while submitting the change auto claim request.',
                    },
                  ],
                },
              })
            );
          })
        )
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() =>
    this.actions$.pipe(
      ofType(submitAutoSendClaimInvitationUpdatedSuccess),
      concatLatestFrom(() => this.store.select(selectAccountId)),
      map(([_, accountId]) =>
        navigateTo({
          route: `/account/${accountId}`,
          extras: {
            queryParams: {
              selectedSideMenu: MenuItemEnum.CONTACTS,
            },
          },
        })
      )
    )
  );

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AutoClaimWizardActions.navigateTo),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([action, accountId]) => {
          this.router.navigate(
            [
              getRouteFromArray([
                'account',
                accountId,
                AUTO_CLAIM_BASE_PATH,
                action.step,
              ]),
            ],
            {
              skipLocationChange: true,
              ...(action.extras || {}),
            }
          );
        })
      );
    },
    { dispatch: false }
  );

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(AutoClaimWizardActions.cancelAutoClaimWizard),
      map((action) =>
        AutoClaimWizardActions.navigateTo({
          step: 'cancel-auto-claim',
          extras: { queryParams: { goBackRoute: action.route } },
        })
      )
    );
  });

  clearRequest$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(AutoClaimWizardActions.cancelRequest),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([, accountId]) => {
          this.router.navigate([`/account/${accountId}`]);
        })
      );
    },
    { dispatch: false }
  );
}
