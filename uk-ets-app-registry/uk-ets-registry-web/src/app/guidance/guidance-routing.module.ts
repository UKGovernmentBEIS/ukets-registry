import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {
  AccountOpeningComponent,
  FeaturesComponent,
  HelpSupportComponent,
  TasksComponent,
  UserRolesComponent,
  ConfirmingYourRegistrySignInComponent,
  DocumentRequestsComponent,
  ProposeTransactionComponent,
  AccessingRegistryComponent,
  AccountTypesComponent,
  OtpAuthenticatorComponent,
  ArComponent,
} from '@guidance/components';
import { LoginGuard } from '@shared/guards';
import { TransactionHoursComponent } from '@guidance/components/transaction-hours/transaction-hours.component';
import { UpdateTalComponent } from '@guidance/components/update-tal/update-tal.component';
import { IntroductionComponent } from '@guidance/components/introduction/introduction.component';
import { SurrenderObligationComponent } from '@guidance/components/surrender-obligation/surrender-obligation.component';

export const routes: Routes = [
  {
    path: 'guidance/introduction',
    canActivate: [LoginGuard],
    component: IntroductionComponent,
  },
  {
    path: 'guidance/accessing-registry',
    canActivate: [LoginGuard],
    component: AccessingRegistryComponent,
  },
  {
    path: 'guidance/accessing-registry/otp-authenticator',
    canActivate: [LoginGuard],
    component: OtpAuthenticatorComponent,
  },
  {
    path: 'guidance/user-roles',
    canActivate: [LoginGuard],
    component: UserRolesComponent,
  },
  {
    path: 'guidance/surrender-obligation',
    canActivate: [LoginGuard],
    component: SurrenderObligationComponent,
  },
  {
    path: 'guidance/account-types',
    canActivate: [LoginGuard],
    component: AccountTypesComponent,
  },
  {
    path: 'guidance/registry-sign-in',
    canActivate: [LoginGuard],
    component: ConfirmingYourRegistrySignInComponent,
  },
  {
    path: 'guidance/authorised-representatives',
    canActivate: [LoginGuard],
    component: ArComponent,
  },
  {
    path: 'guidance/document-requests',
    canActivate: [LoginGuard],
    component: DocumentRequestsComponent,
  },
  {
    path: 'guidance/account-open',
    canActivate: [LoginGuard],
    component: AccountOpeningComponent,
  },
  {
    path: 'guidance/propose-transaction',
    canActivate: [LoginGuard],
    component: ProposeTransactionComponent,
  },
  {
    path: 'guidance/tasks',
    canActivate: [LoginGuard],
    component: TasksComponent,
  },
  {
    path: 'guidance/update-tal',
    canActivate: [LoginGuard],
    component: UpdateTalComponent,
  },
  {
    path: 'guidance/transaction-hours',
    canActivate: [LoginGuard],
    component: TransactionHoursComponent,
  },
  {
    path: 'guidance/features',
    canActivate: [LoginGuard],
    component: FeaturesComponent,
  },
  {
    path: 'guidance/help',
    canActivate: [LoginGuard],
    component: HelpSupportComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class GuidanceRoutingModule {}
