import { NgModule } from '@angular/core';
import {
  FormatAccountHolderResultPipe,
  FormatTypeAheadAccountHolderResultPipe,
} from '@account-shared/pipes';
import { SharedModule } from '@shared/shared.module';
import {
  AccountHolderOverviewComponent,
  AccountHolderContactOverviewComponent,
  AccountHolderShortOverviewComponent,
  AccountHolderHeadingOverviewComponent,
} from '@account-shared/components';

@NgModule({
  declarations: [
    FormatTypeAheadAccountHolderResultPipe,
    FormatAccountHolderResultPipe,
    AccountHolderHeadingOverviewComponent,
    AccountHolderOverviewComponent,
    AccountHolderContactOverviewComponent,
    AccountHolderShortOverviewComponent,
  ],
  exports: [
    FormatTypeAheadAccountHolderResultPipe,
    FormatAccountHolderResultPipe,
    AccountHolderHeadingOverviewComponent,
    AccountHolderOverviewComponent,
    AccountHolderShortOverviewComponent,
    AccountHolderContactOverviewComponent,
  ],
  imports: [SharedModule],
})
export class AccountSharedModule {}
