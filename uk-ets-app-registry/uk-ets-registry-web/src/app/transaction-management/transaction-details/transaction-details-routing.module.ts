import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginGuard } from '@shared/guards';
import {
  ManuallyCancelTransactionContainerComponent,
  TransactionCancelledConfirmationContainerComponent,
  TransactionDetailsContainerComponent,
} from '@transaction-management/transaction-details/components';
import { TransactionHeaderGuard } from '@transaction-management/guards';
import { ClearTransactionProposalGuard } from '@transaction-proposal/guards/clear-transaction-proposal-guard.service';

const routes: Routes = [
  {
    path: ':transactionIdentifier',
    canActivate: [LoginGuard, TransactionHeaderGuard],
    component: TransactionDetailsContainerComponent,
    title: 'Transaction Details',
  },
  {
    path: ':transactionIdentifier/cancel',
    canActivate: [LoginGuard],
    component: ManuallyCancelTransactionContainerComponent,
  },
  {
    path: ':transactionIdentifier/cancelled',
    canActivate: [LoginGuard],
    component: TransactionCancelledConfirmationContainerComponent,
  },
  {
    path: ':transactionIdentifier/transactions',
    canDeactivate: [ClearTransactionProposalGuard],
    loadChildren: () =>
      import('@transaction-proposal/transaction-proposal.module').then(
        (m) => m.TransactionProposalModule
      ),
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TransactionDetailsRoutingModule {}
