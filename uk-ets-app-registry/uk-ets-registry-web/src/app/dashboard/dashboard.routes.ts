import { LoginGuard } from '@shared/guards';
import { DashboardComponent } from './dashboard.component';
import { DashboardContainerComponent } from '@registry-web/dashboard/dashboard-container.component';

export const DASHBOARD_ROUTES = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: DashboardContainerComponent
  },
  {
    path: 'registry-activation',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./registry-activation/registry-activation.module').then(
        m => m.RegistryActivationModule
      )
  },
  {
    path: 'system-administration/reset',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./system-administration/system-administration.module').then(
        m => m.SystemAdministrationModule
      )
  }
];
