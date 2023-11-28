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

@NgModule({
  declarations: [DashboardComponent, DashboardContainerComponent],
  imports: [
    RouterModule.forChild(DASHBOARD_ROUTES),
    CommonModule,
    SharedModule,
    EffectsModule.forFeature([RegistryActivationEffects]),
    AlertsMonitoringModule,
    DashboardNotificationsModule,
  ],
})
export class DashboardModule {}
