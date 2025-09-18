import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { CancelUpdateExclusionStatusContainerComponent } from '@exclusion-status-update-wizard/cancel-update-exclusion-status';
import { CheckUpdateStatusContainerComponent } from '@exclusion-status-update-wizard/check-update-status';
import { GetEmissionDetailsGuard } from '@exclusion-status-update-wizard/guards/get-emission-details.guard';
import { UpdateExclusionStatusPathsModel } from '@exclusion-status-update-wizard/model/update-exclusion-status-paths.model';
import { SelectExclusionStatusContainerComponent } from '@exclusion-status-update-wizard/select-exclusion-status';
import { SelectYearContainerComponent } from '@exclusion-status-update-wizard/select-year';
import { SubmittedUpdateExclusionStatusContainerComponent } from '@exclusion-status-update-wizard/submitted-update-exclusion-status';
import { ExclusionReasonContainerComponent } from './exclusion-reason/exclusion-reason-container.component';

export const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        canActivate: [GetEmissionDetailsGuard],
        component: SelectYearContainerComponent,
      },
    ],
  },
  {
    path: UpdateExclusionStatusPathsModel.SELECT_EXCLUSION_STATUS,
    canLoad: [LoginGuard],
    component: SelectExclusionStatusContainerComponent,
  },
  {
    path: UpdateExclusionStatusPathsModel.EXCLUSION_REASON,
    canLoad: [LoginGuard],
    component: ExclusionReasonContainerComponent,
  },
  {
    path: UpdateExclusionStatusPathsModel.CANCEL_UPDATE_EXCLUSION_STATUS,
    canLoad: [LoginGuard],
    component: CancelUpdateExclusionStatusContainerComponent,
  },
  {
    path: UpdateExclusionStatusPathsModel.CHECK_UPDATE_STATUS,
    canLoad: [LoginGuard],
    component: CheckUpdateStatusContainerComponent,
  },
  {
    path: UpdateExclusionStatusPathsModel.REQUEST_SUBMITTED,
    canLoad: [LoginGuard],
    component: SubmittedUpdateExclusionStatusContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ExclusionStatusUpdateWizardRoutingModule {}
