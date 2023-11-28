import { LoginGuard } from '@shared/guards';
import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import {
  CheckRequestAndSignContainerComponent,
  ProposalSubmittedContainerComponent,
  SelectCommitmentPeriodAccountContainerComponent,
  SelectUnitTypeContainerComponent,
  SetTransactionReferenceContainerComponent,
} from '@issue-kp-units/components';
import { IssueKpUnitsRoutePathsModel } from '@issue-kp-units/model';

const routes: Routes = [
  {
    path: IssueKpUnitsRoutePathsModel['select-commitment-period'],
    canActivate: [LoginGuard],
    component: SelectCommitmentPeriodAccountContainerComponent,
  },
  {
    path: IssueKpUnitsRoutePathsModel['select-units'],
    canActivate: [LoginGuard],
    component: SelectUnitTypeContainerComponent,
  },
  {
    path: IssueKpUnitsRoutePathsModel['set-transaction-reference'],
    canActivate: [LoginGuard],
    component: SetTransactionReferenceContainerComponent,
  },
  {
    path: IssueKpUnitsRoutePathsModel['check-request-and-sign'],
    canActivate: [LoginGuard],
    component: CheckRequestAndSignContainerComponent,
  },
  {
    path: IssueKpUnitsRoutePathsModel['proposal-submitted'],
    canActivate: [LoginGuard],
    component: ProposalSubmittedContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class IssueKpUnitsRoutingModule {}
