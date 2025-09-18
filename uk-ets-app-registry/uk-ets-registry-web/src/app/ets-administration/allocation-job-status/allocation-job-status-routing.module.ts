import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { ViewAllocationJobStatusContainerComponent } from './components/view-allocation-job-status-container.component';
import { CancelPendingAllocationsComponent } from './components/cancel-pending-allocations';
import { AllocationJobStatusResolver } from './resolvers';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: ViewAllocationJobStatusContainerComponent,
    resolve: {
      search: AllocationJobStatusResolver,
    },
    data: {
      headerVisible: true,
      actionsVisible: false,
      backToListVisible: false,
    },
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
export class AllocationJobStatusRoutingModule {}
