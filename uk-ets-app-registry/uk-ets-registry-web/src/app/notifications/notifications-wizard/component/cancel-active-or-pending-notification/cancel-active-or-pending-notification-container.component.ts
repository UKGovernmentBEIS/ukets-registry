import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { cancelActiveOrPendingNotification } from '@notifications/notifications-wizard/actions/notifications-wizard.actions';
import { selectNotificationDetails } from '@registry-web/notifications/notifications-details/reducer/notification-details.selector';
import { Observable } from 'rxjs';
import { Notification } from '../../model';

@Component({
  selector: 'app-cancel-notification-container',
  template: `<app-feature-header-wrapper><app-notification-header
    [notification]="notification$ | async"
    [notificationHeaderVisibility]="true"
    [showRequestUpdate]="false"
    [showCancelUpdate]="false"
    [showClone]="false"
    [showBackToList]="true"></app-notification-header></app-feature-header-wrapper>
  <div class="govuk-grid-row">
  <div class="govuk-grid-column-two-thirds">
    <div class="govuk-fieldset__legend govuk-fieldset__legend--xl">
      <h1 class="govuk-fieldset__heading">
        <span class="govuk-caption-xl">Cancel notification</span>
        <ng-container>Are you sure you want to cancel the notification?</ng-container>
      </h1>
    </div>
    <hr class="govuk-section-break govuk-section-break--m" />
    <div class="govuk-form-group">
      <p class="govuk-body">
        The notification will be cancelled.<br />
        This action cannot be reversed.
      </p>
    </div>
    <button
      (click)="onCancel()"
      class="govuk-button govuk-button--warning"
      data-module="govuk-button">
      Cancel notification
    </button>
  </div></div>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelActiveOrPendingNotificationContainerComponent implements OnInit {
  notificationId: string;
  notification$: Observable<Notification>;
  goBackRoute: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.notificationId = this.activatedRoute.snapshot.paramMap.get('notificationId');
    this.notification$ = this.store.select(selectNotificationDetails); 
  }

  onCancel() {
    this.store.dispatch(cancelActiveOrPendingNotification({notificationId:this.notificationId}));
  }
}
