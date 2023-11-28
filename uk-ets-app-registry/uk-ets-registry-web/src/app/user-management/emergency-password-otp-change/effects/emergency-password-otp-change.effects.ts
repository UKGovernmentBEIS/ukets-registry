import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, exhaustMap, map } from 'rxjs/operators';
import { of } from 'rxjs';

import * as EmergencyPasswordOtpChangeActions from '../actions/emergency-password-otp-change.actions';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';
import { EmergencyPasswordOtpChangeService } from '@user-management/emergency-password-otp-change/services';
import { EmergencyPasswordOtpChangeRoutes } from '@user-management/emergency-password-otp-change/model';

@Injectable()
export class EmergencyPasswordOtpChangeEffects {
  submitEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EmergencyPasswordOtpChangeActions.submitEmail),
      exhaustMap(action =>
        this.emergencyPasswordOtpChangeService.submitEmail(action.email).pipe(
          map(() => EmergencyPasswordOtpChangeActions.submitEmailSuccess()),
          catchError((error: HttpErrorResponse) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                )
              })
            )
          )
        )
      )
    )
  );

  navigateToEmailSubmittedPage$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(EmergencyPasswordOtpChangeActions.submitEmailSuccess),
      concatMap(() => {
        return [
          canGoBack({
            goBackRoute: null
          }),
          navigateTo({
            route: `/${EmergencyPasswordOtpChangeRoutes.ROOT}/${EmergencyPasswordOtpChangeRoutes.EMAIL_SUBMITTED}`,
            extras: { skipLocationChange: true }
          })
        ];
      })
    );
  });

  createEmergencyPasswordOtpChangeTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(
        EmergencyPasswordOtpChangeActions.createEmergencyPasswordOtpChangeTask
      ),
      exhaustMap(action =>
        this.emergencyPasswordOtpChangeService.createTask(action.token).pipe(
          map(taskResponse =>
            EmergencyPasswordOtpChangeActions.createEmergencyPasswordOtpChangeTaskSuccess(
              {
                taskResponse
              }
            )
          )
        )
      ),
      catchError((error: HttpErrorResponse) =>
        of(
          errors({
            errorSummary: this.apiErrorHandlingService.transform(error.error)
          })
        )
      )
    )
  );
  constructor(
    private actions$: Actions,
    private emergencyPasswordOtpChangeService: EmergencyPasswordOtpChangeService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
