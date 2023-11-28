import * as NotificationsActionsTypes from '@registry-web/dashboard/notifications/actions/notifications.actions';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { NotificationsService } from '@registry-web/dashboard/notifications/service/notifications.service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { errors } from '@shared/shared.action';
import { ApiErrorHandlingService } from '@shared/services';

@Injectable({
  providedIn: 'root',
})
export class NotificationsEffect {
  getNotifications$ = createEffect(() =>
    this.actions$.pipe(
      ofType(NotificationsActionsTypes.retrieveNotifications),
      switchMap(() => {
        return this.notificationsService.getNotifications().pipe(
          map((notifications) => {
            return NotificationsActionsTypes.retrieveNotificationsSuccess({
              notifications,
            });
          }),
          catchError((error) =>
            of(
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            )
          )
        );
      })
    )
  );

  constructor(
    private notificationsService: NotificationsService,
    private authApiService: AuthApiService,
    private actions$: Actions,
    private store: Store,
    private apiErrorHandlingService: ApiErrorHandlingService
  ) {}
}
