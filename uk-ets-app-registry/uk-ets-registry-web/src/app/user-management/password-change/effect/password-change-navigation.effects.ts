import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, map, withLatestFrom } from 'rxjs/operators';
import { errors, navigateTo } from '@shared/shared.action';
import {
  navigateToPasswordChangeWizard,
  navigateToConfirmationPage,
  requestPasswordChangeAction,
  successChangePasswordPage
} from '@password-change/action/password-change.actions';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { ApiErrorHandlingService } from '@shared/services';
import { RequestPasswordChangeService } from '@password-change/service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { PasswordChangeRoutePaths } from '@password-change/model';
import { select, Store } from '@ngrx/store';
import { selectEmail } from '@password-change/reducer';

@Injectable()
export class PasswordChangeNavigationEffects {
  constructor(
    private actions$: Actions,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private requestPasswordChangeService: RequestPasswordChangeService,
    private keycloakApi: AuthApiService,
    private store: Store
  ) {}

  /**
   * Navigate to the first page of the Change password wizard
   * in order to set the new password.
   */
  navigateToPasswordChangeWizard$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToPasswordChangeWizard),
      map(() =>
        navigateTo({
          route: `/${PasswordChangeRoutePaths.BASE_PATH}`
        })
      )
    );
  });

  /**
   * Submit the request for changing the Password
   */
  changePassword$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(requestPasswordChangeAction),
      concatMap(action =>
        this.requestPasswordChangeService.changePassword(action.request).pipe(
          map(() => navigateToConfirmationPage()),
          catchError((error: HttpErrorResponse) => {
            return of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            );
          })
        )
      )
    );
  });

  /**
   * After successful submitting the request the application automatically
   * sign out the user and navigates to the confirmation page.
   */
  navigateToConfirmationPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(navigateToConfirmationPage),
      withLatestFrom(this.store.pipe(select(selectEmail))),
      concatMap(([action, email]) => {
        return this.keycloakApi
          .logout(
            location.origin +
              `/${PasswordChangeRoutePaths.BASE_PATH}/${PasswordChangeRoutePaths.CHANGE_PASSWORD_CONFIRMATION_PATH}/` +
              email
          )
          .pipe(
            map(() => successChangePasswordPage()),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  )
                })
              );
            })
          );
      })
    );
  });
}
