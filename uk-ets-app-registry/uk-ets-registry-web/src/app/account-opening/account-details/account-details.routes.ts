import { OverviewComponent } from './overview/overview.component';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { AccountDetailsContainerComponent } from './account-details/account-details-container.component';
import { BillingDetailsContainerComponent } from './billing-details/billing-details-container.component';

export const ACCOUNT_DETAILS_ROUTES = [
  {
    path: 'details',
    canActivate: [LoginGuard],
    component: AccountDetailsContainerComponent,
  },
  {
    path: 'billing',
    canActivate: [LoginGuard],
    component: BillingDetailsContainerComponent,
  },
  { path: 'overview', canActivate: [LoginGuard], component: OverviewComponent },
];
