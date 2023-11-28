import { ContactDetailsContainerComponent } from './contact-details/contact-details-container.component';
import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { OverviewComponent } from './overview/overview.component';
import { PersonalDetailsContainerComponent } from './personal-details/personal-details-container.component';

export const ACCOUNT_HOLDER_CONTACT_ROUTES = [
  {
    path: 'personal-details/:contactType',
    canActivate: [LoginGuard],
    component: PersonalDetailsContainerComponent
  },
  {
    path: 'contact-details/:contactType',
    canActivate: [LoginGuard],
    component: ContactDetailsContainerComponent
  },
  {
    path: 'overview/:contactType',
    canActivate: [LoginGuard],
    component: OverviewComponent
  }
];
