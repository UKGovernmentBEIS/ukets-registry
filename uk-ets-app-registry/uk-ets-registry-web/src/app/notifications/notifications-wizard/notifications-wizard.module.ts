import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { NotificationsWizardRoutingModule } from '@notifications/notifications-wizard/notifications-wizard-routing.module';
import { EffectsModule } from '@ngrx/effects';
import { NotificationsWizardEffect } from '@notifications/notifications-wizard/effects';
import { StoreModule } from '@ngrx/store';
import * as fromNotifications from '@notifications/notifications-wizard/reducers';
import {
  LoadNotificationWizardContainerComponent,
  SelectNotificationTypeComponent,
  SelectNotificationTypeContainerComponent,
} from '@notifications/notifications-wizard/component/select-notification-type';
import { CancelNotificationRequestContainerComponent } from '@notifications/notifications-wizard/component/cancel-notification-request';
import {
  NotificationScheduledDateComponent,
  NotificationScheduledDateContainerComponent,
} from '@notifications/notifications-wizard/component/notification-scheduled-date';
import {
  NotificationContentComponent,
  NotificationContentContainerComponent,
} from '@notifications/notifications-wizard/component/notification-content';
import {
  NotificationCheckAndSubmitComponent,
  NotificationCheckAndSubmitContainerComponent,
} from '@notifications/notifications-wizard/component/notification-check-and-submit';
import { NotificationRequestSubmittedContainerComponent } from '@notifications/notifications-wizard/component/notification-request-submitted';
import { GdsDatePipe } from '@shared/pipes';

@NgModule({
  declarations: [
    SelectNotificationTypeContainerComponent,
    SelectNotificationTypeComponent,
    CancelNotificationRequestContainerComponent,
    NotificationScheduledDateContainerComponent,
    NotificationScheduledDateComponent,
    NotificationContentContainerComponent,
    NotificationContentComponent,
    NotificationCheckAndSubmitContainerComponent,
    NotificationCheckAndSubmitComponent,
    NotificationRequestSubmittedContainerComponent,
    LoadNotificationWizardContainerComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    NotificationsWizardRoutingModule,
    StoreModule.forFeature(
      fromNotifications.notificationsWizardFeatureKey,
      fromNotifications.reducer
    ),
    EffectsModule.forFeature([NotificationsWizardEffect]),
  ],
  providers: [GdsDatePipe],
})
export class NotificationsWizardModule {}
