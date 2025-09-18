import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardComponent } from './dashboard.component';
import { RouterModule } from '@angular/router';
import { DASHBOARD_ROUTES } from './dashboard.routes';
import { EffectsModule } from '@ngrx/effects';
import { SharedModule } from '@shared/shared.module';
import { RegistryActivationEffects } from './registry-activation/effects';
import { DashboardContainerComponent } from './dashboard-container.component';
import { AlertsMonitoringModule } from '@registry-web/dashboard/alert-monitoring/alerts-monitoring.module';
import { DashboardNotificationsModule } from '@registry-web/dashboard/notifications/dashboard-notifications.module';
import { RecoveryMethodsEffects } from '@registry-web/dashboard/recovery-methods/effects';
import { StoreModule } from '@ngrx/store';
import * as fromRecoveryMethods from '@registry-web/dashboard/recovery-methods/reducers/recovery-methods-reducer';
import { RecoveryMethodsNotificationComponent } from '@registry-web/dashboard/recovery-methods/components/recovery-methods-notification';

@NgModule({
  declarations: [
    DashboardComponent,
    DashboardContainerComponent,
    RecoveryMethodsNotificationComponent,
  ],
  imports: [
    RouterModule.forChild(DASHBOARD_ROUTES),
    CommonModule,
    SharedModule,
    EffectsModule.forFeature([
      RegistryActivationEffects,
      RecoveryMethodsEffects,
    ]),
    StoreModule.forFeature(
      fromRecoveryMethods.recoveryMethodsFeatureKey,
      fromRecoveryMethods.reducer
    ),
    AlertsMonitoringModule,
    DashboardNotificationsModule,
  ],
})
export class DashboardModule {}
