import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  CancelUpdateRequestContainerComponent,
  CheckUpdateRequestContainerComponent,
  RequestSubmittedContainerComponent,
  SecondApprovalNecessaryContainerComponent,
  TransfersOutsideListContainerComponent,
} from '@account-management/account/tal-transaction-rules/components';
import { SelectTalTransactionRulesGuard } from '@tal-transaction-rules/guards/select-tal-transaction-rules.guard';
import { SinglePersonSurrenderExcessAllocationContainerComponent } from '@tal-transaction-rules/components/single-person-surrender-excess-allocation';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        canActivate: [SelectTalTransactionRulesGuard],
        component: SecondApprovalNecessaryContainerComponent,
      },
    ],
  },
  {
    path: 'select-second-approval',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'select-transfers-outside-list',
    canLoad: [LoginGuard],
    component: TransfersOutsideListContainerComponent,
  },
  {
    path: 'single-person-surrender-excess-allocation',
    canLoad: [LoginGuard],
    component: SinglePersonSurrenderExcessAllocationContainerComponent,
  },
  {
    path: 'check-update-request',
    canLoad: [LoginGuard],
    component: CheckUpdateRequestContainerComponent,
  },
  {
    path: 'request-submitted',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: RequestSubmittedContainerComponent,
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelUpdateRequestContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TalTransactionRulesRoutingModule {}
