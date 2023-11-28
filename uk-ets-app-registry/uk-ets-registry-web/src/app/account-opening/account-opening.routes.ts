import { InfoComponent } from './info/info.component';
import { LoginGuard } from '@shared/guards';
import { MainWizardComponent } from './main-wizard/main-wizard.component';
import { CheckAnswersComponent } from './check-answers/check-answers.component';
import { ConfirmationComponent } from './confirmation/confirmation.component';
import { AccountTypeContainerComponent } from './account-type/account-type-container.component';

export const ACCOUNT_OPENING_ROUTES = [
  { path: '', canActivate: [LoginGuard], component: InfoComponent },
  {
    path: 'account-type',
    canActivate: [LoginGuard],
    component: AccountTypeContainerComponent,
  },
  {
    path: 'main-wizard',
    // TODO Add guard here if no account type to omit null value errors
    canActivate: [LoginGuard],
    component: MainWizardComponent,
  },
  {
    path: 'check-answers',
    canActivate: [LoginGuard],
    component: CheckAnswersComponent,
  },
  {
    path: 'confirmation',
    canActivate: [LoginGuard],
    component: ConfirmationComponent,
  },
  // TODO: no reason for lazy modules here, this has created a lot of repeating code, and  we also need to reuse the overview components in the end of the wizar
  //  a single module is enough for the account opening
  {
    path: 'account-holder',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./account-holder/account-holder.module').then(
        (m) => m.AccountHolderModule
      ),
  },
  {
    path: 'account-holder-contact',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./account-holder-contact/account-holder-contact.module').then(
        (m) => m.AccountHolderContactModule
      ),
  },
  {
    path: 'account-details',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./account-details/account-details.module').then(
        (m) => m.AccountDetailsModule
      ),
  },
  {
    path: 'trusted-account-list',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./trusted-account-list/trusted-account-list.module').then(
        (m) => m.TrustedAccountListModule
      ),
  },
  {
    path: 'authorised-representative',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import(
        './authorised-representative/authorised-representative.module'
      ).then((m) => m.AuthorisedRepresentativeModule),
  },
  {
    path: 'operator',
    canActivate: [LoginGuard],
    loadChildren: () =>
      import('./operator/operator.module').then((m) => m.OperatorModule),
  },
];
