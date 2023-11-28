import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import {
  cancelClicked,
  setRequestNotificationType,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import {
  Notification,
  NotificationType,
} from '@notifications/notifications-wizard/model';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  selectNewNotification,
  selectNotificationId,
  selectNotificationRequest,
} from '@notifications/notifications-wizard/reducers';
import { Observable } from 'rxjs';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';

@Component({
  selector: 'app-select-notification-type-container',
  template: `<app-select-notification-type
    [notificationRequest]="notificationRequest$ | async"
    [notification]="newNotification$ | async"
    [notificationId]="notificationId$ | async"
    (cancelEmitter)="onCancel()"
    (selectNotificationType)="onContinue($event)"
    (errorDetails)="onError($event)"
  ></app-select-notification-type>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectNotificationTypeContainerComponent implements OnInit {
  notificationRequest$: Observable<NotificationRequestEnum>;
  newNotification$: Observable<Notification>;
  notificationId$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.notificationRequest$ = this.store.select(selectNotificationRequest);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/notifications`,
        extras: { skipLocationChange: false },
      })
    );
    this.newNotification$ = this.store.select(selectNewNotification);
    this.notificationId$ = this.store.select(selectNotificationId);
  }

  onContinue(notificationType: NotificationType) {
    this.store.dispatch(setRequestNotificationType({ notificationType }));
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
