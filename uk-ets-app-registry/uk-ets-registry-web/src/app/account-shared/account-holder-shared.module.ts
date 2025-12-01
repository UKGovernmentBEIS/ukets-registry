import { NgModule } from '@angular/core';
import {
  FormatAccountHolderResultPipe,
  FormatTypeAheadAccountHolderResultPipe,
} from '@account-shared/pipes';
import { SharedModule } from '@shared/shared.module';
import {
  AccountHolderLongOverviewComponent,
  AccountHolderPrimaryContactOverviewComponent,
  AccountHolderShortOverviewComponent,
} from '@account-shared/components';

@NgModule({
  declarations: [
    FormatTypeAheadAccountHolderResultPipe,
    FormatAccountHolderResultPipe,
    AccountHolderLongOverviewComponent,
    AccountHolderPrimaryContactOverviewComponent,
    AccountHolderShortOverviewComponent,
  ],
  exports: [
    FormatTypeAheadAccountHolderResultPipe,
    FormatAccountHolderResultPipe,
    AccountHolderLongOverviewComponent,
    AccountHolderShortOverviewComponent,
    AccountHolderPrimaryContactOverviewComponent,
  ],
  imports: [SharedModule],
})
export class AccountHolderSharedModule {}
