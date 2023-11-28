import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, mergeMap, withLatestFrom, tap } from 'rxjs/operators';
import { of, throwError } from 'rxjs';
import { errors } from '@shared/shared.action';
import { Router } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';
import { ForgotPasswordActions } from '../actions';
import { selectResetPasswordToken } from '../reducers';
import { ForgotPasswordApiService } from '../../services/forgot-password-api.service';

@Injectable()
export class ForgotPasswordEffects {
  constructor(
    private forgotPasswordApiService: ForgotPasswordApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private router: Router,
    private store: Store
  ) {}

  requestResetPasswordEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ForgotPasswordActions.requestResetPasswordEmail),
      mergeMap(action =>
        this.forgotPasswordApiService
          .requestResetPasswordEmail(action.email)
          .pipe(
            map(result =>
              ForgotPasswordActions.requestResetPasswordEmailSuccess()
            ),
            catchError((httpErrorResponse: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpErrorResponse.error
                  )
                })
              )
            )
          )
      )
    )
  );

  resetPassword$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ForgotPasswordActions.resetPassword),
      withLatestFrom(this.store.pipe(select(selectResetPasswordToken))),
      mergeMap(([action, token]) =>
        this.forgotPasswordApiService
          .resetPassword({
            token,
            otp: action.otp,
            newPasswd: action.newPasswd
          })
          .pipe(
            map(response => {
              if (response.success) {
                return ForgotPasswordActions.resetPasswordSuccess(response);
              } else {
                return ForgotPasswordActions.validateTokenFailure();
              }
            }),
            catchError((httpErrorResponse: HttpErrorResponse) =>
              of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    httpErrorResponse.error
                  )
                })
              )
            )
          )
      )
    )
  );

  validateToken$ = createEffect(() =>
    this.actions$.pipe(
      ofType(ForgotPasswordActions.validateToken),
      mergeMap((action: { token: string }) =>
        this.forgotPasswordApiService.validateToken(action.token).pipe(
          map(result => {
            if (result.success) {
              return ForgotPasswordActions.validateTokenSuccess({
                token: action.token
              });
            } else {
              return ForgotPasswordActions.validateTokenFailure();
            }
          }),
          catchError((httpErrorResponse: HttpErrorResponse) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  httpErrorResponse.error
                )
              })
            )
          )
        )
      )
    )
  );

  navigateToEmailSent$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ForgotPasswordActions.requestResetPasswordEmailSuccess),
        tap(action => {
          this.router.navigate(['/forgot-password/email-sent'], {
            skipLocationChange: true
          });
        })
      );
    },
    { dispatch: false }
  );

  navigateToEmailLinkExpired$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ForgotPasswordActions.validateTokenFailure),
        tap(action => {
          this.router.navigate(['/forgot-password/email-link-expired'], {
            skipLocationChange: true
          });
        })
      );
    },
    { dispatch: false }
  );

  navigateToResetPasswordSuccess$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(ForgotPasswordActions.resetPasswordSuccess),
        tap(action => {
          this.router.navigate(['/forgot-password/reset-password-success'], {
            skipLocationChange: true
          });
        })
      );
    },
    { dispatch: false }
  );
}
