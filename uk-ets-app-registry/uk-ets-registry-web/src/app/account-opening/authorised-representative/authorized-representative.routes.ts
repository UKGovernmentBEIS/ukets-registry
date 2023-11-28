import { OverviewComponent } from './overview/overview.component';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { AccessRightsContainerComponent } from './access-rights/access-rights-container.component';
import { SelectionContainerComponent } from '@account-opening/authorised-representative/selection';

export const AUTHORISED_REPRESENTATIVE_ROUTES = [
  {
    path: 'selection',
    canActivate: [LoginGuard],
    component: SelectionContainerComponent
  },
  {
    path: 'access-rights',
    canActivate: [LoginGuard],
    component: AccessRightsContainerComponent
  },
  {
    path: 'overview',
    canActivate: [LoginGuard],
    component: OverviewComponent
  }
];
