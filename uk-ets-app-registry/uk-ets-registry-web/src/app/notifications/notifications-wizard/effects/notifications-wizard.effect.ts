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
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { NotificationsWizardActions } from '@notifications/notifications-wizard/actions';
import {
  NotificationsWizardPathsModel,
  NotificationType,
} from '@notifications/notifications-wizard/model';
import { HttpErrorResponse, HttpEvent } from '@angular/common/http';
import { of } from 'rxjs';
import { canGoBack, errors } from '@shared/shared.action';
import { NotificationApiService } from '@shared/components/notifications/services/notification-api.service';
import {
  selectNotificationId,
  selectNotificationType,
  selectRecipientsEmailsFile,
} from '@notifications/notifications-wizard/reducers';
import {
  requestUploadSelectedRecipientsEmailFile,
  setGoBackPath,
  setNotificationDefinition,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import {
  processSelectedFileError,
  uploadRecipientsEmailFileSuccess,
  uploadSelectedRecipientsEmailFile,
} from '@shared/file/actions/file-upload-api.actions';
import { UploadStatus } from '@shared/model/file';
import { SharedEffects } from '@shared/shared.effect';
import { selectExistingRecipientsEmailsFile } from '@notifications/notifications-details/reducer/notification-details.selector';

@Injectable()
export class NotificationsWizardEffect {
  constructor(
    private notificationApiService: NotificationApiService,
    private apiErrorHandlingService: ApiErrorHandlingService,
    private actions$: Actions,
    private store: Store,
    private router: Router,
    private sharedEffect: SharedEffects
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
                  type: action.notificationType,
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
        let path = '';
        if (
          action.notificationDefinition.type === NotificationType.AD_HOC_EMAIL
        ) {
          path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.ADHOC_EMAIL}`;
        } else {
          path = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.SCHEDULED_DATE}`;
          if (notificationId) {
            path = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.SCHEDULED_DATE}`;
          }
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

  submitEmail = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.submitEmailDetails),
      withLatestFrom(this.store.select(selectNotificationType)),
      switchMap(([action, notificationType]) => {
        if (notificationType === NotificationType.AD_HOC) {
          return of(
            NotificationsWizardActions.setNotificationsContent({
              notificationContent: action.notificationContent,
            })
          );
        } else {
          return of(
            NotificationsWizardActions.validateEmailDetails({
              notificationContent: action.notificationContent,
            })
          );
        }
      })
    );
  });

  validateEmailBody = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.validateEmailDetails),
      withLatestFrom(
        this.store.select(selectRecipientsEmailsFile),
        this.store.select(selectExistingRecipientsEmailsFile),
        this.store.select(selectNotificationType)
      ),
      exhaustMap(([action, file, savedFileId, notificationType]) => {
        const fileId = file?.id || savedFileId;
        return this.notificationApiService
          .validateEmailBody(action.notificationContent.content, fileId, notificationType)
          .pipe(
            map((_) => {
              return NotificationsWizardActions.setNotificationsContent({
                notificationContent: action.notificationContent,
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

  submitRequest$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.submitRequest),
      withLatestFrom(this.store.select(selectRecipientsEmailsFile)),
      exhaustMap(([action, file]) => {
        return this.notificationApiService
          .submitRequest(action.notification, action.notificationId, file.id)
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

  startUploadSelectedReciepientsEmailsFile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(requestUploadSelectedRecipientsEmailFile),
      map((action) =>
        uploadSelectedRecipientsEmailFile({
          file: action.file,
        })
      )
    )
  );

  uploadSelectedRecipientsEmailFile$ = createEffect(() =>
    this.actions$.pipe(
      ofType(uploadSelectedRecipientsEmailFile),
      exhaustMap((action) =>
        this.notificationApiService.uploadSelectedEmailsFile(action.file).pipe(
          mergeMap((event: HttpEvent<any>) => {
            return this.sharedEffect.manageFileUpload(event);
          }),
          catchError((error: HttpErrorResponse) => {
            return of(
              processSelectedFileError({
                status: UploadStatus.Failed,
              }),
              errors({
                errorSummary: this.apiErrorHandlingService.transform(
                  error.error
                ),
              })
            );
          })
        )
      )
    )
  );

  navigateToRequestUploaded$ = createEffect(() =>
    this.actions$.pipe(
      ofType(uploadRecipientsEmailFileSuccess),
      map((action) =>
        setNotificationDefinition({
          notificationDefinition: { shortText: '', longText: '' },
        })
      )
    )
  );

  cancelActiveOrPendingNotificationClicked$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.cancelActiveOrPendingNotification),
      exhaustMap((action) => {
        return this.notificationApiService
          .cancelActiveOrPendingNotification(action.notificationId)
          .pipe(
            map((result) => {
              return NotificationsWizardActions.cancelActiveOrPendingNotificationSuccess({
                notificationId: action.notificationId,
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

  navigateToNotificationCancelled$ = createEffect(() => {
    return this.actions$.pipe(
      ofType(NotificationsWizardActions.cancelActiveOrPendingNotificationSuccess),
      map((action) => {
        const path = `/notifications/${action.notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.NOTIFICATION_CANCELLED}`;
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
