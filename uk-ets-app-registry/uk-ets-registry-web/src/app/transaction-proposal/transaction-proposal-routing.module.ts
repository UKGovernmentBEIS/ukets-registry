import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  CancelTransactionProposalContainerComponent,
  CheckTransactionDetailsContainerComponent,
  TransactionProposalSubmittedContainerComponent,
  SelectTransactionTypeContainerComponent,
  SelectUnitTypesAndQuantityContainerComponent,
  SpecifyAcquiringAccountContainerComponent,
} from '@transaction-proposal/components';
import {
  SpecifyAcquiringAccountContainerComponentGuard,
  TransactionTypesGuard,
} from '@transaction-proposal/guards';
import { SetTransactionReferenceContainerComponent } from '@transaction-proposal/components/set-transaction-reference';

const routes: Routes = [
  {
    path: '',
    canLoad: [LoginGuard],
    children: [
      {
        path: '',
        canActivate: [TransactionTypesGuard],
        component: SelectTransactionTypeContainerComponent,
      },
    ],
  },
  {
    path: 'select-transaction-type',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'select-unit-types-quantity',
    canLoad: [LoginGuard],
    component: SelectUnitTypesAndQuantityContainerComponent,
  },
  {
    path: 'specify-acquiring-account',
    canLoad: [LoginGuard],
    canActivate: [SpecifyAcquiringAccountContainerComponentGuard],
    component: SpecifyAcquiringAccountContainerComponent,
  },
  {
    path: 'set-transaction-reference',
    canLoad: [LoginGuard],
    component: SetTransactionReferenceContainerComponent,
  },
  {
    path: 'check-transaction-details',
    canLoad: [LoginGuard],
    component: CheckTransactionDetailsContainerComponent,
  },
  {
    path: 'proposal-submitted',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: TransactionProposalSubmittedContainerComponent,
  },
  {
    path: 'cancel',
    canLoad: [LoginGuard],
    canActivate: [LoginGuard],
    component: CancelTransactionProposalContainerComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TransactionProposalRoutingModule {}
