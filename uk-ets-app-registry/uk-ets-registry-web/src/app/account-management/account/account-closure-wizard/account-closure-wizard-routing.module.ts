import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { AccountClosureCommentContainerComponent } from '@account-management/account/account-closure-wizard/components/account-closure-comment';
import { AccountClosureWizardPathsModel } from '@account-management/account/account-closure-wizard/models';
import { CheckClosureDetailsContainerComponent } from '@account-management/account/account-closure-wizard/components/check-closure-details';
import { ClosureRequestSubmittedContainerComponent } from '@account-management/account/account-closure-wizard/components/request-submitted';
import { CancelClosureRequestContainerComponent } from '@account-management/account/account-closure-wizard/components/cancel-closure-request';
import { ClosureDeactivationGuard } from '@account-management/account/account-closure-wizard/guards/closure-deactivation-guard';

export const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    canDeactivate: [ClosureDeactivationGuard],
    children: [
      {
        path: '',
        component: AccountClosureCommentContainerComponent,
      },
      {
        path: AccountClosureWizardPathsModel.CLOSURE_COMMENT,
        redirectTo: '',
        pathMatch: 'full',
      },
      {
        path: AccountClosureWizardPathsModel.CHECK_CLOSURE_REQUEST,
        component: CheckClosureDetailsContainerComponent,
      },
      {
        path: AccountClosureWizardPathsModel.REQUEST_SUBMITTED,
        component: ClosureRequestSubmittedContainerComponent,
      },
      {
        path: AccountClosureWizardPathsModel.CANCEL_CLOSURE_REQUEST,
        component: CancelClosureRequestContainerComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountClosureWizardRoutingModule {}
