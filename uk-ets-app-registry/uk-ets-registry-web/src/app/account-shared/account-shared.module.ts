import { NgModule } from '@angular/core';
import {
  FormatAccountHolderResultPipe,
  FormatTypeAheadAccountHolderResultPipe,
} from '@account-shared/pipes';

@NgModule({
  declarations: [
    FormatTypeAheadAccountHolderResultPipe,
    FormatAccountHolderResultPipe,
  ],
  exports: [
    FormatTypeAheadAccountHolderResultPipe,
    FormatAccountHolderResultPipe,
  ],
})
export class AccountSharedModule {}
