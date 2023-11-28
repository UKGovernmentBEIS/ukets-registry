import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  BulkArSubmittedContainerComponent,
  BulkArUploadContainerComponent,
  CancelBulkArUploadComponent,
  CheckRequestAndSubmitContainerComponent,
} from '@registry-web/bulk-ar/components';
import { ClearBulkArGuard } from '@registry-web/bulk-ar/guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: BulkArUploadContainerComponent,
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
    canDeactivate: [ClearBulkArGuard],
    component: BulkArSubmittedContainerComponent,
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [ClearBulkArGuard],
    component: CancelBulkArUploadComponent,
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
export class BulkArRoutingModule {}
