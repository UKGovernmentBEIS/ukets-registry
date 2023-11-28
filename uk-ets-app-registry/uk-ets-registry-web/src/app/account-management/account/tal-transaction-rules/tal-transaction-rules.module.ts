import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CancelUpdateRequestContainerComponent,
  CheckUpdateRequestComponent,
  CheckUpdateRequestContainerComponent,
  RequestSubmittedContainerComponent,
  SecondApprovalNecessaryComponent,
  SecondApprovalNecessaryContainerComponent,
  TransfersOutsideListComponent,
  TransfersOutsideListContainerComponent,
} from '@tal-transaction-rules/components';
import { TalTransactionRulesRoutingModule } from '@tal-transaction-rules/tal-transaction-rules-routing.module';
import { StoreModule } from '@ngrx/store';
import * as fromTrustedAccountList from './reducers';
import { EffectsModule } from '@ngrx/effects';
import { SharedModule } from '@shared/shared.module';
import { ReactiveFormsModule } from '@angular/forms';
import {
  TalTransactionRulesEffects,
  TalTransactionRulesNavigationEffects,
} from '@tal-transaction-rules/effects';
import { TalTransactionRulesApiService } from '@tal-transaction-rules/services';
import { AccountModule } from '@account-management/account/account.module';
import {
  SinglePersonSurrenderExcessAllocationComponent,
  SinglePersonSurrenderExcessAllocationContainerComponent,
} from '@tal-transaction-rules/components/single-person-surrender-excess-allocation';

@NgModule({
  declarations: [
    TransfersOutsideListComponent,
    TransfersOutsideListContainerComponent,
    SecondApprovalNecessaryComponent,
    SecondApprovalNecessaryContainerComponent,
    SinglePersonSurrenderExcessAllocationComponent,
    SinglePersonSurrenderExcessAllocationContainerComponent,
    CheckUpdateRequestComponent,
    CheckUpdateRequestContainerComponent,
    RequestSubmittedContainerComponent,
    CancelUpdateRequestContainerComponent,
  ],
  imports: [
    CommonModule,
    TalTransactionRulesRoutingModule,
    StoreModule.forFeature(
      fromTrustedAccountList.talTransactionRulesFeatureKey,
      fromTrustedAccountList.reducer
    ),
    EffectsModule.forFeature([
      TalTransactionRulesEffects,
      TalTransactionRulesNavigationEffects,
    ]),
    SharedModule,
    ReactiveFormsModule,
    AccountModule,
  ],
  providers: [TalTransactionRulesApiService],
})
export class TalTransactionRulesModule {}
