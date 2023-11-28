import { Injectable } from '@angular/core';
import { ApiErrorHandlingService } from '@shared/services';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import {
  catchError,
  exhaustMap,
  map,
  mergeMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { NotificationsWizardActions } from '@notifications/notifications-wizard/actions';
import { NotificationsWizardPathsModel } from '@notifications/notifications-wizard/model';
import { HttpErrorResponse } from '@angular/common/http';
import { of } from 'rxjs';
import { canGoBack, errors } from '@shared/shared.action';
import { NotificationApiService } from '@shared/components/notifications/services/notification-api.service';
import { selectNotificationId } from '@notifications/notifications-wizard/reducers';
import { setGoBackPath } from '@notifications/notifications-wizard/actions/notifications-wizard.actions';

@Injectable()
export class NotificationsWizardEffect {
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
        ofType(NotificationsWizardActions.navigateTo),
        tap((action) => {
          this.router.navigate([action.route], action.extras);
        })
      );
    },
    { dispatch: false }
  );

  notificationGoBack$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.setGoBackPath),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        let path = `/notifications/${action.path}`;
        if (notificationId) {
          path = `/notifications/${notificationId}/${action.path}`;
        }
        return canGoBack({
          goBackRoute: path,
          extras: { skipLocationChange: action.skipLocationChange },
        });
      })
    );
  });

  goBackToSelectTypeOrNotificationDetails$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(
        NotificationsWizardActions.goBackToSelectTypeOrNotificationDetails
      ),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        if (notificationId) {
          return canGoBack({
            goBackRoute: `/notifications/${notificationId}`,
            extras: { skipLocationChange: false },
          });
        } else {
          return setGoBackPath({
            path: `${NotificationsWizardPathsModel.BASE_PATH}`,
            skipLocationChange: false,
          });
        }
      })
    );
  });

  cancelClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.cancelClicked),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        let path = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CANCEL_UPDATE_REQUEST}`;
        if (notificationId) {
          path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CANCEL_UPDATE_REQUEST}`;
        }
        return NotificationsWizardActions.navigateTo({
          route: path,
          extras: {
            queryParams: { goBackRoute: action.route },
            skipLocationChange: true,
          },
        });
      })
    );
  });

  cancelNotificationRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.cancelNotificationRequest),
      mergeMap(() => [
        NotificationsWizardActions.clearNotificationsRequest(),
        NotificationsWizardActions.navigateTo({
          route: `/notifications`,
        }),
      ])
    );
  });

  retrieveNotificationDefinition$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.setRequestNotificationType),
      exhaustMap((action) => {
        return this.notificationApiService
          .getNotificationDefinition(action.notificationType)
          .pipe(
            map((data) => {
              return NotificationsWizardActions.setNotificationDefinition({
                notificationDefinition: {
                  shortText: data.shortText,
                  longText: data.longText,
                  tentativeRecipients: data.tentativeRecipients,
                },
              });
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

  navigateToScheduledDate$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.setNotificationDefinition),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        let path = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.SCHEDULED_DATE}`;
        if (notificationId) {
          path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.SCHEDULED_DATE}`;
        }
        return NotificationsWizardActions.navigateTo({
          route: path,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToContent$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.setNotificationsScheduledDate),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        let path = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CONTENT}`;
        if (notificationId) {
          path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CONTENT}`;
        }
        return NotificationsWizardActions.navigateTo({
          route: path,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  navigateToCheckAndSubmit$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.setNotificationsContent),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        let path = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CHECK_AND_SUBMIT}`;
        if (notificationId) {
          path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CHECK_AND_SUBMIT}`;
        }
        return NotificationsWizardActions.navigateTo({
          route: path,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });

  submitRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.submitRequest),
      exhaustMap((action) => {
        return this.notificationApiService
          .submitRequest(action.notification, action.notificationId)
          .pipe(
            map((data) => {
              return NotificationsWizardActions.submitRequestSuccess({
                requestId: data,
              });
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

  getNotificationInfo$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.setNotificationsInfo),
      mergeMap((action) => {
        return this.notificationApiService
          .getNotificationInstance(action.notificationId)
          .pipe(
            map((data) => {
              return NotificationsWizardActions.setNotificationsInfoSuccess({
                notification: data,
              });
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

  navigateToRequestSubmitted$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.submitRequestSuccess),
      withLatestFrom(this.store.select(selectNotificationId)),
      map(([action, notificationId]) => {
        let path = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.REQUEST_SUBMITTED}`;
        if (notificationId) {
          path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.REQUEST_SUBMITTED}`;
        }
        return NotificationsWizardActions.navigateTo({
          route: path,
          extras: {
            skipLocationChange: true,
          },
        });
      })
    );
  });
}
