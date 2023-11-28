import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Notification,
  NotificationsWizardPathsModel,
} from '@notifications/notifications-wizard/model';
import {
  selectCurrentNotification,
  selectNewNotification,
  selectNotificationId,
  selectNotificationRequest,
  selectTentativeRecipients,
} from '@notifications/notifications-wizard/reducers';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import {
  cancelClicked,
  setGoBackPath,
  submitRequest,
} from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { NotificationsWizardActions } from '@notifications/notifications-wizard/actions';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import { selectTimeOptionsWithNow } from '@notifications/notifications-list/reducers';

@Component({
  selector: 'app-notification-check-and-submit-container',
  template: `<app-notification-check-and-submit
    [timeOptions]="timeOptions$ | async"
    [notificationId]="notificationId$ | async"
    [notificationRequest]="notificationRequest$ | async"
    [currentNotification]="currentNotification$ | async"
    [newNotification]="newNotification$ | async"
    [tentativeRecipients]="tentativeRecipients$ | async"
    (cancelEmitter)="onCancel()"
    (submitRequest)="onSubmit($event)"
    (navigateToEmitter)="navigateTo($event)"
  ></app-notification-check-and-submit>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationCheckAndSubmitContainerComponent implements OnInit {
  newNotification$: Observable<Notification>;
  currentNotification$: Observable<Notification>;
  notificationRequest$: Observable<NotificationRequestEnum>;
  notificationId$: Observable<string>;
  timeOptions$: Observable<any>;
  tentativeRecipients$: Observable<number>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.notificationRequest$ = this.store.select(selectNotificationRequest);
    this.newNotification$ = this.store.select(selectNewNotification);
    this.currentNotification$ = this.store.select(selectCurrentNotification);
    this.notificationId$ = this.store.select(selectNotificationId);
    this.timeOptions$ = this.store.select(selectTimeOptionsWithNow);
    this.tentativeRecipients$ = this.store.select(selectTentativeRecipients);

    this.store.dispatch(
      setGoBackPath({
        path: `${NotificationsWizardPathsModel.BASE_PATH}/${NotificationsWizardPathsModel.CONTENT}`,
        skipLocationChange: true,
      })
    );
  }

  onSubmit(value) {
    this.store.dispatch(submitRequest({ ...value }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  navigateTo(path: string) {
    const notificationId = this.route.snapshot.paramMap.get('notificationId');
    let pathRoute = `/notifications/${NotificationsWizardPathsModel.BASE_PATH}/${path}`;
    if (notificationId) {
      pathRoute = `/notifications/${notificationId}/${NotificationsWizardPathsModel.BASE_PATH}/${path}`;
    }
    this.store.dispatch(
      NotificationsWizardActions.navigateTo({
        route: pathRoute,
        extras: {
          skipLocationChange: true,
        },
      })
    );
  }
}
