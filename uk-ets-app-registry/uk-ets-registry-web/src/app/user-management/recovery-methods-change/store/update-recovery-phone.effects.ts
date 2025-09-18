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
import {
  selectNewRecoveryCountryCode,
  selectNewRecoveryPhoneNumber,
} from './recovery-methods-change.selectors';

@Injectable()
export class UpdateRecoveryPhoneEffects {
  navigateToUpdateRecoveryPhoneWizard$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_WIZARD),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE}`,
        })
      )
    )
  );

  requestUpdateRecoveryPhone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE),
      exhaustMap(({ request }) =>
        this.recoveryMethodsChangeService.updateRecoveryPhone(request).pipe(
          map((data) =>
            recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_SUCCESS({
              response: data,
            })
          ),
          catchError((error: HttpErrorResponse) =>
            of(
              recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_ERROR({
                error,
              })
            )
          )
        )
      )
    )
  );

  requestUpdateRecoveryPhoneSuccess$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_SUCCESS),
      mergeMap((action) => [
        recoveryMethodsActions.REQUEST_PHONE_SET_EXPIRED_AT({
          expiredAt: action.response.expiresInMillis,
        }),
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_VERIFICATION(),
      ])
    );
  });

  requestUpdateRecoveryPhoneError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_ERROR),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  navigateToUpdateRecoveryPhoneVerification$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_VERIFICATION
      ),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE_VERIFICATION}`,
        })
      )
    )
  );

  requestResendUpdateRecoveryPhoneSecurityCode$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE
      ),
      concatLatestFrom(() => [
        this.store.select(selectNewRecoveryCountryCode),
        this.store.select(selectNewRecoveryPhoneNumber),
      ]),
      concatMap(([_action, newRecoveryCountryCode, newRecoveryPhoneNumber]) =>
        this.recoveryMethodsChangeService
          .resendUpdateRecoveryPhoneSecurityCode({
            newRecoveryCountryCode,
            newRecoveryPhoneNumber,
          })
          .pipe(
            mergeMap((response) => [
              recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE_SUCCESS(
                {
                  response,
                }
              ),
              recoveryMethodsActions.REQUEST_PHONE_SET_EXPIRED_AT({
                expiredAt: response.expiresInMillis,
              }),
            ]),
            catchError((error: HttpErrorResponse) =>
              of(
                recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE_ERROR(
                  { error }
                )
              )
            )
          )
      )
    )
  );

  requestResendUpdateRecoveryPhoneSecurityCodeError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE_ERROR
      ),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  requestUpdateRecoveryPhoneVerification$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION),
      concatMap(({ request }) =>
        this.recoveryMethodsChangeService
          .updateRecoveryPhoneVerification(request)
          .pipe(
            map(() =>
              recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION_SUCCESS()
            ),
            catchError((error: HttpErrorResponse) =>
              of(
                recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION_ERROR(
                  { error }
                )
              )
            )
          )
      )
    )
  );

  requestUpdateRecoveryPhoneVerificationSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION_SUCCESS
      ),
      map(() =>
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_CONFIRMATION()
      )
    )
  );

  requestUpdateRecoveryPhoneVerificationError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION_ERROR
      ),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  navigateToUpdateRecoveryPhoneConfirmation$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_CONFIRMATION
      ),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.UPDATE_RECOVERY_PHONE_CONFIRMATION}`,
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
