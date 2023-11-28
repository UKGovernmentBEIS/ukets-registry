import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import { EmissionsTableUploadContainerComponent } from '@emissions-table/components/emissions-table-upload';
import { CheckRequestAndSubmitContainerComponent } from '@emissions-table/components/check-request-and-submit';
import { CancelEmissionsTableUploadContainerComponent } from '@emissions-table/components/cancel-emissions-table-upload';
import { EmissionsTableSubmittedContainerComponent } from '@emissions-table/components/emissions-table-submitted';
import { ClearEmissionsTableUploadGuard } from '@emissions-table/guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: EmissionsTableUploadContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false,
    },
  },
  {
    path: 'check-request-and-submit',
    canActivate: [LoginGuard],
    component: CheckRequestAndSubmitContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: true,
    },
  },
  {
    path: 'request-submitted',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [ClearEmissionsTableUploadGuard],
    component: EmissionsTableSubmittedContainerComponent,
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [ClearEmissionsTableUploadGuard],
    component: CancelEmissionsTableUploadContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false,
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class EmissionsTableRoutingModule {}
