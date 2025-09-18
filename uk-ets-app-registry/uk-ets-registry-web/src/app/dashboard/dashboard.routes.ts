import { LoginGuard } from '@shared/guards';
import { DashboardComponent } from './dashboard.component';
import { DashboardContainerComponent } from '@registry-web/dashboard/dashboard-container.component';
import { RecoveryMethodsNotificationComponent } from '@registry-web/dashboard/recovery-methods/components/recovery-methods-notification';

export const DASHBOARD_ROUTES = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: DashboardContainerComponent,
  },
  {
    path: 'recovery',
    canActivate: [LoginGuard],
    component: RecoveryMethodsNotificationComponent,
  },
  {
    path: 'registry-activation',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./registry-activation/registry-activation.module').then(
        (m) => m.RegistryActivationModule
      ),
  },
  {
    path: 'system-administration/reset',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./system-administration/system-administration.module').then(
        (m) => m.SystemAdministrationModule
      ),
  },
];
