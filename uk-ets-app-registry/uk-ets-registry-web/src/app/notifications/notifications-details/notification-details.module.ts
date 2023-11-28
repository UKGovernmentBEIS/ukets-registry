import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '@shared/shared.module';
import { NotificationDetailsRoutingModule } from '@notifications/notifications-details/notification-details-routing.module';
import {
  NotificationDetailsComponent,
  NotificationDetailsContainerComponent,
} from '@notifications/notifications-details/components/notification-details';
import { GdsDatePipe } from '@shared/pipes';
import * as fromNotificationsDetails from '@notifications/notifications-details/reducer';
import { StoreModule } from '@ngrx/store';
import { EffectsModule } from '@ngrx/effects';
import { NotificationDetailsEffect } from '@notifications/notifications-details/effects';
import { NotificationDetailsGuard } from '@notifications/notifications-details/guards';
import { ClearNotificationsDetailsGuard } from '@notifications/notifications-details/guards/clear-notifications-details-guard';

@NgModule({
  declarations: [
    NotificationDetailsContainerComponent,
    NotificationDetailsComponent,
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    SharedModule,
    NotificationDetailsRoutingModule,
    EffectsModule.forFeature([NotificationDetailsEffect]),
    StoreModule.forFeature(
      fromNotificationsDetails.notificationsDetailsFeatureKey,
      fromNotificationsDetails.reducer
    ),
  ],
  providers: [
    GdsDatePipe,
    NotificationDetailsGuard,
    ClearNotificationsDetailsGuard,
  ],
})
export class NotificationDetailsModule {}
