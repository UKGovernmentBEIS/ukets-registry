import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  canGoBack,
  clearErrors,
  errors,
} from '@registry-web/shared/shared.action';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import {
  cancelClicked,
  requestUploadSelectedRecipientsEmailFile,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { Observable } from 'rxjs';
import { Configuration } from '@shared/configuration/configuration.interface';
import { UploadStatus } from '@shared/model/file';
import {
  selectConfigurationRegistry,
  selectErrorSummary,
} from '@shared/shared.selector';
import {
  selectNewNotification,
  selectNotificationRequest,
  selectUploadFileIsInProgress,
  selectUploadFileProgress,
} from '@notifications/notifications-wizard/reducers';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import { Notification } from '@notifications/notifications-wizard/model';

@Component({
  selector: 'app-notifications-email-upload-file-container',
  template: `<app-notifications-email-upload-file
    (fileEmitter)="sendFileForUpload($event)"
    [errorSummary]="errorSummary$ | async"
    (cancelEmitter)="onCancel()"
    (errorDetails)="onError($event)"
    [notificationRequest]="notificationRequest$ | async"
    [newNotification]="newNotification$ | async"
  ></app-notifications-email-upload-file>`,
})
export class NotificationsEmailUploadFileContainerComponent implements OnInit {
  isInProgress$: Observable<boolean>;
  fileProgress$: Observable<number>;
  configuration$: Observable<Configuration[]>;
  errorSummary$: Observable<ErrorSummary>;
  notificationRequest$: Observable<NotificationRequestEnum>;
  newNotification$: Observable<Notification>;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.configuration$ = this.store.select(selectConfigurationRegistry);
    this.isInProgress$ = this.store.select(selectUploadFileIsInProgress);
    this.fileProgress$ = this.store.select(selectUploadFileProgress);
    this.errorSummary$ = this.store.select(selectErrorSummary);
    this.notificationRequest$ = this.store.select(selectNotificationRequest);
    this.newNotification$ = this.store.select(selectNewNotification);

    this.store.dispatch(canGoBack({ goBackRoute: null }));
  }

  sendFileForUpload(file): void {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      requestUploadSelectedRecipientsEmailFile({
        file,
        status: UploadStatus.Requested,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
