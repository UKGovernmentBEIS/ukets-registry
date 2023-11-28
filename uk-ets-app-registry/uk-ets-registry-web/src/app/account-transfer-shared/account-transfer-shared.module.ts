import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '@shared/shared.module';
import {
  AcquiringAccountHolderContactSummaryComponent,
  AcquiringAccountHolderIndividualSummaryComponent,
  AcquiringAccountHolderOrganisationSummaryComponent,
  TransferringAccountHolderSummaryComponent,
} from '@account-transfer-shared/components';

@NgModule({
  declarations: [
    AcquiringAccountHolderIndividualSummaryComponent,
    AcquiringAccountHolderOrganisationSummaryComponent,
    TransferringAccountHolderSummaryComponent,
    AcquiringAccountHolderContactSummaryComponent,
  ],
  imports: [CommonModule, SharedModule],
  exports: [
    AcquiringAccountHolderIndividualSummaryComponent,
    AcquiringAccountHolderOrganisationSummaryComponent,
    TransferringAccountHolderSummaryComponent,
    AcquiringAccountHolderContactSummaryComponent,
  ],
})
export class AccountTransferSharedModule {}
