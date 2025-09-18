import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { IndividualDetailsContainerComponent } from './individual-details';
import { IndividualContactDetailsContainerComponent } from './individual-contact-details';

export const INDIVIDUAL_ROUTES = [
  {
    path: 'details',
    canActivate: [LoginGuard],
    component: IndividualDetailsContainerComponent,
  },
  {
    path: 'contact',
    canActivate: [LoginGuard],
    component: IndividualContactDetailsContainerComponent,
  },
];
