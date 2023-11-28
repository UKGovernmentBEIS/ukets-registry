import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  AllocationRequestSubmittedContainerComponent,
  CancelAllocationRequestContainerComponent,
  CheckAllocationRequestContainerComponent,
  RequestAllocationContainerComponent,
  CancelPendingAllocationsComponent,
} from '@request-allocation/components';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: RequestAllocationContainerComponent,
  },
  {
    path: 'check-allocation-request',
    canActivate: [LoginGuard],
    component: CheckAllocationRequestContainerComponent,
  },
  {
    path: 'cancel-request',
    canActivate: [LoginGuard],
    component: CancelAllocationRequestContainerComponent,
  },
  {
    path: 'request-submitted',
    canActivate: [LoginGuard],
    component: AllocationRequestSubmittedContainerComponent,
  },
  {
    path: 'cancel-pending-allocations',
    canActivate: [LoginGuard],
    component: CancelPendingAllocationsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RequestAllocationRoutingModule {}
