import { Injectable } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, concatMap, exhaustMap, map } from 'rxjs/operators';
import { of } from 'rxjs';

import * as EmergencyOtpChangeActions from '../actions/emergency-otp-change.actions';
import { EmergencyOtpChangeService } from '@user-management/emergency-otp-change/services/emergency-otp-change.service';
import { canGoBack, errors, navigateTo } from '@shared/shared.action';
import { EmergencyOtpChangeRoutes } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';
import { HttpErrorResponse } from '@angular/common/http';
import { ApiErrorHandlingService } from '@shared/services';

@Injectable()
export class EmergencyOtpChangeEffects {
  submitEmail$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EmergencyOtpChangeActions.submitEmail),
      exhaustMap(action =>
        this.emergencyOtpChangeService.submitEmail(action.email).pipe(
          map(() => EmergencyOtpChangeActions.submitEmailSuccess()),
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
      ofType(EmergencyOtpChangeActions.submitEmailSuccess),
      concatMap(() => {
        return [
          canGoBack({
            goBackRoute: null
          }),
          navigateTo({
            route: `/${EmergencyOtpChangeRoutes.ROOT}/${EmergencyOtpChangeRoutes.EMAIL_SUBMITTED}`,
            extras: { skipLocationChange: true }
          })
        ];
      })
    );
  });

  createEmergencyOtpChangeTask$ = createEffect(() =>
    this.actions$.pipe(
      ofType(EmergencyOtpChangeActions.createEmergencyOtpChangeTask),
      exhaustMap(action =>
        this.emergencyOtpChangeService.createTask(action.token).pipe(
          map(taskResponse =>
            EmergencyOtpChangeActions.createEmergencyOtpChangeTaskSuccess({
              taskResponse
            })
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
    private emergencyOtpChangeService: EmergencyOtpChangeService,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
