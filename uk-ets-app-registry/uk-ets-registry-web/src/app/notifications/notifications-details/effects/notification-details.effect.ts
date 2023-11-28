import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { catchError, map, mergeMap, tap } from 'rxjs/operators';
import { NotificationsDetailsActions } from '@notifications/notifications-details/actions';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { errors } from '@shared/shared.action';
import { NotificationApiService } from '@shared/components/notifications/services';

@Injectable()
export class NotificationDetailsEffect {
  constructor(
    private notificationApiService: NotificationApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router
  ) {}

  navigateTo$ = createEffect(
    () => {
      return this.actions$.pipe(
        ofType(NotificationsDetailsActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  getNotificationInfo$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsDetailsActions.setNotificationsDetails),
      mergeMap((action) => {
        return this.notificationApiService
          .getNotificationInstance(action.notificationId)
          .pipe(
            map((data) => {
              return NotificationsDetailsActions.setNotificationsDetailsSuccess(
                {
                  notificationDetails: data,
                }
              );
            }),
            catchError((error: HttpErrorResponse) => {
              return of(
                errors({
                  errorSummary: this.apiErrorHandlingService.transform(
                    error.error
                  ),
                })
              );
            })
          );
      })
    );
  });
}
