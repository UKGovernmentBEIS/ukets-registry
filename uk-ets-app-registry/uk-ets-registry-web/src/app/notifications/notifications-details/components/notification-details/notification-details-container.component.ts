import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import {
  Notification,
  NotificationType,
} from '@notifications/notifications-wizard/model';
import {
  selectNotificationDetails,
  selectNotificationId,
} from '@notifications/notifications-details/reducer/notification-details.selector';
import { selectTimeOptions } from '@notifications/notifications-list/reducers';
import { canGoBack } from '@shared/shared.action';
import { isSeniorAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-notifications-details-container',
  template: `<app-feature-header-wrapper
      ><app-notification-header
        [notification]="notificationDetails$ | async"
        [notificationHeaderVisibility]="true"
        [showRequestUpdate]="
          (notificationDetails$ | async)?.status !== 'EXPIRED' &&
          (isSeniorAdmin$ | async) === true
        "
        [showClone]="
          (notificationDetails$ | async)?.type === NotificationType.AD_HOC &&
          (notificationDetails$ | async)?.status === 'EXPIRED' &&
          (isSeniorAdmin$ | async) === true
        "
        [showBackToList]="true"
      ></app-notification-header></app-feature-header-wrapper
    ><app-notifications-details
      [notificationId]="notificationId$ | async"
      [notificationDetails]="notificationDetails$ | async"
      [timeOptions]="timeOptions$ | async"
    ></app-notifications-details>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationDetailsContainerComponent implements OnInit {
  notificationId$: Observable<string>;
  notificationDetails$: Observable<Notification>;
  timeOptions$: Observable<any>;
  isSeniorAdmin$: Observable<boolean>;
  NotificationType = NotificationType;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.notificationId$ = this.store.select(selectNotificationId);
    this.notificationDetails$ = this.store.select(selectNotificationDetails);
    this.timeOptions$ = this.store.select(selectTimeOptions);
    this.isSeniorAdmin$ = this.store.select(isSeniorAdmin);
  }
}
