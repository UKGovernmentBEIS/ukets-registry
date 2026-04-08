import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BulkClaimAccountRoutingModule } from '@bulk-claim-account/bulk-claim-account-routing.module';
import { SharedModule } from '@shared/shared.module';
import {
  BulkClaimAccountInfoComponent,
  BulkClaimAccountInfoContainerComponent,
  BulkClaimAccountSubmittedContainerComponent,
  CancelBulkClaimAccountContainerComponent,
  ConfirmBulkClaimAccountComponent,
  ConfirmBulkClaimAccountContainerComponent,
} from '@bulk-claim-account/components';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import {
  BulkClaimAccountNavigationEffects,
  BulkClaimAccountEffects,
} from '@bulk-claim-account/store/effects';
import * as fromBulkClaimAccountReducer from '@bulk-claim-account/store/reducers';

@NgModule({
  declarations: [
    BulkClaimAccountInfoContainerComponent,
    BulkClaimAccountInfoComponent,
    BulkClaimAccountSubmittedContainerComponent,
    CancelBulkClaimAccountContainerComponent,
    ConfirmBulkClaimAccountContainerComponent,
    ConfirmBulkClaimAccountComponent,
  ],
  imports: [
    CommonModule,
    SharedModule,
    BulkClaimAccountRoutingModule,
    StoreModule.forFeature(
      fromBulkClaimAccountReducer.bulkClaimAccountReducerFeatureKey,
      fromBulkClaimAccountReducer.reducer
    ),
    EffectsModule.forFeature([
      BulkClaimAccountNavigationEffects,
      BulkClaimAccountEffects,
    ]),
  ],
})
export class BulkClaimAccountModule {}
