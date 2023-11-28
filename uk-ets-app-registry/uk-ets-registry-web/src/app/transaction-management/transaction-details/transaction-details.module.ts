import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TransactionDetailsRoutingModule } from './transaction-details-routing.module';
import { EffectsModule } from '@ngrx/effects';
import * as fromTransactionDetails from './transaction-details.reducer';
import * as fromAccount from '@account-management/account/account-details/account.reducer';
import { StoreModule } from '@ngrx/store';
import { SharedModule } from '@shared/shared.module';
import {
  ManuallyCancelTransactionComponent,
  ManuallyCancelTransactionContainerComponent,
  ResponseMessagesComponent,
  TransactionCancelledConfirmationComponent,
  TransactionCancelledConfirmationContainerComponent,
  TransactionDetailsContainerComponent,
  TransactionHeaderComponent,
  TransactionInfoComponent,
  TransactionQuantityComponent,
} from '@transaction-management/transaction-details/components';
import { ReactiveFormsModule } from '@angular/forms';
import { TransactionDetailsEffects } from '@transaction-management/transaction-details/effects';
import { TransactionManagementService } from '@transaction-management/service/transaction-management.service';
import { TransactionProposalModule } from '@transaction-proposal/transaction-proposal.module';
import { TransactionAtrributesPipe } from '@shared/pipes';

@NgModule({
  declarations: [
    TransactionDetailsContainerComponent,
    TransactionInfoComponent,
    ResponseMessagesComponent,
    ResponseMessagesComponent,
    TransactionQuantityComponent,
    ManuallyCancelTransactionComponent,
    ManuallyCancelTransactionContainerComponent,
    TransactionHeaderComponent,
    TransactionCancelledConfirmationComponent,
    TransactionCancelledConfirmationContainerComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    TransactionDetailsRoutingModule,
    StoreModule.forFeature(
      fromTransactionDetails.transactionDetailsFeatureKey,
      fromTransactionDetails.reducer
    ),
    //TODO: Decouple account reducer from transaction details module (UKETS-4581)
    StoreModule.forFeature(fromAccount.accountFeatureKey, fromAccount.reducer),
    EffectsModule.forFeature([TransactionDetailsEffects]),
    ReactiveFormsModule,
    TransactionProposalModule,
  ],
  providers: [TransactionManagementService, TransactionAtrributesPipe],
  exports: [],
})
export class TransactionDetailsModule {}
