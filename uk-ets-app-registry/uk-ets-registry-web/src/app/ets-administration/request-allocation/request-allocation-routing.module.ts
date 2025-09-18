import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  AllocationRequestSubmittedContainerComponent,
  CancelAllocationRequestContainerComponent,
  CheckAllocationRequestContainerComponent,
  RequestAllocationContainerComponent,
} from '@request-allocation/components';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: RequestAllocationContainerComponent,
    title: 'ETS Administration',
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
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RequestAllocationRoutingModule {}
