import { LoginGuard } from '@shared/guards';
import { RegistryActivationComponent } from './components/registry-activation';
import { EnrolledComponent } from './components/enrolled';
import { ActivationCodeRequestContainerComponent } from './components/activation-code-request';
import { ActivationCodeRequestSubmittedContainerComponent } from './components/activation-code-request-submitted';

export const REGISTRY_ACTIVATION_ROUTES = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: RegistryActivationComponent
  },
  {
    path: 'enrolled',
    canActivate: [LoginGuard],
    component: EnrolledComponent
  },
  {
    path: 'activation-code-request',
    component: ActivationCodeRequestContainerComponent,
    loadChildren: () =>
      import('./../../user-management/user-details/user-details.module').then(
        m => m.UserDetailsModule
      )
  },
  {
    path: 'request-submitted',
    canActivate: [LoginGuard],
    component: ActivationCodeRequestSubmittedContainerComponent
  }
];
