import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { ApiErrorHandlingService } from '@registry-web/shared/services';
import { errors, navigateTo } from '@registry-web/shared/shared.action';
import { catchError, concatMap, exhaustMap, map, of } from 'rxjs';
import { recoveryMethodsActions } from './recovery-methods-change.actions';
import { RecoveryMethodsChangeRoutePaths } from '../recovery-methods-change.models';
import { RecoveryMethodsChangeService } from '../recovery-methods-change.service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable()
export class RemoveRecoveryPhoneEffects {
  navigateToRemoveRecoveryPhoneWizard$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.NAVIGATE_TO_REMOVE_RECOVERY_PHONE_WIZARD),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.REMOVE_RECOVERY_PHONE}`,
        })
      )
    )
  );

  requestRemoveRecoveryPhone$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_PHONE),
      exhaustMap(({ request }) =>
        this.recoveryMethodsChangeService.removeRecoveryPhone(request).pipe(
          map(() =>
            recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_PHONE_SUCCESS()
          ),
          catchError((error: HttpErrorResponse) =>
            of(
              recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_PHONE_ERROR({
                error,
              })
            )
          )
        )
      )
    )
  );

  requestRemoveRecoveryPhoneSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_PHONE_SUCCESS),
      map(() =>
        recoveryMethodsActions.NAVIGATE_TO_REMOVE_RECOVERY_PHONE_CONFIRMATION()
      )
    )
  );

  requestRemoveRecoveryPhoneError$ = createEffect(() =>
    this.actions$.pipe(
      ofType(recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_PHONE_ERROR),
      map(({ error }) =>
        errors({
          errorSummary: this.apiErrorHandlingService.transform(error.error),
        })
      )
    )
  );

  navigateToRemoveRecoveryPhoneConfirmation$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        recoveryMethodsActions.NAVIGATE_TO_REMOVE_RECOVERY_PHONE_CONFIRMATION
      ),
      map(() =>
        navigateTo({
          route: `/${RecoveryMethodsChangeRoutePaths.BASE_PATH}/${RecoveryMethodsChangeRoutePaths.REMOVE_RECOVERY_PHONE_CONFIRMATION}`,
        })
      )
    )
  );

  constructor(
    private actions$: Actions,
    private recoveryMethodsChangeService: RecoveryMethodsChangeService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
