import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import {
  AlertsComponent,
  AlertsContainerComponent,
} from '@registry-web/dashboard/alert-monitoring/components';
import * as fromDashboardState from '@registry-web/dashboard/alert-monitoring/reducers/alerts.reducer';
import * as fromAlertsReducer from '@registry-web/dashboard/alert-monitoring/reducers/alerts.reducer';
import { AlertsEffect } from '@registry-web/dashboard/alert-monitoring/effects/alerts.effect';

@NgModule({
  declarations: [AlertsContainerComponent, AlertsComponent],
  exports: [
    AlertsContainerComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromDashboardState.alertsFeatureKey,
      fromAlertsReducer.reducer
    ),
    EffectsModule.forFeature([AlertsEffect]),
  ]
})
export class AlertsMonitoringModule {}
