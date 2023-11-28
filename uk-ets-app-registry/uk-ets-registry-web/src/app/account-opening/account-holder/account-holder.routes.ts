import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { AccountHolderTypeContainerComponent } from './account-holder-type';
import { AccountHolderSelectionContainerComponent } from './account-holder-selection';
import { INDIVIDUAL_ROUTES } from '@account-opening/account-holder/individual';
import { ORGANISATION_ROUTES } from '@account-opening/account-holder/organisation';
import { OverviewComponent } from './overview';

export const ACCOUNT_HOLDER_ROUTES = [
  {
    path: 'type',
    canActivate: [LoginGuard],
    component: AccountHolderTypeContainerComponent
  },
  {
    path: 'selection',
    canActivate: [LoginGuard],
    component: AccountHolderSelectionContainerComponent
  },
  {
    path: 'individual',
    canActivate: [LoginGuard],
    children: INDIVIDUAL_ROUTES
  },
  {
    path: 'organisation',
    canActivate: [LoginGuard],
    children: ORGANISATION_ROUTES
  },
  { path: 'overview', canActivate: [LoginGuard], component: OverviewComponent }
];
