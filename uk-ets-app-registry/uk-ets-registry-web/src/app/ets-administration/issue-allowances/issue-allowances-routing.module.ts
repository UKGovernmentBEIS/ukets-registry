import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import {
  AllowancesProposalSubmittedContainerComponent,
  CancelAllowancesIssuanceProposalContainerComponent,
  CheckAllowancesRequestContainerComponent,
  SetTransactionReferenceContainerComponent,
  SpecifyAllowanceQuantityContainerComponent,
} from '@issue-allowances/components';

const routes: Routes = [
  {
    path: '',
    canActivate: [LoginGuard],
    component: SpecifyAllowanceQuantityContainerComponent,
  },
  {
    path: 'set-transaction-reference',
    canActivate: [LoginGuard],
    component: SetTransactionReferenceContainerComponent,
  },
  {
    path: 'check-request-and-sign',
    canActivate: [LoginGuard],
    component: CheckAllowancesRequestContainerComponent,
  },
  {
    path: 'proposal-submitted',
    canActivate: [LoginGuard],
    component: AllowancesProposalSubmittedContainerComponent,
  },
  {
    path: 'cancel-proposal',
    canActivate: [LoginGuard],
    component: CancelAllowancesIssuanceProposalContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class IssueAllowancesRoutingModule {}
