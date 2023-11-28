import { LoginGuard } from 'src/app/shared/guards/login.guard';
import { SecondApprovalNecessaryComponent } from './second-approval-necessary/second-approval-necessary.component';
import { TransfersOutsideListComponent } from './transfers-outside-list/transfers-outside-list.component';
import { OverviewComponent } from './overview/overview.component';
import { SinglePersonSurrenderExcessAllocationComponent } from '@account-opening/trusted-account-list/single-person-surrender-excess-allocation/single-person-surrender-excess-allocation.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'second-approval-necessary',
    canActivate: [LoginGuard],
    component: SecondApprovalNecessaryComponent,
  },
  {
    path: 'transfers-outside-list',
    canActivate: [LoginGuard],
    component: TransfersOutsideListComponent,
  },
  {
    path: 'single-person-surrender-excess-allocation',
    canActivate: [LoginGuard],
    component: SinglePersonSurrenderExcessAllocationComponent,
  },
  { path: 'overview', canActivate: [LoginGuard], component: OverviewComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TrustedAccountListRoutingModule {}
