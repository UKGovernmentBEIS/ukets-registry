import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { Observable, Subscription } from 'rxjs';
import { Store } from '@ngrx/store';
import {
  selectNewNotification,
  selectSubmittedRequestIdentifier,
} from '@notifications/notifications-wizard/reducers';
import { canGoBack } from '@shared/shared.action';
import { NotificationType } from '@notifications/notifications-wizard/model';

@Component({
  selector: 'app-notification-request-submitted-container',
  template: `<app-request-submitted
    [confirmationMessageTitle]="
      'You have successfully scheduled the notification.'
    "
    [customWhatHappensNext]="
      NotificationType.AD_HOC === this.type
        ? 'Logged in users will be able to see this notification on the dates and times you have selected.'
        : 'The Authorised Representatives will receive the notification on the dates and times you have selected.'
    "
    [notificationId]="submittedIdentifier$ | async"
    [isAdmin]="true"
  ></app-request-submitted>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationRequestSubmittedContainerComponent
  implements OnInit, OnDestroy
{
  submittedIdentifier$: Observable<string>;
  private subscription: Subscription;
  type: NotificationType;
  NotificationType = NotificationType;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.submittedIdentifier$ = this.store.select(
      selectSubmittedRequestIdentifier
    );
    this.subscription = this.store
      .select(selectNewNotification)
      .subscribe((notification) => {
        this.type = notification?.type;
      });
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
