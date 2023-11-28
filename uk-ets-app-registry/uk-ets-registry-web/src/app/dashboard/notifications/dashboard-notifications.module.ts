import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import {
  DashboardNotificationsComponent,
  DashboardNotificationsContainerComponent,
} from '@registry-web/dashboard/notifications/components';
import * as fromDashboardState from './reducers/notifications.reducer';
import { NotificationsEffect } from '@registry-web/dashboard/notifications/effects/notifications.effect';

@NgModule({
  declarations: [
    DashboardNotificationsComponent,
    DashboardNotificationsContainerComponent,
  ],
  exports: [DashboardNotificationsContainerComponent],
  imports: [
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromDashboardState.notificationsFeatureKey,
      fromDashboardState.reducer
    ),
    EffectsModule.forFeature([NotificationsEffect]),
  ],
})
export class DashboardNotificationsModule {}
