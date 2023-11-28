import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  AddAccountContainerComponent,
  CancelPendingActivationContainerComponent,
  CancelUpdateRequestContainerComponent,
  ChangeAccountDescriptionContainerComponent,
  CheckUpdateRequestContainerComponent,
  ConfirmChangeAccountDescriptionContainerComponent,
  RemoveAccountContainerComponent,
  RequestSubmittedContainerComponent,
  SelectTypeContainerComponent,
} from '@trusted-account-list/components';
import { CheckUpdateRequestResolver } from '@trusted-account-list/resolvers';
import { SubmitSuccessChangeDescriptionContainerComponent } from '@trusted-account-list/components/submit-success-change-description/submit-success-change-description-container.component';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        component: SelectTypeContainerComponent,
      },
    ],
  },
  {
    path: 'select-update-type',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'add-account',
    canLoad: [LoginGuard],
    component: AddAccountContainerComponent,
  },
  {
    path: 'remove-account',
    canLoad: [LoginGuard],
    component: RemoveAccountContainerComponent,
  },
  {
    path: 'check-update-request',
    canLoad: [LoginGuard],
    resolve: { goBackPath: CheckUpdateRequestResolver },
    component: CheckUpdateRequestContainerComponent,
  },
  {
    path: 'request-submitted',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelUpdateRequestContainerComponent,
  },
  {
    path: 'change-description',
    canLoad: [LoginGuard],
    component: ChangeAccountDescriptionContainerComponent,
  },
  {
    path: 'check-description-answers',
    canActivate: [LoginGuard],
    component: ConfirmChangeAccountDescriptionContainerComponent,
  },
  {
    path: 'submit-success-change-description',
    canActivate: [LoginGuard],
    component: SubmitSuccessChangeDescriptionContainerComponent,
  },
  {
    path: 'cancel-pending-activation',
    canLoad: [LoginGuard],
    component: CancelPendingActivationContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TrustedAccountListRoutingModule {}
