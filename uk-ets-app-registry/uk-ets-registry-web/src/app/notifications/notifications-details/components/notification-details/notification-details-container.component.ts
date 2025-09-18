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
  template: `
  <ng-container *ngIf="{ 
      notification: notificationDetails$ | async, 
      isSeniorAdmin: isSeniorAdmin$ | async,
      notificationId: notificationId$ | async,
      timeOptions: timeOptions$ | async 
    } as vs">
      <app-feature-header-wrapper><app-notification-header
        [notification]="vs.notification"
        [notificationHeaderVisibility]="true"
        [showRequestUpdate]="
          vs.notification?.status !== 'EXPIRED' &&
          vs.notification?.status !== 'CANCELLED' &&
          vs.isSeniorAdmin === true"
        [showClone]="
          vs.notification?.type === NotificationType.AD_HOC &&
          vs.notification?.status === 'EXPIRED' &&
          vs.isSeniorAdmin === true"
        [showBackToList]="true"
        [showCancelUpdate]="vs.notification?.status === 'ACTIVE' || 
          vs.notification?.status === 'PENDING' &&
          vs.isSeniorAdmin === true"
      ></app-notification-header></app-feature-header-wrapper
    ><app-notifications-details
      [notificationId]="vs.notificationId"
      [notificationDetails]="vs.notification"
      [timeOptions]="vs.timeOptions"
    ></app-notifications-details>
 </ng-container>`,
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
