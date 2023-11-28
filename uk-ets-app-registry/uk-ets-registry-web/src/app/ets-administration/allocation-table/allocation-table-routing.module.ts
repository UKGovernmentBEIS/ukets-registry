import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import {
  AllocationTableUploadContainerComponent,
  CheckRequestAndSubmitContainerComponent
} from '@allocation-table/components';
import { CancelAllocationTableUploadContainerComponent } from '@allocation-table/components/cancel-allocation-table-request';
import { AllocationTableSubmittedContainerComponent } from '@allocation-table/components/allocation-table-submitted';
import { ClearAllocationTableUploadGuard } from '@allocation-table/guards';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: AllocationTableUploadContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false
    }
  },
  {
    path: 'check-request-and-submit',
    canActivate: [LoginGuard],
    component: CheckRequestAndSubmitContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: true
    }
  },
  {
    path: 'request-submitted',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [ClearAllocationTableUploadGuard],
    component: AllocationTableSubmittedContainerComponent
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    canDeactivate: [ClearAllocationTableUploadGuard],
    component: CancelAllocationTableUploadContainerComponent,
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AllocationTableRoutingModule {}
