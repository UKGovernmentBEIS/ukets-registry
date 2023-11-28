import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import {
  Notification,
  NotificationScheduledDate,
} from '@notifications/notifications-wizard/model';
import {
  cancelClicked,
  goBackToSelectTypeOrNotificationDetails,
  setNotificationsScheduledDate,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import {
  selectNewNotification,
  selectNotificationRequest,
} from '@notifications/notifications-wizard/reducers';
import { Observable } from 'rxjs';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import {
  selectTimeOptions,
  selectTimeOptionsWithNow,
} from '@notifications/notifications-list/reducers';

@Component({
  selector: 'app-notifications-scheduled-date-container',
  template: `<app-notifications-scheduled-date
    [timeOptions]="timeOptions$ | async"
    [timeOptionsWithNow]="timeOptionsWithNow$ | async"
    [notificationRequest]="notificationRequest$ | async"
    [newNotification]="newNotification$ | async"
    (cancelEmitter)="onCancel()"
    (notificationScheduledDate)="onContinue($event)"
    (errorDetails)="onError($event)"
  ></app-notifications-scheduled-date>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationScheduledDateContainerComponent implements OnInit {
  newNotification$: Observable<Notification>;
  timeOptions$: Observable<any>;
  timeOptionsWithNow$: Observable<any>;
  notificationRequest$: Observable<NotificationRequestEnum>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.newNotification$ = this.store.select(selectNewNotification);
    this.timeOptions$ = this.store.select(selectTimeOptions);
    this.timeOptionsWithNow$ = this.store.select(selectTimeOptionsWithNow);
    this.notificationRequest$ = this.store.select(selectNotificationRequest);

    this.store.dispatch(goBackToSelectTypeOrNotificationDetails());
  }

  onContinue(notificationScheduledDate: NotificationScheduledDate) {
    this.store.dispatch(
      setNotificationsScheduledDate({ notificationScheduledDate })
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
