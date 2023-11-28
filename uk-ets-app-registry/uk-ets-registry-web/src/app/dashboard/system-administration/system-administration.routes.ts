import { LoginGuard } from '@shared/guards';
import { ResetDatabaseComponent } from './components';
import { SystemAdministrationResetGuard } from './guards';

export const SYSTEM_ADMINISTRATION_ROUTES = [
  {
    path: '',
    canActivate: [LoginGuard, SystemAdministrationResetGuard],
    canDeactivate: [SystemAdministrationResetGuard],
    component: ResetDatabaseComponent
  }
];
