import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { concatLatestFrom } from '@ngrx/operators';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { errors, navigateTo } from '@registry-web/shared/shared.action';
import { catchError, concatMap, exhaustMap, map, mergeMap, of } from 'rxjs';
import { recoveryMethodsActions } from './recovery-methods-change.actions';
import { RecoveryMethodsChangeRoutePaths } from '../recovery-methods-change.models';
import { RecoveryMethodsChangeService } from '../recovery-methods-change.service';
import { HttpErrorResponse } from '@angular/common/http';
import { Store } from '@ngrx/store';
import { selectNewRecoveryEmailAddress } from './recovery-methods-change.selectors';

@Injectable()
export class UpdateRecoveryEmailEffects {
  navigateToUpdateRecoveryEmailWizard$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_WIZARD),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL}`,
        })
      )
    )
  );

  requestUpdateRecoveryEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL),
      exhaustMap(({ request }) =>
        this.recoveryMethodsChangeService.updateRecoveryEmail(request).pipe(
          map((response) =>
            recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_SUCCESS({
              response,
            })
          ),
          catchError((error: HttpErrorResponse) =>
            of(
              recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_ERROR({
                error,
              })
            )
          )
        )
      )
    )
  );

  requestUpdateRecoveryEmailSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_SUCCESS),
      mergeMap((action) => [
        recoveryMethodsActions.REQUEST_EMAIL_SET_EXPIRED_AT({
          expiredAt: action.response.expiresInMillis,
        }),
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_VERIFICATION(),
      ])
    );
  });

  requestUpdateRecoveryEmailError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_ERROR),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  navigateToUpdateRecoveryEmailVerification$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_VERIFICATION
      ),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL_VERIFICATION}`,
        })
      )
    )
  );

  requestResendUpdateRecoveryEmailSecurityCode$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE
      ),
      concatLatestFrom(() => [
        this.store.select(selectNewRecoveryEmailAddress),
      ]),
      concatMap(([_action, newRecoveryEmailAddress]) =>
        this.recoveryMethodsChangeService
          .resendUpdateRecoveryEmailSecurityCode({
            newRecoveryEmailAddress,
          })
          .pipe(
            mergeMap((response) => [
              recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE_SUCCESS(
                {
                  response,
                }
              ),
              recoveryMethodsActions.REQUEST_EMAIL_SET_EXPIRED_AT({
                expiredAt: response.expiresInMillis,
              }),
            ]),
            catchError((error: HttpErrorResponse) =>
              of(
                recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE_ERROR(
                  { error }
                )
              )
            )
          )
      )
    )
  );

  requestResendUpdateRecoveryEmailSecurityCodeError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_EMAIL_SECURITY_CODE_ERROR
      ),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  requestUpdateRecoveryEmailVerification$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION),
      concatMap(({ request }) =>
        this.recoveryMethodsChangeService
          .updateRecoveryEmailVerification(request)
          .pipe(
            map(() =>
              recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION_SUCCESS()
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION_ERROR(
                  { error }
                )
              )
            )
          )
      )
    )
  );

  requestUpdateRecoveryEmailVerificationSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION_SUCCESS
      ),
      map(() =>
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_CONFIRMATION()
      )
    )
  );

  requestUpdateRecoveryEmailVerificationError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION_ERROR
      ),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  navigateToUpdateRecoveryEmailConfirmation$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_CONFIRMATION
      ),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_EMAIL_CONFIRMATION}`,
        })
      )
    )
  );

  constructor(
    private actions$: Actions,
    private recoveryMethodsChangeService: RecoveryMethodsChangeService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private store: Store
  ) {}
}
