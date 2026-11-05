import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import {
  ChangeContactInvitationWizardActions,
  selectSelectedMetsContacts,
  selectSelectedRegistryContacts,
} from '@change-contact-invitation-wizard/store';
import { catchError, exhaustMap, map, of, tap } from 'rxjs';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { ChangeContactInvitationService } from '@change-contact-invitation-wizard/service';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { concatLatestFrom } from '@ngrx/operators';
import { getRouteFromArray } from '@shared/utils/router.utils';
import { CHANGE_CONTACT_INVITATION_BASE_PATH } from '@change-contact-invitation-wizard/change-contact-invitation-wizard-routing.module';
import { HttpErrorResponse } from '@angular/common/http';
import { errors } from '@shared/shared.action';

@Injectable()
export class ChangeContactInvitationWizardEffects {
  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private changeContactInvitationService: ChangeContactInvitationService
  ) {}

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeContactInvitationWizardActions.cancel),
      map((action) =>
        ChangeContactInvitationWizardActions.navigateTo({
          step: 'cancel-request',
          extras: { queryParams: { goBackRoute: action.route } },
        })
      )
    );
  });

  completeSelectContacts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeContactInvitationWizardActions.completeSelectedContacts),
      map(() =>
        ChangeContactInvitationWizardActions.navigateTo({
          step: 'overview',
        })
      )
    );
  });

  submitRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(ChangeContactInvitationWizardActions.submitRequest),
      concatLatestFrom(() => [
        this.store.select(selectAccountId),
        this.store.select(selectSelectedMetsContacts),
        this.store.select(selectSelectedRegistryContacts),
      ]),
      exhaustMap(
        ([
          ,
          accountIdentifier,
          selectedMetsContacts,
          selectedRegistryContacts,
        ]) =>
          this.changeContactInvitationService
            .changeInvitationStatusOfAccountContacts(accountIdentifier, {
              metsContacts: selectedMetsContacts,
              registryContacts: selectedRegistryContacts,
            })
            .pipe(
              map(() =>
                ChangeContactInvitationWizardActions.submitRequestSuccess()
              ),
              catchError((error: HttpErrorResponse) => {
                return of(
                  errors({
                    errorSummary: {
                      errors: [
                        {
                          componentId: '',
                          errorMessage: error.error
                            ? JSON.stringify(error?.error) ||
                              'An error occurred while submitting the invitation request.'
                            : 'An error occurred while submitting the invitation request.',
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

  clearRequest$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ChangeContactInvitationWizardActions.cancelRequest),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([, accountId]) => {
          this.router.navigate([`/account/${accountId}`]);
        })
      );
    },
    { dispatch: false }
  );

  navigateToRequestSubmitted$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ChangeContactInvitationWizardActions.submitRequestSuccess),
      map(() =>
        ChangeContactInvitationWizardActions.navigateTo({
          step: 'request-submitted',
        })
      )
    )
  );

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ChangeContactInvitationWizardActions.navigateTo),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([action, accountId]) => {
          this.router.navigate(
            [
              getRouteFromArray([
                'account',
                accountId,
                CHANGE_CONTACT_INVITATION_BASE_PATH,
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
}
