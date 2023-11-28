import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { errors } from '@shared/shared.action';
import {
  Notification,
  NotificationContent,
  NotificationDefinition,
  NotificationsWizardPathsModel,
} from '@notifications/notifications-wizard/model';
import {
  cancelClicked,
  setGoBackPath,
  setNotificationsContent,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  selectNewNotification,
  selectNotificationDefinition,
  selectNotificationRequest,
} from '@notifications/notifications-wizard/reducers';
import { Observable } from 'rxjs';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';

@Component({
  selector: 'app-notification-content-container',
  template: `<app-notification-content
    [notificationRequest]="notificationRequest$ | async"
    [notificationDefinition]="notificationDefinition$ | async"
    [newNotification]="newNotification$ | async"
    (cancelEmitter)="onCancel()"
    (notificationContent)="onContinue($event)"
    (errorDetails)="onError($event)"
  ></app-notification-content>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationContentContainerComponent implements OnInit {
  newNotification$: Observable<Notification>;
  notificationDefinition$: Observable<NotificationDefinition>;
  notificationRequest$: Observable<NotificationRequestEnum>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.store.dispatch(
      setGoBackPath({
        path: `${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.SCHEDULED_DATE}`,
        skipLocationChange: true,
      })
    );

    this.newNotification$ = this.store.select(selectNewNotification);
    this.notificationDefinition$ = this.store.select(
      selectNotificationDefinition
    );
    this.notificationRequest$ = this.store.select(selectNotificationRequest);
  }

  onContinue(notificationContent: NotificationContent) {
    this.store.dispatch(setNotificationsContent({ notificationContent }));
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
