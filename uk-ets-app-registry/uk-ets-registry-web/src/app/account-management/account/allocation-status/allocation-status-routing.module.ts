import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  CancelUpdateAllocationStatusComponent,
  CheckUpdateRequestContainerComponent,
  UpdateAllocationStatusFormContainerComponent,
  UpdateAllocationStatusWizardComponent,
} from '@allocation-status/components';
import { AuthorisedRepresentativesRoutePaths } from '@authorised-representatives/model/authorised-representatives-route-paths.model';
import { AllocationStatusRoutePathsModel } from '@allocation-status/model/allocation-status-route-paths.model';
import { NgModule } from '@angular/core';
import { AllocationStatusResolver } from '@allocation-status/resolvers/allocation-status.resolver';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    // resolve: { updateTypes: UpdateTypesResolver },
    component: UpdateAllocationStatusWizardComponent,
    children: [
      {
        path: '',
        resolve: { allocationStatus: AllocationStatusResolver },
        component: UpdateAllocationStatusFormContainerComponent,
      },
      {
        path: AllocationStatusRoutePathsModel.CHECK_UPDATE_REQUEST,
        component: CheckUpdateRequestContainerComponent,
      },
    ],
  },
  {
    path: AuthorisedRepresentativesRoutePaths.CANCEL_UPDATE_REQUEST,
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelUpdateAllocationStatusComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AllocationStatusRoutingModule {}
