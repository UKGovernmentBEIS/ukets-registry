import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TransactionProposalRoutingModule } from './transaction-proposal-routing.module';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  SelectAcquiringPredefinedAccountsComponent,
  CancelTransactionProposalComponent,
  CancelTransactionProposalContainerComponent,
  CheckTransactionDetailsComponent,
  CheckTransactionDetailsContainerComponent,
  TransactionProposalSubmittedComponent,
  TransactionProposalSubmittedContainerComponent,
  SelectTransactionTypeComponent,
  SelectTransactionTypeContainerComponent,
  SpecifyUnitTypesAndQuantityComponent,
  SelectUnitTypesAndQuantityContainerComponent,
  SpecifyQuantityComponent,
  SelectAcquiringAccountComponent,
  SpecifyAcquiringAccountContainerComponent,
  TransactionCommentFormComponent,
  TitleProposalTransactionTypeComponent,
} from '@transaction-proposal/components';
import { EffectsModule } from '@ngrx/effects';
import {
  TransactionProposalEffects,
  TransactionProposalWizardNavigationEffects,
} from '@transaction-proposal/effects';
import { TransactionProposalService } from '@transaction-proposal/services';
import { StoreModule } from '@ngrx/store';
import * as fromTransactionProposal from '@transaction-proposal/reducers';
import { SigningModule } from '@signing/signing.module';
import { YearOfReturnComponent } from './components/year-of-return/year-of-return.component';
import { SetTransactionReferenceContainerComponent } from '@transaction-proposal/components/set-transaction-reference';

@NgModule({
  declarations: [
    SelectAcquiringPredefinedAccountsComponent,
    SelectTransactionTypeContainerComponent,
    SelectTransactionTypeComponent,
    SelectUnitTypesAndQuantityContainerComponent,
    SpecifyQuantityComponent,
    SpecifyUnitTypesAndQuantityComponent,
    CancelTransactionProposalComponent,
    CancelTransactionProposalContainerComponent,
    SelectAcquiringAccountComponent,
    SpecifyAcquiringAccountContainerComponent,
    CheckTransactionDetailsComponent,
    CheckTransactionDetailsContainerComponent,
    TransactionProposalSubmittedComponent,
    TransactionProposalSubmittedContainerComponent,
    TransactionCommentFormComponent,
    TitleProposalTransactionTypeComponent,
    YearOfReturnComponent,
    SetTransactionReferenceContainerComponent,
  ],
  imports: [
    CommonModule,
    TransactionProposalRoutingModule,
    StoreModule.forFeature(
      fromTransactionProposal.transactionProposalFeatureKey,
      fromTransactionProposal.reducer
    ),
    EffectsModule.forFeature([
      TransactionProposalEffects,
      TransactionProposalWizardNavigationEffects,
    ]),
    SharedModule,
    SigningModule,
    ReactiveFormsModule,
  ],
  providers: [TransactionProposalService],
})
export class TransactionProposalModule {}
