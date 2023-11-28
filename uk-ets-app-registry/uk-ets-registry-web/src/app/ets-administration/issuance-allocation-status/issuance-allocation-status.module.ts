import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  IssuanceAllocationStatusesComponent,
  IssuanceAllocationStatusesContainerComponent,
  AllocationTableHistoryContainerComponent,
  IssuanceAllocationOverviewComponent
} from './components';
import { IssuanceAllocationStatusRoutingModule } from './issuance-allocation-status-routing.module';
import { SharedModule } from '@shared/shared.module';
import { IssuanceAllocationStatusEffects } from './store/effects';
import { StoreModule } from '@ngrx/store';
import * as fromIssuanceAllocationStatusReducer from './store/reducers';
import { EffectsModule } from '@ngrx/effects';
@NgModule({
  declarations: [
    IssuanceAllocationStatusesComponent,
    IssuanceAllocationStatusesContainerComponent,
    AllocationTableHistoryContainerComponent,
    IssuanceAllocationOverviewComponent
  ],
  imports: [
    IssuanceAllocationStatusRoutingModule,
    CommonModule,
    SharedModule,
    StoreModule.forFeature(
      fromIssuanceAllocationStatusReducer.issuanceAllocationStatusFeatureKey,
      fromIssuanceAllocationStatusReducer.reducer
    ),
    EffectsModule.forFeature([IssuanceAllocationStatusEffects])
  ]
})
export class IssuanceAllocationStatusModule {}
