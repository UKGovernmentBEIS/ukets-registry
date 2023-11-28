import { LoginGuard } from '@shared/guards';
import {
  InstallationContainerComponent,
  AircraftOperatorContainerComponent,
  OverviewContainerComponent,
  IsItAnInstallationTransferContainerComponent,
} from '@account-opening/operator/components';

export const OPERATOR_ROUTES = [
  {
    path: 'is-it-an-installation-transfer',
    canActivate: [LoginGuard],
    component: IsItAnInstallationTransferContainerComponent,
  },
  {
    path: 'installation',
    canActivate: [LoginGuard],
    component: InstallationContainerComponent,
  },
  {
    path: 'installation-transfer',
    canActivate: [LoginGuard],
    component: InstallationContainerComponent,
  },
  //TODO: should be merged with the InstallationContainerOperator -
  {
    path: 'aircraft-operator',
    canActivate: [LoginGuard],
    component: AircraftOperatorContainerComponent,
  },

  {
    path: 'operator-overview',
    canActivate: [LoginGuard],
    component: OverviewContainerComponent,
  },
];
