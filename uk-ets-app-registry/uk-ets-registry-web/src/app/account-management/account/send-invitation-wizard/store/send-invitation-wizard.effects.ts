import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { catchError, exhaustMap, map, tap } from 'rxjs/operators';
import { concatLatestFrom } from '@ngrx/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { getRouteFromArray } from '@shared/utils/router.utils';
import { ApiErrorHandlingService } from '@shared/services';
import { selectAccountId } from '@account-management/account/account-details/account.selector';
import { SendInvitationActions } from '@send-invitation-wizard/store/send-invitation-wizard.actions';
import {
  SEND_INVITATION_BASE_PATH,
  SendInvitationWizardPaths,
} from '@send-invitation-wizard/send-invitation-wizard.helpers';
import { AccountApiService } from '@registry-web/account-management/service/account-api.service';
import {
  selectSelectedMetsContacts,
  selectSelectedRegistryContacts,
} from '@send-invitation-wizard/store/send-invitation-wizard.selectors';

@Injectable()
export class SendInvitationWizardEffects {
  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(SendInvitationActions.NAVIGATE_TO),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([action, accountId]) => {
          this.router.navigate(
            [
              getRouteFromArray([
                'account',
                accountId,
                SEND_INVITATION_BASE_PATH,
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
      ofType(SendInvitationActions.CANCEL),
      map((action) =>
        SendInvitationActions.NAVIGATE_TO({
          step: SendInvitationWizardPaths.CANCEL_REQUEST,
          extras: { queryParams: { goBackRoute: action.route } },
        })
      )
    );
  });

  cancelRequest$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SendInvitationActions.CANCEL_REQUEST),
      map(() => SendInvitationActions.CLEAR_REQUEST())
    )
  );

  navigateOnCancel$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(SendInvitationActions.CANCEL_REQUEST),
        concatLatestFrom(() => this.store.select(selectAccountId)),
        tap(([, accountId]) => {
          this.router.navigate([`/account/${accountId}`]);
        })
      );
    },
    { dispatch: false }
  );

  completeSelectContacts$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SendInvitationActions.COMPLETE_SELECTED_CONTACTS),
      map(() =>
        SendInvitationActions.NAVIGATE_TO({
          step: SendInvitationWizardPaths.OVERVIEW,
        })
      )
    );
  });

  submitRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(SendInvitationActions.SUBMIT_REQUEST),
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
          this.accountService
            .sendInvitationToAccountContacts(accountIdentifier, {
              metsContacts: selectedMetsContacts,
              registryContacts: selectedRegistryContacts,
            })
            .pipe(
              map(() => SendInvitationActions.SUBMIT_REQUEST_SUCCESS()),
              catchError((error: HttpErrorResponse) => {
                return of(
                  errors({
                    errorSummary: this.apiErrorHandlingService.transform(
                      error.error
                    ),
                  })
                );
              })
            )
      )
    );
  });

  navigateToRequestSubmitted$ = createEffect(() =>
    this.actions$.pipe(
      ofType(SendInvitationActions.SUBMIT_REQUEST_SUCCESS),
      map(() =>
        SendInvitationActions.NAVIGATE_TO({
          step: SendInvitationWizardPaths.REQUEST_SUBMITTED,
        })
      )
    )
  );

  constructor(
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private accountService: AccountApiService
  ) {}
}
