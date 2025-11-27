import { LoginGuard } from '@shared/guards';
import {
  InstallationContainerComponent,
  OverviewContainerComponent,
  IsItAnInstallationTransferContainerComponent,
  MaritimeOperatorContainerComponent,
} from '@account-opening/operator/components';
import {AircraftOperatorContainerComponent} from "@account-opening/operator/components/aircraft-operator";

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
    path: 'maritime-operator',
    canActivate: [LoginGuard],
    component: MaritimeOperatorContainerComponent,
  },
  {
    path: 'operator-overview',
    canActivate: [LoginGuard],
    component: OverviewContainerComponent,
  },
];
