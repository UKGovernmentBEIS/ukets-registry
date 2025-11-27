import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { SelectOperatorInfoGuard } from '@operator-update/guards';
import { OperatorUpdateContainerComponent } from '@operator-update/components/operator-update';
import { OperatorUpdateWizardPathsModel } from '@operator-update/model/operator-update-wizard-paths.model';
import { CheckUpdateRequestContainerComponent } from '@operator-update/components/check-update-request';
import { CancelUpdateRequestContainerComponent } from '@operator-update/components/cancel-update-request';
import { RequestSubmittedContainerComponent } from '@operator-update/components/request-submitted';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        canActivate: [SelectOperatorInfoGuard],
        component: OperatorUpdateContainerComponent,
      },
    ],
  },
  {
    path: OperatorUpdateWizardPathsModel.CHECK_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    component: CheckUpdateRequestContainerComponent,
  },
  {
    path: OperatorUpdateWizardPathsModel.CANCEL_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelUpdateRequestContainerComponent,
  },
  {
    path: OperatorUpdateWizardPathsModel.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class OperatorUpdateWizardRoutingModule {}
